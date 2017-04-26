package eu.urbancoders.zonkysniper.core;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.R;

/**
 * Predek vsech fragmentu
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 14.02.2017
 */

public abstract class ZSFragment extends Fragment {

    /**
     * Zobrazeni zlute hlasky
     * @param v
     * @param text
     * @param snackbarLength napr. Snackbar.LENGTH_INDEFINITE
     */
    public void yellowWarning(View v, String text, int snackbarLength) {
        final Snackbar snackbar = Snackbar.make(v, text, snackbarLength);
        View view = snackbar.getView();
        view.setBackgroundResource(R.color.warningYellow);
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setMaxLines(4);
        tv.setTextColor(Color.BLACK);

        snackbar.setAction("x", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorButton));
        snackbar.show();
    }
}
