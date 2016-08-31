package eu.urbancoders.zonkysniper;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Puvodni notifikace s daty
 *
 * {
 "collapse_key" : "zollapse",
 "time_to_live" : 60,
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
        sendNotification(
                remoteMessage.getData().get("title"),
                remoteMessage.getData().get("body"),
                remoteMessage.getData().get("loanId")
                );
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param title
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody, String loanId) {
        // Intent for the activity to open when user selects the notification
        Intent detailsIntent = new Intent(this, LoanDetailsActivity.class);
        detailsIntent.putExtra("loanId", loanId);
        detailsIntent.setAction("OPEN_LOAN_DETAIL_FROM_NOTIFICATION");
        detailsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Use TaskStackBuilder to build the back stack and get the PendingIntent
        PendingIntent pendingIntent =
                TaskStackBuilder.create(this)
                        // add all of DetailsActivity's parents to the stack,
                        // followed by DetailsActivity itself
                        .addNextIntentWithParentStack(detailsIntent)
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
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
