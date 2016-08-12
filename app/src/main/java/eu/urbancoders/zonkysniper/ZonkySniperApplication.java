package eu.urbancoders.zonkysniper;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.firebase.messaging.FirebaseMessaging;
import eu.urbancoders.zonkysniper.dataobjects.AuthToken;
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
        ucClient = new UrbancachingClient();

        // automatický login při startu
        if (_authToken == null) {
            login();
        }

        // checkni a zkus zaregistrovat topicy
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        // zjisti, jestli je uzivatel betatester
        if(!sp.getString("username", getString(R.string.pref_default_username)).equalsIgnoreCase(getString(R.string.pref_default_username))) {
            EventBus.getDefault().post(new BetatesterCheck.Request(sp.getString("username", null)));
        }

        // TODO odstranit, v dalsich verzich - to uz by meli byt vsichni na novych topicich
        EventBus.getDefault().post(new TopicSubscription.Request("ZonkyMainTopic", false));

        for (Rating rating : Rating.values()) {
            String topicNamePattern = "Zonky{0}{1}Topic";
            for (RepaymentEnum repayment : RepaymentEnum.values()) {
                String topicName = MessageFormat.format(topicNamePattern, rating.name(), repayment.monthsTo);
                EventBus.getDefault().post(new TopicSubscription.Request(topicName, sp.getBoolean(topicName, true)));
            }
        }
    }

    public void login() {

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

    public static ZonkySniperApplication getInstance() {
        return instance;
    }
}
