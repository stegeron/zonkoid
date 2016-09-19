package eu.urbancoders.zonkysniper.messaging;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.core.DividerItemDecoration;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Message;
import eu.urbancoders.zonkysniper.events.GetMessagesFromZonky;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Zobrazeni seznamu zprav od Zonky
 *
 * Musi umet nacist pri posunu dolu dalsi a dalsi zpravy, nakonec napsat "Vic zprav uz neni.". Tazeni nahoru provede refresh.
 */
public class MessagesFromZonkyFragment extends Fragment {

    List<Message> messages = new ArrayList<Message>(0);
    private RecyclerView recyclerView;
    private MessagesFromZonkyAdapter mAdapter;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int numberOfRowsToLoad = 10;

    public static MessagesFromZonkyFragment newInstance() {
        MessagesFromZonkyFragment fragment = new MessagesFromZonkyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MessagesFromZonkyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messages_from_zonky, container, false);

        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            TextView header = (TextView) rootView.findViewById(R.id.messages_title);
            header.setText(R.string.canViewAfterLogin);
        } else {
            EventBus.getDefault().post(new GetMessagesFromZonky.Request(numberOfRowsToLoad));
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

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            EventBus.getDefault().post(new GetMessagesFromZonky.Request(numberOfRowsToLoad += 5));
                        }
                    }
                }
            }
        });

        mAdapter = new MessagesFromZonkyAdapter(ZonkySniperApplication.getInstance().getApplicationContext(), messages);
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
    public void onMessagesReceived(GetMessagesFromZonky.Response evt) {
        if (evt.getMessages() != null) {
            messages.clear();
            messages.addAll(evt.getMessages());
            mAdapter.notifyDataSetChanged();
            loading = true;
        }
    }
}
