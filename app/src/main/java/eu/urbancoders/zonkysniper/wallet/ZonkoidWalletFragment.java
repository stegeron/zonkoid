package eu.urbancoders.zonkysniper.wallet;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Investor;
import eu.urbancoders.zonkysniper.events.GetZonkoidWallet;

/**
 * Zobrazení zůstatku u Zonkoida, platba inapp, historie plateb, stažení výpisu poplatků a spol.
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 25.05.2017
 */

public class ZonkoidWalletFragment extends ZSFragment {

    private static final String TAG = ZonkoidWalletFragment.class.getName();

    TextView balance;
    WalletActivity walletActivity;
    public static ProgressBar kolecko;
    Button buyButton1, buyButton2, buyButton3, buySubscription;

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

        balance = (TextView) rootView.findViewById(R.id.balance);

        kolecko = (ProgressBar) rootView.findViewById(R.id.kolecko);

        buyButton1 = (Button) rootView.findViewById(R.id.buyButton1);
        buyButton2 = (Button) rootView.findViewById(R.id.buyButton2);
        buyButton3 = (Button) rootView.findViewById(R.id.buyButton3);
        buySubscription = (Button) rootView.findViewById(R.id.buySubscription);

        if(ZonkySniperApplication.getInstance().getUser() != null) {
            roztocKolecko();
            EventBus.getDefault().post(new GetZonkoidWallet.Request(ZonkySniperApplication.getInstance().getUser().getId()));
        }

        return rootView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onZonkoidWalletReceived(GetZonkoidWallet.Response evt) {
        if(evt != null) {

            Log.i(TAG, "Received Zonkoid Wallet with balance " + evt.getZonkoidWallet().getBalance());
            walletActivity.setZonkoidWallet(evt.getZonkoidWallet());
            if(ZonkySniperApplication.getInstance().getUser().getZonkyCommanderStatus() == Investor.Status.SUBSCRIBER) {
                balance.setText(getString(R.string.subscribed));
                balance.setTextColor(ContextCompat.getColor(getContext(), R.color.greenLight));
                buyButton1.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.greyLighter));
                buyButton2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.greyLighter));
                buyButton3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.greyLighter));
                buySubscription.setText("Spravovat");
            } else if(evt.getZonkoidWallet().getBalance() > 0 && evt.getZonkoidWallet().getBalance() <= 5) {
                balance.setText(
                        String.format(getString(R.string.prepaid_number_of_investments), String.valueOf((int) evt.getZonkoidWallet().getBalance())));
                balance.setTextColor(ContextCompat.getColor(getContext(), R.color.warningYellow));
            } else if(evt.getZonkoidWallet().getBalance() > 5) {
                balance.setText(
                        String.format(getString(R.string.prepaid_number_of_investments), String.valueOf((int) evt.getZonkoidWallet().getBalance())));
                balance.setTextColor(ContextCompat.getColor(getContext(), R.color.greenLight));
            } else {
                balance.setText(getString(R.string.please_pay));
                balance.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            }
            zastavKolecko();
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

    public static void roztocKolecko() {
        kolecko.setVisibility(View.VISIBLE);
    }

    public static void zastavKolecko() {
        kolecko.setVisibility(View.GONE);
    }
}
