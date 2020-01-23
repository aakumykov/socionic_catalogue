package ru.aakumykov.me.sociocat.deep_links_receiver;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

import ru.aakumykov.me.sociocat.BaseView;

public class DeepLinksReceiver extends BaseView {

    private static final String TAG = "DeepLinksReceiver";

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    // Activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        processInputIntent(getIntent());
    }


    // BaseView
    @Override public void onUserLogin() {

    }
    @Override public void onUserLogout() {

    }


    // Внутренние методы
    private void processInputIntent(@Nullable Intent intent) {

        if (null == intent) {
            Log.e(TAG, "Input intent is null");
            return;
        }

        Uri uriData = intent.getData();
        if (null == uriData) {
            Log.e(TAG, "There is no data in Intent");
            return;
        }

        String deepLink = uriData.toString();
        Log.d(TAG, "1, deepLink: "+deepLink);

        if (firebaseAuth.isSignInWithEmailLink(deepLink)) {
            EmailSignInLinkProcessor.process(this, deepLink);
        }
        else {
            DynamicLinkProcessor.process(this, intent);
        }
    }

}
