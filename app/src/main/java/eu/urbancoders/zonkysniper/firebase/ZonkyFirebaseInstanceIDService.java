package eu.urbancoders.zonkysniper.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.events.FcmTokenRegistration;
import org.greenrobot.eventbus.EventBus;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 26.05.2016
 */
public class ZonkyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "ZonkyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     * <p/>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        Log.i(TAG, "FIRETOKEN:"+token);
        String username = ZonkySniperApplication.getInstance().getUsername();
        if(!username.isEmpty()) {
            EventBus.getDefault().post(new FcmTokenRegistration.Request(username, token));
        }
    }
}
