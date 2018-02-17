package eu.urbancoders.zonkysniper;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.appyvet.materialrangebar.IRangeBarFormatter;
import com.appyvet.materialrangebar.RangeBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZSViewActivity;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Investor;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.dataobjects.Rating;
import eu.urbancoders.zonkysniper.dataobjects.ZonkoidWallet;
import eu.urbancoders.zonkysniper.events.GetInvestor;
import eu.urbancoders.zonkysniper.events.GetWallet;
import eu.urbancoders.zonkysniper.events.ReloadMarket;
import eu.urbancoders.zonkysniper.messaging.MessagingActivity;
import eu.urbancoders.zonkysniper.portfolio.PortfolioActivity;
import eu.urbancoders.zonkysniper.wallet.WalletActivity;

public class MainNewActivity extends ZSViewActivity {

    TextView walletSum;
	private List<Loan> loanList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LoansAdapter mAdapter;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private View header;
    private TextView drawer_firstname_surname;
    private TextView drawer_username;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private SwipeRefreshLayout swipeRefreshLayout;
    int pastVisiblesItems, visibleItemCount, totalItemCount, pageNumber;
    private boolean loading = true;
    private TextView noLoanOnMarketMessage;
    private ImageView zonkoidWalletWarning;
    private List<MenuItem> authUserMenuItems = new ArrayList<>();
    public FloatingActionButton fabFilter;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.marketplace);
        walletSum = (TextView) toolbar.findViewById(R.id.walletSum);
        walletSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // prejit na SettingsUser, pokud nejsem prihlaseny.
                if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
                    Intent userSettingsIntent = new Intent(MainNewActivity.this, SettingsUser.class);
                    startActivity(userSettingsIntent);
                } else {
                    Intent walletIntent = new Intent(MainNewActivity.this, WalletActivity.class);
                    startActivity(walletIntent);
                }
            }
        });

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        zonkoidWalletWarning = (ImageView) toolbar.findViewById(R.id.zonkoidWalletWarning);

        setSupportActionBar(toolbar);

        initDrawer();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new LoansAdapter(getApplicationContext(), loanList);

        // refresher obsahu
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearMarketAndRefresh();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                R.color.greenLight,
                R.color.warningYellow,
                R.color.colorPrimary);

        // samotny obsah
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            EventBus.getDefault().post(new ReloadMarket.Request(
                                    ZonkySniperApplication.getInstance().showCovered(),
                                    pageNumber+=1, Constants.NUM_OF_ROWS
                            ));
                        }
                    }
                }
            }
        });

        // filter FAB
        fabFilter = (FloatingActionButton) findViewById(R.id.fab);
        fabFilter.setAlpha(0.7f);
        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog(view);
            }
        });


        header = navigationView.getHeaderView(0);
        drawer_firstname_surname = (TextView) header.findViewById(R.id.firstname_surname);
        drawer_firstname_surname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // prejit na SettingsUser, pokud nejsem prihlaseny.
                if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
                    Intent userSettingsIntent = new Intent(MainNewActivity.this, SettingsUser.class);
                    startActivity(userSettingsIntent);
                } else {
                    // TODO prechod na detail uzivatele - adresa, cislo uctu apod.
                }

            }
        });

        for(int i=0; i < navigationView.getMenu().size(); i++) {
            MenuItem tmpItem = navigationView.getMenu().getItem(i);
            if(
                    tmpItem.getItemId() == R.id.action_drawer_portfolio ||
                    tmpItem.getItemId() == R.id.action_drawer_wallet ||
                    tmpItem.getItemId() == R.id.action_drawer_messages
                    ) {
                authUserMenuItems.add(tmpItem);
            }
        }

        drawer_username = (TextView) header.findViewById(R.id.username);

        noLoanOnMarketMessage = (TextView) findViewById(R.id.noLoanOnMarketMessage);

        // pokud jeste nevidel coach mark, ukazat
        showCoachMark();
    }

    private void showFilterDialog(View view) {

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(view.getContext());

        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.filter_marketplace);
        dialog.setCanceledOnTouchOutside(true);

        Button filtrovat = (Button) dialog.findViewById(R.id.filter);

        CheckBox showCoveredCheckBox = (CheckBox) dialog.findViewById(R.id.show_covered_checkbox);
        showCoveredCheckBox.setChecked(
                ZonkySniperApplication.getInstance() != null && ZonkySniperApplication.getInstance().showCovered()
        );

        ((CheckBox) dialog.findViewById(R.id.AAAAA))
                .setChecked(sp.getBoolean(Constants.FILTER_MARKETPLACE_RATINGS + Rating.AAAAA.name(), false));
        ((CheckBox) dialog.findViewById(R.id.AAAA))
                .setChecked(sp.getBoolean(Constants.FILTER_MARKETPLACE_RATINGS + Rating.AAAA.name(), false));
        ((CheckBox) dialog.findViewById(R.id.AAA))
                .setChecked(sp.getBoolean(Constants.FILTER_MARKETPLACE_RATINGS + Rating.AAA.name(), false));
        ((CheckBox) dialog.findViewById(R.id.AA))
                .setChecked(sp.getBoolean(Constants.FILTER_MARKETPLACE_RATINGS + Rating.AA.name(), false));
        ((CheckBox) dialog.findViewById(R.id.A))
                .setChecked(sp.getBoolean(Constants.FILTER_MARKETPLACE_RATINGS + Rating.A.name(), false));
        ((CheckBox) dialog.findViewById(R.id.B))
                .setChecked(sp.getBoolean(Constants.FILTER_MARKETPLACE_RATINGS + Rating.B.name(), false));
        ((CheckBox) dialog.findViewById(R.id.C))
                .setChecked(sp.getBoolean(Constants.FILTER_MARKETPLACE_RATINGS + Rating.C.name(), false));
        ((CheckBox) dialog.findViewById(R.id.D))
                .setChecked(sp.getBoolean(Constants.FILTER_MARKETPLACE_RATINGS + Rating.D.name(), false));

        RangeBar rangebarTermInMonths = dialog.findViewById(R.id.rangebarTermInMonths);
        rangebarTermInMonths.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex, String leftPinValue, String rightPinValue) {
                sp.edit().putInt(String.valueOf(Constants.FILTER_MARKETPLACE_TERMINMONTHS_FROM), Integer.valueOf(leftPinValue)).commit();
                sp.edit().putInt(String.valueOf(Constants.FILTER_MARKETPLACE_TERMINMONTHS_TO), Integer.valueOf(rightPinValue)).commit();
            }
        });
        rangebarTermInMonths.setOnTouchListener(new RangeBar.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    clearMarketAndRefresh();
                }
                return false;
            }
        });
        rangebarTermInMonths.setRangePinsByValue(
                sp.getInt(Constants.FILTER_MARKETPLACE_TERMINMONTHS_FROM, 0),
                sp.getInt(Constants.FILTER_MARKETPLACE_TERMINMONTHS_TO, 84)
        );


//        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioStatus);
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @SuppressLint("ApplySharedPref")
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int buttonId) {
//
//                RadioButton rb = (RadioButton) radioGroup.findViewById(buttonId);
//                int index = radioGroup.indexOfChild(rb);
//
//                switch (index) {
//                    case 0:
//                        sp.edit()
//                                .putStringSet(Constants.FILTER_MYINVESTMENTS_STATUSES_NAME, new HashSet<String>(LoanStatus.names()))
//                                .putString(Constants.FILTER_MYINVESTMENTS_STATUS_EQ_NAME, null)
//                                .putBoolean(Constants.FILTER_MYINVESTMENTS_UNPAID_LAST_INSTALLMENT_NAME, false)
//                                .putInt(Constants.FILTER_MYINVESTMENTS_SET, R.id.radioAll)
//                                .commit();
//                        break;
//                    case 1:
//                        sp.edit()
//                                .putStringSet(Constants.FILTER_MYINVESTMENTS_STATUSES_NAME, new HashSet<String>(Arrays.asList(LoanStatus.ACTIVE.name(), LoanStatus.PAID_OFF.name())))
//                                .putString(Constants.FILTER_MYINVESTMENTS_STATUS_EQ_NAME, null)
//                                .putBoolean(Constants.FILTER_MYINVESTMENTS_UNPAID_LAST_INSTALLMENT_NAME, false)
//                                .putInt(Constants.FILTER_MYINVESTMENTS_SET, R.id.radioActive)
//                                .commit();
//                        break;
//                    case 2:
//                        sp.edit()
//                                .putStringSet(Constants.FILTER_MYINVESTMENTS_STATUSES_NAME, new HashSet<String>(Arrays.asList(LoanStatus.ACTIVE.name(), LoanStatus.PAID_OFF.name())))
//                                .putString(Constants.FILTER_MYINVESTMENTS_STATUS_EQ_NAME, null)
//                                .putBoolean(Constants.FILTER_MYINVESTMENTS_UNPAID_LAST_INSTALLMENT_NAME, true)
//                                .putInt(Constants.FILTER_MYINVESTMENTS_SET, R.id.radioProblem)
//                                .commit();
//                        break;
//                    case 3:
//                        sp.edit()
//                                .putStringSet(Constants.FILTER_MYINVESTMENTS_STATUSES_NAME, new HashSet<String>(Arrays.asList(LoanStatus.SIGNED.name())))
//                                .putString(Constants.FILTER_MYINVESTMENTS_STATUS_EQ_NAME, null)
//                                .putBoolean(Constants.FILTER_MYINVESTMENTS_UNPAID_LAST_INSTALLMENT_NAME, false)
//                                .putInt(Constants.FILTER_MYINVESTMENTS_SET, R.id.radioSigned)
//                                .commit();
//                        break;
//                    case 4:
//                        sp.edit()
//                                .putStringSet(Constants.FILTER_MYINVESTMENTS_STATUSES_NAME, new HashSet<String>(Arrays.asList(LoanStatus.PAID.name())))
//                                .putString(Constants.FILTER_MYINVESTMENTS_STATUS_EQ_NAME, null)
//                                .putBoolean(Constants.FILTER_MYINVESTMENTS_UNPAID_LAST_INSTALLMENT_NAME, false)
//                                .putInt(Constants.FILTER_MYINVESTMENTS_SET, R.id.radioPaid)
//                                .commit();
//                        break;
//                    case 5:
//                        // not implemented
//                        break;
//                    case 6:
//                        // not implemented
//                        break;
//                    case 7:
//                        sp.edit()
//                                .putStringSet(Constants.FILTER_MYINVESTMENTS_STATUSES_NAME, new HashSet<String>(LoanStatus.names()))
//                                .putString(Constants.FILTER_MYINVESTMENTS_STATUS_EQ_NAME, PaymentStatus.SOLD.name())
//                                .putBoolean(Constants.FILTER_MYINVESTMENTS_UNPAID_LAST_INSTALLMENT_NAME, false)
//                                .putInt(Constants.FILTER_MYINVESTMENTS_SET, R.id.radioSold)
//                                .commit();
//                        break;
//                }
//            }
//        });
//        radioGroup.check(sp.getInt(Constants.FILTER_MYINVESTMENTS_SET, 0));
//
        filtrovat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                clearMarketAndRefresh();
            }
        });

        dialog.show();

    }

    /**
     * Vycisti seznam a naloaduj uplne nacisto trziste
     *
     * Hodi se treba pri swipu, zmene zobrazeni covered pujcek apod.
     */
    private void clearMarketAndRefresh() {
        resetCounters();
        loanList.clear();
        mAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(true);
        ZonkySniperApplication.isMarketDirty = false;
        EventBus.getDefault().post(new ReloadMarket.Request(
                ZonkySniperApplication.getInstance().showCovered(),
                0, Constants.NUM_OF_ROWS
        ));
    }

    /**
     * resetuj pocitadla strankovani
     */
    private void resetCounters() {
        pastVisiblesItems = 0;
        visibleItemCount = 0;
        totalItemCount = 0;
        pageNumber = 0;
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
                    case R.id.action_drawer_marketplace:
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        return true;
                    case R.id.action_drawer_portfolio:
                        intent = new Intent(getApplicationContext(), PortfolioActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.action_drawer_wallet:
                        intent = new Intent(getApplicationContext(), WalletActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.action_drawer_messages:
                        intent = new Intent(getApplicationContext(), MessagingActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.action_drawer_help:
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.zonkoid.cz/#features"));
                        startActivity(intent);
                        return true;
                    case R.id.action_drawer_settings_user:
                        intent = new Intent(getApplicationContext(), SettingsUser.class);
                        startActivity(intent);
                        return true;
                    case R.id.action_drawer_settings_notifications:
                        intent = new Intent(getApplicationContext(), SettingsNotificationsSignpost.class);
                        startActivity(intent);
                        return true;
                }
                return true;
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v){
                    super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        TextView verze = (TextView) drawerLayout.findViewById(R.id.main_drawer_footer_version);
        verze.setText(MessageFormat.format(getString(R.string.copyright), String.valueOf(BuildConfig.VERSION_NAME)));

    }

    /**
     * Nastavi badge na polozku menu
     * @param itemId
     * @param text
     */
    private void setBadgeText(@IdRes int itemId, String text) {
        TextView view = (TextView) navigationView.getMenu().findItem(itemId).getActionView();
        view.setText(text != null ? text : "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWalletReceived(GetWallet.Response evt) {
        if(walletSum != null) {
            walletSum.setText(getString(R.string.balance) + evt.getWallet().getAvailableBalance() + getString(R.string.CZK));
            ZonkySniperApplication.wallet = evt.getWallet();
        }

        //Zapni, vypni, prepni indikaci stavu investora (DEBTOR, BLOCKED)
        toggleInvestorStatusIndicator(zonkoidWalletWarning);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInvestorDetailReceived(GetInvestor.Response evt) {

        ZonkySniperApplication.getInstance().setUser(evt.getInvestor());

        toggleAuthUserContent();

        // pocet neprectenych zprav
        Menu menu = navigationView.getMenu();
        MenuItem menu_messages = menu.findItem(R.id.action_drawer_messages);
        if(evt.getInvestor().getUnreadNotificationsCount() > 0) {
            setBadgeText(R.id.action_drawer_messages, "+"+ evt.getInvestor().getUnreadNotificationsCount());
        } else {
            setBadgeText(R.id.action_drawer_messages, "");
        }
    }

    /**
     * Pokud je uzivatel prihlaseny, povolit nektera menu a zobrazeni, jinak zamknout
     */
    private void toggleAuthUserContent() {
        for (MenuItem menuItem : authUserMenuItems) {
            menuItem.setEnabled(
                    ZonkySniperApplication.getInstance().getUser() != null);
        }

        Investor user = ZonkySniperApplication.getInstance().getUser();
        if(user != null) {
            drawer_firstname_surname.setText(
                    user.getFirstName() + " " + user.getSurname());
            drawer_username.setText(user.getUsername());
        } else {
            drawer_firstname_surname.setText(getString(R.string.not_logged_in));
            drawer_username.setText("");
            walletSum.setText(getString(R.string.not_logged_in));
        }
    }

    /**
     * Po nacteni Trziste je potreba prekreslit seznam nezainvestovanych uveru
     *
     * @param evt
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMarketReloaded(ReloadMarket.Response evt) {

        if(evt.getMarket() != null && !evt.getMarket().isEmpty()) {
            loanList.addAll(evt.getMarket());
            mAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            loading = true;
            noLoanOnMarketMessage.setVisibility(View.GONE);
        }

        // no a pokud je loanList prazdny - neni pujcka na trzisti, tak hlasku
        if(loanList.isEmpty()) {
            swipeRefreshLayout.setRefreshing(false);
            loading = true;
            noLoanOnMarketMessage.setVisibility(View.VISIBLE);
        }

        // indikuj, jestli je nastaveny filtr
        if(sp.getInt(Constants.FILTER_MYINVESTMENTS_SET, 0) != R.id.radioAll) {
            fabFilter.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)));
        } else {
            fabFilter.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.greyLighter)));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMarketReloadFailed(ReloadMarket.Failure evt) {
        if("503".equalsIgnoreCase(evt.errorCode)) {
            yellowWarning(findViewById(R.id.main_content), getString(R.string.zonkyUnavailable), Snackbar.LENGTH_LONG);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loanList.isEmpty() || ZonkySniperApplication.isMarketDirty) {
            clearMarketAndRefresh();
        }
        if (ZonkySniperApplication.getInstance().isLoginAllowed()) {
            // pouze pro zvane
            EventBus.getDefault().post(new GetWallet.Request());
            if(ZonkySniperApplication.user != null) {
                onInvestorDetailReceived(new GetInvestor.Response(ZonkySniperApplication.user));
            }
        }

        toggleAuthUserContent();

        drawerToggle.syncState();

        /**
         * Zapni, vypni, prepni indikaci stavu investora (DEBTOR, BLOCKED)
         */
        toggleInvestorStatusIndicator(zonkoidWalletWarning);

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
     * Ve filtru po zaskrtnuti checkboxu s ratingem se ulozi hodnota
     * @param view
     */
    public void saveMarketplaceFilterRatingValue(View view) {
        CheckBox checkBox = ((CheckBox) view);
        sp.edit().putBoolean(String.valueOf(Constants.FILTER_MARKETPLACE_RATINGS + checkBox.getTag()), checkBox.isChecked()).commit();
        clearMarketAndRefresh();
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
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
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
        // musime udelat commin a ne apply, protoze hned nato reloadujeme market a nechceme riskovat :]
        sp.edit().putBoolean(Constants.SHARED_PREF_SHOW_COVERED, ((CheckBox) view).isChecked()).commit();
        clearMarketAndRefresh();
    }

    /**
     * Zobrazit uvodni napovedu
     */
    public void showCoachMark() {

        // rozhodnout, jestli zobrazim nebo jestli uz videl
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        if(sp.getString(Constants.SHARED_PREF_COACHMARK_VERSION_READ, "").equals(BuildConfig.VERSION_NAME)) {
            return;
        }

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
        dialog.setContentView(R.layout.coach_mark);
        dialog.setCanceledOnTouchOutside(false);

        Button nastavit = (Button) dialog.findViewById(R.id.nastavit);
        nastavit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // oznacit jako prectene
                sp.edit().putString(Constants.SHARED_PREF_COACHMARK_VERSION_READ, BuildConfig.VERSION_NAME).apply();

                dialog.dismiss();
            }
        });
//
//        Button skryt = (Button) dialog.findViewById(R.id.readmore);
//
//        skryt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showCoachMark2();
//                dialog.dismiss();
//            }
//        });

        dialog.show();
    }
}
