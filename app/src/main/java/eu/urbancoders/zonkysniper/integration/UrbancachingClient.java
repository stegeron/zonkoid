package eu.urbancoders.zonkysniper.integration;

import android.util.Log;
import eu.urbancoders.zonkysniper.events.BetatesterCheck;
import eu.urbancoders.zonkysniper.events.BetatesterRegister;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 04.07.2016
 */
public class UrbancachingClient {

    private static final String TAG = UrbancachingClient.class.getName();
    private static final String BASE_URL = "http://urbancaching.eu/";

    private static Retrofit retrofit;
    private UrbancachingService ucService;

    public UrbancachingClient() {

        EventBus.getDefault().register(this);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ucService = retrofit.create(UrbancachingService.class);
    }

    @Subscribe
    public void isBetatester(BetatesterCheck.Request evt) {

        if(evt.getUsername() != null) {

            Call<String> call = ucService.isBetatester(evt.getUsername());

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    boolean isBetatester = Boolean.getBoolean(response.body());
                    EventBus.getDefault().post(new BetatesterCheck.Response(isBetatester));
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    // nedelej nic, proste neni to betatester
                }
            });
        }
    }

    @Subscribe
    public void requestBetaRegistration(final BetatesterRegister.Request evt) {
        if (evt.getUsername() != null && !evt.getUsername().equalsIgnoreCase("nekdo@zonky.cz")) {
            Call<String> call = ucService.requestBetaRegistration(evt.getUsername());

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.i(TAG, "Zadost o beta registraci odeslana na jmeno "+evt.getUsername());
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(TAG, "Selhalo odeslani zadosti o beta registraci na jmeno " + evt.getUsername());
                }
            });
        }
    }
}
