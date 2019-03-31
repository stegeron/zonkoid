package eu.urbancoders.zonkysniper;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.IdRes;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.appyvet.materialrangebar.RangeBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZSViewActivity;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Investor;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.dataobjects.Rating;
import eu.urbancoders.zonkysniper.events.GetInvestor;
import eu.urbancoders.zonkysniper.events.GetInvestorRestrictions;
import eu.urbancoders.zonkysniper.events.GetWallet;
import eu.urbancoders.zonkysniper.events.ReloadMarket;
import eu.urbancoders.zonkysniper.events.UpdateMandatoryWarning;
import eu.urbancoders.zonkysniper.messaging.MessagingActivity;
import eu.urbancoders.zonkysniper.portfolio.PortfolioActivity;
import eu.urbancoders.zonkysniper.wallet.WalletActivity;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Copyright 2019
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.marketplace);
        walletSum = toolbar.findViewById(R.id.walletSum);
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

        zonkoidWalletWarning = toolbar.findViewById(R.id.zonkoidWalletWarning);

        setSupportActionBar(toolbar);

        initDrawer();

        recyclerView = findViewById(R.id.recycler_view);

        mAdapter = new LoansAdapter(getApplicationContext(), loanList);

        // refresher obsahu
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
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

                Loan loan;
                try {
                    loan = loanList.get(position);
                } catch (ArrayIndexOutOfBoundsException aibe) {
                    Log.e(TAG, "Kliknuti na pujcku vyhodilo chybu.");
                    return;
                }
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
        fabFilter = findViewById(R.id.fab);
        fabFilter.setAlpha(0.7f);
        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog(view);
            }
        });


        header = navigationView.getHeaderView(0);
        drawer_firstname_surname = header.findViewById(R.id.firstname_surname);
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
            // pro podmenu Nastavení
            if(tmpItem.getSubMenu() != null) {
                SubMenu tmpSubmenu = tmpItem.getSubMenu();
                for(int s=0; s < tmpSubmenu.size(); s++) {
                    if(
                            tmpSubmenu.getItem(s).getItemId() == R.id.action_drawer_settings_autoinvest
                            ) {
                        authUserMenuItems.add(tmpSubmenu.getItem(s));
                    }
                }
            }
        }

        drawer_username = header.findViewById(R.id.username);

        noLoanOnMarketMessage = findViewById(R.id.noLoanOnMarketMessage);

        // reklama
        mAdView = findViewById(R.id.adView);
        initAndLoadAd(mAdView);

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

        Button filtrovat = dialog.findViewById(R.id.filter);

        CheckBox showCoveredCheckBox = dialog.findViewById(R.id.show_covered_checkbox);
        showCoveredCheckBox.setChecked(
                ZonkySniperApplication.getInstance() != null && ZonkySniperApplication.getInstance().showCovered()
        );

        CheckBox showInsuredOnlyCheckBox = dialog.findViewById(R.id.show_insured_checkbox);
        showInsuredOnlyCheckBox.setChecked(
                sp.getBoolean(Constants.SHARED_PREF_SHOW_INSURED_ONLY, false)
        );

        CheckBox showReservedCheckBox = dialog.findViewById(R.id.show_reserved_checkbox);
        showReservedCheckBox.setChecked(
                sp.getBoolean(Constants.SHARED_PREF_SHOW_RESERVED, true)
        );

        ((CheckBox) dialog.findViewById(R.id.AAAAAA))
                .setChecked(sp.getBoolean(Constants.FILTER_MARKETPLACE_RATINGS + Rating.AAAAAA.name(), false));
        ((CheckBox) dialog.findViewById(R.id.AAAAA))
                .setChecked(sp.getBoolean(Constants.FILTER_MARKETPLACE_RATINGS + Rating.AAAAA.name(), false));
        ((CheckBox) dialog.findViewById(R.id.AAAA))
                .setChecked(sp.getBoolean(Constants.FILTER_MARKETPLACE_RATINGS + Rating.AAAA.name(), false));
        ((CheckBox) dialog.findViewById(R.id.AAA))
                .setChecked(sp.getBoolean(Constants.FILTER_MARKETPLACE_RATINGS + Rating.AAA.name(), false));
        ((CheckBox) dialog.findViewById(R.id.AAE))
                .setChecked(sp.getBoolean(Constants.FILTER_MARKETPLACE_RATINGS + Rating.AAE.name(), false));
        ((CheckBox) dialog.findViewById(R.id.AA))
                .setChecked(sp.getBoolean(Constants.FILTER_MARKETPLACE_RATINGS + Rating.AA.name(), false));
        ((CheckBox) dialog.findViewById(R.id.AE))
                .setChecked(sp.getBoolean(Constants.FILTER_MARKETPLACE_RATINGS + Rating.AE.name(), false));
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
                Integer pinRightValue = Integer.valueOf(rightPinValue);
                if(pinRightValue > Constants.REPAYMENTS_MONTHS_TO) {
                    pinRightValue = Constants.REPAYMENTS_MONTHS_TO;
                }
                Integer pinLeftValue = Integer.valueOf(leftPinValue);
                if(pinLeftValue < Constants.REPAYMENTS_MONTHS_FROM) {
                    pinLeftValue = Constants.REPAYMENTS_MONTHS_FROM;
                }
                sp.edit().putInt(String.valueOf(Constants.FILTER_MARKETPLACE_TERMINMONTHS_FROM), pinLeftValue).commit();
                sp.edit().putInt(String.valueOf(Constants.FILTER_MARKETPLACE_TERMINMONTHS_TO), pinRightValue).commit();
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

        Integer pinLeftValue = sp.getInt(Constants.FILTER_MARKETPLACE_TERMINMONTHS_FROM, Constants.REPAYMENTS_MONTHS_FROM);
        if(pinLeftValue < Constants.REPAYMENTS_MONTHS_FROM) {
            pinLeftValue = Constants.REPAYMENTS_MONTHS_FROM;
        }
        Integer pinRightValue = sp.getInt(Constants.FILTER_MARKETPLACE_TERMINMONTHS_TO, Constants.REPAYMENTS_MONTHS_TO);
        if(pinRightValue > Constants.REPAYMENTS_MONTHS_TO) {
            pinRightValue = Constants.REPAYMENTS_MONTHS_TO;
        }
        rangebarTermInMonths.setRangePinsByValue(pinLeftValue, pinRightValue);

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
        navigationView = findViewById(R.id.nav_view);
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
                    case R.id.action_drawer_settings_autoinvest:
                        intent = new Intent(getApplicationContext(), SettingsAutoinvest.class);
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

        drawerLayout = findViewById(R.id.drawer_layout);
        final ImageView updateIcon = ((NavigationView)drawerLayout.findViewById(R.id.nav_view))
                .getHeaderView(0).findViewById(R.id.updateWarning);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v){
                super.onDrawerOpened(v);
                final String newVersion = sp.getString(Constants.UPDATE_AVAILABLE_VERSION, null);
                if(newVersion != null) {
                    updateIcon.setVisibility(View.VISIBLE);
                    updateIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showMandatoryUpgradeWarning(new UpdateMandatoryWarning.Request(newVersion));
                        }
                    });
                    animateUpdateWarning();
                }
            }

            public void animateUpdateWarning() {
                int colorFrom = getResources().getColor(R.color.white);
                int colorTo = getResources().getColor(R.color.greyLight);
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                colorAnimation.setDuration(500); // milliseconds
                colorAnimation.setRepeatCount(ValueAnimator.INFINITE);
                colorAnimation.setRepeatMode(ValueAnimator.REVERSE);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        updateIcon.setColorFilter((int) animator.getAnimatedValue(), PorterDuff.Mode.SRC_ATOP);
                    }
                });
                colorAnimation.start();
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        TextView verze = drawerLayout.findViewById(R.id.main_drawer_footer_version);
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

        // pokud jeste neni nactena max castka, nacti
        if(ZonkySniperApplication.getInstance().getUser().getMaximumInvestmentAmount() == 0) {
            EventBus.getDefault().post(new GetInvestorRestrictions.Request());
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

        fabFilter.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)));
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

    public void showInsuredOnlyChecked(View view) {
        Log.i(TAG, "Show insured only checkbox clicked and isChecked = "+((CheckBox) view).isChecked());
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        // musime udelat commin a ne apply, protoze hned nato reloadujeme market a nechceme riskovat :]
        sp.edit().putBoolean(Constants.SHARED_PREF_SHOW_INSURED_ONLY, ((CheckBox) view).isChecked()).commit();
        clearMarketAndRefresh();
    }

    public void showReservedChecked(View view) {
        Log.i(TAG, "Show reserved checkbox clicked and isChecked = "+((CheckBox) view).isChecked());
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        // musime udelat commin a ne apply, protoze hned nato reloadujeme market a nechceme riskovat :]
        sp.edit().putBoolean(Constants.SHARED_PREF_SHOW_RESERVED, ((CheckBox) view).isChecked()).commit();
        clearMarketAndRefresh();
    }

    /**
     * Zobrazit uvodni napovedu
     */
    public void showCoachMark() {

        if(true) // TODO v teto verzi je vypnuty coachmark
            return;

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

//        Button nastavit = dialog.findViewById(R.id.settings_autoinvest);
//        nastavit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // oznacit jako prectene
//                sp.edit().putString(Constants.SHARED_PREF_COACHMARK_VERSION_READ, BuildConfig.VERSION_NAME).apply();
//                dialog.dismiss();
//
//                Intent settingsAutoinvest = new Intent(v.getContext(), SettingsAutoinvest.class);
//                startActivity(settingsAutoinvest);
//            }
//        });
//
        Button skryt = (Button) dialog.findViewById(R.id.zavrit);
        skryt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().putString(Constants.SHARED_PREF_COACHMARK_VERSION_READ, BuildConfig.VERSION_NAME).apply();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
