package ru.aakumykov.me.sociocat.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationChannelHelper {

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

}
