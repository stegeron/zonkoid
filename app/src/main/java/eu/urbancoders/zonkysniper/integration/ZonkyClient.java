package eu.urbancoders.zonkysniper.integration;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.AuthToken;
import eu.urbancoders.zonkysniper.dataobjects.Investment;
import eu.urbancoders.zonkysniper.dataobjects.Investor;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.dataobjects.Message;
import eu.urbancoders.zonkysniper.dataobjects.Question;
import eu.urbancoders.zonkysniper.dataobjects.Wallet;
import eu.urbancoders.zonkysniper.dataobjects.ZonkyAPIError;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.Portfolio;
import eu.urbancoders.zonkysniper.events.GetInvestments;
import eu.urbancoders.zonkysniper.events.GetInvestor;
import eu.urbancoders.zonkysniper.events.GetLoanDetail;
import eu.urbancoders.zonkysniper.events.GetMessagesFromZonky;
import eu.urbancoders.zonkysniper.events.GetPortfolio;
import eu.urbancoders.zonkysniper.events.GetQuestions;
import eu.urbancoders.zonkysniper.events.GetWallet;
import eu.urbancoders.zonkysniper.events.Invest;
import eu.urbancoders.zonkysniper.events.LogInvestment;
import eu.urbancoders.zonkysniper.events.LoginCheck;
import eu.urbancoders.zonkysniper.events.PasswordReset;
import eu.urbancoders.zonkysniper.events.RefreshToken;
import eu.urbancoders.zonkysniper.events.ReloadMarket;
import eu.urbancoders.zonkysniper.events.SendQuestion;
import eu.urbancoders.zonkysniper.events.UnresolvableError;
import eu.urbancoders.zonkysniper.events.UserLogin;
import eu.urbancoders.zonkysniper.events.UserLogout;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 14.05.2016
 */
public class ZonkyClient {
    private static final String TAG = ZonkyClient.class.getName();
    public static final String BASE_URL = "https://api.zonky.cz/";
//    public static final String BASE_URL = "http://10.0.2.2:8089/"; //xTODO MOCK!!! odstranit pred migraci

    private static Retrofit retrofit;
    private static Converter<ResponseBody, ZonkyAPIError> responseBodyConverter;
    private ZonkyService zonkyService;

    // TODO / tohle je potreba vyresit - na Androidu 4.2.2. hazi chybu, proto tu je tenhle untrusted kod :(
    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            ZonkoidLoggingInterceptor interceptor = new ZonkoidLoggingInterceptor();
            interceptor.setLevel(ZonkoidLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .build();


            return client;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ZonkyClient() {

        EventBus.getDefault().register(this);

//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(interceptor)
//                .build();

        OkHttpClient client = getUnsafeOkHttpClient();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        zonkyService = retrofit.create(ZonkyService.class);

        responseBodyConverter = ZonkyClient.retrofit.responseBodyConverter(ZonkyAPIError.class, new Annotation[0]);
    }

    @Subscribe
    public void loginSynchronous(final UserLogin.Request evt) {

        if(!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        if(evt.userName == null || evt.password == null || evt.userName.isEmpty() || evt.password.isEmpty()) {
            throwIncorrectCredentialsEvent();
            return;
        }

        Call<AuthToken> call = zonkyService.getAuthToken(evt.userName, evt.password, "password", "SCOPE_APP_WEB");

        try {
            Response<AuthToken> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                ZonkySniperApplication.authFailed = false;
                AuthToken newToken = response.body();
                newToken.setExpires_in(System.currentTimeMillis() + newToken.getExpires_in() * 900);
                ZonkySniperApplication.getInstance().setAuthToken(newToken);
                EventBus.getDefault().post(new UserLogin.Response(newToken));
            } else {
                throwIncorrectCredentialsEvent();
                ZonkySniperApplication.authFailed = true;
            }
        } catch (IOException e) {
            Log.d(TAG, "LOGIN error " + e.getMessage(), e);
            ZonkySniperApplication.authFailed = true;
        }
    }

    /**
     * Pokud se nedari prihlasit, vyhodit chybu uzivateli
     */
    private void throwIncorrectCredentialsEvent() {
        ZonkyAPIError noCredentials = new ZonkyAPIError();
        noCredentials.setError("No credentials");
        noCredentials.setError_description("Nejsou zadané správné přihlašovací údaje nebo není Zonky dostupný.");
        EventBus.getDefault().post(new UnresolvableError.Request(noCredentials));
    }

    @Subscribe
    public void refreshToken(final RefreshToken.Request evt) {

        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        Call<AuthToken> call = zonkyService.refreshAuthToken(evt.getTokenInvalid().getRefresh_token(), "refresh_token", "SCOPE_APP_WEB");

        call.enqueue(new Callback<AuthToken>() {
            @Override
            public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthToken newToken = response.body();
                    newToken.setExpires_in(System.currentTimeMillis() + newToken.getExpires_in() * 900);
                    ZonkySniperApplication.getInstance().setAuthToken(newToken);
                    EventBus.getDefault().post(new UserLogin.Response(newToken));
                } else {
                    resolveError(response, evt);
                    ZonkySniperApplication.authFailed = true;
                }
            }

            @Override
            public void onFailure(Call<AuthToken> call, Throwable t) {

            }
        });
    }

    @Subscribe
    public void getLoanDetail(final GetLoanDetail.Request evt) {
        Call<Loan> call = zonkyService.getLoanDetail(evt.getLoanId());

        call.enqueue(new Callback<Loan>() {
            @Override
            public void onResponse(Call<Loan> call, Response<Loan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EventBus.getDefault().post(new GetLoanDetail.Response(response.body()));
                } else {
                    resolveError(response, evt);
                }
            }

            @Override
            public void onFailure(Call<Loan> call, Throwable t) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void getInvestments(final GetInvestments.Request evt) {

        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        AuthToken _authToken = ZonkySniperApplication.getInstance().getAuthToken();
        if (_authToken == null || _authToken.getExpires_in() < System.currentTimeMillis()) {
            ZonkySniperApplication.getInstance().loginSynchronous();
            return;
        }

        Call<List<Investment>> call = zonkyService.getInvestments(
                "Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token(),
                evt.getLoanId());

        try {
            Response<List<Investment>> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                EventBus.getDefault().post(new GetInvestments.Response(response.body()));
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get investors. "+e.getMessage(), e);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void sendOrUpdateQuestion(final SendQuestion.Request evt) {

        if (!ZonkySniperApplication.getInstance().isLoginAllowed() || evt.getQuestion() == null) {
            return;
        }

        AuthToken _authToken = ZonkySniperApplication.getInstance().getAuthToken();
        if (_authToken == null || _authToken.getExpires_in() < System.currentTimeMillis()) {
            ZonkySniperApplication.getInstance().loginSynchronous();
            return;
        }

        Call<Void> call = null;
        if(evt.getQuestion().getId() != null && "".equals(evt.getQuestion().getMessage())) {
            //TODO mazeme otazku
        } else if(evt.getQuestion().getId() != null) {
            // editujeme
            call = zonkyService.sendEditedQuestion(
                    "Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token(),
                    evt.getLoanId(),
                    evt.getQuestion().getId(),
                    evt.getQuestion()
            );
        } else if(evt.getQuestion().getId() == null) {
            // posilame novy dotaz
            call = zonkyService.sendNewQuestion(
                    "Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token(),
                    evt.getLoanId(),
                    evt.getQuestion());
        }

        try {
            Response<Void> response = call.execute();
            if (response.isSuccessful()) {
                // otazku jsme asi propasirovali, tak zkusime prenacist
                EventBus.getDefault().post(new GetQuestions.Request(evt.getLoanId(), Constants.NUM_OF_ROWS));
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to process questions. " + e.getMessage(), e);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void getMessages(final GetMessagesFromZonky.Request evt) {
        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        AuthToken _authToken = ZonkySniperApplication.getInstance().getAuthToken();
        if (_authToken == null || _authToken.getExpires_in() < System.currentTimeMillis()) {
            ZonkySniperApplication.getInstance().loginSynchronous();
            return;
        }

        Call<List<Message>> call = zonkyService.getMessages(
                "Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token(),
                evt.getNumberOfRows()
        );

        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EventBus.getDefault().post(new GetMessagesFromZonky.Response(response.body()));
                } else {
                    resolveError(response, evt);
                }

            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                // todo logcat
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void reloadMarket(final ReloadMarket.Request evt) {

        Call<List<Loan>> call;

        if (ZonkySniperApplication.getInstance().isLoginAllowed()) {
            AuthToken _authToken = ZonkySniperApplication.getInstance().getAuthToken();
            if (_authToken == null || _authToken.getExpires_in() < System.currentTimeMillis()) {
                ZonkySniperApplication.getInstance().loginSynchronous();
            }

            call = zonkyService.getNewLoansOnMarket(
                    evt.getNumOfRows(),
                    evt.getPageNumber(),
                    "-datePublished",
                    "Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token(),
                    evt.isShowCovered()? null : 0
            );
        } else {
            call = zonkyService.getNewLoansOnMarket(
                    evt.getNumOfRows(),
                    evt.getPageNumber(),
                    "-datePublished",
                    null,
                    evt.isShowCovered() ? null : 0
            );
        }

        call.enqueue(new Callback<List<Loan>>() {
            @Override
            public void onResponse(Call<List<Loan>> call, Response<List<Loan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Loan> resultLoans = response.body();

                    // setridit od nejnovejsiho po nejstarsi
//                    Collections.sort(resultLoans, new Comparator<Loan>() {
//                        @Override
//                        public int compare(Loan one, Loan two) {
//                            return one.getDeadline().after(two.getDeadline()) ? -1 : 1;
//                        }
//                    });

                    EventBus.getDefault().post(new ReloadMarket.Response(resultLoans));
                } else {
                    if(response.code() == 503) {
                        // docasne nedostupno
                        EventBus.getDefault().post(new ReloadMarket.Failure("503"));
                    } else {
                        resolveError(response, evt);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Loan>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void getWallet(final GetWallet.Request evt) {

        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        AuthToken _authToken = ZonkySniperApplication.getInstance().getAuthToken();
        if (_authToken == null || _authToken.getExpires_in() < System.currentTimeMillis()) {
            ZonkySniperApplication.getInstance().loginSynchronous();
        }

        // pokud neni nasetovany investor, tak ho tady pro sichr nacteme... obcas jdeme z notifky a pak by to delalo bordel
        if(ZonkySniperApplication.getInstance().getUser() == null) {
            EventBus.getDefault().post(new GetInvestor.Request());
        }

        Call<Wallet> call = zonkyService.getWallet("Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token());

        call.enqueue(new Callback<Wallet>() {
            @Override
            public void onResponse(Call<Wallet> call, Response<Wallet> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EventBus.getDefault().post(new GetWallet.Response(response.body()));
                } else {
                    resolveError(response, evt);
                    ZonkySniperApplication.authFailed = true;
                }
            }

            @Override
            public void onFailure(Call<Wallet> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void getInvestor(final GetInvestor.Request evt) {

        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        AuthToken _authToken = ZonkySniperApplication.getInstance().getAuthToken();
        if (_authToken == null || _authToken.getExpires_in() < System.currentTimeMillis()) {
            ZonkySniperApplication.getInstance().loginSynchronous();
            return;
        }

        Call<Investor> call = zonkyService.getInvestorDetails("Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token());

        call.enqueue(new Callback<Investor>() {
            @Override
            public void onResponse(Call<Investor> call, Response<Investor> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Investor investor = response.body();
                    ZonkySniperApplication.getInstance().setUser(investor);
                    EventBus.getDefault().post(new GetInvestor.Response(investor));
                    EventBus.getDefault().post(new LoginCheck.Request(investor));
                } else {
                    resolveError(response, evt);
                    ZonkySniperApplication.authFailed = true;
                }
            }

            @Override
            public void onFailure(Call<Investor> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void invest(final Invest.Request evt) {

        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        Call<Void> call = zonkyService.invest("Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token(), evt.getInvestment());

        call.enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new Invest.Response());
                    /**
                     * Zalogujeme investici
                     */
                    EventBus.getDefault().post(new LogInvestment.Request(evt.getInvestment(), ZonkySniperApplication.getInstance().getUsername()));
                } else {
                    /*{
                        "error" : "insufficientBalance",
                        "uuid" : "1b92a6eb-6d96-4989-9e07-464795f6c845"
                     }
                    */
                    try {
                        EventBus.getDefault().post(new Invest.Failure("Chyba", responseBodyConverter.convert(response.errorBody()).getError()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                EventBus.getDefault().post(new Invest.Failure("Chyba", t.getMessage()));
            }
        });
    }

    /**
     * Reset hesla
     * @param evt
     */
    @Subscribe
    public void passwordReset(PasswordReset.Request evt) {
        Call<Void> call = zonkyService.passwordReset(evt.getPasswordResetter());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new PasswordReset.Response());
                } else {
                    EventBus.getDefault().post(new PasswordReset.Failure(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    @Subscribe
    public void getPortfolio(GetPortfolio.Request evt) {

        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        Call<Portfolio> call = zonkyService.getPortfolio("Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token());

        call.enqueue(new Callback<Portfolio>() {
            @Override
            public void onResponse(Call<Portfolio> call, Response<Portfolio> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new GetPortfolio.Response(response.body()));
                } else {
                    // nic, mozna nekdy nejakou hlasku?
                }
            }

            @Override
            public void onFailure(Call<Portfolio> call, Throwable t) {

            }
        });
    }

    @Subscribe
    public void logout(UserLogout.Request evt) {

        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        ZonkySniperApplication.getInstance().setAuthToken(null);
        EventBus.getDefault().post(new UserLogout.Response());

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void getQuestions(GetQuestions.Request evt) {
        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        AuthToken _authToken = ZonkySniperApplication.getInstance().getAuthToken();
        if (_authToken == null || _authToken.getExpires_in() < System.currentTimeMillis()) {
            ZonkySniperApplication.getInstance().loginSynchronous();
            return;
        }

        Call<List<Question>> call = zonkyService.getQuestions("Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token(),
                evt.getNumberOfQuestions(), evt.getLoanId());

        try {
            Response<List<Question>> response = call.execute();
            if (response.isSuccessful()) {
                EventBus.getDefault().post(new GetQuestions.Response(response.body()));
            } else {
                EventBus.getDefault().post(new GetQuestions.Failure("Nepodařilo se načíst dotazy"));
            }

        } catch (IOException e) {
            Log.e(TAG, "GetQuestions: " + e.getMessage(), e);
            EventBus.getDefault().post(new GetQuestions.Failure("Nepodařilo se načíst dotazy"));
        }
    }

    /************************************/
    /********* ERROR HANDLING ***********/
    /************************************/
    public static void resolveError(Response<?> response, Object evt) {
        if (response.errorBody() != null) {


            ZonkyAPIError error = new ZonkyAPIError();

            try {
                error = responseBodyConverter.convert(response.errorBody());
            } catch (IOException e) {
                //TODO what todo?
            }

            if ("invalid_grant".equalsIgnoreCase(error.getError()) || "invalid_token".equalsIgnoreCase(error.getError())) {
                if(ZonkySniperApplication.authFailed) { // pokud uz to jednou selhalo, vyhlas chybu klientovi
//                    EventBus.getDefault().post(new UnresolvableError.Request(error));
                } else {
                    EventBus.getDefault().post(evt);
                }
            }

            if("invalid_request".equalsIgnoreCase(error.getError()) && error.getError_description().startsWith("Invalid refresh token")) {
                ZonkySniperApplication.getInstance().setAuthToken(null);
                ZonkySniperApplication.getInstance().loginSynchronous();
            }

            if("insufficientBalance".equalsIgnoreCase(error.getError())) {
                EventBus.getDefault().post(new Invest.Failure(error.getError(), "Nemáte dostatek prostředků"));
            }

            if ("alreadyCovered".equalsIgnoreCase(error.getError())) {
                EventBus.getDefault().post(new Invest.Failure(error.getError(), "Již kompletně zainvestováno"));
            }
        }
    }
}
