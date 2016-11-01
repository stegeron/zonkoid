package eu.urbancoders.zonkysniper;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 16.08.2016
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.dataobjects.Rating;
import eu.urbancoders.zonkysniper.integration.ZonkyClient;

import java.text.DecimalFormat;
import java.util.List;

public class LoansAdapter extends RecyclerView.Adapter<LoansAdapter.LoansViewHolder> {

    private List<Loan> loanList;
    private Context context;

    public class LoansViewHolder extends RecyclerView.ViewHolder {
        public TextView header, name, interestRate, invested, rating;
        public ImageView storyImage;
        public RelativeLayout loanRow;

        public LoansViewHolder(View view) {
            super(view);
            header = (TextView) view.findViewById(R.id.header);
            interestRate = (TextView) view.findViewById(R.id.interestRate);
            rating = (TextView) view.findViewById(R.id.rating);
            name = (TextView) view.findViewById(R.id.name);
            invested = (TextView) view.findViewById(R.id.invested);
            storyImage = (ImageView) view.findViewById(R.id.storyImage);
            loanRow = (RelativeLayout) view.findViewById(R.id.loanRow);
        }
    }


    public LoansAdapter(Context context, List<Loan> loanList) {
        this.loanList = loanList;
        this.context = context;
    }

    @Override
    public LoansViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.loans_list_row, parent, false);

        return new LoansViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LoansViewHolder holder, int position) {
        Loan loan = loanList.get(position);
        holder.header.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(loan.getAmount()) + " Kč na "
                + loan.getTermInMonths() + " měsíců");
        holder.header.setTextColor(Color.parseColor(Rating.getColor(loan.getRating())));

        // nazev pujcky
        holder.name.setText(loan.getName());

        // vybarveny rating
        holder.rating.setText(Rating.getDesc(loan.getRating()));
        holder.rating.setTextColor(Color.parseColor(Rating.getColor(loan.getRating())));

        // vybarvena urokova sazba
        holder.interestRate.setText(new DecimalFormat("#.##").format(loan.getInterestRate() * 100) + "%");
        holder.interestRate.setTextColor(Color.parseColor(Rating.getColor(loan.getRating())));

        // zainvestováno mnou?
        if(loan.getMyInvestment() != null) {
            holder.invested.setText(String.format(context.getString(R.string.myInvestment), loan.getMyInvestment().getAmount()));
        } else {
            holder.invested.setText("");
        }

        // zainvestovano kompletne?
        if(loan.isCovered()) {
            holder.loanRow.setBackgroundColor(ContextCompat.getColor(context, R.color.greyTransparent));
            holder.loanRow.setAlpha(0.75f);
        } else {
            holder.loanRow.setBackgroundColor(Color.TRANSPARENT);
            holder.loanRow.setAlpha(1f);
        }

        Picasso.with(context)
                .load(ZonkyClient.BASE_URL+loan.getPhotos().get(0).getUrl())
                .resize(100, 77)
                .onlyScaleDown()
                .into(holder.storyImage);
    }

    @Override
    public int getItemCount() {
        return loanList.size();
    }
}
