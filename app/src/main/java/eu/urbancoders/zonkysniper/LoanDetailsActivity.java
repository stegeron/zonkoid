package eu.urbancoders.zonkysniper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import eu.urbancoders.zonkysniper.core.ZSViewActivity;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.events.GetLoanDetail;
import eu.urbancoders.zonkysniper.events.GetWallet;
import eu.urbancoders.zonkysniper.integration.ZonkyClient;
import eu.urbancoders.zonkysniper.questions.QuestionsEditFragment;
import eu.urbancoders.zonkysniper.questions.QuestionsFragment;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public static TextView walletSum;
    protected int loanId;
    Loan loan;
    private Toolbar toolbar;
    private ImageView headerImage;
    public FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_details);

        // ziskej loan z extras
        Intent intent = getIntent();
        if("OPEN_LOAN_DETAIL_FROM_NOTIFICATION".equalsIgnoreCase(intent.getAction())) {
            loanId = Integer.valueOf(intent.getStringExtra("loanId"));
        } else {
            loanId = intent.getIntExtra("loanId", 0);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        headerImage = (ImageView) findViewById(R.id.headerImage);

        walletSum = (TextView) toolbar.findViewById(R.id.walletSum);
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
                    case 2:
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

    @Subscribe
    public void onWalletReceived(GetWallet.Response evt) {
        if (walletSum != null) {
            walletSum.setText(getString(R.string.balance) + evt.getWallet().getAvailableBalance() + getString(R.string.CZK));
            ZonkySniperApplication.wallet = evt.getWallet();
//            walletSum.setShadowLayer(1, 1, 1, Color.BLACK);
        }
    }

    @Subscribe
    public void onLoanDetailReceived(GetLoanDetail.Response evt) {
        loan = evt.getLoan();
        if(loan != null) {
            Picasso.with(ZonkySniperApplication.getInstance().getApplicationContext())
                    .load(ZonkyClient.BASE_URL + evt.getLoan().getPhotos().get(0).getUrl())
                    .into(headerImage);

            toolbar.setTitle(evt.getLoan().getName());
        }
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
                return LoanDetailFragment.newInstance(loanId);
            } else if (position == 1) {
                return StoryFragment.newInstance(""); // TODO az budeme umet posilat loan, tak sem vytahnem story
            } else if(position == 2) {
                return QuestionsFragment.newInstance(loanId);
            } else {
                return InvestorsFragment.newInstance(loanId);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Parametry půjčky";
                case 1:
                    return "Příběh";
                case 2:
                    return "Dotazy";
                case 3:
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
