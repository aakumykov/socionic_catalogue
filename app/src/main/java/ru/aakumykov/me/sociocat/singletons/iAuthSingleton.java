package ru.aakumykov.me.sociocat.singletons;

import androidx.annotation.NonNull;

public interface iAuthSingleton {

    void resetPasswordEmail(String email, ResetPasswordCallbacks callbacks);

    void checkUserCredentials(String email, String password, @NonNull CheckUserCredentialsCallbacks callbacks) throws iAuthSingletonException;

    interface ResetPasswordCallbacks {
        void onEmailSendSuccess();
        void onEmailSendFail(String errorMsg);
    }

    interface CreateFirebaseCustomToken_Callbacks {
        void onCreateFirebaseCustomToken_Success(String customToken);
        void onCreateFirebaseCustomToken_Error(String errorMsg);
    }

    interface CheckUserCredentialsCallbacks {
        void onUserCredentialsOk();
        void onUserCredentialsNotOk(String errorMsg);
    }



    class iAuthSingletonException extends Exception {
        public iAuthSingletonException(String message) {
            super(message);
        }
    }


}
