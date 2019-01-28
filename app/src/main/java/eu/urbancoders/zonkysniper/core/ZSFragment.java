package eu.urbancoders.zonkysniper.core;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public abstract class ZSFragment extends Fragment {

    public final String TAG = this.getClass().getName();

    /**
     * Zobrazeni zelene hlasky
     * @param v
     * @param text
     */
    public void greenMessage(View v, String text) {
        final Dialog dialog = new Dialog(v.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.white_message);
        dialog.setCanceledOnTouchOutside(false);

//        TextView warningHeadline = (TextView) dialog.findViewById(R.id.warningHeadline);
//        warningHeadline.setText("");

        TextView warningText = dialog.findViewById(R.id.warningText);
        warningText.setText(text);

        Button doActionButton = dialog.findViewById(R.id.doAction);
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
     * Zobrazeni zlute hlasky
     * @param v
     * @param text
     * @param snackbarLength napr. Snackbar.LENGTH_INDEFINITE
     */
    public void yellowWarning(View v, String text, int snackbarLength) {
        final Dialog dialog = new Dialog(v.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.yellow_warning);
        dialog.setCanceledOnTouchOutside(false);

//        TextView warningHeadline = (TextView) dialog.findViewById(R.id.warningHeadline);
//        warningHeadline.setText("");

        TextView warningText = dialog.findViewById(R.id.warningText);
        warningText.setText(text);

        Button doActionButton = dialog.findViewById(R.id.doAction);
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
    public void redWarning(View v, String text, @Nullable final Intent doAction, String doActionLabel) {
        final Dialog dialog = new Dialog(v.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.red_warning);
        dialog.setCanceledOnTouchOutside(false);

//        TextView warningHeadline = (TextView) dialog.findViewById(R.id.warningHeadline);
//        warningHeadline.setText(headline);

        TextView warningText = dialog.findViewById(R.id.warningText);
        warningText.setText(text);

        Button doActionButton = dialog.findViewById(R.id.doAction);
        doActionButton.setText(doActionLabel);
        doActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(doAction != null) {
                    try {
                        startActivity(doAction);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to start activity " + doAction.getAction(), e);
                    }
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
