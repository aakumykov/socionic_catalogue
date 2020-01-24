package ru.aakumykov.me.sociocat.deep_links_receiver;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid.CardsGrid_View;

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
            goToStartPage(null,"Input intent is null");
            return;
        }

        String deepLink = intent.getDataString();
        if (null == deepLink) {
            goToStartPage(null, "There is no deep link in Intent");
            return;
        }

        Log.d(TAG, "1, deepLink: "+deepLink);

        if (firebaseAuth.isSignInWithEmailLink(deepLink)) {
            SignInLinkProcessor.process(this, deepLink, new SignInLinkProcessor.SignInLinkProcessorCallbacks() {
                @Override
                public void onLinkSignInSuccess() {
                    goToStartPage(R.string.DEEP_LINKS_RECEIVER_successfull_login, null);
                }

                @Override
                public void onLinkSignInFailed(String errorMsg) {
                    goToStartPage(null, R.string.DEEP_LINKS_RECEIVER_error_sign_in_throuth_link);
                }
            });
        }
        else {
            DynamicLinkProcessor.process(this, intent);
        }
    }

    private void goToStartPage(@Nullable Integer infoMsgId, @Nullable Integer errorMsgId) {
        String infoMsg = (null != infoMsgId) ? getString(infoMsgId) : null;
        String errorMsg = (null != errorMsgId) ? getString(errorMsgId) : null;
        goToStartPage(infoMsg, errorMsg);
    }

    private void goToStartPage(@Nullable String infoMsg, @Nullable String errorMsg) {
        Intent intent = new Intent(this, CardsGrid_View.class);

        if (null != infoMsg)
            intent.putExtra(Constants.INFO_MESSAGE, infoMsg);

        if (null != errorMsg)
            intent.putExtra(Constants.ERROR_MESSAGE, errorMsg);

        startActivity(intent);
    }
}
