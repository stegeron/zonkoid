package eu.urbancoders.zonkysniper;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.iid.FirebaseInstanceId;
import eu.urbancoders.zonkysniper.core.AppCompatPreferenceActivity;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.events.FcmTokenRegistration;
import eu.urbancoders.zonkysniper.events.RegisterThirdpartyNotif;
import eu.urbancoders.zonkysniper.events.UnregisterThirdpartyNotif;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Nastavení oznámení z aplikace Robozonky
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 16.06.2016
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
public class SettingsNotificationsRobozonky extends AppCompatPreferenceActivity {

    private RobozonkyPreferenceFragment robozonkyPreferenceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        robozonkyPreferenceFragment = new RobozonkyPreferenceFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, robozonkyPreferenceFragment).commit();
        setupActionBar();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(getString(R.string.colorTyrkys))));
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private static Preference.OnPreferenceChangeListener preferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary("");
                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));
                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }
            } else if("notif_robozonky_allowed".equalsIgnoreCase(preference.getKey())) {

                // zobrazit jeste robozonky user kod
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ZonkySniperApplication.getInstance().getApplicationContext());
                Integer userCode = sp.getInt(Constants.NOTIF_ROBOZONKY_USERCODE, -1);

                if(stringValue.equalsIgnoreCase("true") && userCode == -1) {
                    if(ZonkySniperApplication.getInstance().isLoginAllowed()) {
                        EventBus.getDefault().post(new RegisterThirdpartyNotif.Request(
                                ZonkySniperApplication.getInstance().getUsername(),
                                Constants.ClientApps.ROBOZONKY));
                        // pro jistotu jeste updatuj FCM token
                        String token = FirebaseInstanceId.getInstance().getToken();
                        EventBus.getDefault().post(new FcmTokenRegistration.Request(
                                ZonkySniperApplication.getInstance().getUsername(),
                                token));
                    }
                } else if(stringValue.equalsIgnoreCase("false") && userCode > -1 ) {
                    EventBus.getDefault().post(new UnregisterThirdpartyNotif.Request(
                            ZonkySniperApplication.getInstance().getUsername(),
                            Constants.ClientApps.ROBOZONKY));
                    sp.edit().putInt(Constants.NOTIF_ROBOZONKY_USERCODE, -1).apply();
                    preference.setSummary("Kód: ");
                } else if(userCode == -1) {
                    preference.setSummary("Kód: ");
                } else {
                    preference.setSummary("Kód: "+userCode);
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(preferenceSummaryToValueListener);

        if(preference.getKey().equalsIgnoreCase("notif_robozonky_allowed")) {
            preferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getBoolean(preference.getKey(), false));
        } else {
            preferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRobozonkyCodeReceived(RegisterThirdpartyNotif.Response evt) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        sp.edit().putInt(Constants.NOTIF_ROBOZONKY_USERCODE, evt.getUserCode()).commit();
        robozonkyPreferenceFragment.notifRobozonkyUserCode.setSummary("Kód: "+ evt.getUserCode());
    }

    public static class RobozonkyPreferenceFragment extends PreferenceFragment {

        public Preference notifRobozonkyUserCode;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences_notifications_robozonky);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                RingtonePreference ringPref = (RingtonePreference) findPreference("robozonky_notif_sound");
                ringPref.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_notification_sound, null));

                SwitchPreference vibeSwitchPref = (SwitchPreference) findPreference(Constants.SHARED_PREF_ROBOZONKY_NOTIF_VIBRATE);
                vibeSwitchPref.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_notification_vibrate, null));
            }

            bindPreferenceSummaryToValue(findPreference("robozonky_notif_sound"));

            notifRobozonkyUserCode = findPreference("notif_robozonky_allowed");
            bindPreferenceSummaryToValue(notifRobozonkyUserCode);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }
}