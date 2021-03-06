package eu.urbancoders.zonkysniper;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import eu.urbancoders.zonkysniper.core.Constants;

/**
 * Selektor pro volbu castky k auto-investovani
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 19.09.2018
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

public class AmountToAutoInvestPreference extends DialogPreference {

    private static final String CANCEL = "VYPNUTO";

    public static String[] amountsToInvest = new String[(Constants.AMOUNT_TO_INVEST_MAX / Constants.AMOUNT_TO_INVEST_STEP) + 1];

    static {
        for (int i = Constants.AMOUNT_TO_INVEST_MIN; i <= Constants.AMOUNT_TO_INVEST_MAX; i += Constants.AMOUNT_TO_INVEST_STEP) {
            amountsToInvest[(i/Constants.AMOUNT_TO_INVEST_STEP)-1] = String.valueOf(i);
        }
        amountsToInvest[amountsToInvest.length - 1] = CANCEL;
    }

    // enable or disable the 'circular behavior'
    public static final boolean WRAP_SELECTOR_WHEEL = true;

    private NumberPicker picker;
    private int value;

    public AmountToAutoInvestPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AmountToAutoInvestPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View onCreateDialogView() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        picker = new NumberPicker(getContext());
        picker.setLayoutParams(layoutParams);
        picker.setDisplayedValues(amountsToInvest);

        FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(picker);

        return dialogView;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        picker.setMinValue(0);
        picker.setMaxValue(amountsToInvest.length - 1);
        picker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        picker.setValue((getValue() / Constants.AMOUNT_TO_INVEST_STEP)-1);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            picker.clearFocus();
            int newValue;
            if(CANCEL.equals(amountsToInvest[picker.getValue()])) {
                newValue = -1;
            } else {
                newValue = Integer.valueOf(amountsToInvest[picker.getValue()]);
            }
            setValue(newValue);
            callChangeListener(newValue);
        }
    }

//    @Override
//    protected Object onGetDefaultValue(TypedArray a, int index) {
//        return a.getInt(index, amountsToInvest.length - 1);
//    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(amountsToInvest.length - 1) : (Integer) defaultValue);
    }

    public void setValue(int value) {
        this.value = value;
        persistInt(this.value);
    }

    public int getValue() {
        return this.value;
    }
}