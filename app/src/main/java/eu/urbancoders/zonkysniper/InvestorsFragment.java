package eu.urbancoders.zonkysniper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
 */
public class InvestorsFragment extends ZSFragment {

    int loanId;
    List<Investment> investments = new ArrayList<Investment>(0);
    private RecyclerView recyclerView;
    private InvestorsAdapter mAdapter;
    TextView header;
    private TextView investorsNumber;
    private TextView zonkoidInvestorsNumber;

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
        header = (TextView) rootView.findViewById(R.id.investors_title);
        investorsNumber = (TextView) rootView.findViewById(R.id.investors_number);
        zonkoidInvestorsNumber = (TextView) rootView.findViewById(R.id.zonkoid_investors_number);

        if(!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            header.setText(R.string.canViewAfterLogin);
        } else {
            header.setText(R.string.list_of_investors);

            int loanId = (int) getArguments().getSerializable("loanId");
            EventBus.getDefault().post(new GetInvestments.Request(loanId));
            EventBus.getDefault().post(new GetInvestmentsByZonkoid.Request(loanId));
        }

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(inflater.getContext(), LinearLayoutManager.VERTICAL));

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
            // naplnit hlavicku
            investorsNumber.setText(String.format(getString(R.string.number_of_investors), evt.getInvestments().size()));

            //naplnit adapter se seznamem investoru
            investments.clear();
            investments.addAll(evt.getInvestments());
            mAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInvestorsByZonkoidReceived(GetInvestmentsByZonkoid.Response evt) {
        if(evt.getInvestments() != null && !evt.getInvestments().isEmpty()) {
            // doplnit hlavicku o pocet zonkoid investoru
            zonkoidInvestorsNumber.setText(String.format(getString(R.string.zonkoid_investors_number), evt.getInvestments().size()));

            for(Investment invZonkoid : evt.getInvestments()) {
                for(int i=0; i < investments.size(); i++) {
                    Investment inv = investments.get(i);
                    if(inv.getInvestorNickname().equalsIgnoreCase(invZonkoid.getInvestorNickname())) {
                        inv.setZonkoidInvested(true);
                        mAdapter.notifyItemChanged(i);
                        break; // investor muze byt v seznamu jen jednou, takze pokracuj dalsim investorem
                    }
                }
            }

//            mAdapter.notifyDataSetChanged();
        }
    }
}
