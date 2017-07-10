package eu.urbancoders.zonkysniper;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.core.ZSViewActivity;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Investor;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.dataobjects.Rating;
import eu.urbancoders.zonkysniper.events.GetLoanDetail;
import eu.urbancoders.zonkysniper.events.GetWallet;
import eu.urbancoders.zonkysniper.events.Invest;
import eu.urbancoders.zonkysniper.events.ReloadMarket;
import eu.urbancoders.zonkysniper.events.UnresolvableError;
import eu.urbancoders.zonkysniper.investing.InvestingActivity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 26.08.2016
 */
public class LoanDetailFragment extends ZSFragment {

    Loan loan;
    int loanId;
    double presetAmount;
    static final int INVESTING_ACTIVITY = 1;

    TextView header;
    TextView storyName;
    TextView invested;
    TextView zbyva;
    TextView income;
    TextView region;
    ImageView storyImage;
    TextView interestRate;
    ImageView covered;
    LinearLayout investingPanel;
    static LoanDetailFragment fragment;

    public LoanDetailFragment() {
    }

    public static LoanDetailFragment newInstance(int loanId, double presetAmount) {
        fragment = new LoanDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("loanId", loanId);
        args.putSerializable("presetAmount", presetAmount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_loan_details, container, false);

        header = (TextView) rootView.findViewById(R.id.header);
        storyName = (TextView) rootView.findViewById(R.id.storyName);
        storyImage = (ImageView) rootView.findViewById(R.id.storyImage);

        invested = (TextView) rootView.findViewById(R.id.invested);
        zbyva = (TextView) rootView.findViewById(R.id.zbyva);
        income = (TextView) rootView.findViewById(R.id.income);
        region = (TextView) rootView.findViewById(R.id.region);

        interestRate = (TextView) rootView.findViewById(R.id.interestRate);

        covered = (ImageView) rootView.findViewById(R.id.covered);

        investingPanel = (LinearLayout) rootView.findViewById(R.id.investingPanel);

        loanId = (int) getArguments().getSerializable("loanId");
        presetAmount = (double) getArguments().getSerializable("presetAmount");
//        EventBus.getDefault().post(new GetWallet.Request());
        EventBus.getDefault().post(new GetLoanDetail.Request(loanId));

        return rootView;
    }

    private void prepareInvestingButtons(LinearLayout investingPanel) {

//        if (ZonkySniperApplication.getInstance().getRemoteConfig().getLong(Constants.FORCED_VERSION_CODE) > BuildConfig.VERSION_CODE) {
//                // toxdo fejk !!!!!!!!!!!!!!!!!!!!!!!!!!!!11
//            Snackbar.make(investingPanel, "Nutno upgradovat", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
//        }

        for (int i = 200; i <= 5000; i += 200) {
            Button but = (Button) investingPanel.findViewWithTag("button_" + i);
            if (ZonkySniperApplication.wallet != null && ZonkySniperApplication.wallet.getAvailableBalance() >= i) {
                if(presetAmount == i) {
                    but.setBackgroundResource(R.drawable.invest_button_enabled_preset_amount);
                } else {
                    but.setBackgroundResource(R.drawable.invest_button_enabled);
                }
                // navesit investovani
                final int toInvest = i;
                but.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {

                        if(loan.getRemainingInvestment() < toInvest) {
                            // hlasku o tom, ze k investici zbyva mene, nez chcete investovat
                            Snackbar.make(view, String.format(getString(R.string.already_invested_or_less), toInvest)
                                    , Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else if (ZonkySniperApplication.getInstance().isLoginAllowed()
                                && ZonkySniperApplication.getInstance().getUser() != null
                                && ZonkySniperApplication.getInstance().getUser().getZonkyCommanderStatus() == Investor.Status.BLOCKED) {
                            /**
                             * Blokni investovani, pokud investor nezaplatil
                             */
                            yellowWarning(getView(), getString(R.string.investor_blocked), Snackbar.LENGTH_INDEFINITE);
                        } else {
                            Intent detailIntent = new Intent(ZonkySniperApplication.getInstance().getApplicationContext(), InvestingActivity.class);
                            detailIntent.putExtra("loan", loan);
                            detailIntent.putExtra("amount", toInvest);
                            startActivityForResult(detailIntent, INVESTING_ACTIVITY);
                        }

                    }
                });
            } else {
                but.setBackgroundResource(R.drawable.invest_button_disabled);

                // neni mozne investovani, nejak se zachovej - hlaska o nedostatku prostredku nebo redirect na zonky
                but.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        // dialog, jestli chci investovat, presun na web pro lamy.
                        // pokud se nemuze prihlasit, neumozni investovani, ale prechod na zonky.cz
                        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
                            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://app.zonky.cz/#/marketplace/detail/" + loan.getId() + "/"));
                            if(fragment.isAdded()) {
                                fragment.startActivity(webIntent);
                            }
                        } else {
                            yellowWarning(view, getString(R.string.not_enough_cash), Snackbar.LENGTH_LONG);
                        }
                    }
                });
            }
        }
    }

    @Subscribe
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
        }
        displayInvestingStatus(fragment.getView(), errorDesc);
    }

    @Subscribe
    public void onInvested(Invest.Response noMeaning) {
        // bude potreba prenacist trziste
        ZonkySniperApplication.isMarketDirty = true;
        displayInvestingStatus(fragment.getView(), getString(R.string.investedOk));
        EventBus.getDefault().post(new GetLoanDetail.Request(loanId));
        EventBus.getDefault().post(new GetWallet.Request());
        EventBus.getDefault().post(new ReloadMarket.Request(ZonkySniperApplication.getInstance().showCovered(), 0, Constants.NUM_OF_ROWS));
    }

    @Subscribe
    public void onWalletReceived(GetWallet.Response evt) {
        if (LoanDetailsActivity.walletSum != null) {
            LoanDetailsActivity.walletSum.setText(getString(R.string.balance) + evt.getWallet().getAvailableBalance() + getString(R.string.CZK));
            ZonkySniperApplication.wallet = evt.getWallet();
        }

        // refreshni tlacitka pro investovani
        prepareInvestingButtons(investingPanel);
    }

    public void displayInvestingStatus(View view, final String message) {
        android.app.AlertDialog.Builder statusDialog = new android.app.AlertDialog.Builder(getContext());
        statusDialog.setMessage(message);
        statusDialog.setCancelable(false);

        statusDialog.setNeutralButton(
                R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        android.app.AlertDialog alert = statusDialog.create();
        alert.show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loan = null;
        loanId = 0;
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onLoanDetailReceived(GetLoanDetail.Response evt) {

        if (evt.getLoan() == null) {
            return;
        }

        loan = evt.getLoan();

        header.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(loan.getAmount()) + " Kč na "
                + loan.getTermInMonths() + " měsíců");
        header.setTextColor(Color.parseColor(Rating.getColor(loan.getRating())));

        storyName.setText(loan.getName());

        income.setText(
                getResources().getString(getResources().getIdentifier("income_"+loan.getMainIncomeType(), "string", getContext().getPackageName())) + ", "
        );

        region.setText(getResources().getString(getResources().getIdentifier("region_"+loan.getRegion(), "string", getContext().getPackageName())));

        if(loan.isCovered()) {
            // pujcka je pokryta, investicni tlacitka nezobrazovat
            zbyva.setText(getText(R.string.covered));
            covered.setVisibility(View.VISIBLE);

            investingPanel.setVisibility(View.INVISIBLE);
        } else {
            zbyva.setText("Zbývá " + Constants.FORMAT_NUMBER_NO_DECIMALS.format(loan.getRemainingInvestment()) + " Kč");
            // pripravit a zobrazit investicni tlacitka
            prepareInvestingButtons(investingPanel);
            investingPanel.setVisibility(View.VISIBLE);
//            konec.setText("Konec " + Constants.DATE_DD_MM_YYYY_HH_MM.format(loan.getDeadline()));
        }

        // mam investovano
        if (loan.getMyInvestment() != null) {
            invested.setText(String.format(getContext().getString(R.string.myInvestment), loan.getMyInvestment().getAmount()));
            invested.setVisibility(View.VISIBLE);
        } else {
            invested.setText("");
            invested.setVisibility(View.GONE);
        }

        // vybarvena urokova sazba
        interestRate.setText(Rating.getDesc(loan.getRating()) + " | " + new DecimalFormat("#.##").format(loan.getInterestRate() * 100) + "%");
        interestRate.setTextColor(Color.parseColor(Rating.getColor(loan.getRating())));
    }
}
