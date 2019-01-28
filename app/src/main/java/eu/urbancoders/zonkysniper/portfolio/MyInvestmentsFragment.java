package eu.urbancoders.zonkysniper.portfolio;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.urbancoders.zonkysniper.LoanDetailsActivity;
import eu.urbancoders.zonkysniper.MainNewActivity;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Investment;
import eu.urbancoders.zonkysniper.dataobjects.LoanStatus;
import eu.urbancoders.zonkysniper.dataobjects.PaymentStatus;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.MyInvestmentsFilter;
import eu.urbancoders.zonkysniper.events.GetMyInvestments;

/**
 * Seznam mych investic na Zonky
 *
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
public class MyInvestmentsFragment extends ZSFragment {

    private int page;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    List<Investment> investments = new ArrayList<>(0);
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private MyInvestmentsAdapter mAdapter;
    public FloatingActionButton fabFilter;
    TextView nothingFound;
    SharedPreferences sp;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MyInvestmentsFragment newInstance() {
        return new MyInvestmentsFragment();
    }

    public MyInvestmentsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_investments, container, false);

        sp = PreferenceManager.getDefaultSharedPreferences(getContext());

        nothingFound = rootView.findViewById(R.id.notfound);

        recyclerView = rootView.findViewById(R.id.recycler_view);

        // refresher obsahu
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearMyInvestmentsAndRefresh();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                R.color.greenLight,
                R.color.warningYellow,
                R.color.colorPrimary);


        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new DividerItemDecoration(inflater.getContext(), LinearLayoutManager.VERTICAL));

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
                            swipeRefreshLayout.setRefreshing(true);
                            EventBus.getDefault().post(new GetMyInvestments.Request(getMyInvestmentsFilter(), Constants.NUM_OF_ROWS_LONG, page += 1));
                        }
                    }
                }
            }
        });

        recyclerView.addOnItemTouchListener(new PortfolioActivity.RecyclerTouchListener(ZonkySniperApplication.getInstance().getApplicationContext(), recyclerView, new MainNewActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (position == 0) {
                    return;
                }

                try {
                    Investment investment = investments.get(position - 1);
                    Intent detailIntent = new Intent(getContext(), LoanDetailsActivity.class);
                    detailIntent.putExtra("loanId", investment.getLoanId());
                    startActivity(detailIntent);
                } catch (IndexOutOfBoundsException ex) {
                    Log.e(TAG, "Chybna pozice v seznamu investic.", ex);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        mAdapter = new MyInvestmentsAdapter(ZonkySniperApplication.getInstance().getApplicationContext(), investments);
        recyclerView.setAdapter(mAdapter);

        // filter FAB
        fabFilter = rootView.findViewById(R.id.fab);
        fabFilter.setAlpha(0.7f);
        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog(view);
            }
        });

        swipeRefreshLayout.setRefreshing(true);
        EventBus.getDefault().post(new GetMyInvestments.Request(getMyInvestmentsFilter(), Constants.NUM_OF_ROWS_LONG, page = 0));

        return rootView;
    }

    private void showFilterDialog(View view) {

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.filter_myinvestments);
        dialog.setCanceledOnTouchOutside(true);

        Button filtrovat = dialog.findViewById(R.id.filter);

        RadioGroup radioGroup = dialog.findViewById(R.id.radioStatus);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int buttonId) {

                RadioButton rb = radioGroup.findViewById(buttonId);
                int index = radioGroup.indexOfChild(rb);

                switch (index) {
                    case 0:
                        sp.edit()
                                .putStringSet(Constants.FILTER_MYINVESTMENTS_STATUSES_NAME, new HashSet<>(LoanStatus.names()))
                                .putString(Constants.FILTER_MYINVESTMENTS_STATUS_EQ_NAME, null)
                                .putBoolean(Constants.FILTER_MYINVESTMENTS_UNPAID_LAST_INSTALLMENT_NAME, false)
                                .putInt(Constants.FILTER_MYINVESTMENTS_SET, R.id.radioAll)
                            .commit();
                        break;
                    case 1:
                        sp.edit()
                                .putStringSet(Constants.FILTER_MYINVESTMENTS_STATUSES_NAME, new HashSet<>(Arrays.asList(LoanStatus.ACTIVE.name(), LoanStatus.PAID_OFF.name())))
                                .putString(Constants.FILTER_MYINVESTMENTS_STATUS_EQ_NAME, null)
                                .putBoolean(Constants.FILTER_MYINVESTMENTS_UNPAID_LAST_INSTALLMENT_NAME, false)
                                .putInt(Constants.FILTER_MYINVESTMENTS_SET, R.id.radioActive)
                            .commit();
                        break;
                    case 2:
                        sp.edit()
                                .putStringSet(Constants.FILTER_MYINVESTMENTS_STATUSES_NAME, new HashSet<>(Arrays.asList(LoanStatus.ACTIVE.name(), LoanStatus.PAID_OFF.name())))
                                .putString(Constants.FILTER_MYINVESTMENTS_STATUS_EQ_NAME, null)
                                .putBoolean(Constants.FILTER_MYINVESTMENTS_UNPAID_LAST_INSTALLMENT_NAME, true)
                                .putInt(Constants.FILTER_MYINVESTMENTS_SET, R.id.radioProblem)
                            .commit();
                        break;
                    case 3:
                        sp.edit()
                                .putStringSet(Constants.FILTER_MYINVESTMENTS_STATUSES_NAME, new HashSet<>(Arrays.asList(LoanStatus.SIGNED.name())))
                                .putString(Constants.FILTER_MYINVESTMENTS_STATUS_EQ_NAME, null)
                                .putBoolean(Constants.FILTER_MYINVESTMENTS_UNPAID_LAST_INSTALLMENT_NAME, false)
                                .putInt(Constants.FILTER_MYINVESTMENTS_SET, R.id.radioSigned)
                                .commit();
                        break;
                    case 4:
                        sp.edit()
                                .putStringSet(Constants.FILTER_MYINVESTMENTS_STATUSES_NAME, new HashSet<>(Arrays.asList(LoanStatus.PAID.name())))
                                .putString(Constants.FILTER_MYINVESTMENTS_STATUS_EQ_NAME, null)
                                .putBoolean(Constants.FILTER_MYINVESTMENTS_UNPAID_LAST_INSTALLMENT_NAME, false)
                                .putInt(Constants.FILTER_MYINVESTMENTS_SET, R.id.radioPaid)
                                .commit();
                        break;
                    case 5:
                        // not implemented
                        break;
                    case 6:
                        // not implemented
                        break;
                    case 7:
                        sp.edit()
                                .putStringSet(Constants.FILTER_MYINVESTMENTS_STATUSES_NAME, new HashSet<>(LoanStatus.names()))
                                .putString(Constants.FILTER_MYINVESTMENTS_STATUS_EQ_NAME, PaymentStatus.SOLD.name())
                                .putBoolean(Constants.FILTER_MYINVESTMENTS_UNPAID_LAST_INSTALLMENT_NAME, false)
                                .putInt(Constants.FILTER_MYINVESTMENTS_SET, R.id.radioSold)
                                .commit();
                        break;
                }
            }
        });
        radioGroup.check(sp.getInt(Constants.FILTER_MYINVESTMENTS_SET, 0));

        filtrovat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nothingFound.setVisibility(View.GONE);
                dialog.dismiss();
                clearMyInvestmentsAndRefresh();
            }
        });

        dialog.show();

    }

    private MyInvestmentsFilter getMyInvestmentsFilter() {
        MyInvestmentsFilter filter = new MyInvestmentsFilter();

        Set<String> statusesSet = sp.getStringSet(Constants.FILTER_MYINVESTMENTS_STATUSES_NAME, new HashSet<>(LoanStatus.names()));
        filter.setStatuses(new ArrayList<>(statusesSet));

        Boolean unpaidLastInstallment = sp.getBoolean(Constants.FILTER_MYINVESTMENTS_UNPAID_LAST_INSTALLMENT_NAME, false);
        if(unpaidLastInstallment) {
            filter.setUnpaidLastInstallment(unpaidLastInstallment);
        } else {
            filter.setUnpaidLastInstallment(null);
        }

        filter.setStausEq(sp.getString(Constants.FILTER_MYINVESTMENTS_STATUS_EQ_NAME, null));

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
            mAdapter.setTotalInvestmentsCount(evt.getTotalCount());
            mAdapter.notifyDataSetChanged();
            loading = true;
            swipeRefreshLayout.setRefreshing(false);
            // zobrazit, ze nic neni nalezeno
            if(investments.isEmpty()) {
                nothingFound.setVisibility(View.VISIBLE);
            } else {
                nothingFound.setVisibility(View.GONE);
            }
        }

        // indikuj, jestli je nastaveny filtr
        if(sp.getInt(Constants.FILTER_MYINVESTMENTS_SET, 0) != R.id.radioAll) {
            fabFilter.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent)));
        } else {
            fabFilter.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.greyLighter)));
        }
    }

    private void clearMyInvestmentsAndRefresh() {
        resetCounters();
        investments.clear();
        mAdapter.setTotalInvestmentsCount(0);
        mAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(true);
        EventBus.getDefault().post(new GetMyInvestments.Request(getMyInvestmentsFilter(), Constants.NUM_OF_ROWS_LONG, page = 0));
    }

    /**
     * resetuj pocitadla strankovani
     */
    private void resetCounters() {
        pastVisiblesItems = 0;
        visibleItemCount = 0;
        totalItemCount = 0;
        page = 0;
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
