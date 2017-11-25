package eu.urbancoders.zonkysniper.portfolio;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.dataobjects.Investment;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 25.11.2017
 */

public class MyInvestmentsAdapter extends RecyclerView.Adapter<MyInvestmentsAdapter.MyInvestmentsViewHolder> {

    public final String TAG = this.getClass().getName();

    private List<Investment> myInvestmentList;
    private Context context;

    public class MyInvestmentsViewHolder extends RecyclerView.ViewHolder {
        public TextView name, status, interest, invested, returned, due;

        public MyInvestmentsViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.loanName);
            status = (TextView) view.findViewById(R.id.status);
            interest = (TextView) view.findViewById(R.id.interest);
            invested = (TextView) view.findViewById(R.id.invested);
            returned = (TextView) view.findViewById(R.id.returned);
            due = (TextView) view.findViewById(R.id.due);
        }
    }


    public MyInvestmentsAdapter(Context context, List<Investment> myInvestmentList) {
        this.myInvestmentList = myInvestmentList;
        this.context = context;
    }

    @Override
    public MyInvestmentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_investments_list_row, parent, false);

        return new MyInvestmentsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyInvestmentsViewHolder holder, int position) {

        Investment investment = myInvestmentList.get(position);

        try {
            holder.name.setText(investment.getLoanName());
            holder.status.setText(investment.getPaymentStatus());
            holder.interest.setText(String.valueOf(investment.getPaidInterest()));

        } catch (Throwable t) {
            Log.e(TAG, "Chyba pri zpracovani radku v Moje Investice");
        }

//        Loan loan = loanList.get(position);
//        holder.header.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(loan.getAmount()) + " Kč na "
//                + loan.getTermInMonths() + " měsíců");
//        holder.header.setTextColor(Color.parseColor(Rating.getColor(loan.getRating())));
//
//        // nazev pujcky
//        holder.name.setText(loan.getName());
//
//        // vybarveny rating
//        holder.rating.setText(Rating.getDesc(loan.getRating()));
//        holder.rating.setTextColor(Color.parseColor(Rating.getColor(loan.getRating())));
//
//        // vybarvena urokova sazba
//        holder.interestRate.setText(new DecimalFormat("#.##").format(loan.getInterestRate() * 100) + "%");
//        holder.interestRate.setTextColor(Color.parseColor(Rating.getColor(loan.getRating())));
//
//        // zainvestováno mnou?
//        if (loan.getMyInvestment() != null) {
//            holder.invested.setText(String.format(context.getString(R.string.myInvestment), loan.getMyInvestment().getAmount()));
//            holder.invested.setVisibility(View.VISIBLE);
//        } else {
//            holder.invested.setText("");
//            holder.invested.setVisibility(View.GONE);
//        }
//
//        // zainvestovano kompletne?
//        if (loan.isCovered()) {
//            holder.interestRate.setTextColor(Color.GRAY);
//            holder.rating.setTextColor(Color.GRAY);
//            holder.header.setTextColor(Color.GRAY);
//            holder.loanRow.setBackgroundColor(ContextCompat.getColor(context, R.color.greyTransparent));
//            holder.loanRow.setAlpha(0.75f);
//        } else {
//            holder.loanRow.setBackgroundColor(Color.TRANSPARENT);
//            holder.loanRow.setAlpha(1f);
//        }
//
//        if (loan.getPhotos() != null && loan.getPhotos().size() > 0 && loan.getPhotos().get(0) != null) {
//            try {
//                Picasso.with(context)
//                        .load(ZonkyClient.BASE_URL + loan.getPhotos().get(0).getUrl())
//                        .resize(picture_width, picture_height)
//                        .onlyScaleDown()
//                        .into(holder.storyImage);
//            } catch (Exception e) {
//                Log.w(TAG, "Není vyplněný obrázek, smůla...");
//            }
//        } else {
//            Picasso.with(context)
//                    .load(R.mipmap.default_story_picture)
//                    .resize(picture_width, picture_height)
//                    .onlyScaleDown()
//                    .into(holder.storyImage);
//        }
//
//        // progressbar
//        if (!loan.isCovered()) {
//            holder.progressBar.setMax(Double.valueOf(loan.getAmount()).intValue());
//            holder.progressBar.setProgress(Double.valueOf(loan.getAmount() - loan.getRemainingInvestment()).intValue());
//            holder.progressBar.setVisibility(View.VISIBLE);
//        } else {
//            holder.progressBar.setVisibility(View.INVISIBLE);
//        }
    }

    @Override
    public int getItemCount() {
        return myInvestmentList.size();
    }
}
