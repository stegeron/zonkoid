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
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.dataobjects.MyInvestment;
import eu.urbancoders.zonkysniper.dataobjects.Rating;
import eu.urbancoders.zonkysniper.events.Invest;
import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
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
                    numberValues[i] = String.valueOf(step + i * step) + activity.getString(R.string.CZK);
                }

                np.setMinValue(0);
                np.setMaxValue(numberValues.length - 1);

                np.setWrapSelectorWheel(false);
                np.setDisplayedValues(numberValues);

//                np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//                    @Override
//                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                        //Display the newly selected number from picker
//                        //                            amount.setText(newVal);
//                    }
//                });
            }

            ImageButton snipeButton = (ImageButton) convertView.findViewById(R.id.snipeButton);
            snipeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(np.getDisplayedValues() == null) {
                        Snackbar.make(view.findViewById(R.id.snipeButton), R.string.not_enough_cash, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {

                        // zobrazit Alert
                        final int toInvest = Integer.parseInt(np.getDisplayedValues()[np.getValue()].replaceAll("[^0-9]", ""));


                        AlertDialog.Builder investYesNoDialog = new AlertDialog.Builder(view.getContext());
                        investYesNoDialog.setMessage("Opravdu investovat " + toInvest + " Kč?");
                        investYesNoDialog.setCancelable(true);

                        investYesNoDialog.setPositiveButton(
                                R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        MyInvestment investment = new MyInvestment();
                                        investment.setLoanId(loan.getId());
                                        investment.setAmount(toInvest);
                                        EventBus.getDefault().post(new Invest.Request(investment));
                                    }
                                });

                        investYesNoDialog.setNegativeButton(
                                R.string.no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert = investYesNoDialog.create();
                        alert.show();
                    }
                }
            });
        }

        storyName = (TextView) convertView.findViewById(R.id.storyName);
        storyName.setText(loan.getName());

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


