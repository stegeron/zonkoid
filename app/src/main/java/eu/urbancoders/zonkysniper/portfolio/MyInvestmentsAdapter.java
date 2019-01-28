package eu.urbancoders.zonkysniper.portfolio;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Investment;
import eu.urbancoders.zonkysniper.dataobjects.PaymentStatus;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 25.11.2017
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

public class MyInvestmentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final String TAG = this.getClass().getName();

    private static final int TYPE_FIRST_ITEM = 0;
    private static final int TYPE_INVESTMENT_ITEM = 1;

    private List<Investment> myInvestmentList;
    private Context context;
    private int totalCount;

    class MyInvestmentsViewHolder extends RecyclerView.ViewHolder {
        TextView name, status, interestExpected, interestPaid, interestDue, amount, principalReturned, principalDue,
                soldFor, sellingFee;
        TableRow due, soldForRow;
        ProgressBar progressbar;

        MyInvestmentsViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.loanName);
            status = view.findViewById(R.id.status);
            interestExpected = view.findViewById(R.id.interestExpected);
            interestPaid = view.findViewById(R.id.interestPaid);
            interestDue = view.findViewById(R.id.interestDue);
            amount = view.findViewById(R.id.amount);
            principalReturned = view.findViewById(R.id.principalReturned);
            principalDue = view.findViewById(R.id.principalDue);
            due = view.findViewById(R.id.due);
            soldForRow = view.findViewById(R.id.soldForRow);
            soldFor = view.findViewById(R.id.soldFor);
            sellingFee = view.findViewById(R.id.sellingFee);
            progressbar = view.findViewById(R.id.progressbar);
        }
    }

    class MyInvestmentsCountViewHolder extends RecyclerView.ViewHolder {
        private TextView investmentsCount;

        MyInvestmentsCountViewHolder(View view) {
            super(view);
            investmentsCount = view.findViewById(R.id.totalInvestmentsCount);
        }
    }


    public MyInvestmentsAdapter(Context context, List<Investment> myInvestmentList) {
        this.myInvestmentList = myInvestmentList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        switch (viewType) {
            case TYPE_FIRST_ITEM:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.my_investments_count_row, parent, false);

                return new MyInvestmentsCountViewHolder(itemView);
            default:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.my_investments_list_row, parent, false);

                return new MyInvestmentsViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_FIRST_ITEM;
        } else {
            return TYPE_INVESTMENT_ITEM;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_FIRST_ITEM:
                if (totalCount == 0) {
                    holder.itemView.setVisibility(View.GONE);
                } else {
                    holder.itemView.setVisibility(View.VISIBLE);
                    MyInvestmentsCountViewHolder investmentsCountHolder = (MyInvestmentsCountViewHolder) holder;
                    investmentsCountHolder.investmentsCount.setText(context.getString(
                            R.string.my_investments_count,
                            Constants.FORMAT_NUMBER_NO_DECIMALS.format(this.totalCount)));
                }
                break;
            case TYPE_INVESTMENT_ITEM:
                Investment investment = myInvestmentList.get(position - 1);
                if(investment.getPaymentStatus() == null) {
                    investment.setPaymentStatus(investment.getStatus().name());
                }

                try {
                    MyInvestmentsViewHolder investmentHolder = (MyInvestmentsViewHolder)holder;
                    investmentHolder.name.setText(investment.getLoanName());

                    investmentHolder.status.setText(
                            ZonkySniperApplication.getInstance().getApplicationContext().getResources().getIdentifier(
                                    "paymentStatus_"+investment.getPaymentStatus(),
                                    "string",
                                    ZonkySniperApplication.getInstance().getApplicationContext().getPackageName()));
                    PaymentStatus investmentStatus = PaymentStatus.valueOf(investment.getPaymentStatus());
                    if(investmentStatus != null) {
                        investmentHolder.status.setBackgroundColor(Color.parseColor(investmentStatus.getBackgroundColor()));
                    }

                    investmentHolder.amount.setText(Constants.FORMAT_NUMBER_WITH_DECIMALS.format(investment.getPurchasePrice()) + " Kč");
                    investmentHolder.principalReturned.setText(Constants.FORMAT_NUMBER_WITH_DECIMALS.format(investment.getPaidPrincipal()) + " Kč");
                    investmentHolder.interestExpected.setText(Constants.FORMAT_NUMBER_WITH_DECIMALS.format(investment.getExpectedInterest())+" Kč");
                    investmentHolder.interestPaid.setText(Constants.FORMAT_NUMBER_WITH_DECIMALS.format(investment.getPaidInterest())+" Kč");


                    if(investmentStatus != null && (investmentStatus == PaymentStatus.DUE || investmentStatus == PaymentStatus.PAID_OFF)) {
                        investmentHolder.principalDue.setText(Constants.FORMAT_NUMBER_WITH_DECIMALS.format(investment.getDuePrincipal()) + " Kč");
                        investmentHolder.interestDue.setText(Constants.FORMAT_NUMBER_WITH_DECIMALS.format(investment.getDueInterest()) + " Kč");
                        investmentHolder.due.setVisibility(View.VISIBLE);
                        investmentHolder.soldForRow.setVisibility(View.GONE);
                    } else if(investmentStatus != null && investmentStatus == PaymentStatus.SOLD) { // u prodanych na SMP zobrazit prodano za a poplatek
                        investmentHolder.soldFor.setText(Constants.FORMAT_NUMBER_WITH_DECIMALS.format(investment.getSmpSoldFor()) + " Kč");
                        investmentHolder.sellingFee.setText(Constants.FORMAT_NUMBER_WITH_DECIMALS.format(investment.getSmpFee()) + " Kč");
                        investmentHolder.soldForRow.setVisibility(View.VISIBLE);
                        investmentHolder.due.setVisibility(View.GONE);
                    } else {
                        investmentHolder.soldForRow.setVisibility(View.GONE);
                        investmentHolder.due.setVisibility(View.GONE);
                    }

                    // progressbar pouze pokud neni splacena nebo zesplatnena
                    if (investment.getRemainingMonths() > 0 &&
                            (PaymentStatus.valueOf(investment.getPaymentStatus()) != PaymentStatus.PAID_OFF
                                    && PaymentStatus.valueOf(investment.getPaymentStatus()) != PaymentStatus.SOLD
                            )) {
                        investmentHolder.progressbar.setMax(investment.getLoanTermInMonth());
                        investmentHolder.progressbar.setProgress(investment.getLoanTermInMonth() - investment.getRemainingMonths());
                        investmentHolder.progressbar.setVisibility(View.VISIBLE);
                    } else {
                        investmentHolder.progressbar.setVisibility(View.GONE);
                    }

                } catch (Throwable t) {
                    Log.e(TAG, "Chyba pri zpracovani radku v Moje Investice");
                }
                break;

            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return myInvestmentList.size() + 1;
    }

    public void setTotalInvestmentsCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
