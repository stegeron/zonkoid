package eu.urbancoders.zonkysniper.investing;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.view.ContextThemeWrapper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.HashSet;
import java.util.Set;

import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.IncomeType;

/**
 * Prepinac zdroje prijmu
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 20.11.2018
 */
public class AutoInvestIncomePreference extends Preference {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AutoInvestIncomePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AutoInvestIncomePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AutoInvestIncomePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AutoInvestIncomePreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View v = super.onCreateView(parent);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ZonkySniperApplication.getInstance().getApplicationContext());

        TableLayout layout = v.findViewById(R.id.auto_invest_income);

        TableLayout incomeTable = (TableLayout) layout.findViewById(R.id.income_table);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

        TableRow incomeRow = new TableRow(incomeTable.getContext());
        incomeRow.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER);
        incomeTable.addView(incomeRow);
        int iterator = -1;
        HashSet<ToggleButton> tmpButtonSet = new HashSet<>();
        for (IncomeType incomeType : IncomeType.values()) {
            if(incomeType.getParent() == null) {
                iterator++;
                ToggleButton tmpBtn = new ToggleButton(incomeTable.getContext());
                tmpBtn.setTextOff(incomeType.getDescCZ());
                tmpBtn.setTextOn(incomeType.getDescCZ());
                tmpBtn.setText(incomeType.getDescCZ());
                tmpBtn.setTag(incomeType.name());
                tmpBtn.setOnCheckedChangeListener(new IncomeTypeButtonChangeListener());

                if(iterator % 2 == 0) {
                    incomeRow = new TableRow(incomeTable.getContext());
                    incomeRow.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER);
                    incomeTable.addView(incomeRow);
                }
                incomeRow.addView(tmpBtn);
                tmpButtonSet.add(tmpBtn);
            }
        }

        // ted projedeme vsechny toggle buttony a nastavime spravnou hodnotu
        Set<String> incomeTypesSet = sp.getStringSet(Constants.SHARED_PREF_AUTOINVEST_INCOME_TYPES, null);
        if(incomeTypesSet == null) {
            incomeTypesSet = new HashSet<String>();
            for (IncomeType incomeType : IncomeType.getParentIncomeTypes()) {
                incomeTypesSet.add(incomeType.name());
            }
            sp.edit().putStringSet(Constants.SHARED_PREF_AUTOINVEST_INCOME_TYPES, incomeTypesSet).commit();
        }

        for (ToggleButton toggleButton : tmpButtonSet) {
            if(incomeTypesSet.contains((String)toggleButton.getTag())) {
                toggleButton.setChecked(true);
            }
        }

        return v;
    }

    /**
     *  Ulozeni / smazani typu prijmu z filtru
     */
    private class IncomeTypeButtonChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ZonkySniperApplication.getInstance().getApplicationContext());
            // nacti z shared preferenci cely set
            Set<String> incomeTypes = sp.getStringSet(Constants.SHARED_PREF_AUTOINVEST_INCOME_TYPES, new HashSet<String>());

            // pokud isChecked false a zaroven je posledni v setu, nedovol vypnuti
            if(incomeTypes.size() <= 1 && incomeTypes.contains((String)buttonView.getTag()) && !isChecked) {
                buttonView.setChecked(true);
                return;
            } else {
                // pridej nebo odeber ze setu
                if(isChecked) {
                    incomeTypes.add((String)buttonView.getTag());
                } else {
                    incomeTypes.remove((String)buttonView.getTag());
                }
            }

            SharedPreferences.Editor edit = sp.edit();
            edit.remove(Constants.SHARED_PREF_AUTOINVEST_INCOME_TYPES);
            edit.commit();
            edit.putStringSet(Constants.SHARED_PREF_AUTOINVEST_INCOME_TYPES, incomeTypes).commit();
        }
    }
}
