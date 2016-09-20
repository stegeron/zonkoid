package eu.urbancoders.zonkysniper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.core.DividerItemDecoration;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Question;
import eu.urbancoders.zonkysniper.events.GetQuestions;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class QuestionsFragment extends Fragment {

    int loanId;
    List<Question> questions = new ArrayList<Question>(0);
    int previousLength;
    private RecyclerView recyclerView;
    private QuestionsAdapter mAdapter;

    private boolean loading = false;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int numberOfRowsToLoad = 10;

    public static QuestionsFragment newInstance(int loanId) {
        QuestionsFragment fragment = new QuestionsFragment();
        Bundle args = new Bundle();
        args.putSerializable("loanId", loanId);
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

        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            TextView header = (TextView) rootView.findViewById(R.id.messages_title);
            header.setText(R.string.canViewAfterLogin);
        } else {
            loanId = (int) getArguments().getSerializable("loanId");
            EventBus.getDefault().post(new GetQuestions.Request(loanId, numberOfRowsToLoad));
        }

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

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
                            EventBus.getDefault().post(new GetQuestions.Request(loanId, numberOfRowsToLoad += 5));
                        }
                    }
                }
            }
        });

        mAdapter = new QuestionsAdapter(ZonkySniperApplication.getInstance().getApplicationContext(), questions);
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

    @Subscribe
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
