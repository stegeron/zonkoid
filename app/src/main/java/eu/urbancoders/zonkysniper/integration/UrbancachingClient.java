package eu.urbancoders.zonkysniper.integration;

import android.util.Log;
import com.google.gson.Gson;
import eu.urbancoders.zonkysniper.dataobjects.MyInvestment;
import eu.urbancoders.zonkysniper.events.BetatesterCheck;
import eu.urbancoders.zonkysniper.events.BetatesterRegister;
import eu.urbancoders.zonkysniper.events.Bugreport;
import eu.urbancoders.zonkysniper.events.LogInvestment;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import retrofit2.Call;
import retrofit2.Callback;
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
import java.security.cert.CertificateException;

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

        ZonkoidLoggingInterceptor interceptor = new ZonkoidLoggingInterceptor();
        interceptor.setLevel(ZonkoidLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
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
    @Deprecated
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

    @Subscribe
    public void sendBugreport(Bugreport.Request evt) {
        Call<String> call = ucService.sendBugreport(
                evt.getUsername(),
                evt.getDescription(),
                evt.getLogs(),
                evt.getTimestamp());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i(TAG, "Ulozeni bugreportu probehlo v poradku");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "Nezdarilo se ulozeni bugreportu. "+t.getMessage());
            }
        });
    }

    /**
     * Zaloguje investici, asynchronne tak, aby neobtezoval thready
     * @param evt
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void logInvestment(LogInvestment.Request evt) {
        Call<Void> call = ucService.logInvestment(
                evt.getUsername(),
                evt.getMyInvestment()
        );

        try {
            call.execute();
        } catch (IOException e) {
            Log.w(TAG, "Failed to log investment to zonkycommander. "+e.getMessage());
        }
    }

}
