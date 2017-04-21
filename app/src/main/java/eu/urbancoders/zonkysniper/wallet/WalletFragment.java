package eu.urbancoders.zonkysniper.wallet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.DividerItemDecoration;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Wallet;
import eu.urbancoders.zonkysniper.dataobjects.WalletTransaction;
import eu.urbancoders.zonkysniper.events.GetWallet;
import eu.urbancoders.zonkysniper.events.GetWalletTransactions;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class WalletFragment extends ZSFragment {

    private static final String TAG = WalletFragment.class.getName();
    private static final int NUMBER_OF_DAYS = 30;   // pocet dnu "od" pro zobrazeni seznamu transakci - vychozi hodnota
    private List<WalletTransaction> walletTransactions = new ArrayList<>();

    private RecyclerView recyclerView;
    private WalletTransactionsAdapter mAdapter;

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
        EventBus.getDefault().post(new GetWalletTransactions.Request(NUMBER_OF_DAYS, Constants.NUM_OF_ROWS_LONG));

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(inflater.getContext(), LinearLayoutManager.VERTICAL));

        mAdapter = new WalletTransactionsAdapter(ZonkySniperApplication.getInstance().getApplicationContext(), walletTransactions);
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Subscribe
    public void onWalletReceived(GetWallet.Response evt) {
        ZonkySniperApplication.wallet = evt.getWallet();
        drawWallet(evt.getWallet());
    }

    private void drawWallet(Wallet wallet) {
        availableBalance.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(wallet.getAvailableBalance()) + " Kč");
        blockedBalance.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(wallet.getBlockedBalance()) + " Kč");
        creditSum.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(wallet.getCreditSum()) + " Kč");
        debitSum.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(wallet.getDebitSum()) + " Kč");
    }

    @Subscribe
    public void onWalletTransactionsReceived(GetWalletTransactions.Response evt) {
        walletTransactions.clear();
        walletTransactions.addAll(evt.getWalletTransactions());
        mAdapter.notifyDataSetChanged();
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
