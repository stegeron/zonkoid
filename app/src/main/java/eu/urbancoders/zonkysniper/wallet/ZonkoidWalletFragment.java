package eu.urbancoders.zonkysniper.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import eu.urbancoders.zonkysniper.LoanDetailsActivity;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.DividerItemDecoration;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.WalletTransaction;
import eu.urbancoders.zonkysniper.events.GetWalletTransactions;
import eu.urbancoders.zonkysniper.events.GetZonkoidWallet;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

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
    TextView feeDescShort;

    private List<WalletTransaction> walletTransactions = new ArrayList<>();

//    static final String ITEM_SKU = "android.test.purchased";
    private RecyclerView recyclerView;
    private ZonkoidWalletTransactionsAdapter mAdapter;

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

        feeDescShort = (TextView) rootView.findViewById(R.id.feeDescShort);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(inflater.getContext(), LinearLayoutManager.VERTICAL));

        recyclerView.addOnItemTouchListener(new WalletFragment.RecyclerTouchListener(ZonkySniperApplication.getInstance().getApplicationContext(), recyclerView, new WalletFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                WalletTransaction wt = walletTransactions.get(position);
                if (wt.getLoanId() > 0) {
                    Intent detailIntent = new Intent(getContext(), LoanDetailsActivity.class);
                    detailIntent.putExtra("loanId", wt.getLoanId());
                    startActivity(detailIntent);
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        mAdapter = new ZonkoidWalletTransactionsAdapter(ZonkySniperApplication.getInstance().getApplicationContext(), walletTransactions);
        recyclerView.setAdapter(mAdapter);


        return rootView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onZonkoidWalletReceived(GetZonkoidWallet.Response evt) {
        if(evt != null) {

            Log.i(TAG, "Received Zonkoid Wallet with balance " + evt.getZonkoidWallet().getBalance());
            walletActivity.setZonkoidWallet(evt.getZonkoidWallet());
            balance.setText(Constants.FORMAT_NUMBER_WITH_DECIMALS.format(Math.abs(evt.getZonkoidWallet().getBalance())) + " Kč");
            feePerInvestmentDesc.setText(String.format(getString(R.string.fee_per_investment),
                    Constants.FORMAT_NUMBER_WITH_DECIMALS.format(evt.getZonkoidWallet().getPricePerInvestment()) + " Kč"));
            feeDescShort.setText(String.format(getString(R.string.fee_desc_short),
                    Constants.FORMAT_NUMBER_WITH_DECIMALS.format(evt.getZonkoidWallet().getPricePerInvestment())));

            walletTransactions.clear();
            walletTransactions.addAll(evt.getZonkoidWallet().getWalletTransactions());
            mAdapter.notifyDataSetChanged();
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
