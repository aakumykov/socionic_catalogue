package ru.aakumykov.me.sociocat.dynamic_link_processor;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.interfaces.iAuthSingleton;
import ru.aakumykov.me.sociocat.interfaces.iUsersSingleton;
import ru.aakumykov.me.sociocat.login.Login_View;
import ru.aakumykov.me.sociocat.register.register_step_2.RegisterStep2_View;
import ru.aakumykov.me.sociocat.services.AuthSingleton;
import ru.aakumykov.me.sociocat.services.UsersSingleton;


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
                    case "/reset_password":
                        resetPasswordStep2(deepLink);
                        break;

                    default:
                        throw new IllegalArgumentException("Unknown deep link: " + deepLink);
                }
            }

        } catch (Exception e) {
            onErrorOccured(e.getMessage());
            e.printStackTrace();
        }
    }

    private void registrationStep2(@NonNull Intent inputIntent) {
        String emailURL = inputIntent.getDataString();
        Intent intent = new Intent(view.getAppContext(), RegisterStep2_View.class);
        intent.putExtra("emailSignInURL", emailURL);
        view.startMyActivity(intent);
    }

    private void resetPasswordStep2(Uri deepLink) {
//        Intent intent = new Intent(view.getAppContext(), ResetPasswordStep2_View.class);
//        intent.putExtra("deepLink", deepLink);
        Intent intent = new Intent(view.getAppContext(), Login_View.class);
        intent.setAction(Constants.ACTION_TRY_NEW_PASSWORD);
        view.startMyActivity(intent);
    }

    private void onErrorOccured(String consoleMsg) {
//        authService.logout();
        FirebaseAuth.getInstance().signOut();
        view.showErrorMsg(R.string.DLP_error_processing_link, consoleMsg);
        view.showHomeButton();
    }
}
