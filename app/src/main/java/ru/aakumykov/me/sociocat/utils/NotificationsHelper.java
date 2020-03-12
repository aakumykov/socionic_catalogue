package ru.aakumykov.me.sociocat.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

public class NotificationsHelper {

    public interface NotificationChannelCreationCallbacks {
        void onNotificationChannelCreateSuccess();
        void onNotificationChannelCreateError(String errorMsg);
    }

    private static final String TAG = "NotifChannelHelper";

    public static void createNotificationChannel(
            Context context,
            String channelId,
            int channelTitleId,
            int channelDescriptionId,
            NotificationChannelCreationCallbacks callbacks
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

    public static void removeNotification(Context context, int notificationId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

        if (null != notificationManager)
            notificationManager.cancel(notificationId);
    }
}
