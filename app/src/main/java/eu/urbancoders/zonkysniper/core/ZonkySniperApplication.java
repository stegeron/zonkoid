package eu.urbancoders.zonkysniper.core;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import eu.urbancoders.zonkysniper.BuildConfig;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.dataobjects.AuthToken;
import eu.urbancoders.zonkysniper.dataobjects.Investor;
import eu.urbancoders.zonkysniper.dataobjects.Rating;
import eu.urbancoders.zonkysniper.dataobjects.RepaymentEnum;
import eu.urbancoders.zonkysniper.dataobjects.Wallet;
import eu.urbancoders.zonkysniper.events.BetatesterCheck;
import eu.urbancoders.zonkysniper.events.TopicSubscription;
import eu.urbancoders.zonkysniper.events.UserLogin;
import eu.urbancoders.zonkysniper.integration.UrbancachingClient;
import eu.urbancoders.zonkysniper.integration.ZonkyClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;


/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 14.05.2016
 */
public class ZonkySniperApplication extends Application {

    private static final String TAG = ZonkySniperApplication.class.getName();
    private static ZonkySniperApplication instance;
    public static EventBus eventBus;
    public static ZonkyClient zonkyClient;
    public static UrbancachingClient ucClient;
    private FirebaseRemoteConfig remoteConfig;

    private static AuthToken _authToken = null;
    public static boolean authFailed = false;
    public static Wallet wallet;
    public static Investor user;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        // vytvorime eventbus
        eventBus = EventBus.getDefault();
        // zaregistrujeme tuto tridu do eventbusu
        eventBus.register(this);

        zonkyClient = new ZonkyClient();
        ucClient = new UrbancachingClient();

        // automatický login při startu
//        if (_authToken == null) {
//            login();
//        }

        // checkni a zkus zaregistrovat topicy
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        // zjisti, jestli je uzivatel betatester
        if(!sp.getString("username", getString(R.string.pref_default_username)).equalsIgnoreCase(getString(R.string.pref_default_username))) {
            EventBus.getDefault().post(new BetatesterCheck.Request(sp.getString("username", null)));
        }

        // TOxDO tohle je pro testovani notifek
//        EventBus.getDefault().post(new TopicSubscription.Request("ZonkyTestTopic", true));

        // informacni notifikace pro vsechny bez moznosti opt outu (zatim)
        EventBus.getDefault().post(new TopicSubscription.Request("ZonkyInfoTopic", true));

        for (Rating rating : Rating.values()) {
            String topicNamePattern = "Zonky{0}{1}Topic";
            for (RepaymentEnum repayment : RepaymentEnum.values()) {
                String topicName = MessageFormat.format(topicNamePattern, rating.name(), repayment.monthsTo);
                EventBus.getDefault().post(new TopicSubscription.Request(topicName, sp.getBoolean(topicName, true)));
            }
        }

        // remote config
        remoteConfig = FirebaseRemoteConfig.getInstance();

        /**
         * pokud chci overit vyvoj, pak odkomentovat a pri fetch zadavat 1 jako parametr: remoteConfig.fetch();
         */
//        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
//                .setDeveloperModeEnabled(BuildConfig.DEBUG)
//                .build();
//        remoteConfig.setConfigSettings(configSettings);

        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put(Constants.FORCED_VERSION_CODE, BuildConfig.VERSION_CODE);
        remoteConfig.setDefaults(defaultConfigMap);
        remoteConfig.fetch();
        remoteConfig.activateFetched();
    }

    public void loginSynchronous() {

        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            return;
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String password = SecurityManager.getInstance(this.getApplicationContext()).decryptString(sp.getString("password", ""));
        EventBus.getDefault().post(new UserLogin.Request(sp.getString("username", ""), password));
    }

    @Subscribe
    public void subscribeToZonkyMainTopic(TopicSubscription.Request evt) {
        if(evt.shouldSubscribe()) {
            FirebaseMessaging.getInstance().subscribeToTopic(evt.getTopicName());
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(evt.getTopicName());
        }
    }

    public AuthToken getAuthToken() {
        return _authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        if (authToken != null) {
            authFailed = false;
        }
        _authToken = authToken;
    }

    /**
     * overeni, jestli ma uzivatel zaskrtnuty checkbox - povazuje se za betatestera :)
     * @return
     */
    public boolean isLoginAllowed() {
        return PreferenceManager.getDefaultSharedPreferences(
                ZonkySniperApplication.getInstance().getApplicationContext()).getBoolean("isBetatester", false);
    }

    public String getUsername() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return sp.getString("username", "");
    }

    public Investor getUser() {
        return user;
    }

    public void setUser(Investor user) {
        ZonkySniperApplication.user = user;
    }

    public static ZonkySniperApplication getInstance() {
        return instance;
    }

    public boolean showCovered() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return sp.getBoolean(Constants.SHARED_PREF_SHOW_COVERED, false);
    }

    public FirebaseRemoteConfig getRemoteConfig() {
        remoteConfig.fetch();
        remoteConfig.activateFetched();
        return remoteConfig;
    }
}
