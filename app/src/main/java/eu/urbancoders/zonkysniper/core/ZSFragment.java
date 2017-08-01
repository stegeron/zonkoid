package eu.urbancoders.zonkysniper.core;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.R;

/**
 * Predek vsech fragmentu
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 14.02.2017
 */

public abstract class ZSFragment extends Fragment {

    public final String TAG = this.getClass().getName();

    /**
     * Zobrazeni zlute hlasky
     * @param v
     * @param text
     * @param snackbarLength napr. Snackbar.LENGTH_INDEFINITE
     */
    public void yellowWarning(View v, String text, int snackbarLength) {
        final Dialog dialog = new Dialog(v.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffc80b")));
//        dialog.getWindow().setBackgroundDrawable(
//                new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)));
        dialog.setContentView(R.layout.yellow_warning);
        dialog.setCanceledOnTouchOutside(false);

        TextView warningHeadline = (TextView) dialog.findViewById(R.id.warningHeadline);
        warningHeadline.setText("");

        TextView warningText = (TextView) dialog.findViewById(R.id.warningText);
        warningText.setText(text);

        Button doActionButton = (Button) dialog.findViewById(R.id.doAction);
        doActionButton.setText(R.string.close);
        doActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Zobrazení červené hlášky pres celou obrazovku
     * @param v
     * @param text
     * @param doAction akce po stisknuti tlacitka doAction
     * @param doActionLabel napis na tlacitku doAction
     */
    public void redWarning(View v, String headline, String text, final Intent doAction, String doActionLabel) {
        final Dialog dialog = new Dialog(v.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D24121")));
//        dialog.getWindow().setBackgroundDrawable(
//                new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)));
        dialog.setContentView(R.layout.red_warning);
        dialog.setCanceledOnTouchOutside(false);

        TextView warningHeadline = (TextView) dialog.findViewById(R.id.warningHeadline);
        warningHeadline.setText(headline);

        TextView warningText = (TextView) dialog.findViewById(R.id.warningText);
        warningText.setText(text);

        Button doActionButton = (Button) dialog.findViewById(R.id.doAction);
        doActionButton.setText(doActionLabel);
        doActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(doAction);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to start activity "+doAction.getAction(), e);
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
