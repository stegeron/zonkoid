package eu.urbancoders.zonkysniper.loandetail;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.RepaymentCalendarItem;

/**
 * Adapter pro splatkovy kalendar investic
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 15.10.2017
 */

public class RepaymentCalendarAdapter extends RecyclerView.Adapter<RepaymentCalendarAdapter.RepaymentCalendarViewHolder> {

    List<RepaymentCalendarItem> calendarItems = new ArrayList<>();
    private Context context;

    public class RepaymentCalendarViewHolder extends RecyclerView.ViewHolder {
        TextView number, interest, amortization, revenue;

        public RepaymentCalendarViewHolder(View view) {
            super(view);
            number = view.findViewById(R.id.number);
            interest = view.findViewById(R.id.interest);
            amortization = view.findViewById(R.id.amortization);
            revenue = view.findViewById(R.id.revenue);
        }
    }

    public RepaymentCalendarAdapter(Context context, List<RepaymentCalendarItem> calendarItems) {
        this.calendarItems = calendarItems;
        this.context = context;
    }

    @Override
    public RepaymentCalendarAdapter.RepaymentCalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.repayment_calendar_list_row, parent, false);

        return new RepaymentCalendarAdapter.RepaymentCalendarViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RepaymentCalendarAdapter.RepaymentCalendarViewHolder holder, int position) {
        RepaymentCalendarItem calendarItem = calendarItems.get(position);

        holder.number.setText(String.valueOf(calendarItem.getMonth()));
        holder.interest.setText(String.format("%.2f", calendarItem.getInterest()));
        holder.amortization.setText(String.format("%.2f", calendarItem.getPrinciple()));
        holder.revenue.setText(String.format("%.2f", calendarItem.getRevenue()) + " Kč");

        if(calendarItem.getRevenue() >= 0) {
            holder.revenue.setTextColor(ContextCompat.getColor(
                    ZonkySniperApplication.getInstance().getApplicationContext(),
                    R.color.black));
        } else {
            holder.revenue.setTextColor(ContextCompat.getColor(
                    ZonkySniperApplication.getInstance().getApplicationContext(),
                    R.color.colorAccent));
        }
    }

    @Override
    public int getItemCount() {
        if(calendarItems == null) {
            return 0;
        } else {
            return calendarItems.size();
        }
    }
}
