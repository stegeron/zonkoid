package eu.urbancoders.zonkysniper.portfolio;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Investment;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.MyInvestmentsFilter;
import eu.urbancoders.zonkysniper.events.GetMyInvestments;

/**
 * Seznam mych investic na Zonky
 */
public class MyInvestmentsFragment extends ZSFragment {

    private int page;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    List<Investment> investments = new ArrayList<Investment>(0);
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private MyInvestmentsAdapter mAdapter;
    private TextView header;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MyInvestmentsFragment newInstance() {
        MyInvestmentsFragment fragment = new MyInvestmentsFragment();
        return fragment;
    }

    public MyInvestmentsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_investments, container, false);

//        header = (TextView) rootView.findViewById(R.id.header);

//        if(!ZonkySniperApplication.getInstance().isLoginAllowed()) {
//            header.setText(R.string.canViewAfterLogin);
//            header.setVisibility(View.VISIBLE);
//        } else {
//            header.setText("");
//            header.setVisibility(View.GONE);
//            EventBus.getDefault().post(new GetMyInvestments.Request(getMyInvestmentsFilter(), Constants.NUM_OF_ROWS_LONG, page = 0));
//        }

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        // refresher obsahu
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                investments.clear();
                EventBus.getDefault().post(new GetMyInvestments.Request(getMyInvestmentsFilter(), Constants.NUM_OF_ROWS_LONG, page = 0));
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                R.color.greenLight,
                R.color.warningYellow,
                R.color.colorPrimary);


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
                            EventBus.getDefault().post(new GetMyInvestments.Request(getMyInvestmentsFilter(), Constants.NUM_OF_ROWS_LONG, page += 1));
                        }
                    }
                }
            }
        });

        mAdapter = new MyInvestmentsAdapter(ZonkySniperApplication.getInstance().getApplicationContext(), investments);
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    private MyInvestmentsFilter getMyInvestmentsFilter() {
        MyInvestmentsFilter filter = new MyInvestmentsFilter();

        // TODO nacteni filtru z filtrovacich kriterii
        filter.setStatuses(Arrays.asList("SIGNED","COVERED","PAID","PAID_OFF","STOPPED"));

        return filter;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMyInvestmentsReceived(GetMyInvestments.Response evt) {
        if(evt.getInvestments() != null) {
            //naplnit adapter se seznamem investic
            if(loading) {
                investments.clear();
            }
            investments.addAll(evt.getInvestments());
            mAdapter.notifyDataSetChanged();
            loading = true;
            swipeRefreshLayout.setRefreshing(false);
        }
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
