package ru.aakumykov.me.sociocat.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ru.aakumykov.me.sociocat.R;

public class NotificationsHelper {

    private static final String TAG = "NotificationsHelper";


    // Каналы уведомлений
    public interface iNotificationChannelCreationCallbacks {
        void onNotificationChannelCreateSuccess();
        void onNotificationChannelCreateError(String errorMsg);
    }

    public static void createNotificationChannel(
            Context context,
            String channelId,
            int channelTitleId,
            int channelDescriptionId,
            iNotificationChannelCreationCallbacks callbacks
    ) {
//        if (TextUtils.isEmpty(channelId)) {
//            callbacks.onNotificationChannelCreateError("Channel id cannot be empty");
//            return;
//        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            callbacks.onNotificationChannelCreateSuccess();
            return;
        }

        String channelTitle = context.getString(channelTitleId);
        String channelDescription = context.getString(channelDescriptionId);

        int importance = NotificationManager.IMPORTANCE_LOW;

        try {
            NotificationChannel channel = new NotificationChannel(channelId, channelTitle, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            callbacks.onNotificationChannelCreateSuccess();
        }
        catch (Exception e) {
            callbacks.onNotificationChannelCreateError(e.getMessage());
            MyUtils.printError(TAG, e);
        }
    }

    public static boolean isNotificationChannelEnabled(Context context, @Nullable String channelId){
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


    // Уведомления
    public static <T> void showSimpleNotification(
            Context context,
            int notificationId,
            String channelId,
            T titleIdOrString,
            T messageIdOrString
    ) {
        String title = (titleIdOrString instanceof String) ?
                (String) titleIdOrString : context.getString((Integer)titleIdOrString);

        String message = (messageIdOrString instanceof String) ?
                (String) messageIdOrString : context.getString((Integer) messageIdOrString);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_notification_default)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true);

        Notification notification = notificationBuilder.build();

        NotificationManagerCompat.from(context).notify(notificationId, notification);
    }

    public static void removeNotification(Context context, int notificationId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

        if (null != notificationManager)
            notificationManager.cancel(notificationId);
    }
}
