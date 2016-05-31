package eu.urbancoders.zonkysniper;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.firebase.messaging.FirebaseMessaging;
import eu.urbancoders.zonkysniper.dataobjects.AuthToken;
import eu.urbancoders.zonkysniper.dataobjects.Wallet;
import eu.urbancoders.zonkysniper.events.UserLogin;
import eu.urbancoders.zonkysniper.integration.ZonkyClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 14.05.2016
 */
public class ZonkySniperApplication extends Application {

    private static final String TAG = ZonkySniperApplication.class.getName();
    private static ZonkySniperApplication instance;
    public static EventBus eventBus;
    public static ZonkyClient zonkyClient;

    private static AuthToken _authToken = null;
    public static boolean authFailed = false;
    public static Wallet wallet;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        // vytvorime eventbus
        eventBus = EventBus.getDefault();
        // zaregistrujeme tuto tridu do eventbusu
        eventBus.register(this);

        zonkyClient = new ZonkyClient();

        // automatický login při startu
        getAuthToken();

        // TODO: implementovat unsubscribe
        FirebaseMessaging.getInstance().subscribeToTopic("ZonkyMainTopic");
    }

    public AuthToken getAuthToken() {
        if (_authToken == null) {
            // TODO nacist username a password z persistence
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
            EventBus.getDefault().post(new UserLogin.Request(sp.getString("username", "-"), sp.getString("password", "-")));

        }
        return _authToken;
    }

    public static void setAuthToken(AuthToken authToken) {
        if (authToken != null) {
            authFailed = false;
        }
        _authToken = authToken;
    }

    @Subscribe
    public void dummyEventHandler(Void v) {

    }

    /**
     * Pokud jednou selze, nastavim, dalsi pokusy o automaticke prihlaseni nebudou - hlaska uzivateli.
     */
    public static void setAuthFailed() {
        authFailed = true;
        setAuthToken(null);
    }

    public static ZonkySniperApplication getInstance() {
        return instance;
    }
}
