package eu.urbancoders.zonkysniper.wallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;

import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.ConfigurationItem;
import eu.urbancoders.zonkysniper.events.GetConfiguration;
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
    TextView zetons_1_price, zetons_2_price, zetons_3_price;

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

        balance = rootView.findViewById(R.id.balance);

        kolecko = rootView.findViewById(R.id.kolecko);

        buyButton1 = rootView.findViewById(R.id.buyButton1);
        buyButton2 = rootView.findViewById(R.id.buyButton2);
        buyButton3 = rootView.findViewById(R.id.buyButton3);
        buySubscription = rootView.findViewById(R.id.buySubscription);

        zetons_1_price = rootView.findViewById(R.id.zetons_1_price);
        zetons_2_price = rootView.findViewById(R.id.zetons_2_price);
        zetons_3_price = rootView.findViewById(R.id.zetons_3_price);

        if(ZonkySniperApplication.getInstance().getUser() != null) {
            roztocKolecko();
            EventBus.getDefault().post(new GetZonkoidWallet.Request(ZonkySniperApplication.getInstance().getUser().getId()));
            EventBus.getDefault().post(new GetConfiguration.Request(
                    Arrays.asList("zonkoid_consumable_60", "zonkoid_consumable_70", "zonkoid_consumable_80")));
        }

        return rootView;
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onZonkoidWalletReceived(GetZonkoidWallet.Response evt) {
//        if(evt != null) {
//
//            Log.i(TAG, "Received Zonkoid Wallet with balance " + evt.getZonkoidWallet().getBalance());
//            walletActivity.setZonkoidWallet(evt.getZonkoidWallet());
//            if(ZonkySniperApplication.getInstance().getUser().getZonkyCommanderStatus() == Investor.Status.SUBSCRIBER) {
//                balance.setText(getString(R.string.subscribed));
//                balance.setTextColor(ContextCompat.getColor(getContext(), R.color.greenLight));
//                buyButton1.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.greyLighter));
//                buyButton2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.greyLighter));
//                buyButton3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.greyLighter));
//                buySubscription.setText("Spravovat");
//            } else if(evt.getZonkoidWallet().getBalance() > 0 && evt.getZonkoidWallet().getBalance() <= 5) {
//                balance.setText(
//                        String.format(getString(R.string.prepaid_number_of_investments), String.valueOf((int) evt.getZonkoidWallet().getBalance())));
//                balance.setTextColor(ContextCompat.getColor(getContext(), R.color.warningYellow));
//            } else if(evt.getZonkoidWallet().getBalance() > 5) {
//                balance.setText(
//                        String.format(getString(R.string.prepaid_number_of_investments), String.valueOf((int) evt.getZonkoidWallet().getBalance())));
//                balance.setTextColor(ContextCompat.getColor(getContext(), R.color.greenLight));
//            } else {
//                balance.setText(getString(R.string.please_pay));
//                balance.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
//            }
//            zastavKolecko();
//        }
//    }

    /**
     * Dosazeni spravnych poctu kreditu za danou cenu
     * @param evt
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConfigurationReceived(GetConfiguration.Response evt) {
        if(evt != null) {
            for (ConfigurationItem item : evt.getItems()) {
                switch (item.getKey()) {
                    case "zonkoid_consumable_60":
                        zetons_1_price.setText("za " + item.getValue() + " investic");
                        break;
                    case "zonkoid_consumable_70":
                        zetons_2_price.setText("za " + item.getValue() + " investic");
                        break;
                    case "zonkoid_consumable_80":
                        zetons_3_price.setText("za " + item.getValue() + " investic");
                        break;
                }
            }
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
