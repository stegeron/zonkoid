package eu.urbancoders.zonkysniper;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
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

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 16.06.2016
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AmountToAutoInvestPreference amountToAutoInvestPreference = (AmountToAutoInvestPreference) findPreference(Constants.SHARED_PREF_PRESET_AUTOINVEST_AMOUNT);
                amountToAutoInvestPreference.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_covered, null));
            }

            bindPreferenceSummaryToValue(findPreference(Constants.SHARED_PREF_PRESET_AUTOINVEST_AMOUNT));
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
        editor.commit();
    }
}