package eu.urbancoders.zonkysniper.integration;

import android.util.Log;
import com.google.gson.Gson;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Investment;
import eu.urbancoders.zonkysniper.dataobjects.Investor;
import eu.urbancoders.zonkysniper.events.LoginCheck;
import eu.urbancoders.zonkysniper.events.Bugreport;
import eu.urbancoders.zonkysniper.events.FcmTokenRegistration;
import eu.urbancoders.zonkysniper.events.GetInvestmentsByZonkoid;
import eu.urbancoders.zonkysniper.events.LogInvestment;
import eu.urbancoders.zonkysniper.events.RegisterThirdpartyNotif;
import eu.urbancoders.zonkysniper.events.UnregisterThirdpartyNotif;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 04.07.2016
 */
public class UrbancodersClient {

    private static final String TAG = UrbancodersClient.class.getName();
    private static final String BASE_URL = "https://urbancoders.eu/";
//    private static final String BASE_URL = "http://10.0.2.2:8080/";  // TOxDO remove fejk URL

    private static Retrofit retrofit;
    private UrbancodersService ucService;

    public UrbancodersClient() {

        EventBus.getDefault().register(this);

        ZonkoidLoggingInterceptor interceptor = new ZonkoidLoggingInterceptor();
        interceptor.setLevel(ZonkoidLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(client)
                .build();

        ucService = retrofit.create(UrbancodersService.class);
    }

    @Subscribe
    public void loginCheck(LoginCheck.Request evt) {
        if(ZonkySniperApplication.getInstance().getUser() == null) {
            Log.e(TAG, "Uzivatel neni prihlaseny, nelze zavolat checkpoint");
            return;
        }

        Call<Investor> call = ucService.loginCheck(evt.getInvestor());

        call.enqueue(new Callback<Investor>() {
            @Override
            public void onResponse(Call<Investor> call, Response<Investor> response) {
                if(response.isSuccessful() && response.body() != null) {
                    ZonkySniperApplication.getInstance().setZonkyCommanderStatus(response.body().getZonkyCommanderStatus());
                } else {
                    // nechci klienta mucit, povolim mu vsechno :)
                    Log.e(TAG, "Nepodarilo se ziskat stav investora na checkpointu.");
                    ZonkySniperApplication.getInstance().setZonkyCommanderStatus(Investor.Status.ACTIVE);
                }
            }

            @Override
            public void onFailure(Call<Investor> call, Throwable t) {
                // nechci klienta mucit, povolim mu vsechno :)
                Log.e(TAG, "Nepodarilo se ziskat stav investora na checkpointu.");
                ZonkySniperApplication.getInstance().setZonkyCommanderStatus(Investor.Status.ACTIVE);
            }
        });
    }

    @Subscribe
    public void sendBugreport(Bugreport.Request evt) {
        Call<String> call = ucService.sendBugreport(
                evt.getUsername(),
                evt.getDescription(),
                evt.getLogs(),
                evt.getTimestamp(),
                Constants.ClientApps.ZONKOID
        );

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
     * @param evt udalost
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void logInvestment(LogInvestment.Request evt) {
        Call<ResponseBody> call = ucService.logInvestment(
                evt.getUsername(),
                Constants.ClientApps.ZONKOID,
                evt.getMyInvestment()
        );

        try {
            Response<ResponseBody> response = call.execute();
            if (response != null && response.isSuccessful()) {
                ZonkySniperApplication.getInstance().setZonkyCommanderStatus(Investor.Status.valueOf(response.body().string()));
            } else {
                ZonkySniperApplication.getInstance().setZonkyCommanderStatus(Investor.Status.ACTIVE);
            }
        } catch (IOException e) {
            Log.w(TAG, "Failed to log investment to zonkycommander. "+e.getMessage());
        }
    }

    /**
     * Ziskat seznam investic pres Zonkoida do dane pujcky
     * @param evt
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void getInvestmentsByZonkoid(GetInvestmentsByZonkoid.Request evt) {
        Call<List<Investment>> call = ucService.getInvestmentsByZonkoid(
                evt.getLoanId()
        );

        try {
            Response<List<Investment>> response = call.execute();
            if(response != null && response.isSuccessful()) {
                EventBus.getDefault().post(new GetInvestmentsByZonkoid.Response(response.body()));
            }
        } catch (IOException e) {
            Log.w(TAG, "Failed to get investments by Zonkoid. "+e.getMessage());
        }

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void registerUserAndThirdParty(RegisterThirdpartyNotif.Request evt) {
        Call<String> call = ucService.registerUserAndThirdParty(evt.getUsername(), evt.getClientApp().name());

        try {
            Response<String> response = call.execute();
            if (response != null && response.isSuccessful()) {
                EventBus.getDefault().post(new RegisterThirdpartyNotif.Response(Integer.parseInt(response.body())));
            } else {
                EventBus.getDefault().post(new RegisterThirdpartyNotif.Failure());
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to registerUserAndThirdParty and get code", e);
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void unregisterUserAndThirdParty(UnregisterThirdpartyNotif.Request evt) {
        Call<String> call = ucService.unregisterUserAndThirdParty(evt.getUsername(), evt.getClientApp().name());

        try {
            Response<String> response = call.execute();
            if (response != null && response.isSuccessful()) {
                EventBus.getDefault().post(new UnregisterThirdpartyNotif.Response());
            } else {
                EventBus.getDefault().post(new RegisterThirdpartyNotif.Failure());
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to unregisterUserAndThirdParty and get code", e);
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void registerUserToFcm(FcmTokenRegistration.Request evt) {
        Call<Void> call = ucService.registerUserToFcm(evt.getUsername(), evt.getToken());

        try {
            Response<Void> response = call.execute();
            if (response != null && response.isSuccessful()) {
                EventBus.getDefault().post(new FcmTokenRegistration.Response());
            } else {
                EventBus.getDefault().post(new FcmTokenRegistration.Failure());
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to registerUserToFcm.", e);
        }
    }

}
