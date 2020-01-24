package ru.aakumykov.me.sociocat.deep_links_receiver;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;

public class DeepLinksReceiver extends BaseView {

    private static final String TAG = "DeepLinksReceiver";

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    // Activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deep_links_receiver);

        setPageTitle(R.string.DEEP_LINKS_RECEIVER_page_title);

        processInputIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
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

        Uri intentData = intent.getData();
        if (null == intentData) {
            Log.e(TAG, "There is no data in Intent");
            return;
        }

        String deepLink = intentData.toString();
        Log.d(TAG, "1, deepLink: "+deepLink);

        if (firebaseAuth.isSignInWithEmailLink(deepLink)) {
            SignInLinkProcessor.process(this, deepLink, new SignInLinkProcessor.SignInLinkProcessorCallbacks() {
                @Override
                public void onLinkSignInSuccess() {
                    showToast(R.string.DEEP_LINKS_RECEIVER_successfull_login);

                }

                @Override
                public void onLinkSignInFailed(String errorMsg) {
                    showErrorMsg(R.string.DEEP_LINKS_RECEIVER_);
                }
            });
        }
        else {
            DynamicLinkProcessor.process(this, intent);
        }
    }

}
