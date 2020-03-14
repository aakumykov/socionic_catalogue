package ru.aakumykov.me.sociocat.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashSet;
import java.util.Set;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.event_bus_objects.NewCardEvent;

public class NewCardsService extends Service {

    private static final String TAG = "NewCardsService";
    private ServiceBinder binder = new ServiceBinder();
    private int newCardsCount = 0;
    private Set<String> locallyCreatedCardsKeys = new HashSet<>();

    // Service
    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    // Подписка на событие NewCardEvent
    @Subscribe
    public void onNewCardEvent(NewCardEvent newCardEvent) {
        String cardKey = newCardEvent.getCardKey();
        incrementNewCardsCount(cardKey);

        showNewCardNotification(newCardEvent);
    }


    // Внешние методы
    public int getNewCardsCount() {
        return newCardsCount;
    }

    public void addLocallyCreatedCard(String cardKey) {
        locallyCreatedCardsKeys.add(cardKey);
    }

    public void reset() {
        setNewCardsCount(0);
        clearLocallyCreatedCardsList();
    }


    // Внутренние методы
    private void incrementNewCardsCount(@Nullable String cardKey) {
        if (cardIsNotCreatedLocally(cardKey))
            setNewCardsCount(newCardsCount+1);
    }

    private boolean cardIsNotCreatedLocally(String cardKey) {
        return !locallyCreatedCardsKeys.contains(cardKey);
    }

    private synchronized void setNewCardsCount(int count) {
        newCardsCount = count;
    }

    private synchronized void clearLocallyCreatedCardsList() {
        locallyCreatedCardsKeys.clear();
    }

    private void showNewCardNotification(@NonNull NewCardEvent newCardEvent) {
        String cardKey = newCardEvent.getCardKey();
        int notificationId = cardKey.hashCode();

        Intent intent = new Intent(this, CardShow_View.class);
        intent.putExtra(Constants.CARD_KEY, cardKey);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                Constants.CODE_SHOW_CARD,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        String title = getString(R.string.aquotes, newCardEvent.getCardTitle());

        String message = getString(R.string.NOTIFICATIONS_new_card_notification_message, newCardEvent.getCardAuthor());

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, Constants.NOTIFICATIONS_CHANNEL_AND_TOPIC_NAME_NEW_CARDS)
                        .setSmallIcon(R.drawable.ic_notification_new_card)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        Notification notification = notificationBuilder.build();
        NotificationManagerCompat.from(this).notify(notificationId, notification);
    }


    // Классы
    public class ServiceBinder extends Binder {
        public NewCardsService getService() {
            return NewCardsService.this;
        }
    }
}
