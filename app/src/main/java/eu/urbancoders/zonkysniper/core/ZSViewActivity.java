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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;

import java.util.List;

import javax.annotation.Nonnull;

import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.events.ShowHideAd;
import eu.urbancoders.zonkysniper.events.UnresolvableError;
import eu.urbancoders.zonkysniper.wallet.WalletActivity;


/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 14.05.2016
 */
public abstract class ZSViewActivity extends AppCompatActivity {

    public final String TAG = this.getClass().getName();

    public AdView mAdView; // placeholder pro adView, je pripadne volane z kodu nize

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

    public void initAndLoadAd(final AdView mAdView) {

        Checkout checkout = ZonkySniperApplication.getInstance().getCheckout();

        final Inventory.Request request = Inventory.Request.create();
        request.loadAllPurchases();
        request.loadSkus(ProductTypes.SUBSCRIPTION, Constants.SUBSCRIPTION_AD_REMOVE);
        checkout.loadInventory(request, new Inventory.Callback() {
            boolean adRemovePurchased = false;

            @Override
            public void onLoaded(@Nonnull Inventory.Products products) {
                final Inventory.Product product = products.get(ProductTypes.SUBSCRIPTION);
                if (product.supported) {
                    final List<Purchase> purchases = product.getPurchases();
                    if (purchases.size() > 0) {
                        for (int i = 0; i < purchases.size(); i++) {
                            if (purchases.get(i).sku.equals(Constants.SUBSCRIPTION_AD_REMOVE)) {
                                // zakazat reklamu
                                adRemovePurchased = true;
                                break;
                            }
                        }
                    }
                }

                EventBus.getDefault().post(new ShowHideAd.Request(adRemovePurchased));
            }
        });
    }

    /**
     * zobrazit reklamu nebo ne :)
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowHideAd(ShowHideAd.Request evt) {
        if(mAdView == null) {
            return;
        }

        if(evt.isAdRemovePurchased()) {
            // hide and destroy
            mAdView.setVisibility(View.GONE);
        } else {
            // load ad
            mAdView.setVisibility(View.VISIBLE);
            mAdView.loadAd(ZonkySniperApplication.getInstance().getAdRequest());
        }
    }
}
