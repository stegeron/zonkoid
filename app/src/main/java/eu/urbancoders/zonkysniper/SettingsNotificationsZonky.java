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
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import eu.urbancoders.zonkysniper.core.AppCompatPreferenceActivity;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.events.TopicSubscription;
import org.greenrobot.eventbus.EventBus;

/**
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
public class SettingsNotificationsZonky extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new ZonkoidPreferenceFragment()).commit();
        setupActionBar();
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
//            if (!super.onMenuItemSelected(featureId, item)) {
//                NavUtils.navigateUpFromSameTask(this);
//            }
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
            } else if(preference instanceof AmountToInvestPreference) {
                if(((AmountToInvestPreference) preference).getValue() < Constants.AMOUNT_TO_INVEST_MIN) {
                    preference.setSummary(R.string.noPresetAmountSet);
                } else {
                    preference.setSummary(String.format(preference.getContext().getString(R.string.presetAmountSet),
                            ((AmountToInvestPreference) preference).getValue()));
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

        if(preference.getKey().equalsIgnoreCase(Constants.SHARED_PREF_PRESET_AMOUNT)) {
            preferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getInt(preference.getKey(), Constants.AMOUNT_TO_INVEST_MIN));
        } else {
            preferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
        }
    }

    public static class ZonkoidPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences_notifications_zonky);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                RingtonePreference ringPref = (RingtonePreference) findPreference(Constants.SHARED_PREF_ZONKOID_NOTIF_SOUND);
                ringPref.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_notification_sound, null));

                SwitchPreference vibeSwitchPref = (SwitchPreference) findPreference(Constants.SHARED_PREF_ZONKOID_NOTIF_VIBRATE);
                vibeSwitchPref.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_notification_vibrate, null));

                SwitchPreference muteNotif = (SwitchPreference) findPreference(Constants.SHARED_PREF_MUTE_NOTIFICATIONS);
                muteNotif.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_notifications_mute, null));

                AmountToInvestPreference amountToInvestPreference = (AmountToInvestPreference) findPreference(Constants.SHARED_PREF_PRESET_AMOUNT);
                amountToInvestPreference.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_invest, null));

                SwitchPreference insuredOnly = (SwitchPreference) findPreference(Constants.SHARED_PREF_NOTIF_INSURED_ONLY);
                insuredOnly.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_insured, null));

                SwitchPreference reserved = (SwitchPreference) findPreference(Constants.SHARED_PREF_NOTIF_UNRESERVED_ONLY);
                reserved.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_reserved, null));
            }

            bindPreferenceSummaryToValue(findPreference("zonkoid_notif_sound"));
            bindPreferenceSummaryToValue(findPreference(Constants.SHARED_PREF_PRESET_AMOUNT));
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = super.onCreateView(inflater, container, savedInstanceState);

            return v;
        }


    }

    /**
     * Ulozi nastaveni notifikaci do preferenci a vyvola event pro subscribe / unsubscribe z topicu
     *
     * @param view
     */
    public void saveNotifCheckboxValue(View view) {

        CheckBox checkBox = (CheckBox) view;
        String checkBoxName = getResources().getResourceEntryName(checkBox.getId());
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ZonkySniperApplication.getInstance().getApplicationContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(checkBoxName, checkBox.isChecked());

        // subscribe / unsubscribe
        EventBus.getDefault().post(new TopicSubscription.Request(checkBoxName, checkBox.isChecked()));

        // pokud odskrtavam notifku, musim odskrtnout taky prislusny autoinvest
        if(!checkBox.isChecked()) {
            editor.putBoolean(checkBoxName.replace("Topic", "Autoinvest"), false);
        }

        editor.commit();
    }
}