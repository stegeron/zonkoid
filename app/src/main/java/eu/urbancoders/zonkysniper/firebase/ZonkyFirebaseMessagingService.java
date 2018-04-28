package eu.urbancoders.zonkysniper.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import eu.urbancoders.zonkysniper.LoanDetailsActivity;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.dataobjects.Photo;
import eu.urbancoders.zonkysniper.investing.InvestingActivity;

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



 Nejnovejsi format zpravy pro rich notifky:


 {
 "to": "/topics/ZonkyTestTopic",
 "priority" : "high",
 "data" : {
     "loanId" : 105848,
     "body" : "Test notifikace o pujcce",
     "title" : "Nova pujcka",
     "clientApp" : "ROBOZONKY",
     "presetAmount" : 1500,
     "photoUrl" : "/loans/43449/photos/3346",
     "amount" : 120000,
     "termInMonths" : 54,
     "rating" : "AAAA",
     "interestRate" : 0.049900,
     "name" : "Auto místo moto"
 },
 "time_to_live" : 1800,
 "collapse_key" : "ZONKYCOMMANDER"
 }
 *
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 26.05.2016
 * Reworked: 04.07.2017
 */
public class ZonkyFirebaseMessagingService  extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "FCM dorucil zpravu s ID: " + remoteMessage.getMessageId());
        if(remoteMessage.getData() != null && !remoteMessage.getData().isEmpty()) {
            sendNotification(
                    remoteMessage.getData(), true
            );
        } else { // tohle neni od zonkycommanderu...
            // TODO kdyz poslu z konzole FCM, tak se zobrazi spravne notifka, ale skonci blbe na prazdnem detailu. Dodelat zobrazeni obecne zpravy
//            sendNotification(
//                    remoteMessage.getData(),
//                    remoteMessage.getNotification().getTitle(),
//                    remoteMessage.getNotification().getBody(),
//                    "0",
//                    "0",
//                    "ZONKYCOMMANDER",
//                    false
//            );
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     * @param data
     * @param openLoanDetail
     */
    private void sendNotification(Map<String, String> data, boolean openLoanDetail) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String clientApp = data.get("clientApp");
        String title = data.get("title");
        String messageBody = data.get("body");
        String loanId = data.get("loanId");
        String presetAmount = data.get("presetAmount");

        // hlavni vypinac notifek ze ZonkyCommandera
        boolean muteNotif = sp.getBoolean(Constants.SHARED_PREF_MUTE_NOTIFICATIONS, false);
        if(clientApp != null && clientApp.equalsIgnoreCase("ZONKYCOMMANDER") && muteNotif) {
            Log.i(TAG, "Notifky z ZONKYCOMMANDERA jsou muted v nastaveni!");
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
        PendingIntent detailsPendingIntent =
                TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(detailsIntent)
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // nastaveni zvuku a vibrace podle clientApp (ZONKYCOMMANDER, ROBOZONKY atd.)
        String notifSound = null;
        boolean notifVibe = false;
        if(clientApp != null && clientApp.equalsIgnoreCase("ZONKYCOMMANDER")) {
            notifSound = sp.getString("zonkoid_notif_sound", null);
            notifVibe = sp.getBoolean("zonkoid_notif_vibrate", false);
        } else if(clientApp != null && clientApp.equalsIgnoreCase("ROBOZONKY")) {
            notifSound = sp.getString("robozonky_notif_sound", null);
            notifVibe = sp.getBoolean("robozonky_notif_vibrate", false);
        }
        Uri defaultSoundUri;
        if(notifSound == null) {
            defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        } else {
            defaultSoundUri = Uri.parse(notifSound);
        }

        // TOHLE JE TU KVULI KOMPATIBILITE V OREO+
        String channelId = "zonkoid-channel-1";
        String channelName = "Zonkoid";
        NotificationManager notificationManager = null;
        NotificationManagerCompat notificationManagerCompat = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        } else {
            notificationManagerCompat = NotificationManagerCompat.from(this);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_notif)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setTicker(messageBody)    // tohle kvuli starejm hodinkam
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(detailsPendingIntent);

        if(sp.getInt(Constants.SHARED_PREF_PRESET_AMOUNT, -1) > 0) {  // pridat invest akci a udelat tim padem rich notifku
            // Investicni intent
            Intent investIntent = new Intent(this, InvestingActivity.class);
            // naplnit loan pro primou investici
            Loan loan = new Loan();
            loan.setId(Integer.valueOf(loanId));
            Photo photo = new Photo();
            photo.setUrl(data.get("photoUrl"));
            loan.setPhotos(Collections.singletonList(photo));
            if(data != null && data.get("amount") != null) {
                loan.setAmount(Double.valueOf(data.get("amount")));
            }
            loan.setTermInMonths(Integer.valueOf(data.get("termInMonths")));
            loan.setRating(data.get("rating"));
            loan.setInterestRate(Double.valueOf(data.get("interestRate")));
            loan.setName(data.get("name"));
            loan.setDatePublished(new Date(System.currentTimeMillis()));
            loan.setInsuranceActive(Boolean.valueOf(data.get("insuranceActive")));
            investIntent.putExtra("loan", loan);
            int toInvest;
            if(presetAmount != null && !presetAmount.isEmpty() && Double.parseDouble(presetAmount) > 0) { // pokud jde o Robozonky, dej mu prednost
                toInvest = (int) Double.parseDouble(presetAmount);
            } else {
                toInvest = sp.getInt(Constants.SHARED_PREF_PRESET_AMOUNT, 200);  // nacist predvolenou castku
            }
            investIntent.putExtra("amount", toInvest);
            investIntent.setAction("OPEN_INVESTING_FROM_NOTIFICATION");
            investIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent investPendingIntent =
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(investIntent)
                            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationBuilder.addAction(R.drawable.ic_invest_notif, "Investovat " + toInvest + ",-Kč", investPendingIntent);
        }

        if(notifVibe) {
            notificationBuilder.setVibrate(new long[]{0, 400, 200, 300});
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationManager.notify(666, notificationBuilder.build());
        } else {
            notificationManagerCompat.notify(666, notificationBuilder.build());
        }
    }
}
