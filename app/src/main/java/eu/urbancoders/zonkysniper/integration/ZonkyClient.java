package eu.urbancoders.zonkysniper.integration;

import eu.urbancoders.zonkysniper.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.AuthToken;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.events.UserLogin;
import eu.urbancoders.zonkysniper.events.ReloadMarket;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 14.05.2016
 */
public class ZonkyClient {
    private static final String TAG = ZonkyClient.class.getName();
    private static final String BASE_URL = "https://api.zonky.cz/";

    private ZonkyService zonkyService;

    public ZonkyClient() {

        EventBus.getDefault().register(this);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        zonkyService = retrofit.create(ZonkyService.class);
    }

    @Subscribe
    public void loginUser(UserLogin.Request evt) {

        Call<AuthToken> call = zonkyService.getAuthToken(evt.userName, evt.password, "password", "SCOPE_APP_WEB");

        call.enqueue(new Callback<AuthToken>() {
            @Override
            public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                ZonkySniperApplication.setAuthToken(response.body());
                EventBus.getDefault().post(new UserLogin.Response(response.body()));
            }

            @Override
            public void onFailure(Call<AuthToken> call, Throwable t) {

            }
        });
    }

    @Subscribe
    public void reloadMarket(ReloadMarket.Request evt) {
        Call<List<Loan>> call = zonkyService.getNewLoansOnMarket("Bearer " + ZonkySniperApplication.getAuthToken().getAccess_token());

        call.enqueue(new Callback<List<Loan>>() {
            @Override
            public void onResponse(Call<List<Loan>> call, Response<List<Loan>> response) {
                EventBus.getDefault().post(new ReloadMarket.Response(response.body()));
            }

            @Override
            public void onFailure(Call<List<Loan>> call, Throwable t) {

            }
        });
    }
}
