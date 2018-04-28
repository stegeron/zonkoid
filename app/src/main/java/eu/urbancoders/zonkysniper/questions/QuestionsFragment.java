package eu.urbancoders.zonkysniper.questions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.DividerItemDecoration;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.core.ZSViewActivity;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.dataobjects.Question;
import eu.urbancoders.zonkysniper.events.GetQuestions;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class QuestionsFragment extends ZSFragment {

    Loan loan;
    List<Question> questions = new ArrayList<>(0);
    int previousLength;
    private RecyclerView recyclerView;
    private QuestionsAdapter mAdapter;

    private boolean loading = false;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int numberOfRowsToLoad = Constants.NUM_OF_ROWS;

    public static QuestionsFragment newInstance(Loan loan) {
        QuestionsFragment fragment = new QuestionsFragment();
        Bundle args = new Bundle();
        args.putSerializable("loan", loan);
        fragment.setArguments(args);
        return fragment;
    }

    public QuestionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_questions, container, false);

        TextView header = rootView.findViewById(R.id.messages_title);
        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            header.setText(R.string.canViewAfterLogin);
        } else {
            loan = (Loan) getArguments().getSerializable("loan");
            header.setText("");
            if(loan != null) {
                EventBus.getDefault().post(new GetQuestions.Request(loan.getId(), numberOfRowsToLoad));
            }
        }

        recyclerView = rootView.findViewById(R.id.recycler_view);

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

                    if (!loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = true;
                            EventBus.getDefault().post(new GetQuestions.Request(loan.getId(), numberOfRowsToLoad += 5));
                        }
                    }
                }
            }
        });

        mAdapter = new QuestionsAdapter((ZSViewActivity) getActivity(), loan, questions);
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
    public void onMessagesReceived(GetQuestions.Response evt) {
        if (evt.getQuestions() != null) {
            previousLength = questions.size();
            questions.clear();
            questions.addAll(evt.getQuestions());
            mAdapter.notifyDataSetChanged();
            if(previousLength < evt.getQuestions().size()) {
                loading = false;
            } // jinak nech nastaveno true, abychom uz nezkouseli dal nacitat.
        }
    }
}
