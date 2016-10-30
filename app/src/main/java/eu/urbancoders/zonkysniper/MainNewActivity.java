package eu.urbancoders.zonkysniper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.DividerItemDecoration;
import eu.urbancoders.zonkysniper.core.ZSViewActivity;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.events.GetInvestor;
import eu.urbancoders.zonkysniper.events.GetWallet;
import eu.urbancoders.zonkysniper.events.Invest;
import eu.urbancoders.zonkysniper.events.ReloadMarket;
import eu.urbancoders.zonkysniper.messaging.MessagingActivity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainNewActivity extends ZSViewActivity {

    TextView walletSum;
	private List<Loan> loanList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LoansAdapter mAdapter;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.marketplace);
        walletSum = (TextView) toolbar.findViewById(R.id.walletSum);

        setSupportActionBar(toolbar);

        initDrawer();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new LoansAdapter(getApplicationContext(), loanList);

        // refresher obsahu
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                EventBus.getDefault().post(new ReloadMarket.Request(true));
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                R.color.greenLight,
                R.color.warningYellow,
                R.color.colorPrimary);
//        swipeRefreshLayout.setRefreshing(true); // při prvnim startu zobrazit kolecko

        // samotny obsah
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Loan loan = loanList.get(position);
                Intent detailIntent = new Intent(MainNewActivity.this, LoanDetailsActivity.class);
                detailIntent.putExtra("loanId", loan.getId());
                startActivity(detailIntent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

//        if(loanList.isEmpty()) {
//            EventBus.getDefault().post(new ReloadMarket.Request(true));
//        }
    }

    /**
     * Levy drawer a menu vcetne akci
     */
    private void initDrawer() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();
                Intent intent;

                switch (id) {
                    case R.id.action_drawer_messages:
                        intent = new Intent(getApplicationContext(), MessagingActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.action_drawer_settings_user:
                        intent = new Intent(getApplicationContext(), SettingsUser.class);
                        startActivity(intent);
                        return true;
                    case R.id.action_drawer_settings_notifications:
                        intent = new Intent(getApplicationContext(), SettingsNotifications.class);
                        startActivity(intent);
                        return true;
                }
                return true;
            }
        });

        CheckBox showCoveredCheckBox = (CheckBox) navigationView.getMenu().findItem(R.id.action_drawer_show_covered).getActionView();

        showCoveredCheckBox.setChecked(
                PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getBoolean(Constants.SHARED_PREF_SHOW_COVERED, false)
        );

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

    }

    @Subscribe
    public void onWalletReceived(GetWallet.Response evt) {
        if(walletSum != null) {
            walletSum.setText(getString(R.string.balance) + evt.getWallet().getAvailableBalance() + getString(R.string.CZK));
            ZonkySniperApplication.wallet = evt.getWallet();
        }
    }

    @Subscribe
    public void onInvestorDetailReceived(GetInvestor.Response evt) {
        View header = navigationView.getHeaderView(0);

        TextView drawer_firstname_surname = (TextView) header.findViewById(R.id.firstname_surname);
        drawer_firstname_surname.setText(evt.getInvestor().getFirstName() + " " + evt.getInvestor().getSurname());

        TextView drawer_username = (TextView) header.findViewById(R.id.username);
        drawer_username.setText(evt.getInvestor().getUsername());

        // pocet neprectenych zprav
        Menu menu = navigationView.getMenu();
        MenuItem menu_messages = menu.findItem(R.id.action_drawer_messages);
        if(evt.getInvestor().getUnreadNotificationsCount() > 0) {
            menu_messages.setTitle(getString(R.string.action_messages) + " (nové: " + evt.getInvestor().getUnreadNotificationsCount() + ")");
        } else {
            menu_messages.setTitle(getString(R.string.action_messages));
        }
    }

    /**
     * Po nacteni Trziste je potreba prekreslit seznam nezainvestovanych uveru
     *
     * @param evt
     */
    @Subscribe
    public void onMarketReloaded(ReloadMarket.Response evt) {
        loanList.clear();
        loanList.addAll(evt.getMarket());
        mAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe
    public void onMarketReloadFailed(ReloadMarket.Failure evt) {
        if("503".equalsIgnoreCase(evt.errorCode)) {
            Snackbar.make(findViewById(R.id.main_content), R.string.zonkyUnavailable, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loanList.isEmpty()) {
            swipeRefreshLayout.setRefreshing(true);
            EventBus.getDefault().post(new ReloadMarket.Request(true));
        }
        if (ZonkySniperApplication.getInstance().isLoginAllowed()) {
            // pouze pro zvane
            EventBus.getDefault().post(new GetWallet.Request());
            EventBus.getDefault().post(new GetInvestor.Request());
        }

        drawerToggle.syncState();

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

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MainNewActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainNewActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public void showCoveredChecked(View view) {
        Log.i(TAG, "Show covered checkbox clicked and isChecked = "+((CheckBox) view).isChecked());
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        sp.edit().putBoolean(Constants.SHARED_PREF_SHOW_COVERED, ((CheckBox) view).isChecked()).commit();
    }
}
