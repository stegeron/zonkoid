package eu.urbancoders.zonkysniper.portfolio;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Rating;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.CurrentOverview;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.Portfolio;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.RiskPortfolio;
import eu.urbancoders.zonkysniper.events.GetInvestorRestrictions;
import eu.urbancoders.zonkysniper.events.GetPortfolio;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Copyright 2019
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
public class PortfolioCurrentFragment extends ZSFragment implements OnChartValueSelectedListener {

    private static final String TAG = PortfolioCurrentFragment.class.getName();

    private FragmentTabHost mTabHost;

    private TextView investmentCount;
    private TextView totalInvestment;
    private TextView principalPaid;
    private TextView principalLeft;
    private TextView principalLeftDue;
    private TextView interestPlanned;
    private TextView interestPaid;
    private TextView interestLeft;
    private TextView interestLeftDue;

    private TextView expectedProfitability;
    private TextView currentProfitability;

    private TextView maxInvestmentAmount;

    private PieChart riskPortfolioChartInvested;
    private PieChart riskPortfolioChartUnpaid;
    private PieChart riskPortfolioChartPaid;

    private TextView riskPortfolioExplainInvested;
    private TextView riskPortfolioExplainPaid;
    private TextView riskPortfolioExplainUnpaid;

    public static PortfolioCurrentFragment newInstance() {
        PortfolioCurrentFragment fragment = new PortfolioCurrentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PortfolioCurrentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_portfolio_current, container, false);

        investmentCount = rootView.findViewById(R.id.investmentCount);
        totalInvestment = rootView.findViewById(R.id.totalInvestment);
        principalPaid = rootView.findViewById(R.id.principalPaid);
        principalLeft = rootView.findViewById(R.id.principalLeft);
        principalLeftDue = rootView.findViewById(R.id.principalLeftDue);
        interestPlanned = rootView.findViewById(R.id.interestPlanned);
        interestPaid = rootView.findViewById(R.id.interestPaid);
        interestLeft = rootView.findViewById(R.id.interestLeft);
        interestLeftDue = rootView.findViewById(R.id.interestLeftDue);

        currentProfitability = rootView.findViewById(R.id.currentProfitability);
        expectedProfitability = rootView.findViewById(R.id.expectedProfitability);

        maxInvestmentAmount = rootView.findViewById(R.id.maxInvestmentAmount);
        maxInvestmentAmount.setVisibility(View.GONE);
        drawMaxInvestmentAmount();

        riskPortfolioChartInvested = rootView.findViewById(R.id.riskPortfolioChartInvested);
        riskPortfolioChartUnpaid = rootView.findViewById(R.id.riskPortfolioChartUnpaid);
        riskPortfolioChartPaid = rootView.findViewById(R.id.riskPortfolioChartPaid);

        FragmentTabHost mTabHost = rootView.findViewById(R.id.tabHost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.tabHost);
        //Lets add the first Tab
        FragmentTabHost.TabSpec mSpec = mTabHost.newTabSpec("Půjčeno");
        mSpec.setContent(R.id.first_Tab);
        mSpec.setIndicator("Půjčeno");
        mTabHost.addTab(mSpec);
        //Lets add the second Tab
        mSpec = mTabHost.newTabSpec("Vráceno");
        mSpec.setContent(R.id.second_Tab);
        mSpec.setIndicator("Vráceno");
        mTabHost.addTab(mSpec);
        //Lets add the third Tab
        mSpec = mTabHost.newTabSpec("Zbývá");
        mSpec.setContent(R.id.third_Tab);
        mSpec.setIndicator("Zbývá");
        mTabHost.addTab(mSpec);

        riskPortfolioExplainInvested = rootView.findViewById(R.id.riskPortfolioExplainInvested);
        riskPortfolioExplainPaid = rootView.findViewById(R.id.riskPortfolioExplainPaid);
        riskPortfolioExplainUnpaid = rootView.findViewById(R.id.riskPortfolioExplainUnpaid);

        return rootView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPortfolioReturned(GetPortfolio.Response evt) {
        ((PortfolioActivity) getActivity()).setPortfolio(evt.getPortfolio());
        drawPortfolio(evt.getPortfolio());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onInvestorRestrictionsReceived(GetInvestorRestrictions.Response evt) {
        int maxInvestmentAmount = evt.getRestrictions().getMaximumInvestmentAmount();
        ZonkySniperApplication.getInstance().getUser().setMaximumInvestmentAmount(maxInvestmentAmount);

        if (maxInvestmentAmount > 0) {
            drawMaxInvestmentAmount();
        }
    }

    private void drawMaxInvestmentAmount() {
        if (ZonkySniperApplication.getInstance().getUser() == null) {
            return;
        }

        double maxInvestmentAmount = ZonkySniperApplication.getInstance().getUser().getMaximumInvestmentAmount();

        if (maxInvestmentAmount == 0) {
            // In case the information is not loaded yet
            EventBus.getDefault().post(new GetInvestorRestrictions.Request());
        } else {
            this.maxInvestmentAmount.setText(getString(
                            R.string.max_investment_info,
                            Constants.FORMAT_NUMBER_NO_DECIMALS.format(maxInvestmentAmount)));
            this.maxInvestmentAmount.setVisibility(View.VISIBLE);
        }
    }

    private void drawPortfolio(Portfolio portfolio) {


        CurrentOverview currentOverview = portfolio.getCurrentOverview();

        if(currentOverview == null) {
            return;
        }

        investmentCount.setText(currentOverview.getInvestmentCount() + " aktivních investic");
        totalInvestment.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(currentOverview.getTotalInvestment()) + " Kč");
        principalPaid.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(currentOverview.getPrincipalPaid()) + " Kč");
        principalLeft.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(currentOverview.getPrincipalLeft()) + " Kč");
        principalLeftDue.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(currentOverview.getPrincipalLeftDue()) + " Kč");
        interestPlanned.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(currentOverview.getInterestPlanned()) + " Kč");
        interestPaid.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(currentOverview.getInterestPaid()) + " Kč");
        interestLeft.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(currentOverview.getInterestLeft()) + " Kč");
        interestLeftDue.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(currentOverview.getInterestLeftDue()) + " Kč");

        if(portfolio.getCurrentProfitability() == null) {
            portfolio.setCurrentProfitability(0d);
            Log.w(TAG, "getCurrentProfitability == null");
        }
        currentProfitability.setText(String.format("%.2f", portfolio.getCurrentProfitability() * 100) + " %");
        if(portfolio.getExpectedProfitability() == null) {
            portfolio.setExpectedProfitability(0d);
            Log.w(TAG, "getExpectedProfitability == null");
        }
        expectedProfitability.setText(String.format("%.2f", portfolio.getExpectedProfitability() * 100) + " %");

        riskPortfolioExplainInvested.setText(String.format(
                getString(R.string.riskPortfolioExplainInvested),
                Constants.FORMAT_NUMBER_NO_DECIMALS.format(currentOverview.getTotalInvestment())));
        riskPortfolioExplainPaid.setText(String.format(
                getString(R.string.riskPortfolioExplainPaid),
                Constants.FORMAT_NUMBER_NO_DECIMALS.format(currentOverview.getPrincipalPaid())));
        riskPortfolioExplainUnpaid.setText(String.format(
                getString(R.string.riskPortfolioExplainUnpaid),
                Constants.FORMAT_NUMBER_NO_DECIMALS.format(currentOverview.getPrincipalLeft())));

        /** CHARTS */
        List<RiskPortfolio> riskPortfolio = portfolio.getRiskPortfolio();
        /**
         * PieChart Invested
         */
        setGraphParams(riskPortfolioChartInvested);
        setData(riskPortfolioChartInvested, riskPortfolio, 1);

        /**
         * PieChart Paid
         */
        setGraphParams(riskPortfolioChartPaid);
        setData(riskPortfolioChartPaid, riskPortfolio, 3);

        /**
         * PieChart Unpaid
         */
        setGraphParams(riskPortfolioChartUnpaid);
        setData(riskPortfolioChartUnpaid, riskPortfolio, 2);
    }

    private void setGraphParams(PieChart riskPortfolioChart) {
        riskPortfolioChart.setUsePercentValues(true);
        riskPortfolioChart.getDescription().setEnabled(false);
//        riskPortfolioChart.setExtraOffsets(5, 10, 5, 5);
//        riskPortfolioChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);
        riskPortfolioChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        riskPortfolioChart.setDragDecelerationFrictionCoef(0.95f);
        riskPortfolioChart.setDrawCenterText(true);
        riskPortfolioChart.setCenterText(getString(R.string.portfolioGrafCenterText));
        riskPortfolioChart.setCenterTextSize(14);
        riskPortfolioChart.setDrawHoleEnabled(true);
        riskPortfolioChart.setHoleColor(Color.WHITE);
        riskPortfolioChart.setTransparentCircleColor(Color.WHITE);
        riskPortfolioChart.setTransparentCircleAlpha(110);
        riskPortfolioChart.setHoleRadius(48f);
        riskPortfolioChart.setTransparentCircleRadius(56f);
        riskPortfolioChart.setRotationAngle(-90);
        riskPortfolioChart.setRotationEnabled(false);
        riskPortfolioChart.setHighlightPerTapEnabled(true);
        riskPortfolioChart.setDrawEntryLabels(false);
        riskPortfolioChart.setOnChartValueSelectedListener(this);

        Legend l = riskPortfolioChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void setData(PieChart riskPortfolioChart, List<RiskPortfolio> riskPortfolio, int type) {

        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        if(riskPortfolio != null) { // oprava po hlaseni Dzendyse, ktery nemel zadnou pujcku
            switch (type) {
                case 1: // Pujceno
                    for (int i = 0; i < riskPortfolio.size(); i++) {
                        entries.add(
                                new PieEntry(
                                        riskPortfolio.get(i).getTotalAmount().floatValue(),
                                        riskPortfolio.get(i).getRating().getDesc(), riskPortfolio.get(i))
                        );
                        int color = Color.parseColor(riskPortfolio.get(i).getRating().getColor());
                        colors.add(color);
                    }
                    break;
                case 2: // Zbyva
                    for (int i = 0; i < riskPortfolio.size(); i++) {
                        entries.add(
                                new PieEntry(
                                        riskPortfolio.get(i).getUnpaid().floatValue() + riskPortfolio.get(i).getDue().floatValue(),
                                        riskPortfolio.get(i).getRating().getDesc(), riskPortfolio.get(i))
                        );
                        int color = Color.parseColor(riskPortfolio.get(i).getRating().getColor());
                        colors.add(color);
                    }
                    break;
                case 3: // Vraceno
                    for (int i = 0; i < riskPortfolio.size(); i++) {
                        entries.add(
                                new PieEntry(
                                        riskPortfolio.get(i).getPaid().floatValue(),
                                        riskPortfolio.get(i).getRating().getDesc(), riskPortfolio.get(i))
                        );
                        int color = Color.parseColor(riskPortfolio.get(i).getRating().getColor());
                        colors.add(color);
                    }
                    break;
            }

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
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;

        selectPieces(riskPortfolioChartInvested, e);
        selectPieces(riskPortfolioChartUnpaid, e);
        selectPieces(riskPortfolioChartPaid, e);
    }

    private void selectPieces(PieChart chart, Entry e) {
        Double totalAmount = ((RiskPortfolio) e.getData()).getTotalAmount();
        Double paid = ((RiskPortfolio) e.getData()).getPaid();
        Double unpaid = ((RiskPortfolio) e.getData()).getDue() + ((RiskPortfolio) e.getData()).getUnpaid();
        Double due = ((RiskPortfolio) e.getData()).getDue();
        Rating rating = ((RiskPortfolio) e.getData()).getRating();
        chart.setCenterText("půjčeno " + Constants.FORMAT_NUMBER_NO_DECIMALS.format(totalAmount) + " Kč" +
                "\n vráceno " + Constants.FORMAT_NUMBER_NO_DECIMALS.format(paid) + " Kč" +
                "\n po splatnosti " + Constants.FORMAT_NUMBER_NO_DECIMALS.format(due) + " Kč" +
                "\n zbývá " + Constants.FORMAT_NUMBER_NO_DECIMALS.format(unpaid) + " Kč");

        chart.setHoleColor(Color.parseColor(rating.getColor()));
    }

    @Override
    public void onNothingSelected() {
        riskPortfolioChartInvested.setCenterText(getString(R.string.portfolioGrafCenterText));
        riskPortfolioChartUnpaid.setCenterText(getString(R.string.portfolioGrafCenterText));
        riskPortfolioChartPaid.setCenterText(getString(R.string.portfolioGrafCenterText));
        riskPortfolioChartInvested.setHoleColor(Color.WHITE);
        riskPortfolioChartUnpaid.setHoleColor(Color.WHITE);
        riskPortfolioChartPaid.setHoleColor(Color.WHITE);
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

    @Override
    public void onResume() {
        super.onResume();
        Portfolio portfolio = ((PortfolioActivity) getActivity()).getPortfolio();
        if (portfolio != null) {
            drawPortfolio(portfolio);
        }
    }
}
