package eu.urbancoders.zonkysniper;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.firebase.messaging.FirebaseMessaging;
import eu.urbancoders.zonkysniper.dataobjects.AuthToken;
import eu.urbancoders.zonkysniper.dataobjects.Wallet;
import eu.urbancoders.zonkysniper.events.RefreshToken;
import eu.urbancoders.zonkysniper.events.TopicSubscription;
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
    public static String fcmMainTopic = "ZonkyMainTopic";

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
        if (_authToken == null) {
            login();
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        if(sp.getBoolean("push_notif_switch", true)) {
            EventBus.getDefault().post(new TopicSubscription.Request(true));
        }
    }

    public void login() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String password = SecurityManager.getInstance(this.getApplicationContext()).decryptString(sp.getString("password", ""));
        EventBus.getDefault().post(new UserLogin.Request(sp.getString("username", ""), password));
    }

    @Subscribe
    public void subscribeToZonkyMainTopic(TopicSubscription.Request evt) {
        if(evt.shouldSubscribe()) {
            FirebaseMessaging.getInstance().subscribeToTopic(fcmMainTopic);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(fcmMainTopic);
        }
    }

    public AuthToken getAuthToken() {
//        if (_authToken == null) {
//            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
//            String password = SecurityManager.getInstance(this.getApplicationContext()).decryptString(sp.getString("password", ""));
//            EventBus.getDefault().post(new UserLogin.Request(sp.getString("username", ""), password));
//        if(_authToken.getExpires_in() < System.currentTimeMillis()) {
//            EventBus.getDefault().post(new RefreshToken.Request(_authToken));
//        }
        return _authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        if (authToken != null) {
            authFailed = false;
        }
        _authToken = authToken;
    }

    @Subscribe
    public void dummyEventHandler(Void v) {
                                                                                           //        if (_authToken == null) {
//            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
//            String password = SecurityManager.getInstance(this.getApplicationContext()).decryptString(sp.getString("password", ""));
//            EventBus.getDefault().post(new UserLogin.Request(sp.getString("username", ""), password));
//        if(_authToken.getExpires_in() < System.currentTimeMillis()) {
//            EventBus.getDefault().post(new RefreshToken.Request(_authToken));
//        }
    }

    /**
     * Pokud jednou selze, nastavim, dalsi pokusy o automaticke prihlaseni nebudou - hlaska uzivateli.
     */
//    public void setAuthFailed() {
//        authFailed = true;
//        setAuthToken(null);
//    }

    public static ZonkySniperApplication getInstance() {
        return instance;
    }
}
