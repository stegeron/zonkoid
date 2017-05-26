package eu.urbancoders.zonkysniper.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.billing.util.IabHelper;
import eu.urbancoders.zonkysniper.billing.util.IabResult;
import eu.urbancoders.zonkysniper.billing.util.Inventory;
import eu.urbancoders.zonkysniper.billing.util.Purchase;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.dataobjects.Wallet;

/**
 * Zobrazení zůstatku u Zonkoida, platba inapp, historie plateb, stažení výpisu poplatků a spol.
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 25.05.2017
 */

public class ZonkoidBalanceFragment extends ZSFragment {

    private static final String TAG = ZonkoidBalanceFragment.class.getName();

    Button clickButton;
    Button buyButton;
    WalletActivity walletActivity;

    static final String ITEM_SKU = "android.test.purchased";

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
            if (result.isFailure()) {
                // Handle error
                return;
            }
            else if (purchase.getSku().equals(ITEM_SKU)) {
                consumeItem();
                buyButton.setEnabled(false);
            }

        }
    };

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            if (result.isFailure()) {
                // Handle failure
                return;
            } else {
                if(inventory.getPurchase(ITEM_SKU) != null)
                    walletActivity.mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU), mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {

            if (result.isSuccess()) {
                clickButton.setEnabled(true);
            } else {
                // handle error
                return;
            }
        }
    };

    public static ZonkoidBalanceFragment newInstance() {
        ZonkoidBalanceFragment fragment = new ZonkoidBalanceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ZonkoidBalanceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_zonkoidbalance, container, false);

        walletActivity = (WalletActivity) getActivity();

        buyButton = (Button) rootView.findViewById(R.id.buyButton);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walletActivity.mHelper.launchPurchaseFlow(getActivity(), ITEM_SKU, 50,
                        mPurchaseFinishedListener, "17052605012345");  // TODO mypurchase token bude rrmmddcastkainvestorId, kde castka je vzdy 3 mista, napr. 050
            }
        });
        clickButton = (Button) rootView.findViewById(R.id.clickButton);
        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButton.setEnabled(false);
                buyButton.setEnabled(true);
            }
        });
        clickButton.setEnabled(false);


        walletActivity.mHelper.startSetup(
            new IabHelper.OnIabSetupFinishedListener() {
                   public void onIabSetupFinished(IabResult result)
                   {
                       if (!result.isSuccess()) {
                           Log.d(TAG, "In-app Billing setup failed: " + result);
                       } else {
                           Log.d(TAG, "In-app Billing is set up OK");
                           walletActivity.mHelper.queryInventoryAsync(true, mReceivedInventoryListener);
                       }
                   }
               });

        return rootView;
    }

    public void buttonClicked(View view) {
        clickButton.setEnabled(false);
        buyButton.setEnabled(true);
    }

    public void consumeItem() {
        walletActivity.mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }
}
