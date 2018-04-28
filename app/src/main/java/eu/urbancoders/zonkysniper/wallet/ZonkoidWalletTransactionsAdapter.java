package eu.urbancoders.zonkysniper.wallet;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.dataobjects.WalletTransaction;

/**
 * Jeden radek transakci v Zonkoid penezence
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 11.09.2017
 */

public class ZonkoidWalletTransactionsAdapter extends RecyclerView.Adapter<ZonkoidWalletTransactionsAdapter.WalletTransactionsViewHolder> {

    private List<WalletTransaction> walletTransactionsList;
    private Context context;

    public class WalletTransactionsViewHolder extends RecyclerView.ViewHolder {
        TextView transactionDate, category, loanName, orientation, amount;

        public WalletTransactionsViewHolder(View view) {
            super(view);
            transactionDate = view.findViewById(R.id.transactionDate);
            category = view.findViewById(R.id.category);
            loanName = view.findViewById(R.id.loanName);
            orientation = view.findViewById(R.id.orientation);
            amount = view. findViewById(R.id.amount);
        }
    }

    public ZonkoidWalletTransactionsAdapter(Context context, List<WalletTransaction> walletTransactionsList) {
        this.walletTransactionsList = walletTransactionsList;
        this.context = context;
    }

    @Override
    public ZonkoidWalletTransactionsAdapter.WalletTransactionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wallet_transactions_list_row, parent, false);

        return new ZonkoidWalletTransactionsAdapter.WalletTransactionsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ZonkoidWalletTransactionsAdapter.WalletTransactionsViewHolder holder, int position) {
        WalletTransaction wt = walletTransactionsList.get(position);

        holder.transactionDate.setText(Constants.DATE_DD_MM_YYYY.format(wt.getTransactionDate()));
        if(wt.getCategory() != null) {  // oprava reportovane chyby, kdyz nezname kategorii
            holder.category.setText(
                    context.getResources().getString(
                            context.getResources().getIdentifier("wallet_category_" + wt.getCategory().name(), "string", context.getPackageName())));
        } else {
            holder.category.setText("UNKNOWN_CATEGORY");
        }

        if(wt.getPurchaseSKU().startsWith("FIO")) {
            holder.loanName.setText("Bankovním převodem, " + wt.getCustomMessage());
        } else {
            holder.loanName.setText("Google platba č. " + wt.getOrderId());
        }


        if("IN".equalsIgnoreCase(wt.getOrientation())) {
            holder.orientation.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            holder.orientation.setText(context.getText(R.string.zonkoid_wallet_transaction_in));
        } else {
            holder.orientation.setTextColor(ContextCompat.getColor(context, R.color.AAA));
            holder.orientation.setText(context.getString(R.string.zonkoid_wallet_transaction_out));
        }

        holder.amount.setText(Constants.FORMAT_NUMBER_WITH_DECIMALS.format(wt.getAmount()) + " Kč");
    }

    @Override
    public int getItemCount() {
        return walletTransactionsList.size();
    }


}
