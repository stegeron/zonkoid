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
 */
public class AutoInvestMatrixPreference extends Preference {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AutoInvestMatrixPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
//        setDialogLayoutResource(R.layout.push_notif_matrix);
    }

    public AutoInvestMatrixPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        setDialogLayoutResource(R.layout.push_notif_matrix);
    }

    public AutoInvestMatrixPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setDialogLayoutResource(R.layout.push_notif_matrix);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AutoInvestMatrixPreference(Context context) {
        super(context);
//        setDialogLayoutResource(R.layout.push_notif_matrix);
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
                    currBox.setChecked(sp.getBoolean(v.getResources().getResourceEntryName(currBox.getId()), true));
                }
            }
        }
        return v;
    }
}
