package eu.urbancoders.zonkysniper.loandetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.DividerItemDecoration;
import eu.urbancoders.zonkysniper.core.LoanCalculator;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.dataobjects.Rating;
import eu.urbancoders.zonkysniper.dataobjects.RepaymentCalendar;
import eu.urbancoders.zonkysniper.dataobjects.RepaymentCalendarItem;
import eu.urbancoders.zonkysniper.events.GetLoanDetail;

public class CalculationFragment extends ZSFragment {

    static CalculationFragment fragment;
    private NumberPicker investedPicker;
    List<RepaymentCalendarItem> calendarItems;
    private RecyclerView recyclerView;
    private RepaymentCalendarAdapter mAdapter;
    private int value;
    private Loan loan;

    TextView feeAmount, monthlyRepayment, feeRate,
            netIncome, grossIncome, tax, netInterestRate, months;

    public static String[] amountsToInvest = new String[(Constants.AMOUNT_TO_INVEST_MAX / Constants.AMOUNT_TO_INVEST_STEP)];

    public static final boolean WRAP_SELECTOR_WHEEL = false;

    public CalculationFragment() {
    }

    public static CalculationFragment newInstance(Loan loan) {
        fragment = new CalculationFragment();
        Bundle args = new Bundle();
        args.putSerializable("loan", loan);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        loan = (Loan) getArguments().getSerializable("loan");

        View rootView = inflater.inflate(R.layout.fragment_calculation, container, false);

        feeAmount = (TextView) rootView.findViewById(R.id.feeAmount);
        monthlyRepayment = (TextView) rootView.findViewById(R.id.monthly_repayment);
        feeRate = (TextView) rootView.findViewById(R.id.feeRate);
        netIncome = (TextView) rootView.findViewById(R.id.netIncome);
        grossIncome = (TextView) rootView.findViewById(R.id.grossIncome);
        tax = (TextView) rootView.findViewById(R.id.tax);
        netInterestRate = (TextView) rootView.findViewById(R.id.netInterestRate);
        months = (TextView) rootView.findViewById(R.id.months);

        for (int i = Constants.AMOUNT_TO_INVEST_MIN; i <= Constants.AMOUNT_TO_INVEST_MAX; i += Constants.AMOUNT_TO_INVEST_STEP) {
            amountsToInvest[(i/Constants.AMOUNT_TO_INVEST_STEP)-1] = String.valueOf(i);
        }

        investedPicker = (NumberPicker) rootView.findViewById(R.id.invested_picker);
        investedPicker.setDisplayedValues(amountsToInvest);
        investedPicker.setMinValue(0);
        investedPicker.setMaxValue(amountsToInvest.length - 1);
        investedPicker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        investedPicker.setValue((getValue() / Constants.AMOUNT_TO_INVEST_STEP)-1);

        investedPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Double amount = Double.valueOf(amountsToInvest[newVal]);
                recalculate(amount);
            }
        });

        // splatkovy kalendar
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_calendar);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(inflater.getContext(), LinearLayoutManager.VERTICAL));

        mAdapter = new RepaymentCalendarAdapter(ZonkySniperApplication.getInstance().getApplicationContext(), calendarItems);
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    /**
     * Prepocitat splatkac a vykreslit
     * @param amount
     */
    private void recalculate(double amount) {

        double feeRateValue = 0;
        try {
            if(loan.getDatePublished().after(Constants.DATE_DD_MM_YYYY.parse("01.09.2017"))) {
                feeRateValue = Rating.valueOf(loan.getRating()).getFeeRate();
            } else {
                feeRateValue = 1d;
            }
        } catch (Exception e) {}

        RepaymentCalendar repaymentCalendar = LoanCalculator.calculateAmortization(
                amount,
                loan.getInterestRate() * 100,
                loan.getTermInMonths(),
                feeRateValue);

        calendarItems = repaymentCalendar.getCalendarItems();

        double feeAmountDouble = repaymentCalendar.sumFees();
        feeAmount.setText(Constants.FORMAT_NUMBER_WITH_DECIMALS.format(feeAmountDouble) + " Kč");
        monthlyRepayment.setText(Constants.FORMAT_NUMBER_WITH_DECIMALS.format(repaymentCalendar.avgMothlyRepayments()) + " Kč");
        feeRate.setText(Constants.FORMAT_NUMBER_WITH_DECIMALS.format(feeRateValue) + " %");

        double grossIncomeDouble = repaymentCalendar.sumGrossIncome();
        grossIncome.setText(Constants.FORMAT_NUMBER_WITH_DECIMALS.format(grossIncomeDouble) + " Kč");

        double taxDouble =  repaymentCalendar.getTax();
        tax.setText(Constants.FORMAT_NUMBER_WITH_DECIMALS.format(taxDouble) + " Kč");

        double netIncomeDouble = repaymentCalendar.getNetIncome();
        netIncome.setText(Constants.FORMAT_NUMBER_WITH_DECIMALS.format(netIncomeDouble) + " Kč");

        Rating rating = Rating.valueOf(loan.getRating());
        double netInterestRateDoubleFrom = LoanCalculator.calculateNetInterestRate(rating);
        double netInterestRateDoubleTo = netInterestRateDoubleFrom + rating.getRiskCost();
        netInterestRate.setText("do " +
                        Constants.FORMAT_NUMBER_WITH_DECIMALS.format(netInterestRateDoubleTo) + " %");

        months.setText(String.valueOf(loan.getTermInMonths()));

        mAdapter.calendarItems = calendarItems;
        mAdapter.notifyDataSetChanged();
    }

    public void setValue(int value) {
        this.value = value;
//        persistInt(this.value);
    }

    public int getValue() {
        return this.value;
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
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoanDetailReceived(GetLoanDetail.Response evt) {
        loan = evt.getLoan();
        recalculate(200);
    }
}
