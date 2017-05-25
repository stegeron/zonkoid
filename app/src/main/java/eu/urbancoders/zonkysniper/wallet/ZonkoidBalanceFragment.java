package eu.urbancoders.zonkysniper.wallet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.billing.util.IabHelper;
import eu.urbancoders.zonkysniper.billing.util.IabResult;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZSFragment;

/**
 * Zobrazení zůstatku u Zonkoida, platba inapp, historie plateb, stažení výpisu poplatků a spol.
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 25.05.2017
 */

public class ZonkoidBalanceFragment extends ZSFragment {

    private static final String TAG = ZonkoidBalanceFragment.class.getName();

    Button clickButton;
    Button buyButton;

    IabHelper mHelper;

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

        buyButton = (Button) rootView.findViewById(R.id.buyButton);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

        mHelper = new IabHelper(getActivity(), Constants.LICENCE_KEY);
        mHelper.startSetup(
            new IabHelper.OnIabSetupFinishedListener() {
                   public void onIabSetupFinished(IabResult result)
                   {
                       if (!result.isSuccess()) {
                           Log.d(TAG, "In-app Billing setup failed: " + result);
                       } else {
                           Log.d(TAG, "In-app Billing is set up OK");
                       }
                   }
               });

        return rootView;
    }

    public void buttonClicked(View view) {
        clickButton.setEnabled(false);
        buyButton.setEnabled(true);
    }
}
