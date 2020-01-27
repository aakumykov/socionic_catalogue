package ru.aakumykov.me.sociocat.deep_links_receiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.BuildConfig;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.DeepLink_Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid.CardsGrid_View;
import ru.aakumykov.me.sociocat.register_step_2.RegisterStep2_View;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.user_edit_email.UserEditEmail_View;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class DeepLinksReceiver extends BaseView {

    private static final String TAG = "DeepLinksReceiver";

    private Button continueButton;

    // Activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deep_links_receiver);

        continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContinueButtonClicked();
            }
        });

        setPageTitle(R.string.DEEP_LINKS_RECEIVER_page_title);
    }

    @Override
    protected void onStart() {
        super.onStart();

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

        try { processDeepLink(deepLink); }
        catch (DeepLinksReceiverException e) {
            showErrorMsg(R.string.DEEP_LINKS_RECEIVER_error_processing_link, e.getMessage());
            MyUtils.printError(TAG, e);
            MyUtils.show(continueButton);
        }
    }

    private void processDeepLink(@NonNull String deepLink) throws DeepLinksReceiverException {

        String continueUrl = Uri.parse(deepLink).getQueryParameter("continueUrl");
        if (TextUtils.isEmpty(continueUrl))
            throw new DeepLinksReceiverException("continueUrl is empty");

        Uri continueURI;
        try {
            continueURI = Uri.parse(continueUrl);
        }
        catch (Exception e) {
            throw new DeepLinksReceiverException(e);
        }

        String action = continueURI.getQueryParameter(DeepLink_Constants.KEY_ACTION);
        if (TextUtils.isEmpty(action)) {
            throw new DeepLinksReceiverException("There is no action query parameter or it is empty");
        }

        switch (action) {
            case DeepLink_Constants.ACTION_CONTINUE_REGISTRATION:
                continueRegistration(deepLink);
                break;

            case DeepLink_Constants.ACTION_CHANGE_EMAIL:
                continueChangeEmail(deepLink);
                break;

            default:
                throw new DeepLinksReceiverException("Unknown action: "+action);
        }
    }

    private void continueRegistration(@NonNull String deepLink) {
        Intent intent = new Intent(this, RegisterStep2_View.class);
        intent.setAction(Constants.ACTION_CONTINUE_REGISTRATION);
        intent.putExtra(Intent.EXTRA_TEXT, deepLink);
        startActivity(intent);
    }

    private void continueChangeEmail(@NonNull String deepLink) {
        Intent intent = new Intent(this, UserEditEmail_View.class);
        intent.setAction(Constants.ACTION_CONFIRM_EMAIL_CHANGE);
        intent.putExtra(Intent.EXTRA_TEXT, deepLink);
        startActivity(intent);
    }


/*
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


                if (null == continueUrl) {
                    continueWithSuccess(R.string.DEEP_LINKS_RECEIVER_login_success);
                    return;
                }

                try {
                    ContinueUrlProcessor.process(continueUrl);
                }
                catch (ContinueUrlProcessor.ContinueUrlProcessorException e) {

                }
            }

            @Override
            public void onEmailLinkSignInError(String errorMsg) {
                continueWithError(R.string.DEEP_LINKS_RECEIVER_login_error, errorMsg);
            }
        });
    }
*/

/*
    private void processOther_DeepLink(String deepLink) {

    }
*/


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

    private void onContinueButtonClicked() {
        Intent intent = new Intent(this, CardsGrid_View.class);
        startActivity(intent);
    }


    // Классы исключений
    public static class DeepLinksReceiverException extends Exception {
        public DeepLinksReceiverException(String message) {
            super(message);
        }
        public DeepLinksReceiverException(Throwable cause) {
            super(cause);
        }
    }
}
