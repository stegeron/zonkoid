package eu.urbancoders.zonkysniper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.events.GetWallet;
import eu.urbancoders.zonkysniper.events.Invest;
import eu.urbancoders.zonkysniper.events.ReloadMarket;
import eu.urbancoders.zonkysniper.events.UserLogin;
import eu.urbancoders.zonkysniper.events.UserLogout;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends ZSViewActivity {

    List<Loan> loanList = new ArrayList<Loan>(0);
    ExpandableListView listView;
    TextView walletSum;
    int previousSelectedPosition = -1;
    public int previousSelectedLoanId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        walletSum = (TextView) toolbar.findViewById(R.id.walletSum);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(findViewById(R.id.fab), R.string.reloadingMarket, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                EventBus.getDefault().post(new ReloadMarket.Request(true));
                if(ZonkySniperApplication.getInstance().getAuthToken() != null) {
                    EventBus.getDefault().post(new GetWallet.Request());
                }
            }
        });

        // zavrit ostatni pri otevreni jineho
        listView = (ExpandableListView) findViewById(R.id.marketListView);
        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
//                Loan loan = (Loan) listView.getAdapter().getItem(groupPosition);
//                if(loan.getId() != previousSelectedPosition) {
//                    for(int i=0; i < listView.getAdapter().getCount(); i++) {
//                        if(((Loan)listView.getAdapter().getItem(i)).getId() == previousSelectedPosition) {
//                            listView.collapseGroup(i-1);
//                        }
//                    }
//                 }
//                previousSelectedPosition = loan.getId();
                if (groupPosition != previousSelectedPosition)
                    listView.collapseGroup(previousSelectedPosition);
                previousSelectedPosition = groupPosition;
            }
        });

        listView.setChildDivider(new ColorDrawable(Color.parseColor(getString(R.string.white))));
        listView.setDivider(new ColorDrawable(Color.parseColor(getString(R.string.white))));
        listView.setDividerHeight(20);
    }

    @Subscribe
    public void onTokenReceived(UserLogin.Response evt) {
        try {
            Snackbar.make(findViewById(R.id.fab), R.string.authorizingUser, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
//            EventBus.getDefault().post(new ReloadMarket.Request(true));
            EventBus.getDefault().post(new GetWallet.Request());
        } catch (Exception e) {
            Log.d(TAG, "onClick: exception: " + e.getMessage());
        }
    }

    @Subscribe
    public void onWalletReceived(GetWallet.Response evt) {
        if(walletSum != null) {
            walletSum.setText(getString(R.string.balance) + evt.getWallet().getAvailableBalance() + getString(R.string.CZK));
            ZonkySniperApplication.wallet = evt.getWallet();
        }
    }

    /**
     * Po nacteni Trziste je potreba prekreslit seznam nezainvestovanych uveru
     * @param evt
     */
    @Subscribe
    public void onMarketReloaded(ReloadMarket.Response evt) {
        loanList = evt.getMarket();
        listView = (ExpandableListView) findViewById(R.id.marketListView);
        MarketListViewAdapter adapter = new MarketListViewAdapter(this, loanList);
        listView.setAdapter(adapter);

        /**
         * otevrit drive zobrazenou pujcku, pokud jdu z detailu nebo z notifikace
         */
        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
            if (((Loan) listView.getAdapter().getItem(i)).getId() == previousSelectedLoanId) {
                listView.expandGroup(i>0?i-1:0);
            }
        }

    }

    @Subscribe
    public void onInvestError(Invest.Failure evt) {
        displayInvestingStatus(findViewById(R.id.marketListView), evt.getDesc());
    }

    @Subscribe
    public void onInvested(Invest.Response noMeaning) {
        displayInvestingStatus(findViewById(R.id.marketListView), getString(R.string.investedOk));
        EventBus.getDefault().post(new ReloadMarket.Request(true));
        EventBus.getDefault().post(new GetWallet.Request());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings_general) {
            Intent intent = new Intent(this, ZonkoidSettings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().post(new ReloadMarket.Request(true));
        if (ZonkySniperApplication.getInstance().isLoginAllowed()) {
            // pouze pro zvane
            EventBus.getDefault().post(new GetWallet.Request());
        }

//        loadPreferences();
    }

    @SuppressWarnings("unchecked")
    public void loadPreferences() {
        Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getAll();
        for (String key : prefs.keySet()) {
            Object pref = prefs.get(key);
            String printVal = "";
            if (pref instanceof Boolean) {
                printVal = key + " : " + (Boolean) pref;
            }
            if (pref instanceof Float) {
                printVal = key + " : " + (Float) pref;
            }
            if (pref instanceof Integer) {
                printVal = key + " : " + (Integer) pref;
            }
            if (pref instanceof Long) {
                printVal = key + " : " + (Long) pref;
            }
            if (pref instanceof String) {
                printVal = key + " : " + (String) pref;
            }
            if (pref instanceof Set<?>) {
                printVal = key + " : " + (Set<String>) pref;
            }

            Log.d(TAG, "PREFERENCE " + printVal);
        }
    }

    /**
     * Volano po kliknuti na rozbalenou polozku, konkretne na nazev a popis
     *
     * @param view
     */
    public void showLoanBasicDetails(View view, Loan loan) {
        Intent detailIntent = new Intent(this, LoanDetailsActivity.class);
        detailIntent.putExtra("loan", loan);
        startActivity(detailIntent);
    }

    public void openZonkyWeb(Loan loan) {
        Intent detailIntent = new Intent(this, LoanDetailsActivity.class);
        detailIntent.putExtra("loan", loan);
        detailIntent.putExtra("tab", 1);
        startActivity(detailIntent);
    }

    public void displayInvestingStatus(View view, final String message) {
        AlertDialog.Builder statusDialog = new AlertDialog.Builder(view.getContext());
        statusDialog.setMessage(message);
        statusDialog.setCancelable(false);

        statusDialog.setNeutralButton(
                R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = statusDialog.create();
        alert.show();
    }
}
