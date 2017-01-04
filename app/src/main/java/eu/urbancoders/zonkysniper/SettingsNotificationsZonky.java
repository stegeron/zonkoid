package eu.urbancoders.zonkysniper;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
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
import android.widget.CheckBox;
import eu.urbancoders.zonkysniper.core.AppCompatPreferenceActivity;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.events.TopicSubscription;
import org.greenrobot.eventbus.EventBus;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 16.06.2016
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
        // Trigger the listener immediately with the preference's
        // current value.
        preferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }

    public static class ZonkoidPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences_notifications_zonky);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                RingtonePreference ringPref = (RingtonePreference) findPreference("zonkoid_notif_sound");
                ringPref.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_notification_sound, null));

                SwitchPreference muteNotif = (SwitchPreference) findPreference(Constants.SHARED_PREF_MUTE_NOTIFICATIONS);
                muteNotif.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_notifications_mute, null));
            }

            bindPreferenceSummaryToValue(findPreference("zonkoid_notif_sound"));
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
        editor.commit();
        // subscribe / unsubscribe
        EventBus.getDefault().post(new TopicSubscription.Request(checkBoxName, checkBox.isChecked()));
    }
}