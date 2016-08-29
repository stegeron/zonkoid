package eu.urbancoders.zonkysniper;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.dataobjects.MyInvestment;
import eu.urbancoders.zonkysniper.dataobjects.Rating;
import eu.urbancoders.zonkysniper.events.GetLoanDetail;
import eu.urbancoders.zonkysniper.events.GetWallet;
import eu.urbancoders.zonkysniper.events.Invest;
import eu.urbancoders.zonkysniper.events.ReloadMarket;
import eu.urbancoders.zonkysniper.integration.ZonkyClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 26.08.2016
 */
public class LoanDetailFragment extends Fragment {

    static Loan loan;
    int loanId;

    TextView header;
    TextView storyName;
    TextView konec;
    TextView zbyva;
    TextView investice;
    ImageView storyImage;
    TextView interestRate;
    LinearLayout ip;
    static LoanDetailFragment fragment;

    public LoanDetailFragment() {
    }

    public static LoanDetailFragment newInstance(int loanId) {
        fragment = new LoanDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("loanId", loanId);
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

        konec = (TextView) rootView.findViewById(R.id.konec);
        zbyva = (TextView) rootView.findViewById(R.id.zbyva);
        investice = (TextView) rootView.findViewById(R.id.investice);

        interestRate = (TextView) rootView.findViewById(R.id.interestRate);

        ip = (LinearLayout) rootView.findViewById(R.id.investingPanel);
        prepareInvestingButtons(ip);

        loanId = (int) getArguments().getSerializable("loanId");
        EventBus.getDefault().post(new GetLoanDetail.Request(loanId));

        return rootView;
    }

    private void prepareInvestingButtons(LinearLayout investingPanel) {
        for (int i = 200; i <= 5000; i += 200) {
            Button but = (Button) investingPanel.findViewWithTag("button_" + i);
            if (ZonkySniperApplication.wallet != null && ZonkySniperApplication.wallet.getAvailableBalance() >= i) {
                but.setBackgroundResource(R.drawable.invest_button_enabled);
                // navesit investovani
                final int toInvest = i;
                but.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        AlertDialog.Builder investYesNoDialog = new AlertDialog.Builder(view.getContext());
                        investYesNoDialog.setMessage("Opravdu investovat " + toInvest + " Kč?");
                        investYesNoDialog.setCancelable(true);

                        investYesNoDialog.setPositiveButton(
                                R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        MyInvestment investment = new MyInvestment();
                                        investment.setLoanId(loan.getId());
                                        investment.setAmount(toInvest);
                                        EventBus.getDefault().post(new Invest.Request(investment));
                                    }
                                });

                        investYesNoDialog.setNegativeButton(
                                R.string.no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert = investYesNoDialog.create();
                        alert.show();
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
                            fragment.startActivity(webIntent);
                        } else {
                            Snackbar.make(view, R.string.not_enough_cash, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                });
            }
        }
    }

    @Subscribe
    public void onInvestError(Invest.Failure evt) {
        displayInvestingStatus(fragment.getView(), evt.getDesc());
    }

    @Subscribe
    public void onInvested(Invest.Response noMeaning) {
        displayInvestingStatus(fragment.getView(), getString(R.string.investedOk));
        EventBus.getDefault().post(new GetLoanDetail.Request(loanId));
        EventBus.getDefault().post(new GetWallet.Request());
    }

    @Subscribe
    public void onWalletReceived(GetWallet.Response evt) {
        if (LoanDetailsActivity.walletSum != null) {
            LoanDetailsActivity.walletSum.setText(getString(R.string.balance) + evt.getWallet().getAvailableBalance() + getString(R.string.CZK));
            ZonkySniperApplication.wallet = evt.getWallet();
        }
        // refreshni tlacitka pro investovani
        prepareInvestingButtons(ip);
    }

    public void displayInvestingStatus(View view, final String message) {
        android.app.AlertDialog.Builder statusDialog = new android.app.AlertDialog.Builder(view.getContext());
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

        Picasso.with(ZonkySniperApplication.getInstance().getApplicationContext())
                .load(ZonkyClient.BASE_URL + loan.getPhotos().get(0).getUrl())
                .resize(143, 110)
                .onlyScaleDown()
                .into(storyImage);

        storyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO zobrazit vetsi obrazek v dialogu
            }
        });

        storyName.setText(loan.getName());

        konec.setText("Konec " + Constants.DATE_DD_MM_YYYY_HH_MM.format(loan.getDeadline()));
        investice.setText(loan.getInvestmentsCount() + " investorů");
        zbyva.setText("Zbývá " + Constants.FORMAT_NUMBER_NO_DECIMALS.format(loan.getRemainingInvestment()) + " Kč");

        // vybarvena urokova sazba
        interestRate.setText(Rating.getDesc(loan.getRating()) + " | " + new DecimalFormat("#.##").format(loan.getInterestRate() * 100) + "%");
        interestRate.setTextColor(Color.parseColor(Rating.getColor(loan.getRating())));
    }
}
