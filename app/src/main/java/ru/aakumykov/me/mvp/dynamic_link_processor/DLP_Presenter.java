package ru.aakumykov.me.mvp.dynamic_link_processor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.models.User;
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
                                processDeepLink(deepLink, intent);
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
    private void processDeepLink(Uri deepLink, @NonNull Intent intent) {
        try {
            String emailLink = intent.getData().toString();
            final FirebaseAuth auth = FirebaseAuth.getInstance();

            SharedPreferences sharedPreferences = view.getAppContext().getSharedPreferences(Constants.SHARED_PREFERENCES_EMAIL, Context.MODE_PRIVATE);
            if (sharedPreferences.contains("email")) {
                String storedEmail = sharedPreferences.getString("email", "");

                if (auth.isSignInWithEmailLink(emailLink)) {
                    auth.signInWithEmailLink(storedEmail, emailLink)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    String userId = authResult.getUser().getUid();
                                    verifyEmail(userId);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    onErrorOccured(e.getMessage());
                                    e.printStackTrace();
                                }
                            });
                }
            } else {
                onErrorOccured("There is no locally stored email.");
            }

        } catch (Exception e) {
            onErrorOccured(e.getMessage());
            e.printStackTrace();
        }
    }


    private void verifyEmail(String userId) {

        try {
            usersService.setEmailVerified(userId, true, new iUsersSingleton.EmailVerificationCallbacks() {
                @Override
                public void OnEmailVerificationSuccess() {
                    view.showToast(R.string.DLP_email_verified);
                    view.goHomePage();
                }

                @Override
                public void OnEmailVerificationFail(String errorMsg) {
                    onErrorOccured(errorMsg);
                }
            });

        } catch (Exception e) {
            onErrorOccured(e.getMessage());
            e.printStackTrace();
        }
    }

    private void onErrorOccured(String consoleMsg) {
//        authService.logout();
        FirebaseAuth.getInstance().signOut();
        view.showErrorMsg(R.string.DLP_error_processing_link, consoleMsg);
        view.showHomeButton();
    }
}
