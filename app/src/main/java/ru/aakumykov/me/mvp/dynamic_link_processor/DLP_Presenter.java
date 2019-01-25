package ru.aakumykov.me.mvp.dynamic_link_processor;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.register2.Register2_View;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.UsersSingleton;


public class DLP_Presenter implements iDLP.Presenter {

    private iDLP.View view;
    private FirebaseDynamicLinks firebaseDynamicLinks = FirebaseDynamicLinks.getInstance();
    private iAuthSingleton authService = AuthSingleton.getInstance();
    private iUsersSingleton usersService = UsersSingleton.getInstance();

    @Override
    public void processDynamicLink(Activity activity, @Nullable final Intent intent) {

        try {
            view.showProgressBar();

            firebaseDynamicLinks
                    .getDynamicLink(intent)
                    .addOnSuccessListener(activity, new OnSuccessListener<PendingDynamicLinkData>() {
                        @Override
                        public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {

                            Uri deepLink = null;

                            if (null != pendingDynamicLinkData) {
                                deepLink = pendingDynamicLinkData.getLink();

                                chooseActionFromDeepLink(deepLink, intent);
                            } else {
                                onErrorOccured("Deep link not found");
                            }

                        }
                    })
                    .addOnFailureListener(activity, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            onErrorOccured(e.getMessage());
                            e.printStackTrace();
                        }
                    });

        } catch (Exception e) {
            onErrorOccured(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void linkView(iDLP.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }

    // Внутренние методы
    private void chooseActionFromDeepLink(Uri deepLink, @NonNull Intent intent) {

        try {

            String emailLink = intent.getDataString();

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            if (firebaseAuth.isSignInWithEmailLink(emailLink)) {
                Uri continueUrl = Uri.parse(deepLink.getQueryParameter("continueUrl"));
                String continueUrlPath = continueUrl.getPath();
                switch (continueUrlPath) {
                    case "/registration_step_2":
                        registrationStep2(intent);
                        break;
                    default:
                        throw new IllegalAccessException("Unknown continueUrl in dynamic link");
                }

            } else {
                String deepLinkPath = deepLink.getPath();
                switch (deepLinkPath) {
                    case "/registration_step_2":
                        break;

                    default:
                        throw new IllegalArgumentException("Unknown deep link: " + deepLink);
                }
            }

        } catch (Exception e) {

        }
    }

    private void registrationStep2(@NonNull Intent inputIntent) {
        String emailURL = inputIntent.getDataString();
        Intent intent = new Intent(view.getAppContext(), Register2_View.class);
        intent.putExtra("emailSignInURL", emailURL);
        view.startMyActivity(intent);
    }

    private void onErrorOccured(String consoleMsg) {
//        authService.logout();
        FirebaseAuth.getInstance().signOut();
        view.showErrorMsg(R.string.DLP_error_processing_link, consoleMsg);
        view.showHomeButton();
    }
}
