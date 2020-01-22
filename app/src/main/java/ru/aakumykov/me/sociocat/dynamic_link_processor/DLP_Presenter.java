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
import ru.aakumykov.me.sociocat.DeepLink_Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.login.Login_View;
import ru.aakumykov.me.sociocat.register.register_step_2.RegisterStep2_View;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.utils.MyUtils;


public class DLP_Presenter implements iDLP.Presenter {

    private static final String TAG = "DLP_Presenter";
    private iDLP.View view;
    private FirebaseDynamicLinks firebaseDynamicLinks = FirebaseDynamicLinks.getInstance();
    private iAuthSingleton authSingleton = AuthSingleton.getInstance();
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();


    @Override
    public void linkView(iDLP.View view) {
        this.view = view;
    }
    @Override
    public void unlinkView() {
        this.view = new DLP_ViewStub();
    }


    @Override
    public void processDynamicLink(Activity activity, @Nullable final Intent intent) {

        if (null == intent) {
            view.showLongToast(R.string.DLP_error_processing_link);
            view.closePage();
            return;
        }

        view.showProgressMessage(R.string.DLP_processing_dynamic_link);

        firebaseDynamicLinks
                .getDynamicLink(intent)
                .addOnSuccessListener(activity, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        chooseActionFromDeepLink(
                                pendingDynamicLinkData.getLink(),
                                intent
                        );
                    }
                })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        MyUtils.printError(TAG, e);
                        view.showLongToast(R.string.DLP_error_processing_link);
                        view.closePage();
                    }
                });
    }


    // Внутренние методы
    private void chooseActionFromDeepLink(Uri deepLink, @NonNull Intent intent){

        String emailLink = intent.getDataString();

        if (null != emailLink && FirebaseAuth.getInstance().isSignInWithEmailLink(emailLink)) {
            processEmailSignInDeepLink(deepLink, intent);
        }
        else {
            processNonSignInDeepLink(deepLink);
        }

    }

    private void processEmailSignInDeepLink(Uri deepLink, @NonNull Intent intent) {

        Uri continueUrl = Uri.parse(deepLink.getQueryParameter("continueUrl"));

        String continueUrlPath = continueUrl.getPath() + "";

        switch (continueUrlPath) {
            case DeepLink_Constants.REGISTRATION_STEP2_PATH:
                registrationStep2(intent);
                break;

            case DeepLink_Constants.CONFIRM_EMAIL_PATH:

                break;

            default:
                view.showLongToast(R.string.DLP_unknown_action);
        }
    }

    private void processNonSignInDeepLink(Uri deepLink) {

        String deepLinkPath = deepLink.getPath();

        switch (deepLinkPath) {
            case DeepLink_Constants.PASSWORD_RESET_PATH:
                resetPasswordStep2(deepLink);
                break;

            default:
                view.showLongToast(R.string.DLP_unknown_action);
        }
    }

    private void registrationStep2(@NonNull Intent inputIntent) {
        String emailURL = inputIntent.getDataString();
        Intent intent = new Intent(view.getAppContext(), RegisterStep2_View.class);
        intent.putExtra("emailSignInURL", emailURL);
        view.startSomeActivity(intent);
    }

    private void resetPasswordStep2(Uri deepLink) {
//        Intent intent = new Intent(view.getAppContext(), ResetPasswordStep2_View.class);
//        intent.putExtra("deepLink", deepLink);
        Intent intent = new Intent(view.getAppContext(), Login_View.class);
        intent.setAction(Constants.ACTION_TRY_NEW_PASSWORD);
        view.startSomeActivity(intent);
    }


/*
    public static class DLP_Exception extends Exception {
        public DLP_Exception(String message) {
            super(message);
        }
    }

    public static class NoContinueURLException extends DLP_Exception {
        public NoContinueURLException(String message) {
            super(message);
        }
    }

    public static class NoDeepLinkPathException extends DLP_Exception {
        public NoDeepLinkPathException(String message) {
            super(message);
        }
    }
*/
}
