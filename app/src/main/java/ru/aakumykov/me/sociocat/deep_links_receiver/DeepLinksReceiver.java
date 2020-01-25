package ru.aakumykov.me.sociocat.deep_links_receiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.BuildConfig;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid.CardsGrid_View;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;

public class DeepLinksReceiver extends BaseView {

    private static final String TAG = "DeepLinksReceiver";

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    // Activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deep_links_receiver);

        setPageTitle(R.string.DEEP_LINKS_RECEIVER_page_title);
    }

    @Override
    protected void onStart() {
        super.onStart();

        processInputIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        showProgressMessage(R.string.DEEP_LINKS_RECEIVER_processing_link);

        if (null == intent) {
            continueWithError(R.string.DEEP_LINKS_RECEIVER_error_processing_link, "Input intent is null");
            return;
        }

        String deepLink = intent.getDataString();
        if (null == deepLink) {
            continueWithError(R.string.DEEP_LINKS_RECEIVER_error_processing_link,"There is no deep link in Intent");
            return;
        }

        if (firebaseAuth.isSignInWithEmailLink(deepLink))
        {
            processEmailSignIn_DeepLink(deepLink);
        }
        else
        {
            processOther_DeepLink(deepLink);
        }
    }


    private void processEmailSignIn_DeepLink(String deepLink) {

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
        String storedEmail = sharedPreferences.getString(Constants.KEY_STORED_EMAIL, "");

        if (TextUtils.isEmpty(storedEmail)) {
            continueWithError(R.string.DEEP_LINKS_RECEIVER_login_error, "There is no stored email to complete sign in process");
            return;
        }

        AuthSingleton.signInWithEmailLink(storedEmail, deepLink, new iAuthSingleton.EmailLinkSignInCallbacks() {
            @Override
            public void onEmailLinkSignInSuccess() {
                String continueUrl = Uri.parse(deepLink).getQueryParameter("continueUrl");

                if (null != continueUrl)
                    ContinueUrlProcessor.process(continueUrl);
                else
                    continueWithSuccess(R.string.DEEP_LINKS_RECEIVER_login_success);
            }

            @Override
            public void onEmailLinkSignInError(String errorMsg) {
                continueWithError(R.string.DEEP_LINKS_RECEIVER_login_error, errorMsg);
            }
        });
    }

    private void processOther_DeepLink(String deepLink) {

    }

    private void continueWithSuccess(int messageId) {
        String message = getResources().getString(messageId);
        Intent intent = new Intent(this, CardsGrid_View.class);
        intent.putExtra(Constants.INFO_MESSAGE_ID, message);
        startActivity(intent);
    }

    private void continueWithError(int errorMessageId, String consoleError) {
        Intent intent = new Intent(this, CardsGrid_View.class);
        intent.putExtra(Constants.ERROR_MESSAGE_ID, errorMessageId);
        intent.putExtra(Constants.CONSOLE_ERROR_MESSAGE, consoleError);
        startActivity(intent);
    }
}
