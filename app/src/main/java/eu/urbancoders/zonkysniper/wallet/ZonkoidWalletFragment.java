package eu.urbancoders.zonkysniper.wallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.events.GetZonkoidWallet;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Zobrazení zůstatku u Zonkoida, platba inapp, historie plateb, stažení výpisu poplatků a spol.
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 25.05.2017
 */

public class ZonkoidWalletFragment extends ZSFragment {

    private static final String TAG = ZonkoidWalletFragment.class.getName();

    TextView balance;
    TextView feePerInvestmentDesc;
    Button buyButton;
    WalletActivity walletActivity;

    static final String ITEM_SKU = "android.test.purchased";


    public static ZonkoidWalletFragment newInstance() {
        ZonkoidWalletFragment fragment = new ZonkoidWalletFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ZonkoidWalletFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_zonkoid_wallet, container, false);

        walletActivity = (WalletActivity) getActivity();

        buyButton = (Button) rootView.findViewById(R.id.buyButton);

        balance = (TextView) rootView.findViewById(R.id.balance);
        feePerInvestmentDesc = (TextView) rootView.findViewById(R.id.fee_per_investment_desc);

        if(ZonkySniperApplication.getInstance().getUser() != null) {
            EventBus.getDefault().post(new GetZonkoidWallet.Request(ZonkySniperApplication.getInstance().getUser().getId()));
        }

        return rootView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onZonkoidWalletReceived(GetZonkoidWallet.Response evt) {
        if(evt != null) {

            walletActivity.setZonkoidWallet(evt.getZonkoidWallet());
            balance.setText(Constants.FORMAT_NUMBER_WITH_DECIMALS.format(Math.abs(evt.getZonkoidWallet().getBalance())) + " Kč");
            feePerInvestmentDesc.setText(String.format(getString(R.string.fee_per_investment),
                    Constants.FORMAT_NUMBER_WITH_DECIMALS.format(evt.getZonkoidWallet().getPricePerInvestment()) + " Kč"));

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
