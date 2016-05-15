package eu.urbancoders.zonkysniper;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.events.ReloadMarket;
import eu.urbancoders.zonkysniper.events.UserLogin;
import eu.urbancoders.zonkysniper.integration.ZonkyClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ZSViewActivity {

    List<Loan> loanList = new ArrayList<Loan>(1);
    ExpandableListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(findViewById(R.id.fab), R.string.reloadingMarket, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                EventBus.getDefault().post(new ReloadMarket.Request(true));
            }
        });
    }

    @Subscribe
    public void onTokenReceived(UserLogin.Response evt) {
        try {
            Snackbar.make(findViewById(R.id.fab), R.string.authorizingUser, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            EventBus.getDefault().post(new ReloadMarket.Request(true));
        } catch (Exception e) {
            Log.d(TAG, "onClick: exception: " + e.getMessage());
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
