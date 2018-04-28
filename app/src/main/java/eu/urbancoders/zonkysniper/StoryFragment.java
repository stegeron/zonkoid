package eu.urbancoders.zonkysniper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.events.GetLoanDetail;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Fragment, ve kterem je pribeh
 */
public class StoryFragment extends Fragment {

    String story = "";
    TextView nickName;
    TextView storyView;
    LoanDetailsActivity activity;

    public static StoryFragment newInstance(Loan loan) {
        StoryFragment fragment = new StoryFragment();
        Bundle args = new Bundle();
        args.putSerializable("loan", loan);
        fragment.setArguments(args);
        return fragment;
    }

    public StoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_story, container, false);

        activity = (LoanDetailsActivity) getActivity();
        if(activity != null && activity.loan != null) {
            story = activity.loan.getStory();
        }

        nickName = rootView.findViewById(R.id.nickName);
        storyView = rootView.findViewById(R.id.story);
        storyView.setText(story);

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(activity != null && activity.loan != null) {
            story = activity.loan.getStory();
            storyView.setText(story);
            nickName.setText(activity.loan.getNickName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoanDetailReceived(GetLoanDetail.Response evt) {

        if (evt.getLoan() == null) {
            return;
        }

        nickName.setText(evt.getLoan().getNickName());

        story = evt.getLoan().getStory();
        storyView.setText(story);
    }
}
