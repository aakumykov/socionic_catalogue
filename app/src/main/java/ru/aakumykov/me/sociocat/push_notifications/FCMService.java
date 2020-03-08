package ru.aakumykov.me.sociocat.push_notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ru.aakumykov.me.sociocat.R;

public class FCMService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";

    public FCMService() {
        Log.d(TAG, "new FCMService()");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /*String channelId = getString(R.string.NOTIFICATIONS_new_cards_channel_name);
        if (isNotificationChannelEnabled(this, channelId))
            Log.d(TAG, "Канал уведомлений "+channelId+" включен");
        else
            Log.d(TAG, "Канал уведомлений "+channelId+" отключен");*/

        createNotificationChannel();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        RemoteMessage.Notification notification = remoteMessage.getNotification();

        if (notification != null) {
            String title = notification.getTitle();
            String body = notification.getBody();
            String icon = notification.getIcon();
            Uri imageURL = notification.getImageUrl();
            Integer count = notification.getNotificationCount();

            String channelId = getString(R.string.NOTIFICATIONS_new_cards_channel_id);

            int notificationId = notification.hashCode();

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_notification_default)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(notificationId, notificationBuilder.build());
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Log.e(TAG, "onDeletedMessages()");
    }

    @Override
    public void onMessageSent(@NonNull String s) {
        super.onMessageSent(s);
        Log.e(TAG, "onMessageSent(), "+s);
    }

    @Override
    public void onSendError(@NonNull String s, @NonNull Exception e) {
        super.onSendError(s, e);
        Log.d(TAG, "onSendError(), "+s);
        Log.w(TAG, e);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "New token: " + token);
        //sendRegistrationToServer(token);
    }

    /*private void sendRegistrationToServer(String token) {
    }*/

    private boolean isNotificationChannelEnabled(Context context, @Nullable String channelId){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return NotificationManagerCompat.from(context).areNotificationsEnabled();

        if(TextUtils.isEmpty(channelId))
            return false;

        NotificationManagerCompat notificationManagerCompat =  NotificationManagerCompat.from(context);
        NotificationChannel channel = notificationManagerCompat.getNotificationChannel(channelId);
        if (null != channel)
            return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
        else
            return false;
    }

    private void createNotificationChannel() {
        /*if (TextUtils.isEmpty(channelName)) {
            Log.e(TAG, "Channel name cannot be empty");
            return;
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId = getString(R.string.NOTIFICATIONS_new_cards_channel_id);
            String channelName = getString(R.string.NOTIFICATIONS_new_cards_channel_name);
            String channelDescription = getString(R.string.NOTIFICATIONS_new_cards_channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
