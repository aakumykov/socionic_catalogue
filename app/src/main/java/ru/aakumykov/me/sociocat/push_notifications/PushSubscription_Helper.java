package ru.aakumykov.me.sociocat.push_notifications;

import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;

import ru.aakumykov.me.sociocat.utils.MyUtils;

public class PushSubscription_Helper {

    public interface SubscriptionCallbacks {
        void onSubscribeSuccess();
        void onSubscribeError(String errorMsg);
    }

    public interface UnsubscriptionCallbacks {
        void onUnsubscribeSuccess();
        void onUnsubscribeError(String errorMsg);
    }


    private static final String TAG = "NotificationsHelper";


    public static void subscribe2topic(String topicName, SubscriptionCallbacks callbacks) {

        FirebaseMessaging.getInstance().subscribeToTopic(topicName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onSubscribeSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onSubscribeError(e.getMessage());
                        MyUtils.printError(TAG, e);
                    }
                });
    }

    public static void unsubscribeFromTopic(String topicName, UnsubscriptionCallbacks callbacks) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topicName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onUnsubscribeSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onUnsubscribeError(e.getMessage());
                        MyUtils.printError(TAG, e);
                    }
                });
    }

}
