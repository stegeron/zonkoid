package eu.urbancoders.zonkysniper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.DividerItemDecoration;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Investment;
import eu.urbancoders.zonkysniper.events.GetInvestments;
import eu.urbancoders.zonkysniper.events.GetInvestmentsByZonkoid;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 26.08.2016
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
public class InvestorsFragment extends ZSFragment {

    int loanId;
    private int page;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    List<Investment> investments = new ArrayList<>(0);
    int totalInvestorsCount;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private InvestorsAdapter mAdapter;
    TextView header;
    private TextView investorsNumber;
    private TextView zonkoidInvestorsNumber;
    List<Investment> investmentsByZonkoid;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static InvestorsFragment newInstance(int loanId) {
        InvestorsFragment fragment = new InvestorsFragment();
        Bundle args = new Bundle();
        args.putInt("loanId", loanId);
        fragment.setArguments(args);
        return fragment;
    }

    public InvestorsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_investors, container, false);
        header = rootView.findViewById(R.id.investors_title);
        investorsNumber = rootView.findViewById(R.id.investors_number);
        zonkoidInvestorsNumber = rootView.findViewById(R.id.zonkoid_investors_number);

        if(!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            header.setText(R.string.canViewAfterLogin);
            header.setVisibility(View.VISIBLE);
        } else {
            header.setText("");
            header.setVisibility(View.GONE);

            loanId = (int) getArguments().getSerializable("loanId");
            EventBus.getDefault().post(new GetInvestments.Request(loanId, Constants.NUM_OF_ROWS_LONG, page = 0));
            EventBus.getDefault().post(new GetInvestmentsByZonkoid.Request(loanId));
        }

        recyclerView = rootView.findViewById(R.id.recycler_view);

        // refresher obsahu
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                investments.clear();
                EventBus.getDefault().post(new GetInvestments.Request(loanId, Constants.NUM_OF_ROWS_LONG, page = 0));
                EventBus.getDefault().post(new GetInvestmentsByZonkoid.Request(loanId));
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.black);


        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(inflater.getContext(), LinearLayoutManager.VERTICAL));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            EventBus.getDefault().post(new GetInvestments.Request(loanId, Constants.NUM_OF_ROWS_LONG, page += 1));
                            EventBus.getDefault().post(new GetInvestmentsByZonkoid.Request(loanId));
                        }
                    }
                }
            }
        });

        mAdapter = new InvestorsAdapter(ZonkySniperApplication.getInstance().getApplicationContext(), investments);
        recyclerView.setAdapter(mAdapter);

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInvestorsReceived(GetInvestments.Response evt) {
        if(evt.getInvestments() != null) {

            totalInvestorsCount = evt.getTotalNumOfInvestors();
            // naplnit hlavicku
            investorsNumber.setText(String.format(getString(R.string.number_of_investors), totalInvestorsCount));

            //naplnit adapter se seznamem investoru
            if(loading) {
                investments.clear();
            }
            investments.addAll(evt.getInvestments());
            mAdapter.notifyDataSetChanged();
            loading = true;
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInvestorsByZonkoidReceived(GetInvestmentsByZonkoid.Response evt) {
        if(evt.getInvestments() != null && !evt.getInvestments().isEmpty()) {
            investmentsByZonkoid = evt.getInvestments();

            // doplnit hlavicku o pocet zonkoid investoru
            zonkoidInvestorsNumber.setText(String.format(
                    getString(R.string.zonkoid_investors_number),
                    investmentsByZonkoid.size(), ((double)investmentsByZonkoid.size() / (double)totalInvestorsCount * 100d)
                    ));

//            for(Investment invZonkoid : investmentsByZonkoid) {
//                for(int i=0; i < investments.size(); i++) {
//                    Investment inv = investments.get(i);
//                    if(inv.getInvestorNickname().equalsIgnoreCase(invZonkoid.getInvestorNickname())) {
//                        inv.setZonkoidInvested(true);
//                        mAdapter.notifyItemChanged(i);
//                        break; // investor muze byt v seznamu jen jednou, takze pokracuj dalsim investorem
//                    }
//                }
//            }

            for (Investment investment : investments) {
                for (int i = 0; i < investmentsByZonkoid.size(); i++) {
                    Investment inv = investmentsByZonkoid.get(i);

                    if(ZonkySniperApplication.getInstance().isLoginAllowed()
                            && ZonkySniperApplication.getInstance().getUsername().equalsIgnoreCase(investment.getInvestorNickname())) {
                        investment.setMyInvestment(true);
                    }

                    if (investment.getInvestorNickname().equalsIgnoreCase(inv.getInvestorNickname())) {
                        investment.setZonkoidInvested(true);
//                        mAdapter.notifyItemChanged(i);
                        break; // investor muze byt v seznamu jen jednou, takze pokracuj dalsim investorem
                    }
                }
            }

            mAdapter.notifyDataSetChanged();
        }
    }
}
