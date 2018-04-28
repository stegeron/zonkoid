package eu.urbancoders.zonkysniper.portfolio;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.dataobjects.Rating;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.CashFlow;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.OverallOverview;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.Portfolio;
import eu.urbancoders.zonkysniper.events.GetPortfolio;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PortfolioOverallFragment extends ZSFragment {

    private static final String TAG = PortfolioOverallFragment.class.getName();

    TextView investmentCount;
    TextView totalInvestment;
    TextView principalPaid;
    TextView interestPaid;
    TextView feesAmount;
    TextView netIncome;
    TextView principalLost;

    LineChart cashFlowChart;

    public static PortfolioOverallFragment newInstance() {
        PortfolioOverallFragment fragment = new PortfolioOverallFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PortfolioOverallFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_portfolio_overall, container, false);

        investmentCount = rootView.findViewById(R.id.investmentCount);
        totalInvestment = rootView.findViewById(R.id.totalInvestment);
        principalPaid = rootView.findViewById(R.id.principalPaid);
        feesAmount = rootView.findViewById(R.id.feesAmount);
        netIncome = rootView.findViewById(R.id.netIncome);
        interestPaid = rootView.findViewById(R.id.interestPaid);
        principalLost = rootView.findViewById(R.id.principalLost);

        cashFlowChart = rootView.findViewById(R.id.cashFlowChart);

        return rootView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPortfolioReturned(GetPortfolio.Response evt) {
        ((PortfolioActivity)getActivity()).setPortfolio(evt.getPortfolio());
        drawPortfolio(evt.getPortfolio());
    }

    private void drawPortfolio(Portfolio portfolio) {

        OverallOverview overallOverview = portfolio.getOverallOverview();

        investmentCount.setText(overallOverview.getInvestmentCount() + " investic celkem");
        totalInvestment.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(overallOverview.getTotalInvestment()) + " Kč");
        principalPaid.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(overallOverview.getPrincipalPaid()) + " Kč");
        feesAmount.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(overallOverview.getFeesAmount()) + " Kč");
        // cisty prijem (vydelano) je netIncome - principalLost - feesAmount
        Double tmpNetIncome = overallOverview.getNetIncome() - overallOverview.getPrincipalLost() - overallOverview.getFeesAmount();
        netIncome.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(tmpNetIncome) + " Kč");
        interestPaid.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(overallOverview.getInterestPaid()) + " Kč");
        principalLost.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(overallOverview.getPrincipalLost()) + " Kč");

        /**
         * Line Chart
         */
        List<CashFlow> cashFlows = portfolio.getCashFlow();

        cashFlowChart.setDrawGridBackground(false);

        // no description text
        cashFlowChart.getDescription().setEnabled(false);

        // enable touch gestures
        cashFlowChart.setTouchEnabled(true);

        // enable scaling and dragging
        cashFlowChart.setDragEnabled(false);
        cashFlowChart.setScaleEnabled(false);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        cashFlowChart.setPinchZoom(true);

        setData(cashFlows);

        XAxis xAxis = cashFlowChart.getXAxis();
        YAxis yAxis = cashFlowChart.getAxisLeft();

        cashFlowChart.getAxisRight().setEnabled(false);

        cashFlowChart.animateX(2500);

        // get the legend (only possible after setting data)
        Legend l = cashFlowChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.CIRCLE);

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

    public void setData(List<CashFlow> cashFlows) {

        if(cashFlows == null) {
            cashFlows = new ArrayList<>(0);
        }

        ArrayList<Entry> valuesForInstallment = new ArrayList<>();
        ArrayList<Entry> valuesForPayment = new ArrayList<>();
        ArrayList<Entry> valuesForInterest = new ArrayList<>();
        ArrayList<Date> months = new ArrayList<>();

        int vfiIndex = 0;
        for (CashFlow cashFlow : cashFlows) {
            Float installmentAmount = null;
            Float paymentAmount = null;
            Float interestAmount = null;

            // date for xAxis label
            months.add(cashFlow.getMonth());

            // installment
            if(cashFlow.getInstalmentAmount() != null) {
                installmentAmount = cashFlow.getInstalmentAmount().floatValue();
                valuesForInstallment.add(new Entry(vfiIndex, installmentAmount));
            } else {
                valuesForInstallment.add(new Entry(vfiIndex, 0));
                Log.w(TAG, "getInstalmentAmount == null");
            }

            // payment
            if(cashFlow.getInterestPaid() != null && cashFlow.getPrincipalPaid() != null) {
                paymentAmount = Double.valueOf(cashFlow.getInterestPaid() + cashFlow.getPrincipalPaid()).floatValue();
                valuesForPayment.add(new Entry(vfiIndex, paymentAmount));
            } else {
                valuesForPayment.add(new Entry(vfiIndex, 0));
                Log.w(TAG, "getInterestPaid && getPrincipalPaid == null");
            }

            // interests
            if(cashFlow.getInterestPaid() != null) {
                interestAmount = cashFlow.getInterestPaid().floatValue();
                valuesForInterest.add(new Entry(vfiIndex, interestAmount));
            } else {
                valuesForInterest.add(new Entry(vfiIndex, 0));
                Log.w(TAG, "getInterestPaid == null when drawing interest line");
            }

            vfiIndex++;
        }

        LineDataSet vfiSet = null;
        LineDataSet vfpSet = null;
        LineDataSet vfnSet = null;
        if(!valuesForInstallment.isEmpty()) {
            vfiSet = new LineDataSet(valuesForInstallment, getString(R.string.portfolioGrafValuesForInstallment));
            vfiSet.setColor(Color.parseColor(Rating.D.getColor()));
            vfiSet.setCircleColor(Color.parseColor(Rating.D.getColor()));
            vfiSet.setValueTextColor(Color.parseColor(Rating.D.getColor()));
            vfiSet.setLineWidth(2f);
            vfiSet.setCircleRadius(3f);
            vfiSet.setDrawCircleHole(false);
            vfiSet.setValueTextSize(10f);
            vfiSet.setDrawFilled(false);
            vfiSet.setFormLineWidth(1f);
            vfiSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            vfiSet.setFormSize(15.f);
            vfiSet.setValueFormatter(new LineValueFormatter());
        }

        if(!valuesForPayment.isEmpty()) {
            vfpSet = new LineDataSet(valuesForPayment, getString(R.string.portfolioGrafValuesForPayments));
            vfpSet.setColor(Color.parseColor(Rating.AAA.getColor()));
            vfpSet.setCircleColor(Color.parseColor(Rating.AAA.getColor()));
            vfpSet.setValueTextColor(Color.parseColor(Rating.AAA.getColor()));
            vfpSet.setLineWidth(2f);
            vfpSet.setCircleRadius(3f);
            vfpSet.setDrawCircleHole(false);
            vfpSet.setValueTextSize(10f);
            vfpSet.setDrawFilled(false);
            vfpSet.setFormLineWidth(1f);
            vfpSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            vfpSet.setFormSize(15.f);
            vfpSet.setValueFormatter(new LineValueFormatter());
        }

        if (!valuesForInterest.isEmpty()) {
            vfnSet = new LineDataSet(valuesForInterest, getString(R.string.portfolioGrafValuesForInterest));
            vfnSet.setColor(Color.parseColor(Rating.B.getColor()));
            vfnSet.setCircleColor(Color.parseColor(Rating.B.getColor()));
            vfnSet.setValueTextColor(Color.parseColor(Rating.B.getColor()));
            vfnSet.setLineWidth(2f);
            vfnSet.setCircleRadius(3f);
            vfnSet.setDrawCircleHole(false);
            vfnSet.setValueTextSize(10f);
            vfnSet.setDrawFilled(false);
            vfnSet.setFormLineWidth(1f);
            vfnSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            vfnSet.setFormSize(15.f);
            vfnSet.setValueFormatter(new LineValueFormatter());
        }

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        if(vfiSet != null) {
            dataSets.add(vfiSet);
        }
        if(vfpSet != null) {
            dataSets.add(vfpSet);
        }
        if(vfnSet != null) {
            dataSets.add(vfnSet);
        }

        // create a data object with the datasets
        LineData data = new LineData(dataSets);

        // set data
        cashFlowChart.setData(data);

        XAxis xAxis = cashFlowChart.getXAxis();
        xAxis.setValueFormatter(new DateXAxisValueFormatter(months));
    }

    public class DateXAxisValueFormatter implements IAxisValueFormatter {

        private List<Date> months;

        public DateXAxisValueFormatter(ArrayList<Date> months) {
            this.months = months;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return Constants.DATE_MM_YY.format(months.get((int) value));
        }
    }

    public class LineValueFormatter implements IValueFormatter {

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return String.valueOf(Math.round(value));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Portfolio portfolio = ((PortfolioActivity) getActivity()).getPortfolio();
        if (portfolio != null) {
            drawPortfolio(portfolio);
        }
    }
}
