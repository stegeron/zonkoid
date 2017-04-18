package eu.urbancoders.zonkysniper.portfolio;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Wallet;
import eu.urbancoders.zonkysniper.events.GetWallet;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class WalletFragment extends ZSFragment {

    private static final String TAG = WalletFragment.class.getName();

    TextView availableBalance, blockedBalance, creditSum, debitSum;

    public static WalletFragment newInstance() {
        WalletFragment fragment = new WalletFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public WalletFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_wallet, container, false);

        availableBalance = (TextView) rootView.findViewById(R.id.availableBalance);
        blockedBalance = (TextView) rootView.findViewById(R.id.blockedBalance);
        creditSum = (TextView) rootView.findViewById(R.id.creditSum);
        debitSum = (TextView) rootView.findViewById(R.id.debitSum);

        if (ZonkySniperApplication.wallet == null) {
            EventBus.getDefault().post(new GetWallet.Request());
        } else {
            drawWallet(ZonkySniperApplication.wallet);
        }

        return rootView;
    }

    @Subscribe
    public void onWalletReceived(GetWallet.Response evt) {
        ZonkySniperApplication.wallet = evt.getWallet();
        drawWallet(evt.getWallet());
    }

    private void drawWallet(Wallet wallet) {

        // totalInvestment.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(currentOverview.getTotalInvestment()) + " Kč");

        availableBalance.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(wallet.getAvailableBalance()) + " Kč");
        blockedBalance.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(wallet.getBlockedBalance()) + " Kč");
        creditSum.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(wallet.getCreditSum()) + " Kč");
        debitSum.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(wallet.getDebitSum()) + " Kč");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
