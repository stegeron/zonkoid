package eu.urbancoders.zonkysniper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
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
import eu.urbancoders.zonkysniper.dataobjects.MyInvestment;
import eu.urbancoders.zonkysniper.events.GetWallet;
import eu.urbancoders.zonkysniper.events.Invest;
import eu.urbancoders.zonkysniper.events.ReloadMarket;
import eu.urbancoders.zonkysniper.events.UserLogout;
import eu.urbancoders.zonkysniper.events.UserLogin;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ZSViewActivity {

    List<Loan> loanList = new ArrayList<Loan>(0);
    ExpandableListView listView;
    TextView walletSum;

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
    }

    @Subscribe
    public void onInvestError(Invest.Failure evt) {
        Snackbar.make(findViewById(R.id.marketListView), evt.getDesc(), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Subscribe
    public void onInvested(Invest.Response noMeaning) {
        Snackbar.make(findViewById(R.id.marketListView), R.string.investedOk, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        EventBus.getDefault().post(new ReloadMarket.Request(true));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            menu.findItem(R.id.action_logout).setVisible(false);
        }

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
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName());
            intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
            startActivity(intent);
            return true;
        } else if(id == R.id.action_logout) {
            EventBus.getDefault().post(new UserLogout.Request());
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

//    @SuppressWarnings("unchecked")
//    public void loadPreferences() {
//        Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getAll();
//        for (String key : prefs.keySet()) {
//            Object pref = prefs.get(key);
//            String printVal = "";
//            if (pref instanceof Boolean) {
//                printVal = key + " : " + (Boolean) pref;
//            }
//            if (pref instanceof Float) {
//                printVal = key + " : " + (Float) pref;
//            }
//            if (pref instanceof Integer) {
//                printVal = key + " : " + (Integer) pref;
//            }
//            if (pref instanceof Long) {
//                printVal = key + " : " + (Long) pref;
//            }
//            if (pref instanceof String) {
//                printVal = key + " : " + (String) pref;
//            }
//            if (pref instanceof Set<?>) {
//                printVal = key + " : " + (Set<String>) pref;
//            }
//
//            Log.d(TAG, "PREFERENCE " + printVal);
//        }
//    }

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

    public void displayLoginWarning(View view) {
        displayLoginWarning(view, "https://app.zonky.cz/#/account/login/%252Fmarketplace%252F");
    }

    public void displayLoginWarning(View view, final String urlToGo) {
        AlertDialog.Builder loginYesNoDialog = new AlertDialog.Builder(view.getContext());
        loginYesNoDialog.setMessage("Na žádost Zonky zatím nesmíte v této aplikaci ukládat hesla ani se přihlašovat. Chcete přejít na oficiální stránky zonky.cz?");
        loginYesNoDialog.setCancelable(true);

        loginYesNoDialog.setPositiveButton(
                R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToGo));
                        startActivity(webIntent);
                    }
                });

        loginYesNoDialog.setNegativeButton(
                R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = loginYesNoDialog.create();
        alert.show();
    }
}
