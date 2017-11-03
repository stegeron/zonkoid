package eu.urbancoders.zonkysniper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

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
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LoanDetailsActivity extends ZSViewActivity {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        headerImage = (ImageView) findViewById(R.id.headerImage);

        walletSum = (TextView) toolbar.findViewById(R.id.walletSum);
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

        zonkoidWalletWarning = (ImageView) findViewById(R.id.zonkoidWalletWarning);

        EventBus.getDefault().post(new GetWallet.Request());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // init fab button
        fab = (FloatingActionButton) findViewById(R.id.fab);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
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

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
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

            // nazev je moc dlouhy a vypada to blbe...
//            toolbar.setTitle(evt.getLoan().getName());
        } else {
            Picasso.with(ZonkySniperApplication.getInstance().getApplicationContext())
                    .load(R.mipmap.default_story_picture)
                    .into(headerImage);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInvestError(Invest.Failure evt) {
        // TODO preklad tyhle chybove hlasky predelat poradne! Na lepsi misto! Nejlip do ZonkyAPIErrorNecoMejkr
        String errorDesc = evt.getDesc();
        if("multipleInvestment".equalsIgnoreCase(errorDesc)) {
            errorDesc = getString(R.string.multipleInvestment);
        } else if("alreadyCovered".equalsIgnoreCase(errorDesc)) {
            errorDesc = getString(R.string.alreadyCovered);
        } else if("tooLowIncrease".equalsIgnoreCase(errorDesc)) {
            errorDesc = getString(R.string.tooLowIncreaseInvestment);
        } else if("insufficientBalance".equalsIgnoreCase(errorDesc)) {
            errorDesc = getString(R.string.not_enough_cash);
        } else if("invalidStatus".equalsIgnoreCase(errorDesc)) {
            errorDesc = getString(R.string.invalidStatusOfLoan);
        } else if("unauthorized".equalsIgnoreCase(errorDesc)) {
            if(ZonkySniperApplication.getInstance().getUser() != null
                    && !ZonkySniperApplication.getInstance().getUser().getRoles().contains("ROLE_INVESTOR")) {
                errorDesc = getString(R.string.unauthorized_new_investor);
            } else {
                errorDesc = getString(R.string.unauthorized);
            }
        }
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

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                return LoanDetailFragment.newInstance(loanId, presetAmount);
            } else if (position == 1) {
                return CalculationFragment.newInstance(loan);
            } else if (position == 2) {
                return StoryFragment.newInstance(loan);
            } else if(position == 3) {
                return QuestionsFragment.newInstance(loan);
            } else {
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
    }

    /**
     * Tohle se zavola, kdyz chci novy dotaz odeslat
     * @param view
     */
    public void sendNewQuestion(View view) {
    }
}
