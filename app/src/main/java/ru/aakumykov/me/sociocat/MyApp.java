package ru.aakumykov.me.sociocat;

import android.app.Application;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import ru.aakumykov.me.sociocat.event_objects.UserAuthorizedEvent;
import ru.aakumykov.me.sociocat.event_objects.UserUnauthorizedEvent;

public class MyApp extends Application {

    private final static String TAG = "MyApp";

    @Override public void onCreate() {
        super.onCreate();

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {

            @Override public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (null != firebaseUser) {
                    EventBus.getDefault().post(new UserAuthorizedEvent(firebaseUser.getUid()));
                } else {
                    EventBus.getDefault().post(new UserUnauthorizedEvent());
                }
            }
        });
    }
}
