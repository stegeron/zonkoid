package eu.urbancoders.zonkysniper.messaging;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.LoanDetailsActivity;
import eu.urbancoders.zonkysniper.MainNewActivity;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.DividerItemDecoration;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Message;
import eu.urbancoders.zonkysniper.events.GetMessagesFromZonky;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Zobrazeni seznamu zprav od Zonky
 *
 * Musi umet nacist pri posunu dolu dalsi a dalsi zpravy, nakonec napsat "Vic zprav uz neni.".
 * Tazeni nahoru provede refresh.
 *
 * Author: Ondrej Steger (onrej@steger.cz)
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
public class MessagesFromZonkyFragment extends ZSFragment {

    List<Message> messages = new ArrayList<>(0);
    private RecyclerView recyclerView;
    private MessagesFromZonkyAdapter mAdapter;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int numberOfRowsToLoad = Constants.NUM_OF_ROWS;

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
            TextView header = rootView.findViewById(R.id.messages_title);
            header.setText(R.string.canViewAfterLogin);
        } else {
            EventBus.getDefault().post(new GetMessagesFromZonky.Request(numberOfRowsToLoad));
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

        recyclerView.addOnItemTouchListener(new MainNewActivity.RecyclerTouchListener(ZonkySniperApplication.getInstance().getApplicationContext(), recyclerView, new MainNewActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Message message = messages.get(position);
                if(message.getLink() != null && message.getLink().getParams() != null && message.getLink().getParams().getLoanId() != null) {
                    Intent detailIntent = new Intent(getContext(), LoanDetailsActivity.class);
                    detailIntent.putExtra("loanId", message.getLink().getParams().getLoanId());
                    startActivity(detailIntent);
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

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
    public void onMessagesReceived(GetMessagesFromZonky.Response evt) {
        if (evt.getMessages() != null) {
            messages.clear();
            messages.addAll(evt.getMessages());
            mAdapter.notifyDataSetChanged();
            loading = true;

            // vymazat pocet novych zprav, abychom nemuseli znova nacitat investora ze Zonky
            if(ZonkySniperApplication.user != null) { // pro sichr :]
                ZonkySniperApplication.user.setUnreadNotificationsCount(0);
            }
        }
    }
}
