package eu.urbancoders.zonkysniper.portfolio;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.CurrentOverview;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.OverallOverview;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.Portfolio;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.RiskPortfolio;
import eu.urbancoders.zonkysniper.events.GetPortfolio;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
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

    PieChart riskPortfolioChart;

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

        riskPortfolioChart = (PieChart) rootView.findViewById(R.id.riskPortfolioChart);

        return rootView;
    }

    @Subscribe
    public void onPortfolioReturned(GetPortfolio.Response evt) {
        ((PortfolioActivity)getActivity()).portfolio = evt.getPortfolio();
        drawPortfolio(evt.getPortfolio());
    }

    private void drawPortfolio(Portfolio portfolio) {

        OverallOverview overallOverview = portfolio.getOverallOverview();

        investmentCount.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(overallOverview.getInvestmentCount()) + " aktivních investic");
        totalInvestment.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(overallOverview.getTotalInvestment()) + " Kč");
        principalPaid.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(overallOverview.getPrincipalPaid()) + " Kč");
        feesAmount.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(overallOverview.getFeesAmount()) + " Kč");
        netIncome.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(overallOverview.getNetIncome()) + " Kč");
        interestPaid.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(overallOverview.getInterestPaid()) + " Kč");
        principalLost.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(overallOverview.getPrincipalLost()) + " Kč");

        /**
         * PieChart
         */
        List<RiskPortfolio> riskPortfolio = portfolio.getRiskPortfolio();

        riskPortfolioChart.setUsePercentValues(true);
        riskPortfolioChart.getDescription().setEnabled(false);
//        riskPortfolioChart.setExtraOffsets(5, 10, 5, 5);
//        riskPortfolioChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

        riskPortfolioChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        riskPortfolioChart.setDragDecelerationFrictionCoef(0.95f);

        riskPortfolioChart.setDrawCenterText(true);
        riskPortfolioChart.setCenterText(getString(R.string.graf_current_portfolio));
        riskPortfolioChart.setCenterTextSize(14);

        riskPortfolioChart.setDrawHoleEnabled(true);
        riskPortfolioChart.setHoleColor(Color.WHITE);

        riskPortfolioChart.setTransparentCircleColor(Color.WHITE);
        riskPortfolioChart.setTransparentCircleAlpha(110);

        riskPortfolioChart.setHoleRadius(48f);
        riskPortfolioChart.setTransparentCircleRadius(56f);

        riskPortfolioChart.setRotationAngle(0);
        riskPortfolioChart.setRotationEnabled(false);
        riskPortfolioChart.setHighlightPerTapEnabled(true);

        riskPortfolioChart.setDrawEntryLabels(false);


        setData(riskPortfolio);

        Legend l = riskPortfolioChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);

    }

    private void setData(List<RiskPortfolio> riskPortfolio) {

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int i = 0; i < riskPortfolio.size(); i++) {
            entries.add(
                    new PieEntry(
                            riskPortfolio.get(i).getTotalAmount().floatValue(),
                            riskPortfolio.get(i).getRating().getDesc())
            );
            int color = Color.parseColor(riskPortfolio.get(i).getRating().getColor());
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setSliceSpace(1f);
        dataSet.setSelectionShift(8f);

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);

        riskPortfolioChart.setData(data);

        // undo all highlights
        riskPortfolioChart.highlightValues(null);

        riskPortfolioChart.invalidate();
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


}
