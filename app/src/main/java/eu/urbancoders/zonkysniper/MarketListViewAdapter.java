package eu.urbancoders.zonkysniper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.dataobjects.Rating;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;


/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 15.05.2016
 */
public class MarketListViewAdapter extends BaseExpandableListAdapter {

    private final List<Loan> groups;
    public LayoutInflater inflater;
    public Activity activity;

    public MarketListViewAdapter(Activity act, List<Loan> groups) {
        activity = act;
        this.groups = groups;
        inflater = act.getLayoutInflater();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Loan loan = (Loan) getChild(groupPosition, childPosition);
        TextView storyName = null;
        TextView story = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.market_listrow_details, null);
        }

        final TextView konec = (TextView) convertView.findViewById(R.id.konec);
        final TextView zbyva = (TextView) convertView.findViewById(R.id.zbyva);
        final TextView investice = (TextView) convertView.findViewById(R.id.investice);
        konec.setText("Konec "+ Constants.DATE_DD_MM_YYYY_HH_MM.format(loan.getDeadline()));
        investice.setText(loan.getInvestmentsCount() + " investorů");
        zbyva.setText("Zbývá "+ Constants.FORMAT_NUMBER_NO_DECIMALS.format(loan.getRemainingInvestment()) + " Kč");

        if(loan.getMyInvestment() == null && loan.getRemainingInvestment() > 1) {
            final NumberPicker np = (NumberPicker) convertView.findViewById(R.id.amountPicker);
            int minValue = 200;
            if (ZonkySniperApplication.wallet != null && ZonkySniperApplication.wallet.getAvailableBalance() > minValue) {
                int maxValue = ZonkySniperApplication.wallet.getAvailableBalance() < 5000
                        ? (loan.getRemainingInvestment() < new Double(ZonkySniperApplication.wallet.getAvailableBalance())
                        ? new Double(loan.getRemainingInvestment()).intValue() : new Double(ZonkySniperApplication.wallet.getAvailableBalance()).intValue()) : 5000;
                int step = 200;

                String[] numberValues = new String[maxValue / minValue];

                for (int i = 0; i < numberValues.length; i++) {
                    numberValues[i] = String.valueOf(step + i * step);
                }

                np.setMinValue(0);
                np.setMaxValue(numberValues.length - 1);

                np.setWrapSelectorWheel(false);
                np.setDisplayedValues(numberValues);

                np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        //Display the newly selected number from picker
                        //                            amount.setText(newVal);
                    }
                });
            }

            ImageButton snipeButton = (ImageButton) convertView.findViewById(R.id.snipeButton);
            snipeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // zobrazit Alert
                    int toInvest = Integer.valueOf(np.getDisplayedValues()[np.getValue()]);


                    AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                    builder1.setMessage("Opravdu investovat "+ toInvest + " Kč?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ano",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "Ne",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            });
        } else {
            // TODO zobrazit popis "investovano"
        }

        storyName = (TextView) convertView.findViewById(R.id.storyName);
        storyName.setText(loan.getName());
        //        convertView.setOnClickListener(new OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                Toast.makeText(activity, "kliknul jsem",
        //                        Toast.LENGTH_SHORT).show();
        //            }
        //        });

        story = (TextView) convertView.findViewById(R.id.story);
        story.setText(loan.getStory());
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
//        return groups.size();
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.market_listrow_group, null);
        }
        Loan loan = (Loan) getGroup(groupPosition);
        ((CheckedTextView) convertView).setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(loan.getAmount()) + " Kč / "
                + loan.getTermInMonths() + " měs. / " + Rating.getDesc(loan.getRating())
                + " / " + new DecimalFormat("#.##").format(loan.getInterestRate()*100) + "%" );
        ((CheckedTextView) convertView).setTextColor(Color.parseColor(Rating.getColor(loan.getRating())));
        ((CheckedTextView) convertView).setChecked(isExpanded);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}


