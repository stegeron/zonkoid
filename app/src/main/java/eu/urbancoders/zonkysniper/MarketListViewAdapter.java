package eu.urbancoders.zonkysniper;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.dataobjects.Rating;

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
        final Loan children = (Loan) getChild(groupPosition, childPosition);
        TextView text = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.market_listrow_details, null);
        }

        final TextView amount = (TextView) convertView.findViewById(R.id.amount);
        amount.setText("200");

        NumberPicker np = (NumberPicker) convertView.findViewById(R.id.amountPicker);
        np.setMinValue(200);
        np.setMaxValue(5000);

//        // steps
//        int step = 200;
//        String[] valueSet = new String[np.getMaxValue() / np.getMinValue()];
//        for (int i = np.getMinValue(); i <= np.getMaxValue(); i += step) {
//            valueSet[(i / step) - 2] = String.valueOf(i);
//        }
//        np.setDisplayedValues(valueSet);

        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Display the newly selected number from picker
                amount.setText("Selected Number : " + newVal);
            }
        });

        text = (TextView) convertView.findViewById(R.id.textView1);
        text.setText(children.getName());
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "kliknul jsem",
                        Toast.LENGTH_SHORT).show();
            }
        });
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
                + " / " +loan.getInterestRate()*100 + "%" );
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


