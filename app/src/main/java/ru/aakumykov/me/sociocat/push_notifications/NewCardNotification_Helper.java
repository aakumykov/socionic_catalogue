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
import ru.aakumykov.me.sociocat.event_bus_objects.NewCardEvent;

public class NewCardNotification_Helper {

    public static void processNotification(@NonNull Context context, @NonNull NewCardEvent newCardEvent, @Nullable String currentUserId) {

        if (null != currentUserId) {

            if (! currentUserId.equals(newCardEvent.getUserId()))
            {
                int notificationId = newCardEvent.getKey().hashCode();

                Notification notification = prepareNotification(context, newCardEvent);

                NotificationManagerCompat.from(context).notify(notificationId, notification);
            }
        }
    }


    private static Notification prepareNotification(Context context, NewCardEvent newCardEvent) {

        String title = context.getString(R.string.NOTIFICATIONS_new_card_notification_title, newCardEvent.getTitle());

        String shortMessage = context.getString(R.string.NOTIFICATIONS_new_card_notification_message, newCardEvent.getUserName());

        PendingIntent pendingIntent = preparePendingIntent(context, newCardEvent);

        return new NotificationCompat.Builder(context, NotificationConstants.NEW_CARDS_CHANNEL_NAME)
                .setSmallIcon(R.drawable.ic_notification_new_card)
                .setContentTitle(title)
                .setContentText(shortMessage)
                //.setStyle(new NotificationCompat.BigTextStyle().bigText(longMessage))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
    }

    private static PendingIntent preparePendingIntent(Context context, NewCardEvent newCardEvent) {

        Intent intent = new Intent(context, CardShow_View.class);
        intent.putExtra(Constants.CARD_KEY, newCardEvent.getKey());

        return PendingIntent.getActivity(
                context,
                Constants.CODE_SHOW_CARD,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

}
