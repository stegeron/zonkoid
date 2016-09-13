package eu.urbancoders.zonkysniper.integration;

import eu.urbancoders.zonkysniper.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.AuthToken;
import eu.urbancoders.zonkysniper.dataobjects.Investment;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.dataobjects.Message;
import eu.urbancoders.zonkysniper.dataobjects.Wallet;
import eu.urbancoders.zonkysniper.dataobjects.ZonkyAPIError;
import eu.urbancoders.zonkysniper.events.GetInvestments;
import eu.urbancoders.zonkysniper.events.GetLoanDetail;
import eu.urbancoders.zonkysniper.events.GetMessagesFromZonky;
import eu.urbancoders.zonkysniper.events.GetWallet;
import eu.urbancoders.zonkysniper.events.Invest;
import eu.urbancoders.zonkysniper.events.PasswordReset;
import eu.urbancoders.zonkysniper.events.RefreshToken;
import eu.urbancoders.zonkysniper.events.ReloadMarket;
import eu.urbancoders.zonkysniper.events.UserLogin;
import eu.urbancoders.zonkysniper.events.UserLogout;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


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

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        zonkyService = retrofit.create(ZonkyService.class);

        responseBodyConverter = ZonkyClient.retrofit.responseBodyConverter(ZonkyAPIError.class, new Annotation[0]);
    }

    @Subscribe
    public void loginUser(final UserLogin.Request evt) {

        if(!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        if(evt.userName == null || evt.password == null || evt.userName.isEmpty() || evt.password.isEmpty()) {
            ZonkyAPIError noCredentials = new ZonkyAPIError();
            noCredentials.setError("No credentials");
            noCredentials.setError_description("Nejsou zadané přihlašovací údaje.");
//            EventBus.getDefault().post(new UnresolvableError.Request(noCredentials));
            return;
        }

        Call<AuthToken> call = zonkyService.getAuthToken(evt.userName, evt.password, "password", "SCOPE_APP_WEB");

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

    @Subscribe
    public void getInvestments(final GetInvestments.Request evt) {

        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        AuthToken _authToken = ZonkySniperApplication.getInstance().getAuthToken();
        if (_authToken == null || _authToken.getExpires_in() < System.currentTimeMillis()) {
            ZonkySniperApplication.getInstance().login();
            return;
        }

        Call<List<Investment>> call = zonkyService.getInvestments(
                "Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token(),
                evt.getLoanId());

        call.enqueue(new Callback<List<Investment>>() {
            @Override
            public void onResponse(Call<List<Investment>> call, Response<List<Investment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EventBus.getDefault().post(new GetInvestments.Response(response.body()));
                } else {
                    resolveError(response, evt);
                }
            }

            @Override
            public void onFailure(Call<List<Investment>> call, Throwable t) {

            }
        });

    }

    @Subscribe
    public void getMessages(final GetMessagesFromZonky.Request evt) {
        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        AuthToken _authToken = ZonkySniperApplication.getInstance().getAuthToken();
        if (_authToken == null || _authToken.getExpires_in() < System.currentTimeMillis()) {
            ZonkySniperApplication.getInstance().login();
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

    @Subscribe
    public void reloadMarket(final ReloadMarket.Request evt) {
        Call<List<Loan>> call = zonkyService.getNewLoansOnMarket(40, "covered");

        call.enqueue(new Callback<List<Loan>>() {
            @Override
            public void onResponse(Call<List<Loan>> call, Response<List<Loan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Loan> resultLoans = response.body();

                    // setridit od nejnovejsiho po nejstarsi
                    Collections.sort(resultLoans, new Comparator<Loan>() {
                        @Override
                        public int compare(Loan one, Loan two) {
                            return one.getDeadline().after(two.getDeadline()) ? -1 : 1;
                        }
                    });

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

    @Subscribe
    public void getWallet(final GetWallet.Request evt) {

        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        AuthToken _authToken = ZonkySniperApplication.getInstance().getAuthToken();
        if (_authToken == null || _authToken.getExpires_in() < System.currentTimeMillis()) {
            ZonkySniperApplication.getInstance().login();
            return;
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

    @Subscribe
    public void invest(final Invest.Request evt) {

        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        Call<String> call = zonkyService.invest("Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token(), evt.getInvestment());

        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new Invest.Response());
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
            public void onFailure(Call<String> call, Throwable t) {
                // TODO sem to pada i kdyz se zainvestuje, a pise to "End of line 1 and column 1" - protoze resource vraci prazdne body
                EventBus.getDefault().post(new Invest.Response());
//                EventBus.getDefault().post(new Invest.Failure("Chyba", t.getMessage()));
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
    public void logout(UserLogout.Request evt) {

        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        ZonkySniperApplication.getInstance().setAuthToken(null);
        EventBus.getDefault().post(new UserLogout.Response());

//        Call<String> call = zonkyService.logout("Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token());
//
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                if (response.isSuccessful()) {
//                    EventBus.getDefault().post(new UserLogout.Response());
//                    ZonkySniperApplication.getInstance().setAuthToken(null);
//                } else {
//                    Log.e(TAG, "Logout failed: "+response.errorBody());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Log.e(TAG, "Logout failed: " + t.getMessage());
//            }
//        });
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
                ZonkySniperApplication.getInstance().login();
            }

            if("insufficientBalance".equalsIgnoreCase(error.getError())) {
                EventBus.getDefault().post(new Invest.Failure(error.getError(), "Nemáte dostatek prostředků"));
            }
        }
    }
}
