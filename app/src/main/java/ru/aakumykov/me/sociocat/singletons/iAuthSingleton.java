package ru.aakumykov.me.sociocat.singletons;

import androidx.annotation.NonNull;

public interface iAuthSingleton {

    interface ResetPasswordCallbacks {
        void onEmailSendSuccess();
        void onEmailSendFail(String errorMsg);
    }

    interface CreateFirebaseCustomToken_Callbacks {
        void onCreateFirebaseCustomToken_Success(String customToken);
        void onCreateFirebaseCustomToken_Error(String errorMsg);
    }

    interface CheckPasswordCallbacks {
        void onUserCredentialsOk();
        void onUserCredentialsNotOk(String errorMsg);
    }

    interface SendSignInLinkCallbacks {
        void onSignInLinkSendSuccess();
        void onSignInLinkSendFail(String errorMsg);
    }

    interface EmailLinkSignInCallbacks {
        void onEmailLinkSignInSuccess();
        void onEmailLinkSignInError(String errorMsg);
    }


    class iAuthSingletonException extends Exception {
        public iAuthSingletonException(String message) {
            super(message);
        }
    }
}
