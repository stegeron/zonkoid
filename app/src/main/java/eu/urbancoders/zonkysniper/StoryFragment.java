package eu.urbancoders.zonkysniper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.events.GetLoanDetail;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Fragment, ve kterem je pribeh
 */
public class StoryFragment extends Fragment {

    String story;
    TextView nickName;
    TextView storyView;

    public static StoryFragment newInstance(String story) {
        StoryFragment fragment = new StoryFragment();
        Bundle args = new Bundle();
        args.putString("story", story);
        fragment.setArguments(args);
        return fragment;
    }

    public StoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_story, container, false);

        nickName = (TextView) rootView.findViewById(R.id.nickName);
        storyView = (TextView) rootView.findViewById(R.id.story);

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
