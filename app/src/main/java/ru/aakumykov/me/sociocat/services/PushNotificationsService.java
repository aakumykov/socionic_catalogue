package ru.aakumykov.me.sociocat.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;

public class PushNotificationsService extends FirebaseMessagingService {

    private final static String TAG = "PushNotifService";


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG, "token: "+s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        showNotification(remoteMessage.getNotification(), remoteMessage.getData());
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    private void showNotification(@Nullable RemoteMessage.Notification notification, @Nullable Map<String,String> data) {

        if (null == notification || null == data)
            return;

        String cardKey = data.get(Constants.CARD_KEY);

        Intent intent = new Intent(this, CardShow_View.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.CARD_KEY, cardKey);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT
        );

        String title = notification.getTitle();
        String message = notification.getBody();

        Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_default)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (null != notificationManager)
            notificationManager.notify(0, notificationBuilder.build());
    }
}
