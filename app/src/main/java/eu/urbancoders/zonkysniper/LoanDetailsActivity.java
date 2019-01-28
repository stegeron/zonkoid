package eu.urbancoders.zonkysniper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZSViewActivity;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.events.GetLoanDetail;
import eu.urbancoders.zonkysniper.events.GetWallet;
import eu.urbancoders.zonkysniper.events.Invest;
import eu.urbancoders.zonkysniper.events.ReloadMarket;
import eu.urbancoders.zonkysniper.integration.ZonkyClient;
import eu.urbancoders.zonkysniper.loandetail.CalculationFragment;
import eu.urbancoders.zonkysniper.questions.QuestionsEditFragment;
import eu.urbancoders.zonkysniper.questions.QuestionsFragment;
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
public class LoanDetailsActivity extends ZSViewActivity {
    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    static Activity activity;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public static TextView walletSum;
    protected int loanId;
    protected double presetAmount;
    Loan loan;
    private Toolbar toolbar;
    private ImageView headerImage;
    public FloatingActionButton fab;
    private ImageView zonkoidWalletWarning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_details);

        activity = this;

        // ziskej loan z extras
        Intent intent = getIntent();
        if("OPEN_LOAN_DETAIL_FROM_NOTIFICATION".equalsIgnoreCase(intent.getAction())) {
            loanId = Integer.valueOf(intent.getStringExtra("loanId"));
            if(intent.getStringExtra("presetAmount") != null) {
                presetAmount = Double.valueOf(intent.getStringExtra("presetAmount"));
            }
        } else {
            loanId = intent.getIntExtra("loanId",
                    ZonkySniperApplication.getInstance().getCurrentLoanId() != null ? ZonkySniperApplication.getInstance().getCurrentLoanId() : 0);
        }

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        headerImage = findViewById(R.id.headerImage);

        walletSum = toolbar.findViewById(R.id.walletSum);
        walletSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // prejit na SettingsUser, pokud nejsem prihlaseny.
                if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
                    Intent userSettingsIntent = new Intent(LoanDetailsActivity.this, SettingsUser.class);
                    startActivity(userSettingsIntent);
                } else {
                    Intent walletIntent = new Intent(LoanDetailsActivity.this, WalletActivity.class);
                    startActivity(walletIntent);
                }
            }
        });

        zonkoidWalletWarning = findViewById(R.id.zonkoidWalletWarning);

        EventBus.getDefault().post(new GetWallet.Request());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // init fab button
        fab = findViewById(R.id.fab);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {


            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 3:
                        prepareFabQuestions();
                        break;
                    default:
                        fab.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // reklama
        mAdView = findViewById(R.id.adView);
        initAndLoadAd(mAdView);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        TabLayout.Tab tab = tabLayout.getTabAt(getIntent().getIntExtra("tab", 0));
        tab.select();
    }

    /**
     * Pro tab s dotazy pripravi tlacitko pro pridani dotazu
     */
    private void prepareFabQuestions() {
        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            fab.setVisibility(View.INVISIBLE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }

        fab.setImageResource(R.drawable.ic_comment);
        fab.setAlpha(0.7f);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment questionsEditFragment = QuestionsEditFragment.newInstance(null, loan);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_questions, questionsEditFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWalletReceived(GetWallet.Response evt) {
        if (walletSum != null) {
            walletSum.setText(getString(R.string.balance) + evt.getWallet().getAvailableBalance() + getString(R.string.CZK));
            ZonkySniperApplication.wallet = evt.getWallet();
//            walletSum.setShadowLayer(1, 1, 1, Color.BLACK);
        }

        //Zapni, vypni, prepni indikaci stavu investora (DEBTOR, BLOCKED)
        toggleInvestorStatusIndicator(zonkoidWalletWarning);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoanDetailReceived(GetLoanDetail.Response evt) {
        loan = evt.getLoan();
        if(loan != null && loan.getPhotos() != null && loan.getPhotos().size() > 0) {
            Picasso.with(ZonkySniperApplication.getInstance().getApplicationContext())
                    .load(ZonkyClient.BASE_URL + evt.getLoan().getPhotos().get(0).getUrl())
                    .into(headerImage);

            // pokud je pujcka zainvestovana a mam v ni podil, pak nacist prehled splatek
            if(loan.isCovered() && loan.getMyInvestment() != null) {
                mSectionsPagerAdapter.showRepaymentsFragment();
//                FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
//                RepaymentsFragment frag = RepaymentsFragment.newInstance(loan);
//                ft.replace(, frag);
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                ft.addToBackStack(null);
//                ft.commit();
            }
        } else {
            Picasso.with(ZonkySniperApplication.getInstance().getApplicationContext())
                    .load(R.mipmap.default_story_picture)
                    .into(headerImage);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInvestError(Invest.Failure evt) {
        String errorDesc = ZonkySniperApplication.getInstance().translateError(evt.getDesc());
        yellowWarning(walletSum.getRootView(), errorDesc, Snackbar.LENGTH_INDEFINITE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInvested(Invest.Response noMeaning) {
        // bude potreba prenacist trziste
        ZonkySniperApplication.isMarketDirty = true;
        whiteMessage(walletSum.getRootView(), getString(R.string.investedOk));
        EventBus.getDefault().post(new GetLoanDetail.Request(loanId));
        EventBus.getDefault().post(new GetWallet.Request());
        EventBus.getDefault().post(new ReloadMarket.Request(ZonkySniperApplication.getInstance().showCovered(), 0, Constants.NUM_OF_ROWS));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

//        getMenuInflater().inflate(R.menu.menu_loan_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Loan getLoan() {
        return loan;
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private FragmentManager fm;
        private Fragment fragmentAtPos1;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return LoanDetailFragment.newInstance(loanId, presetAmount);
                case 1:
                    if (fragmentAtPos1 == null) {
                        fragmentAtPos1 = CalculationFragment.newInstance(loan);
                    }
                    return fragmentAtPos1;
                case 2:
                    return StoryFragment.newInstance(loan);
                case 3:
                    return QuestionsFragment.newInstance(loan);
                default:
                    return InvestorsFragment.newInstance(loanId);
            }
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Půjčka";
                case 1:
                    return "Výnos";
                case 2:
                    return "Příběh";
                case 3:
                    return "Dotazy";
                case 4:
                    return "Investoři";
            }
            return null;
        }

        public void showRepaymentsFragment() {
//            fm.beginTransaction().remove(fragmentAtPos1).commit();
//            fragmentAtPos1 = RepaymentsFragment.newInstance(loan);
//            notifyDataSetChanged();
        }
    }

    /**
     * Tohle se zavola, kdyz chci novy dotaz odeslat
     * @param view
     */
    public void sendNewQuestion(View view) {
    }
}
