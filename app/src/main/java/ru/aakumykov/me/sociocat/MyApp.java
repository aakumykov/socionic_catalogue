package ru.aakumykov.me.sociocat;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import androidx.annotation.NonNull;
import ru.aakumykov.me.sociocat.interfaces.iUsersSingleton;
import ru.aakumykov.me.sociocat.services.UsersSingleton;

public class MyApp extends Application {

    private final static String TAG = "MyApp";
    private iUsersSingleton usersService = UsersSingleton.getInstance();

    @Override public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        processPushToken();
    }

    private void processPushToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {

                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        InstanceIdResult instanceIdResult = task.getResult();

                        if (null != instanceIdResult) {
                            String token = instanceIdResult.getToken();
                            updatePushToken(token);
                        } else {
                            Log.e(TAG, "InstanceIdResult is NULL");
                        }
                    }
                });
    }

    private void updatePushToken(String token) {
        usersService.updatePushToken(token, null);
    }
}
