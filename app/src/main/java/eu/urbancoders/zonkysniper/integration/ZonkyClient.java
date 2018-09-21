package eu.urbancoders.zonkysniper.integration;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.perf.metrics.AddTrace;
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
import eu.urbancoders.zonkysniper.dataobjects.Rating;
import eu.urbancoders.zonkysniper.dataobjects.Restrictions;
import eu.urbancoders.zonkysniper.dataobjects.Wallet;
import eu.urbancoders.zonkysniper.dataobjects.WalletTransaction;
import eu.urbancoders.zonkysniper.dataobjects.ZonkyAPIError;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.CashFlow;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.Portfolio;
import eu.urbancoders.zonkysniper.events.GetInvestments;
import eu.urbancoders.zonkysniper.events.GetInvestor;
import eu.urbancoders.zonkysniper.events.GetInvestorRestrictions;
import eu.urbancoders.zonkysniper.events.GetLoanDetail;
import eu.urbancoders.zonkysniper.events.GetMessagesFromZonky;
import eu.urbancoders.zonkysniper.events.GetMyInvestments;
import eu.urbancoders.zonkysniper.events.GetPortfolio;
import eu.urbancoders.zonkysniper.events.GetQuestions;
import eu.urbancoders.zonkysniper.events.GetWallet;
import eu.urbancoders.zonkysniper.events.GetWalletTransactions;
import eu.urbancoders.zonkysniper.events.GetWalletTransactionsForLoan;
import eu.urbancoders.zonkysniper.events.Invest;
import eu.urbancoders.zonkysniper.events.InvestAdditional;
import eu.urbancoders.zonkysniper.events.InvestAuto;
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
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
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
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
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

            return new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .build();
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

    @AddTrace(name = "ZonkyClient.loginSynchronous")
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

    @AddTrace(name = "ZonkyClient.getLoanDetail")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void getLoanDetail(final GetLoanDetail.Request evt) {

        Call<Loan> call;
        if (ZonkySniperApplication.getInstance().isLoginAllowed() && ZonkySniperApplication.getInstance().getAuthToken() != null) {

            AuthToken _authToken = ZonkySniperApplication.getInstance().getAuthToken();
            if (_authToken == null || _authToken.getExpires_in() < System.currentTimeMillis()) {
                ZonkySniperApplication.getInstance().loginSynchronous();
                return;
            }

            call = zonkyService.getLoanDetail(
                    "Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token(),
                    evt.getLoanId());
        } else {
            call = zonkyService.getLoanDetail(
                    null,
                    evt.getLoanId());
        }

        try {
            Response<Loan> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                EventBus.getDefault().post(new GetLoanDetail.Response(response.body()));
            } else {
                //resolveError(response, evt);
            }
        }
        catch (IOException e) {
            Log.d(TAG, "Failed to get loan detail " + e.getMessage(), e);
        }
    }

    @AddTrace(name = "ZonkyClient.getInvestments")
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
                evt.getNumberOfItems(),
                evt.getNumberOfPage(),
                "-timeCreated",
                "Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token(),
                evt.getLoanId());

        try {
            Response<List<Investment>> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                int totalNumOfInvestors = 0;
                String xTotal = response.headers().get("X-Total");
                if(xTotal != null) {
                    totalNumOfInvestors = Integer.valueOf(xTotal);
                }
                EventBus.getDefault().post(new GetInvestments.Response(response.body(), totalNumOfInvestors));
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

        try {
            Response<List<Message>> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                EventBus.getDefault().post(new GetMessagesFromZonky.Response(response.body()));
            } else {
                Log.e(TAG, "Failed to get zonky messages");
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get zonky messages: "+ e.getMessage(), e);
        }
    }

    /**
     * Nacteni trziste na hlavni strane.
     * @param evt
     */
    @AddTrace(name = "ZonkyClient.reloadMarket")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void reloadMarket(final ReloadMarket.Request evt) {

        String fieldsToGet = "id,name,amount,photos,termInMonths," +
                "rating,interestRate,myInvestment,remainingInvestment,covered,insuranceActive";

        Call<List<Loan>> call;

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ZonkySniperApplication.getInstance().getApplicationContext());

//        PRIPRAVIT SELECTOR RATINGU
        StringBuilder ratingIn = new StringBuilder();
        for (Rating rating : Rating.values()) {
            if(sp.getBoolean(Constants.FILTER_MARKETPLACE_RATINGS + rating.name(), false)) {
                ratingIn.append("\"").append(rating.name()).append("\",");
            }
        }
        if(ratingIn.length() > 0) {
            ratingIn.deleteCharAt(ratingIn.length()-1);
            ratingIn.insert(0, "[");
            ratingIn.append("]");
        }
        try {
            ratingIn = new StringBuilder(URLEncoder.encode(ratingIn.toString(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//        PRIPRAVIT TERMINMONTHSFROM a TO
        Integer termInMonthsFrom = sp.getInt(Constants.FILTER_MARKETPLACE_TERMINMONTHS_FROM, 0);
        Integer termInMonthsTo = sp.getInt(Constants.FILTER_MARKETPLACE_TERMINMONTHS_TO, 84);

        // PRIPRAVIT POJISTENE ONLY
        Boolean insuredOnly = sp.getBoolean(Constants.SHARED_PREF_SHOW_INSURED_ONLY, false);

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
                    evt.isShowCovered()? null : 0,
                    fieldsToGet,
                    ratingIn.length() > 0 ? ratingIn.toString() : null,
                    termInMonthsFrom, termInMonthsTo,
                    insuredOnly ? true : null
            );
        } else {
            call = zonkyService.getNewLoansOnMarket(
                    evt.getNumOfRows(),
                    evt.getPageNumber(),
                    "-datePublished",
                    null,
                    evt.isShowCovered() ? null : 0,
                    fieldsToGet,
                    ratingIn.length() > 0 ? ratingIn.toString() : null,
                    termInMonthsFrom, termInMonthsTo,
                    insuredOnly ? true : null
            );
        }

        try {
            Response<List<Loan>> response = call.execute();
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
                    Log.e(TAG, "Failed to get/reload market.");
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "Failed to get/reload market.", e);
        }
    }

    @AddTrace(name = "ZonkyClient.getWallet")
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

        try {
            Response<Wallet> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                EventBus.getDefault().post(new GetWallet.Response(response.body()));
            } else {
//                resolveError(response, evt);
                ZonkySniperApplication.authFailed = true;
            }

        } catch (IOException e) {
            Log.e(TAG, "Failed to get wallet.", e);
        }
    }

    /**
     * Seznam transakci v penezence
     * @param evt
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void getWalletTransactions(GetWalletTransactions.Request evt) {
        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        AuthToken _authToken = ZonkySniperApplication.getInstance().getAuthToken();
        if (_authToken == null || _authToken.getExpires_in() < System.currentTimeMillis()) {
            ZonkySniperApplication.getInstance().loginSynchronous();
        }

        Call<List<WalletTransaction>> call = zonkyService.getWalletTransactions(
                "Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token(), evt.getNumberOfItemsMax(), evt.getTransactionDateFromFormatted());

        try {
            Response<List<WalletTransaction>> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                List<WalletTransaction> transactions = response.body();
                EventBus.getDefault().post(new GetWalletTransactions.Response(transactions));
            } else {
                Log.e(TAG, "Failed to getWalletTransactions.");
            }

        } catch (IOException e) {
            Log.e(TAG, "Failed to get wallet transactions", e);
        }
    }

    /**
     * Seznam transakci v penezence pro jednu pujcku
     * @param evt
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void getWalletTransactionsForLoan(GetWalletTransactionsForLoan.Request evt) {
        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        AuthToken _authToken = ZonkySniperApplication.getInstance().getAuthToken();
        if (_authToken == null || _authToken.getExpires_in() < System.currentTimeMillis()) {
            ZonkySniperApplication.getInstance().loginSynchronous();
        }

        Call<List<WalletTransaction>> call = zonkyService.getWalletTransactionsForLoan(
                "Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token(), evt.getLoanId());

        try {
            Response<List<WalletTransaction>> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                List<WalletTransaction> transactions = response.body();
                EventBus.getDefault().post(new GetWalletTransactionsForLoan.Response(transactions));
            } else {
                Log.e(TAG, "Failed to GetWalletTransactionsForLoan.");
            }

        } catch (IOException e) {
            Log.e(TAG, "Failed to get wallet transactions for loan", e);
        }
    }

    /**
     * Detail investora po prihlaseni
     * @param evt
     */
    @AddTrace(name = "ZonkyClient.getInvestor")
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

        try {
            Response<Investor> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                Investor investor = response.body();
                ZonkySniperApplication.getInstance().setUser(investor);
                EventBus.getDefault().post(new GetInvestor.Response(investor));
                EventBus.getDefault().post(new LoginCheck.Request(investor));
            } else {
                resolveError(response, evt);
                ZonkySniperApplication.authFailed = true;
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get investor detail.", e);
        }
    }

    @AddTrace(name = "ZonkyClient.getInvestorRestrictions")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void getInvestorRestrictions(final GetInvestorRestrictions.Request evt) {

        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        AuthToken _authToken = ZonkySniperApplication.getInstance().getAuthToken();
        if (_authToken == null || _authToken.getExpires_in() < System.currentTimeMillis()) {
            ZonkySniperApplication.getInstance().loginSynchronous();
            return;
        }

        Call<Restrictions> call = zonkyService.getInvestorRestrictions(
                "Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token());

        try {
            Response<Restrictions> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                EventBus.getDefault().post(new GetInvestorRestrictions.Response(response.body()));
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get investor restrictions.", e);
        }
    }

    @AddTrace(name = "ZonkyClient.invest")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void invest(final Invest.Request evt) {

        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            EventBus.getDefault().post(new Invest.Failure("Chyba", "Nemáte zadané přihlašovací údaje."));
            return;
        }

        Call<Void> call = zonkyService.invest("Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token(), evt.getInvestment());

        try {
            Response<Void> response = call.execute();
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
                    Log.e(TAG, "Failed to invest." + response.errorBody());
                    EventBus.getDefault().post(new Invest.Failure("Chyba", responseBodyConverter.convert(response.errorBody()).getError()));
                } catch (IOException e) {
                    EventBus.getDefault().post(new Invest.Failure("Chyba", e.getMessage()));
                    Log.e(TAG, "Failed to convert Error from zonky when investing", e);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to invest.", e);
        }
    }

    @AddTrace(name = "ZonkyClient.autoinvest")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void autoinvest(final InvestAuto.Request evt) {

        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            EventBus.getDefault().post(new InvestAuto.Failure("Chyba", "Nemáte zadané přihlašovací údaje.", evt.getInvestment().getLoanId()));
            return;
        }

        AuthToken _authToken = ZonkySniperApplication.getInstance().getAuthToken();
        if (_authToken == null || _authToken.getExpires_in() < System.currentTimeMillis()) {
            // login
            Call<AuthToken> callLogin = zonkyService.getAuthToken(
                    ZonkySniperApplication.getInstance().getUsername(),
                    ZonkySniperApplication.getInstance().getPassword(),
                    "password", "SCOPE_APP_WEB");

            try {
                Response<AuthToken> response = callLogin.execute();
                if (response.isSuccessful() && response.body() != null) {
                    ZonkySniperApplication.authFailed = false;
                    AuthToken newToken = response.body();
                    newToken.setExpires_in(System.currentTimeMillis() + newToken.getExpires_in() * 900);
                    ZonkySniperApplication.getInstance().setAuthToken(newToken);
                } else {
                    ZonkySniperApplication.authFailed = true;
                    Log.e(TAG, "LOGIN error within autoinvest. " + response.body());
                }
            } catch (IOException e) {
                Log.e(TAG, "LOGIN error within autoinvest" + e.getMessage(), e);
                ZonkySniperApplication.authFailed = true;
            }
        }

        // neslo se prihlasit, nema cenu pokracovat
        if(ZonkySniperApplication.authFailed) {
            return;
        }

        // getwallet checknout, jestli ma smysl investovat nebo je prazdna penezenka
        Wallet wallet = null;
        Call<Wallet> call = zonkyService.getWallet("Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token());
        try {
            Response<Wallet> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                wallet = response.body();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get wallet during autoinvest", e);
        }

        if(wallet == null || wallet.getAvailableBalance() < evt.getInvestment().getAmount()) {
            Log.i(TAG, "Failed to invest, available balance lower than amount to invest");
            return;
        }

        // invest
        Call<Void> callInvest = zonkyService.invest(
                "Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token(),
                evt.getInvestment());

        try {
            Response<Void> response = callInvest.execute();
            if (response.isSuccessful()) {
                EventBus.getDefault().post(new InvestAuto.Response(evt.getInvestment(), evt.getLoanHeader()));
                /**
                 * Zalogujeme investici
                 */
                EventBus.getDefault().post(new LogInvestment.Request(evt.getInvestment(), ZonkySniperApplication.getInstance().getUsername()));
            } else {
                try {
                    Log.e(TAG, "Failed to invest." + response.errorBody());
                    EventBus.getDefault().post(new InvestAuto.Failure("Chyba", responseBodyConverter.convert(response.errorBody()).getError(), evt.getInvestment().getLoanId()));
                } catch (IOException e) {
                    EventBus.getDefault().post(new InvestAuto.Failure("Chyba", e.getMessage(), evt.getInvestment().getLoanId()));
                    Log.e(TAG, "Failed to convert Error from zonky when investing", e);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to invest.", e);
        }
    }

    @AddTrace(name = "ZonkyClient.investAdditional")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void investAdditional(final InvestAdditional.Request evt) {

        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        Call<Void> call = zonkyService.investAdditional(
                "Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token(),
                evt.getInvestment(),
                evt.getInvestment().getId()
                );

        try {
            Response<Void> response = call.execute();

            if (response.isSuccessful()) {
                EventBus.getDefault().post(new Invest.Response());
                /**
                 * Zalogujeme dodatecnou investici
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
                    Log.e(TAG, "Failed to convert error when additional investment", e);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to post additional investment", e);
        }
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

    @AddTrace(name = "ZonkyClient.getPortfolio")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void getPortfolio(GetPortfolio.Request evt) {

        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        AuthToken _authToken = ZonkySniperApplication.getInstance().getAuthToken();
        if (_authToken == null || _authToken.getExpires_in() < System.currentTimeMillis()) {
            ZonkySniperApplication.getInstance().loginSynchronous();
//            return;
        }

        Call<Portfolio> call = zonkyService.getPortfolio("Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token());

        try {
            Response<Portfolio> response = call.execute();
            if (response.isSuccessful()) {
                // doplneni do 12 mesicu, pokud Zonky nevraci komplet cashflow
                Portfolio port = response.body();
                List<CashFlow> dummyList = new ArrayList<>();
                if(port.getCashFlow() == null) {
                    port.setCashFlow(new ArrayList<CashFlow>());
                }
                for(int i = 1; i < 12 - port.getCashFlow().size(); i++) {
                    CashFlow tmpCf = new CashFlow();
                    tmpCf.setInstalmentAmount(0d);
                    tmpCf.setInterestPaid(0d);
                    tmpCf.setPrincipalPaid(0d);
                    Calendar c = GregorianCalendar.getInstance();
                    c.add(Calendar.MONTH, -11 + i);
                    tmpCf.setMonth(c.getTime());
                    dummyList.add(tmpCf);
                }
                dummyList.addAll(port.getCashFlow());
                port.setCashFlow(dummyList);

                EventBus.getDefault().post(new GetPortfolio.Response(port));
            } else {
                // nic, mozna nekdy nejakou hlasku?
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get portfolio.", e);
        }
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

    @AddTrace(name = "ZonkyClient.getMyInvestments")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void getMyInvestments(GetMyInvestments.Request evt) {
        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        AuthToken _authToken = ZonkySniperApplication.getInstance().getAuthToken();
        if (_authToken == null || _authToken.getExpires_in() < System.currentTimeMillis()) {
            ZonkySniperApplication.getInstance().loginSynchronous();
            return;
        }

        String[] statusesArray = evt.getFilter().getStatuses().toArray(new String[evt.getFilter().getStatuses().size()]);

        Boolean unpaidInstallment = evt.getFilter().getUnpaidLastInstallment();
        String unpaidLastInstallment = ( unpaidInstallment != null && unpaidInstallment ?
                String.valueOf(unpaidInstallment) : null);

        Call<List<Investment>> call = zonkyService.getMyInvestments(
                "Bearer " + ZonkySniperApplication.getInstance().getAuthToken().getAccess_token(),
                evt.getNumberOfRows(), evt.getPageNumber(),
                Arrays.toString(statusesArray), unpaidLastInstallment, null, evt.getFilter().getStausEq(), null);

        try {
            Response<List<Investment>> response = call.execute();
            if (response.isSuccessful()) {

                List<Investment> investments = response.body();
                // setridit od nejnovejsiho po nejstarsi
//                Collections.sort(investments, new Comparator<Investment>() {
//                    @Override
//                    public int compare(Investment one, Investment two) {
//                        return one.getLoanId() > two.getLoanId() ? -1 : 1;
//                    }
//                });
                Collections.sort(investments, new Comparator<Investment>() {
                    @Override
                    public int compare(Investment one, Investment two) {
                        if(one.getInvestmentDate() == null || two.getInvestmentDate() == null) {
                            return 1;
                        }
                        return one.getInvestmentDate().after(two.getInvestmentDate()) ? -1 : 1;
                    }
                });

                EventBus.getDefault().post(new GetMyInvestments.Response(investments));
            } else {
//                EventBus.getDefault().post(new GetQuestions.Failure("Nepodařilo se načíst dotazy"));
            }

        } catch (IOException e) {
            Log.e(TAG, "GetQuestions: " + e.getMessage(), e);
//            EventBus.getDefault().post(new GetQuestions.Failure("Nepodařilo se načíst dotazy"));
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
                e.printStackTrace();
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
