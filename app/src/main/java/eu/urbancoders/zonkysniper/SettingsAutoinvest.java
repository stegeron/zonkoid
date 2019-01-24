package eu.urbancoders.zonkysniper;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import org.greenrobot.eventbus.EventBus;

import java.util.HashSet;
import java.util.Set;

import eu.urbancoders.zonkysniper.core.AppCompatPreferenceActivity;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Region;
import eu.urbancoders.zonkysniper.events.TopicSubscription;
import eu.urbancoders.zonkysniper.investing.AutoInvestIncomePreference;

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
public class SettingsAutoinvest extends AppCompatPreferenceActivity {

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
            if(preference instanceof AmountToAutoInvestPreference) {
                if(((AmountToAutoInvestPreference) preference).getValue() < Constants.AMOUNT_TO_INVEST_MIN) {
                    preference.setSummary(R.string.noPresetAutoinvestAmountSet);
                } else {
                    preference.setSummary(String.format(preference.getContext().getString(R.string.presetAutoinvestAmountSet),
                            ((AmountToAutoInvestPreference) preference).getValue()));
                }
            }
            else if (preference.getKey().equalsIgnoreCase(Constants.SHARED_PREF_AUTOINVEST_MAX_AMOUNT)) {
                int maxAmount = 0;

                if(!((String)value).isEmpty()) {
                    maxAmount = Integer.parseInt((String)value);
                }

                if(maxAmount > 0) {
                    preference.setSummary(String.format(preference.getContext().getString(R.string.autoinvest_max_amount_summary_set),
                            maxAmount));
                } else {
                    preference.setSummary(preference.getContext().getString(R.string.autoinvest_max_amount_summary_unset));
                }
            } else if(preference.getKey().startsWith("switch_region")) {
                Set<String> storedRegions = PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                        .getStringSet(Constants.SHARED_PREF_AUTOINVEST_REGIONS, new HashSet<String>());

                if(!((SwitchPreference)preference).isChecked()) {
                    storedRegions.add(preference.getKey().substring(7));
                } else {
                    if(storedRegions.size() > 1) {
                        storedRegions.remove(preference.getKey().substring(7));
                    } else {
                        ((SwitchPreference) preference).setChecked(true);
                        return false;
                    }
                }

                PreferenceManager.getDefaultSharedPreferences(preference.getContext()).edit()
                        .putStringSet(Constants.SHARED_PREF_AUTOINVEST_REGIONS, storedRegions).commit();
            }
            else {
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

        if(preference.getKey().equalsIgnoreCase(Constants.SHARED_PREF_PRESET_AUTOINVEST_AMOUNT)) {
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
            addPreferencesFromResource(R.xml.preferences_autoinvest);

            Preference regionsTextOnly = (Preference) findPreference("autoinvest_regions_textonly");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AmountToAutoInvestPreference amountToAutoInvestPreference = (AmountToAutoInvestPreference) findPreference(Constants.SHARED_PREF_PRESET_AUTOINVEST_AMOUNT);
                amountToAutoInvestPreference.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_covered, null));

                SwitchPreference insuredOnly = (SwitchPreference) findPreference(Constants.SHARED_PREF_AUTOINVEST_INSURED_ONLY);
                insuredOnly.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_insured, null));

                EditTextPreference maxAmount = (EditTextPreference) findPreference(Constants.SHARED_PREF_AUTOINVEST_MAX_AMOUNT);
                maxAmount.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_autoinvest_max_amount, null));

                regionsTextOnly.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_region_select, null));
            }

            bindPreferenceSummaryToValue(findPreference(Constants.SHARED_PREF_PRESET_AUTOINVEST_AMOUNT));
            bindPreferenceSummaryToValue(findPreference(Constants.SHARED_PREF_AUTOINVEST_MAX_AMOUNT));
            for (int i = 1; i < 15; i++) {
                findPreference("switch_region_"+i).setOnPreferenceChangeListener(preferenceSummaryToValueListener);
            }

            // switch on of regions - jsou totiz ulozeny v poli a ne jednotlive v kazdem Switchi
            Set<String> regionsSet = PreferenceManager.getDefaultSharedPreferences(regionsTextOnly.getContext())
                    .getStringSet(Constants.SHARED_PREF_AUTOINVEST_REGIONS, null);
            if(regionsSet == null) {
                regionsSet = new HashSet<>();
                for (Region region : Region.values()) {
                    regionsSet.add(region.name());
                }
                PreferenceManager.getDefaultSharedPreferences(regionsTextOnly.getContext()).edit()
                        .putStringSet(Constants.SHARED_PREF_AUTOINVEST_REGIONS, regionsSet).commit();
            }
            Region[] regionsEnumValues = Region.values();
            for (Region regionsEnumValue : regionsEnumValues) {
                SwitchPreference preference = (SwitchPreference) findPreference("switch_" + regionsEnumValue);
                preference.setChecked(regionsSet.contains(regionsEnumValue.name()));
            }

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = super.onCreateView(inflater, container, savedInstanceState);

            return v;
        }


    }

    /**
     * Ulozi nastaveni notifikaci do preferenci
     *
     * @param view
     */
    public void saveAutoinvestCheckboxValue(View view) {

        CheckBox checkBox = (CheckBox) view;
        String checkBoxName = getResources().getResourceEntryName(checkBox.getId());
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ZonkySniperApplication.getInstance().getApplicationContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(checkBoxName, checkBox.isChecked());

        // pokud zaskrtavam autoinvest, automaticky musim zaskrtnout taky notifku
        if(checkBox.isChecked()) {
            // ulozit zaskrtnute tlacitko, tedy zmenit ZonkyD24Autoinvest na ZonkyD24Topic
            String topicCheckboxName = checkBoxName.replace("Autoinvest", "Topic");
            editor.putBoolean(topicCheckboxName, true);
            // zaregistrovat k odberu topicu
            EventBus.getDefault().post(new TopicSubscription.Request(topicCheckboxName, true));
        }

        editor.commit();
    }
}