package eu.urbancoders.zonkysniper.core;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.events.UnresolvableError;
import eu.urbancoders.zonkysniper.wallet.WalletActivity;


/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 14.05.2016
 */
public abstract class ZSViewActivity extends AppCompatActivity {

    public final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getAttributes().windowAnimations = R.style.FadeAnimation;
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Spusti libovolnou activity
     *
     * @param activity
     */
    public boolean startActivity(Class<? extends ZSViewActivity> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        return true;
    }

    @Subscribe
    public void dummyEventHandler(Void v) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnresolvableError(UnresolvableError.Request evt) {
        redWarning(findViewById(R.id.toolbar), evt.getError().getError_description(), null, getString(R.string.close));
    }

    /**
     * Zobrazeni bile hlasky
     * @param v
     * @param text
     */
    public void whiteMessage(View v, String text) {
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
     *
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
     *
     * @param v
     * @param text
     * @param doAction      akce po stisknuti tlacitka doAction
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

    /**
     * Zapne, vypne, prepne zobrazeni indikatoru stavu investora (DEBTOR, BLOCKED)
     * @param indicator
     */
    public void toggleInvestorStatusIndicator(ImageView indicator) {
        if(ZonkySniperApplication.getInstance().getUser() != null) {
            switch (ZonkySniperApplication.getInstance().getUser().getZonkyCommanderStatus()) {
                case DEBTOR:
                    indicator.setVisibility(View.VISIBLE);
                    indicator.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.warningYellow));
                    break;
                case BLOCKED:
                    indicator.setVisibility(View.VISIBLE);
                    indicator.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    break;
                default:
                    indicator.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Zobrazi ZonkoidWallet
     * @param view
     */
    public void gotoZonkoidWallet(View view) {
        Intent walletIntent = new Intent(this, WalletActivity.class);
        walletIntent.putExtra("tab", 1);
        startActivity(walletIntent);
    }
}
