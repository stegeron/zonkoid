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
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

    public final String TAG = this.getClass().getName();

    int picture_width = 140;
    int picture_height = 117;
    private List<Loan> loanList;
    private Context context;

    public class LoansViewHolder extends RecyclerView.ViewHolder {
        public TextView header, name, interestRate, invested, insurance, activeLoansCount, reservedOnly;
        public ImageView storyImage;
        public RelativeLayout loanRow;
        public ProgressBar progressBar;
        public ProgressBar progressBarReserved;

        public LoansViewHolder(View view) {
            super(view);
            header = view.findViewById(R.id.header);
            interestRate = view.findViewById(R.id.interestRate);
            name = view.findViewById(R.id.name);
            invested = view.findViewById(R.id.invested);
            insurance = view.findViewById(R.id.insurance);
            activeLoansCount = view.findViewById(R.id.activeLoansCount);
            reservedOnly = view.findViewById(R.id.reservedOnly);
            storyImage = view.findViewById(R.id.storyImage);
            loanRow = view.findViewById(R.id.loanRow);
            progressBar = view.findViewById(R.id.progressbar);
            progressBarReserved = view.findViewById(R.id.progressbarReserved);

            // resize podle velikosti displeje
            picture_width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 140, view.getResources().getDisplayMetrics());
            picture_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 117, view.getResources().getDisplayMetrics());
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

        // vybarvena urokova sazba
        holder.interestRate.setText(new DecimalFormat("#.##").format(loan.getInterestRate() * 100) + "%");
        holder.interestRate.setTextColor(Color.parseColor(Rating.getColor(loan.getRating())));

        // zainvestováno mnou?
        if(loan.getMyInvestment() != null) {
            holder.invested.setText(String.format(context.getString(R.string.myInvestment), loan.getMyInvestment().getAmount()));
            holder.invested.setVisibility(View.VISIBLE);
        } else {
            holder.invested.setText("");
            holder.invested.setVisibility(View.GONE);
        }

        // zainvestovano kompletne?
        if(loan.isCovered()) {
            holder.interestRate.setTextColor(Color.GRAY);
            holder.header.setTextColor(Color.GRAY);
            holder.loanRow.setBackgroundColor(ContextCompat.getColor(context, R.color.greyTransparent));
            holder.loanRow.setAlpha(0.75f);
        } else {
            holder.loanRow.setBackgroundColor(Color.TRANSPARENT);
            holder.loanRow.setAlpha(1f);
        }

        if(loan.getPhotos() != null && loan.getPhotos().size() > 0 && loan.getPhotos().get(0) != null) {
            try {
                Picasso.with(context)
                        .load(ZonkyClient.BASE_URL + loan.getPhotos().get(0).getUrl())
                        .resize(picture_width, picture_height)
                        .onlyScaleDown()
                        .into(holder.storyImage);
            } catch (Exception e) {
                Log.w(TAG, "Není vyplněný obrázek, smůla...");
            }
        } else {
            Picasso.with(context)
                    .load(R.mipmap.default_story_picture)
                    .resize(picture_width, picture_height)
                    .onlyScaleDown()
                    .into(holder.storyImage);
        }

        // badges
        if(loan.isInsuranceActive()) {
            holder.insurance.setVisibility(View.VISIBLE);
        } else {
            holder.insurance.setVisibility(View.GONE);
        }
        if(loan.getActiveLoansCount() > 0) {
            holder.activeLoansCount.setVisibility(View.VISIBLE);
            holder.activeLoansCount.setText(String.format(context.getString(R.string.loanscount), loan.getActiveLoansCount()+1));
        } else {
            holder.activeLoansCount.setVisibility(View.GONE);
        }
        if(loan.getRemainingInvestment() - loan.getReservedAmount() == 0 && !loan.isCovered()) {
            holder.reservedOnly.setVisibility(View.VISIBLE);
            holder.reservedOnly.setText(context.getString(R.string.reservedOnlyBadge));
        } else {
            holder.reservedOnly.setVisibility(View.GONE);
        }

        // progressbar
        if(!loan.isCovered()) {
            holder.progressBar.setMax(Double.valueOf(loan.getAmount()).intValue());
            holder.progressBarReserved.setMax(Double.valueOf(loan.getAmount()).intValue());
            holder.progressBar.setProgress(Double.valueOf(loan.getAmount() - loan.getRemainingInvestment()).intValue());
            holder.progressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(context, R.color.progressBarPrimaryOpaque), android.graphics.PorterDuff.Mode.SRC_IN);
            holder.progressBarReserved.setProgress(Double.valueOf(loan.getAmount() - loan.getRemainingInvestment() + loan.getReservedAmount()).intValue());
            holder.progressBarReserved.getProgressDrawable().setColorFilter(ContextCompat.getColor(context, R.color.progressBarSecondaryTransparent), android.graphics.PorterDuff.Mode.SRC_IN);

            holder.progressBar.setVisibility(View.VISIBLE);
            holder.progressBarReserved.setVisibility(View.VISIBLE);
        } else {
            holder.progressBar.setVisibility(View.INVISIBLE);
            holder.progressBarReserved.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return loanList.size();
    }
}
