package eu.urbancoders.zonkysniper.investing;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableLayout;

import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;

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
public class AutoInvestMatrixPreference extends Preference {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AutoInvestMatrixPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AutoInvestMatrixPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AutoInvestMatrixPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AutoInvestMatrixPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View v = super.onCreateView(parent);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ZonkySniperApplication.getInstance().getApplicationContext());

        TableLayout layout = v.findViewById(R.id.auto_invest_matrix_table);
        CheckBox currBox = null;
        for (int tr = 0; tr < layout.getChildCount(); tr++) { // pro kazdou TableRow
            ViewGroup row = (ViewGroup) layout.getChildAt(tr);
            for (int i = 0; i < row.getChildCount(); i++) {    // pro kazdy checkbox v radku
                if (row.getChildAt(i) instanceof CheckBox) {
                    currBox = (CheckBox) row.getChildAt(i);
                    currBox.setChecked(sp.getBoolean(v.getResources().getResourceEntryName(currBox.getId()), false));
                }
            }
        }
        return v;
    }
}
