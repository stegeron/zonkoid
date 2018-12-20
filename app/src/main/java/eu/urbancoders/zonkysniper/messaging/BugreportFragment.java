package eu.urbancoders.zonkysniper.messaging;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.events.Bugreport;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Hlaseni chyb
 */
public class BugreportFragment extends ZSFragment {

    private static final String TAG = BugreportFragment.class.getName();

    public static BugreportFragment newInstance() {
        BugreportFragment fragment = new BugreportFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public BugreportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_bugreport, container, false);

        final Button sendButton = rootView.findViewById(R.id.buttonBugreport);
        sendButton.setEnabled(true);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendButton.setEnabled(false);

                String username, description, logs, timestamp;

                username = ZonkySniperApplication.getInstance().getUsername();
                if (username == null || username.isEmpty()) {
                    username = "nekdo@zonky.cz";
                }

                EditText bugDescriptionView = rootView.findViewById(R.id.bugDescription);
                description = bugDescriptionView.getText().toString();

                StringBuilder log = new StringBuilder();
                log.append(ZonkySniperApplication.getInstance().logPreferences());

                try {
                    Process process = Runtime.getRuntime().exec("logcat -d -v threadtime");
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));


                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        log.append(line);
                    }

                    logs = log.toString();

                } catch (IOException e) {
                    logs = "Chyba pri ziskani logu pres logcat -d: " + e.getMessage();
                }

                timestamp = Constants.DATE_YYYY_MM_DD_HH_MM_SS.format(new Date(System.currentTimeMillis()));

                EventBus.getDefault().post(new Bugreport.Request(username, description, logs, timestamp));

                greenMessage(rootView, getString(R.string.bugreport_sent));

            }
        });

        return rootView;
    }

//    /**
//     * Bugreport by odeslan
//     * @param evt
//     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onBugreportSent(Bugreport.Response evt) {
//        whiteMessage(getView(), getString(R.string.bugreport_sent));
//    }

    /**
     * @deprecated Pouze pro fungovani Eventbusu, neni asi nikdy volane
     */
    @Deprecated
    @Subscribe
    public void onEvent(Bugreport.Failure evt) {
        // nothing to do
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
