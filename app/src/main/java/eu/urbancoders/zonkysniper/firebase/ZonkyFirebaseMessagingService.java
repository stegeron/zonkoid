package eu.urbancoders.zonkysniper.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import eu.urbancoders.zonkysniper.LoanDetailsActivity;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;

/**
 * Puvodni notifikace s daty
 *
 * {
 "collapse_key" : "zollapse",
 "time_to_live" : 1800,
 "to": "/topics/ZonkyTestTopic",
 "notification" : {
    "body" : "Test pujcka",
    "title" : "popis",
    "sound" : "default",
    "icon" : "ic_launcher",
    "click_action" : "OPEN_LOAN_DETAIL_FROM_NOTIFICATION"
 },
 "data" : {
 "loanId" : "43562"
 }
 }

 * Nova pouze data
 *
 * {
 "to": "/topics/ZonkyTestTopic",
 "data" : {
 "loanId" : "43562",
 "body" : "Test pujcka",
 "title" : "popis"
 },
 "collapse_key" : "zonkycollapsible",
 "time_to_live" : 1800
 }
 *
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 26.05.2016
 */
public class ZonkyFirebaseMessagingService  extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage.getData() != null && !remoteMessage.getData().isEmpty()) {
            sendNotification(
                    remoteMessage.getData().get("title"),
                    remoteMessage.getData().get("body"),
                    remoteMessage.getData().get("loanId"),
                    remoteMessage.getData().get("presetAmount"),
                    remoteMessage.getData().get("clientApp"),
                    true
            );
        } else { // tohle neni od zonkycommanderu...
            // TODO kdyz poslu z konzole FCM, tak se zobrazi spravne notifka, ale skonci blbe na prazdnem detailu.
            sendNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(),
                    "0",
                    "0",
                    "ZONKYCOMMANDER",
                    false
            );
        }

    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param title
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody, String loanId, String presetAmount, String clientApp, boolean openLoanDetail) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        // hlavni vypinac notifek ze ZonkyCommandera
        boolean muteNotif = sp.getBoolean(Constants.SHARED_PREF_MUTE_NOTIFICATIONS, false);
        if(clientApp != null && clientApp.equalsIgnoreCase("ZONKYCOMMANDER") && muteNotif) {
            return;
        }

        // Intent for the activity to open when user selects the notification
        Intent detailsIntent = new Intent(this, LoanDetailsActivity.class);
        detailsIntent.putExtra("loanId", loanId);
        detailsIntent.putExtra("presetAmount", presetAmount);
        if(openLoanDetail) {
            detailsIntent.setAction("OPEN_LOAN_DETAIL_FROM_NOTIFICATION");
        }
        detailsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Use TaskStackBuilder to build the back stack and get the PendingIntent
        PendingIntent pendingIntent =
                TaskStackBuilder.create(this)
                        // add all of DetailsActivity's parents to the stack,
                        // followed by DetailsActivity itself
                        .addNextIntentWithParentStack(detailsIntent)
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // nastaveni zvuku podle clientApp (ZONKYCOMMANDER, ROBOZONKY atd.)
        String notifSound = null;
        if(clientApp != null && clientApp.equalsIgnoreCase("ZONKYCOMMANDER")) {
            notifSound = sp.getString("zonkoid_notif_sound", null);
        } else if(clientApp != null && clientApp.equalsIgnoreCase("ROBOZONKY")) {
            notifSound = sp.getString("robozonky_notif_sound", null);
        }
        Uri defaultSoundUri;
        if(notifSound == null) {
            defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        } else {
            defaultSoundUri = Uri.parse(notifSound);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
