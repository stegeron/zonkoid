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
import eu.urbancoders.zonkysniper.events.Invest;
import eu.urbancoders.zonkysniper.integration.ZonkyClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 26.08.2016
 */
public class LoanDetailFragment extends Fragment {

    static Loan loan;

    TextView header;
    TextView storyName;
    TextView konec;
    TextView zbyva;
    //        TextView story;
    TextView investice;
    TextView amountToInvest;
    ImageView storyImage;
    //        NumberPicker np;
    SeekBar amountPicker;
    ImageButton snipeButton;
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

        // investicni panel
        konec = (TextView) rootView.findViewById(R.id.konec);
        zbyva = (TextView) rootView.findViewById(R.id.zbyva);
        investice = (TextView) rootView.findViewById(R.id.investice);

        ZonkySniperApplication.wallet.setAvailableBalance(2345);

        LinearLayout ip = (LinearLayout) rootView.findViewById(R.id.investingPanel);
        for (int i = 200; i <= 5000; i += 200) {
            Button but = (Button) ip.findViewWithTag("button_" + i);
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


        // cela storka
//            story = (TextView) rootView.findViewById(R.id.story);

        int loanId = (int) getArguments().getSerializable("loanId");
        EventBus.getDefault().post(new GetLoanDetail.Request(loanId));

        return rootView;
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
            return; // todo asi nejakou hlasku, ne?
        }

        loan = evt.getLoan();

        header.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(loan.getAmount()) + " Kč na "
                + loan.getTermInMonths() + " měsíců");
        header.setTextColor(Color.parseColor(Rating.getColor(loan.getRating())));

        Picasso.with(ZonkySniperApplication.getInstance().getApplicationContext())
                .load(ZonkyClient.BASE_URL + loan.getPhotos().get(0).getUrl())
                .resize(100, 77)
                .onlyScaleDown()
                .into(storyImage);

        storyName.setText(loan.getName());

        // investicni panel
        if (loan.getMyInvestment() == null && loan.getRemainingInvestment() > 1) {
//                int minValue = 200;
//                if (ZonkySniperApplication.wallet != null && ZonkySniperApplication.wallet.getAvailableBalance() > minValue) {
//                    int maxValue = ZonkySniperApplication.wallet.getAvailableBalance() < 5000
//                            ? (loan.getRemainingInvestment() < new Double(ZonkySniperApplication.wallet.getAvailableBalance())
//                            ? new Double(loan.getRemainingInvestment()).intValue() : new Double(ZonkySniperApplication.wallet.getAvailableBalance()).intValue()) : 5000;
//                    int step = 200;
//
//                    String[] numberValues = new String[maxValue / minValue];
//
//                    for (int i = 0; i < numberValues.length; i++) {
//                        numberValues[i] = String.valueOf(step + i * step) + getString(R.string.CZK);
//                    }
//
//                    np.setMinValue(0);
//                    np.setMaxValue(numberValues.length - 1);
//
//                    np.setWrapSelectorWheel(false);
//                    np.setDisplayedValues(numberValues);
//
////                np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
////                    @Override
////                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
////                        //Display the newly selected number from picker
////                        //                            amount.setText(newVal);
////                    }
////                });
//                }
//                // pokud se nemuze prihlasit, disabluj castku
//                if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
//                    np.setVisibility(View.INVISIBLE);
//                } else {
//                    np.setVisibility(View.VISIBLE);
//                }


//                snipeButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        // pokud se nemuze prihlasit, neumozni investovani, ale prechod na zonky.cz
//                        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
//                            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://app.zonky.cz/#/marketplace/detail/" + loan.getId() + "/"));
//                            fragment.startActivity(webIntent);
//                        }
//
//                        if (np.getDisplayedValues() == null) {
//                            Snackbar.make(view.findViewById(R.id.snipeButton), R.string.not_enough_cash, Snackbar.LENGTH_LONG)
//                                    .setAction("Action", null).show();
//                        } else {
//
//                            // zobrazit Alert
//                            final int toInvest = Integer.parseInt(np.getDisplayedValues()[np.getValue()].replaceAll("[^0-9]", ""));
//
//
//                            AlertDialog.Builder investYesNoDialog = new AlertDialog.Builder(view.getContext());
//                            investYesNoDialog.setMessage("Opravdu investovat " + toInvest + " Kč?");
//                            investYesNoDialog.setCancelable(true);
//
//                            investYesNoDialog.setPositiveButton(
//                                    R.string.yes,
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            MyInvestment investment = new MyInvestment();
//                                            investment.setLoanId(loan.getId());
//                                            investment.setAmount(toInvest);
//                                            EventBus.getDefault().post(new Invest.Request(investment));
//                                        }
//                                    });
//
//                            investYesNoDialog.setNegativeButton(
//                                    R.string.no,
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            dialog.cancel();
//                                        }
//                                    });
//
//                            AlertDialog alert = investYesNoDialog.create();
//                            alert.show();
//                        }
//                    }
//                });
        }

//            story.setText(loan.getStory());
    }
}
