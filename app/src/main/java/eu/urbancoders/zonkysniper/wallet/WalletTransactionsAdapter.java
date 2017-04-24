package eu.urbancoders.zonkysniper.wallet;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.InvestorsAdapter;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.dataobjects.WalletTransaction;

import java.util.List;

/**
 * Jeden radek transakci v penezence
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 19.04.2017
 */

public class WalletTransactionsAdapter extends RecyclerView.Adapter<WalletTransactionsAdapter.WalletTransactionsViewHolder> {

    private List<WalletTransaction> walletTransactionsList;
    private Context context;

    public class WalletTransactionsViewHolder extends RecyclerView.ViewHolder {
        TextView transactionDate, category, loanName, orientation, amount;

        public WalletTransactionsViewHolder(View view) {
            super(view);
            transactionDate = (TextView) view.findViewById(R.id.transactionDate);
            category = (TextView) view.findViewById(R.id.category);
            loanName = (TextView) view.findViewById(R.id.loanName);
            orientation = (TextView) view.findViewById(R.id.orientation);
            amount = (TextView) view. findViewById(R.id.amount);
        }
    }

    public WalletTransactionsAdapter(Context context, List<WalletTransaction> walletTransactionsList) {
        this.walletTransactionsList = walletTransactionsList;
        this.context = context;
    }

    @Override
    public WalletTransactionsAdapter.WalletTransactionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wallet_transactions_list_row, parent, false);

        return new WalletTransactionsAdapter.WalletTransactionsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WalletTransactionsAdapter.WalletTransactionsViewHolder holder, int position) {
        WalletTransaction wt = walletTransactionsList.get(position);

        holder.transactionDate.setText(Constants.DATE_DD_MM_YYYY.format(wt.getTransactionDate()));
        holder.category.setText(
                context.getResources().getString(context.getResources().getIdentifier("wallet_category_" + wt.getCategory().name(), "string", context.getPackageName())));
        holder.loanName.setText(wt.getLoanName());

        if("IN".equalsIgnoreCase(wt.getOrientation())) {
            holder.orientation.setTextColor(ContextCompat.getColor(context, R.color.AAA));
            holder.orientation.setText(context.getText(R.string.wallet_transaction_in));
        } else {
            holder.orientation.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            holder.orientation.setText(context.getString(R.string.wallet_transaction_out));
        }

        holder.amount.setText(Constants.FORMAT_NUMBER_WITH_DECIMALS.format(wt.getAmount()) + " Kč");
    }

    @Override
    public int getItemCount() {
        return walletTransactionsList.size();
    }


}
