package eu.urbancoders.zonkysniper.core;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
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
import org.solovyev.android.checkout.Checkout;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Set;

import androidx.multidex.MultiDexApplication;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 14.05.2016
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation version 3.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
public class ZonkySniperApplication extends MultiDexApplication {

    private static final String TAG = ZonkySniperApplication.class.getName();
    private static ZonkySniperApplication instance;
    public static EventBus eventBus;
    public static ZonkyClient zonkyClient;
    public static UrbancodersClient ucClient;
    private static SharedPreferences sharedPrefs;
    private FirebaseAnalytics analytics;
    private AdRequest adRequest;

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
    private Checkout mCheckout;

    public Billing getBilling() {
        return mBilling;
    }

    public Checkout getCheckout() {
        if(mCheckout == null) {
            mCheckout = Checkout.forApplication(getBilling());
            mCheckout.start();
        }
        return mCheckout;
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

        MobileAds.initialize(this, Constants.KEY_ADMOB);

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
            Log.i(TAG, "Subscribing to topic " + evt.getTopicName());
            FirebaseMessaging.getInstance().subscribeToTopic(evt.getTopicName());
        } else {
            Log.i(TAG, "Unsubscribing from topic " + evt.getTopicName());
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

    public String getPassword() {
        return SecurityManager.getInstance(this.getApplicationContext()).decryptString(sharedPrefs.getString("password", ""));
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
        if(ZonkySniperApplication.getInstance().getUser() != null) {
            ZonkySniperApplication.getInstance().getUser().setZonkyCommanderStatus(status);
            sharedPrefs.edit().putString(Constants.SHARED_PREF_INVESTOR_STATUS, status.name()).commit();
        }
    }

    /**
     * Prelozit hlasku ze Zonky na neco kloudneho pro zobrazeni
     * @param errorDesc
     * @return
     */
    public String translateError(String errorDesc) {
        if("multipleInvestment".equalsIgnoreCase(errorDesc)) {
            return getString(R.string.multipleInvestment);
        } else if("alreadyCovered".equalsIgnoreCase(errorDesc)) {
            return getString(R.string.alreadyCovered);
        } else if("tooLowIncrease".equalsIgnoreCase(errorDesc)) {
            return getString(R.string.tooLowIncreaseInvestment);
        } else if("insufficientBalance".equalsIgnoreCase(errorDesc)) {
            return getString(R.string.not_enough_cash);
        } else if("invalidStatus".equalsIgnoreCase(errorDesc)) {
            return getString(R.string.invalidStatusOfLoan);
        } else if("reservedInvestmentOnly".equalsIgnoreCase(errorDesc)) {
            return getString(R.string.reservedOnly);
        } else if("unauthorized".equalsIgnoreCase(errorDesc)) {
            if(ZonkySniperApplication.getInstance().getUser() != null
                    && !ZonkySniperApplication.getInstance().getUser().getRoles().contains("ROLE_INVESTOR")) {
                return getString(R.string.unauthorized_new_investor);
            } else {
                return getString(R.string.unauthorized);
            }
        } else if("UNEXPECTED_ERROR".equalsIgnoreCase(errorDesc)) {
            return "Nastala neznámá chyba na serveru Zonky.cz";
        }

        return getString(R.string.error); // TODO pouzit Crashlytics na tiche reportovani nezname chyby
    }

    public AdRequest getAdRequest() {
        if(adRequest == null) {
            AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
            adRequest = adRequestBuilder.build();
        }
        return adRequest;
    }

    public static SharedPreferences getSharedPrefs() {
        return sharedPrefs;
    }

    public String logPreferences() {
        Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getAll();
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : prefs.keySet()) {
            Object pref = prefs.get(key);
            String printVal = "";
            if (pref instanceof Boolean) {
                printVal = key + " : " + pref;
            }
            if (pref instanceof Float) {
                printVal = key + " : " + pref;
            }
            if (pref instanceof Integer) {
                printVal = key + " : " + pref;
            }
            if (pref instanceof Long) {
                printVal = key + " : " + pref;
            }
            if (pref instanceof String) {
                printVal = key + " : " + pref;
            }
            if (pref instanceof Set<?>) {
                printVal = key + " : " + pref;
            }

            stringBuilder.append(printVal).append("|");
        }
        return stringBuilder.toString();
    }
}
