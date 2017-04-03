package eu.urbancoders.zonkysniper.portfolio;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.JsonBuilderParser;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.dataobjects.Rating;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.CashFlow;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.CurrentOverview;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.OverallOverview;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.Portfolio;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.RiskPortfolio;
import eu.urbancoders.zonkysniper.events.GetPortfolio;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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

        investmentCount = (TextView) rootView.findViewById(R.id.investmentCount);
        totalInvestment = (TextView) rootView.findViewById(R.id.totalInvestment);
        principalPaid = (TextView) rootView.findViewById(R.id.principalPaid);
        feesAmount = (TextView) rootView.findViewById(R.id.feesAmount);
        netIncome = (TextView) rootView.findViewById(R.id.netIncome);
        interestPaid = (TextView) rootView.findViewById(R.id.interestPaid);
        principalLost = (TextView) rootView.findViewById(R.id.principalLost);

        cashFlowChart = (LineChart) rootView.findViewById(R.id.cashFlowChart);

        return rootView;
    }

    @Subscribe
    public void onPortfolioReturned(GetPortfolio.Response evt) {
        ((PortfolioActivity)getActivity()).setPortfolio(evt.getPortfolio());
        drawPortfolio(evt.getPortfolio());
    }

    private void drawPortfolio(Portfolio portfolio) {

        OverallOverview overallOverview = portfolio.getOverallOverview();

        investmentCount.setText(overallOverview.getInvestmentCount() + " investic");
        totalInvestment.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(overallOverview.getTotalInvestment()) + " Kč");
        principalPaid.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(overallOverview.getPrincipalPaid()) + " Kč");
        feesAmount.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(overallOverview.getFeesAmount()) + " Kč");
        netIncome.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(overallOverview.getNetIncome()) + " Kč");
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

        ArrayList<Entry> valuesForInstallment = new ArrayList<Entry>();
        ArrayList<Entry> valuesForPayment = new ArrayList<Entry>();
        ArrayList<Date> months = new ArrayList<Date>();

        int vfiIndex = 0;
        for (CashFlow cashFlow : cashFlows) {
            Float installmentAmount = null;
            Float paymentAmount = null;

            // date for xAxis label
            months.add(cashFlow.getMonth());

            // installment
            if(cashFlow.getInstalmentAmount() != null) {
                installmentAmount = cashFlow.getInstalmentAmount().floatValue();
                valuesForInstallment.add(new Entry(vfiIndex, installmentAmount));
            }

            // payment
            if(cashFlow.getInterestPaid() != null && cashFlow.getPrincipalPaid() != null) {
                paymentAmount = new Double(cashFlow.getInterestPaid() + cashFlow.getPrincipalPaid()).floatValue();
                valuesForPayment.add(new Entry(vfiIndex, paymentAmount));
            }

            vfiIndex++;
        }

        LineDataSet vfiSet = null;
        LineDataSet vfpSet = null;
        if(!valuesForInstallment.isEmpty()) {
            vfiSet = new LineDataSet(valuesForInstallment, getString(R.string.portfolioGrafValuesForInstallment));
            vfiSet.setColor(Color.parseColor(Rating.D.getColor()));
            vfiSet.setCircleColor(Color.parseColor(Rating.D.getColor()));
            vfiSet.setValueTextColor(Color.parseColor(Rating.D.getColor()));
            vfiSet.setLineWidth(2f);
            vfiSet.setCircleRadius(3f);
            vfiSet.setDrawCircleHole(false);
            vfiSet.setValueTextSize(11f);
            vfiSet.setDrawFilled(false);
            vfiSet.setFormLineWidth(1f);
            vfiSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            vfiSet.setFormSize(15.f);
        }

        if(!valuesForPayment.isEmpty()) {
            vfpSet = new LineDataSet(valuesForPayment, getString(R.string.portfolioGrafValuesForPayments));
            vfpSet.setColor(Color.parseColor(Rating.AAA.getColor()));
            vfpSet.setCircleColor(Color.parseColor(Rating.AAA.getColor()));
            vfpSet.setValueTextColor(Color.parseColor(Rating.AAA.getColor()));
            vfpSet.setLineWidth(2f);
            vfpSet.setCircleRadius(3f);
            vfpSet.setDrawCircleHole(false);
            vfpSet.setValueTextSize(11f);
            vfpSet.setDrawFilled(false);
            vfpSet.setFormLineWidth(1f);
            vfpSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            vfpSet.setFormSize(15.f);
        }

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        if(vfiSet != null) {
            dataSets.add(vfiSet);
        }
        if(vfpSet != null) {
            dataSets.add(vfpSet);
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
}
