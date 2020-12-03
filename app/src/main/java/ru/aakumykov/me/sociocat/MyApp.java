package ru.aakumykov.me.sociocat;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import ru.aakumykov.me.sociocat.event_bus_objects.NewCardEvent;
import ru.aakumykov.me.sociocat.event_bus_objects.NewCommentEvent;
import ru.aakumykov.me.sociocat.event_bus_objects.UserAuthorizedEvent;
import ru.aakumykov.me.sociocat.event_bus_objects.UserUnauthorizedEvent;
import ru.aakumykov.me.sociocat.push_notifications.NewCardNotification_Helper;
import ru.aakumykov.me.sociocat.push_notifications.NewCommentNotification_Helper;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;

public class MyApp extends Application {

    private final static String TAG = "=MyApp=";

    // Методы Application
    @Override
    public void onCreate() {
        super.onCreate();
        //logFCMRegistrationToken();
    }

    // Внутренние методы
    private void logFCMRegistrationToken() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        Log.d(TAG, "InstanceId: "+instanceIdResult.getId());
                        Log.d(TAG, "Token: "+instanceIdResult.getToken());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.getMessage());
                        Log.w(TAG, e);
                    }
                });
    }

}
