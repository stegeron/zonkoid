package eu.urbancoders.zonkysniper;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 16.08.2016
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

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.dataobjects.Investment;

import java.util.List;

public class InvestorsAdapter extends RecyclerView.Adapter<InvestorsAdapter.InvestorsViewHolder> {

    private List<Investment> investorsList;
    private Context context;

    public class InvestorsViewHolder extends RecyclerView.ViewHolder {
        public TextView investorNickname, timeCreated, amount, firstPlusAdditional, zonkoidInvested;

        public InvestorsViewHolder(View view) {
            super(view);
            investorNickname = view.findViewById(R.id.investorNickname);
            timeCreated = view.findViewById(R.id.timeCreated);
            amount = view.findViewById(R.id.amount);
            firstPlusAdditional = view.findViewById(R.id.firstPlusAdditional);
            zonkoidInvested = view.findViewById(R.id.investedByZonkoid);
        }
    }


    public InvestorsAdapter(Context context, List<Investment> investorsList) {
        this.investorsList = investorsList;
        this.context = context;
    }

    @Override
    public InvestorsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.investors_list_row, parent, false);

        return new InvestorsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InvestorsViewHolder holder, int position) {
        Investment investment = investorsList.get(position);
        holder.investorNickname.setText(investment.getInvestorNickname());
        holder.timeCreated.setText(Constants.DATE_DD_MM_YYYY_HH_MM_SS.format(investment.getTimeCreated()));
        holder.amount.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(investment.getAmount()) + " Kč");
        if(investment.getAdditionalAmount() > 0) {
            holder.firstPlusAdditional.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(investment.getFirstAmount()) + " + " +
                    Constants.FORMAT_NUMBER_NO_DECIMALS.format(investment.getAdditionalAmount())
            );
        } else {
            holder.firstPlusAdditional.setText("");
        }
        
        if(investment.isZonkoidInvested()) {
            holder.zonkoidInvested.setText(R.string.zonkoidInvested);
        } else {
            holder.zonkoidInvested.setText("");
        }

        if(investment.isMyInvestment()) {
            holder.investorNickname.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        }
    }

    @Override
    public int getItemCount() {
        return investorsList.size();
    }
}
