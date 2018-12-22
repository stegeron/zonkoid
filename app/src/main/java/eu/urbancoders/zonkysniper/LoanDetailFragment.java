package eu.urbancoders.zonkysniper;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.dataobjects.Rating;
import eu.urbancoders.zonkysniper.events.GetInvestorRestrictions;
import eu.urbancoders.zonkysniper.events.GetLoanDetail;
import eu.urbancoders.zonkysniper.events.GetWallet;
import eu.urbancoders.zonkysniper.investing.InvestingActivity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    TextView insurance, reservedOnly;
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

        header = rootView.findViewById(R.id.header);
        storyName = rootView.findViewById(R.id.storyName);
        storyImage = rootView.findViewById(R.id.storyImage);

        invested = rootView.findViewById(R.id.invested);
        zbyva = rootView.findViewById(R.id.zbyva);
        income = rootView.findViewById(R.id.income);
        region = rootView.findViewById(R.id.region);

        interestRate = rootView.findViewById(R.id.interestRate);

        covered = rootView.findViewById(R.id.covered);

        insurance = rootView.findViewById(R.id.insurance);
        reservedOnly = rootView.findViewById(R.id.reservedOnly);

        investingPanel = rootView.findViewById(R.id.investingPanel);

        loanId = (int) getArguments().getSerializable("loanId");
        presetAmount = (double) getArguments().getSerializable("presetAmount");
//        EventBus.getDefault().post(new GetWallet.Request());
        EventBus.getDefault().post(new GetLoanDetail.Request(loanId));

        // pokud jeste neni nactena max castka, nacti
        if(ZonkySniperApplication.getInstance().getUser() != null
                && ZonkySniperApplication.getInstance().getUser().getMaximumInvestmentAmount() == 0) {
            EventBus.getDefault().post(new GetInvestorRestrictions.Request());
        }

        return rootView;
    }

    private void prepareInvestingButtons(LinearLayout investingPanel) {
        for (int i = 200; i <= 5000; i += 200) {
            Button but = investingPanel.findViewWithTag("button_" + i);
            initButtonFunctions(but, i);
        }

        if(ZonkySniperApplication.getInstance().getUser() != null
                && ZonkySniperApplication.getInstance().getUser().getMaximumInvestmentAmount() >= 10000) {
            for (int i = 6000; i <= 10000; i += 1000) {
                Button but = investingPanel.findViewWithTag("button_" + i);
                initButtonFunctions(but, i);
            }
        }

        if(ZonkySniperApplication.getInstance().getUser() != null
                && ZonkySniperApplication.getInstance().getUser().getMaximumInvestmentAmount() >= 20000) {
            for (int i = 12000; i <= 20000; i += 2000) {
                Button but = investingPanel.findViewWithTag("button_" + i);
                initButtonFunctions(but, i);
            }
        }
    }

    private void initButtonFunctions(Button but, int i) {
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
                        yellowWarning(getView(), String.format(getString(R.string.already_invested_or_less), toInvest), Snackbar.LENGTH_LONG);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWalletReceived(GetWallet.Response evt) {
        if (LoanDetailsActivity.walletSum != null) {
            LoanDetailsActivity.walletSum.setText(getString(R.string.balance) + evt.getWallet().getAvailableBalance() + getString(R.string.CZK));
            ZonkySniperApplication.wallet = evt.getWallet();
        }

        // refreshni tlacitka pro investovani
        prepareInvestingButtons(investingPanel);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInvestorRestrictionsReceived(GetInvestorRestrictions.Response evt) {
        ZonkySniperApplication.getInstance().getUser().setMaximumInvestmentAmount(
                evt.getRestrictions().getMaximumInvestmentAmount()
        );
        prepareInvestingButtons(investingPanel);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
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

        // pojisteno?
        if(loan.isInsuranceActive()) {
            insurance.setVisibility(View.VISIBLE);
        } else {
            insurance.setVisibility(View.INVISIBLE);
        }

        // reserve?
        if(loan.getRemainingInvestment() - loan.getReservedAmount() == 0) {
            reservedOnly.setVisibility(View.VISIBLE);
        } else {
            reservedOnly.setVisibility(View.INVISIBLE);
        }
    }
}
