package ru.aakumykov.me.sociocat.push_notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.constants.Constants;
import ru.aakumykov.me.sociocat.constants.NotificationConstants;
import ru.aakumykov.me.sociocat.event_bus_objects.NewCommentEvent;

public class NewCommentNotification_Helper {

    public static void processNotification(@NonNull Context context, @NonNull NewCommentEvent newCommentEvent, @Nullable String currentUserId) {

        if (null != currentUserId)
        {
            if (! currentUserId.equals(newCommentEvent.getUserId()))
            {
                int notificationId = newCommentEvent.getCommentKey().hashCode();

                Notification notification = prepareNotification(context, newCommentEvent);

                NotificationManagerCompat.from(context).notify(notificationId, notification);
            }
        }
    }

    private static Notification prepareNotification(Context context, NewCommentEvent newCommentEvent) {

        String title = context.getString(R.string.NOTIFICATIONS_new_comment_notification_title, newCommentEvent.getCardTitle());

        String shortMessage = newCommentEvent.getText();

        String longMessage = newCommentEvent.getText();

        PendingIntent pendingIntent = preparePendingIntent(context, newCommentEvent);

        return new NotificationCompat.Builder(context, NotificationConstants.NEW_COMMENTS_CHANNEL_NAME)
                .setSmallIcon(R.drawable.ic_notification_new_comment)
                .setContentTitle(title)
                .setContentText(shortMessage)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(longMessage))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
    }

    private static PendingIntent preparePendingIntent(Context context, NewCommentEvent newCommentEvent) {

        Intent intent = new Intent(context, CardShow_View.class);
        intent.putExtra(Constants.CARD_KEY, newCommentEvent.getCardId());
        intent.putExtra(Constants.COMMENT_KEY, newCommentEvent.getCommentKey());

        return PendingIntent.getActivity(
                context,
                newCommentEvent.getCommentKey().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

}
