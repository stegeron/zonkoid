package eu.urbancoders.zonkysniper.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.EmptyRequestListener;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;

import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZSViewActivity;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 29.09.2018
 */
public class PurchaseActivity extends ZSViewActivity implements View.OnClickListener {

    private class PurchaseListener extends EmptyRequestListener<Purchase> {
        @Override
        public void onSuccess(Purchase purchase) {
            // here you can process the loaded purchase
        }

        @Override
        public void onError(int response, Exception e) {
            // handle errors here
        }
    }

    private class InventoryCallback implements Inventory.Callback {
        @Override
        public void onLoaded(Inventory.Products products) {
            // your code here
        }
    }

    private final ActivityCheckout mCheckout = Checkout.forActivity(this, ZonkySniperApplication.getInstance().getBilling());
    private Inventory mInventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCheckout.start();

        mCheckout.createPurchaseFlow(new PurchaseListener());

        mInventory = mCheckout.makeInventory();
        mInventory.load(Inventory.Request.create()
                .loadAllPurchases()
                .loadSkus(ProductTypes.SUBSCRIPTION, Constants.SUBSCRIPTION_AD_REMOVE), new InventoryCallback());
    }

    @Override
    protected void onDestroy() {
        mCheckout.stop();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCheckout.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        mCheckout.whenReady(new Checkout.EmptyListener() {
            @Override
            public void onReady(BillingRequests requests) {
                requests.purchase(ProductTypes.SUBSCRIPTION, Constants.SUBSCRIPTION_AD_REMOVE, null, mCheckout.getPurchaseFlow());
            }
        });
    }
}
