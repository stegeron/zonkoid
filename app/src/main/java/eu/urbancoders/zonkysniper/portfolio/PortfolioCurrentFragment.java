package eu.urbancoders.zonkysniper.portfolio;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.CurrentOverview;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.Portfolio;
import eu.urbancoders.zonkysniper.events.GetPortfolio;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PortfolioCurrentFragment extends ZSFragment {

    private static final String TAG = PortfolioCurrentFragment.class.getName();

    TextView investmentCount;
    TextView totalInvestment;
    TextView principalPaid;
    TextView principalLeftDue;
    TextView interestPlanned;
    TextView interestPaid;
    TextView interestLeftDue;

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

        EventBus.getDefault().post(new GetPortfolio.Request());

        investmentCount = (TextView) rootView.findViewById(R.id.investmentCount);
        totalInvestment = (TextView) rootView.findViewById(R.id.totalInvestment);
        principalPaid = (TextView) rootView.findViewById(R.id.principalPaid);
        principalLeftDue = (TextView) rootView.findViewById(R.id.principalLeftDue);
        interestPlanned = (TextView) rootView.findViewById(R.id.interestPlanned);
        interestPaid = (TextView) rootView.findViewById(R.id.interestPaid);
        interestLeftDue = (TextView) rootView.findViewById(R.id.interestLeftDue);

        return rootView;
    }

    @Subscribe
    public void onPortfolioReturned(GetPortfolio.Response evt) {

        CurrentOverview currentOverview;

        if(evt == null || evt.getPortfolio() == null) {
            return;
        } else {
            currentOverview = evt.getPortfolio().getCurrentOverview();
        }

        investmentCount.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(currentOverview.getInvestmentCount()) + " aktivních investic");
        totalInvestment.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(currentOverview.getTotalInvestment()) + " Kč");
        principalPaid.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(currentOverview.getPrincipalPaid()) + " Kč");
        principalLeftDue.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(currentOverview.getPrincipalLeftDue()) + " Kč");
        interestPlanned.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(currentOverview.getInterestPlanned()) + " Kč");
        interestPaid.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(currentOverview.getInterestPaid()) + " Kč");
        interestLeftDue.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(currentOverview.getInterestLeftDue()) + " Kč");
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
