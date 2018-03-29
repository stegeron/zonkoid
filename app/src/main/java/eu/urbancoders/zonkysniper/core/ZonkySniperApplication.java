package eu.urbancoders.zonkysniper.core;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.dataobjects.AuthToken;
import eu.urbancoders.zonkysniper.dataobjects.Investor;
import eu.urbancoders.zonkysniper.dataobjects.Rating;
import eu.urbancoders.zonkysniper.dataobjects.RepaymentEnum;
import eu.urbancoders.zonkysniper.dataobjects.Wallet;
import eu.urbancoders.zonkysniper.events.GetInvestor;
import eu.urbancoders.zonkysniper.events.GetInvestorRestrictions;
import eu.urbancoders.zonkysniper.events.LoginCheck;
import eu.urbancoders.zonkysniper.events.TopicSubscription;
import eu.urbancoders.zonkysniper.events.UserLogin;
import eu.urbancoders.zonkysniper.integration.UrbancodersClient;
import eu.urbancoders.zonkysniper.integration.ZonkyClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.solovyev.android.checkout.Billing;

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
    public static UrbancodersClient ucClient;
    private static SharedPreferences sharedPrefs;
    private FirebaseAnalytics analytics;

    private static AuthToken _authToken = null;
    private Integer currentLoanId = null;
    public static boolean authFailed = false;
    public static boolean isMarketDirty = false; // TRUE znamena, ze je potreba prenacist trziste
    public static Wallet wallet;
    public static Investor user;

    /**
     * Billing
     */
    private final Billing mBilling = new Billing(this, new Billing.DefaultConfiguration() {

        public String getPublicKey() {
            return Constants.LICENCE_KEY;

        }
    });


    public Billing getBilling() {
        return mBilling;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        // shared prefs init
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        // vytvorime eventbus
        eventBus = EventBus.getDefault();
        // zaregistrujeme tuto tridu do eventbusu
        eventBus.register(this);

        zonkyClient = new ZonkyClient();
        ucClient = new UrbancodersClient();

        // zkus se prihlasit a nacist info o uzivateli
        if(!sharedPrefs.getString("username", getString(R.string.pref_default_username)).equalsIgnoreCase(getString(R.string.pref_default_username))) {
            EventBus.getDefault().post(new GetInvestor.Request());
        }


        // TOxDO tohle je pro testovani notifek
//        EventBus.getDefault().post(new TopicSubscription.Request("ZonkyTestTopic", true));

        // informacni notifikace pro vsechny bez moznosti opt outu (zatim)
//        EventBus.getDefault().post(new TopicSubscription.Request("ZonkyInfoTopic", true));

        analytics = FirebaseAnalytics.getInstance(this);

        for (Rating rating : Rating.values()) {
            String topicNamePattern = "Zonky{0}{1}Topic";
            for (RepaymentEnum repayment : RepaymentEnum.values()) {
                String topicName = MessageFormat.format(topicNamePattern, rating.name(), repayment.monthsTo);
                EventBus.getDefault().post(new TopicSubscription.Request(topicName, sharedPrefs.getBoolean(topicName, true)));
            }
        }
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

    @Subscribe
    public void onLoginCheckedResponse(LoginCheck.Response evt) {
        if(evt != null && evt.getInvestor() != null && user != null) {
            user.setZonkyCommanderStatus(evt.getInvestor().getZonkyCommanderStatus());
            user.setZonkyCommanderBalance(evt.getInvestor().getZonkyCommanderBalance());
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onInvestorRestrictionsReceived(GetInvestorRestrictions.Response evt) {
        ZonkySniperApplication.getInstance().getUser().setMaximumInvestmentAmount(evt.getRestrictions().getMaximumInvestmentAmount());
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
        return sharedPrefs.getBoolean("isBetatester", false);
    }

    public String getUsername() {
        return sharedPrefs.getString("username", "");
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
        return sharedPrefs.getBoolean(Constants.SHARED_PREF_SHOW_COVERED, false);
    }

    public Integer getCurrentLoanId() {
        return currentLoanId;
    }

    public void setCurrentLoanId(Integer currentLoanId) {
        this.currentLoanId = currentLoanId;
    }

    public void setZonkyCommanderStatus(Investor.Status status) {
        //noinspection deprecation
        ZonkySniperApplication.getInstance().getUser().setZonkyCommanderStatus(status);
        sharedPrefs.edit().putString(Constants.SHARED_PREF_INVESTOR_STATUS, status.name()).commit();
    }
}
