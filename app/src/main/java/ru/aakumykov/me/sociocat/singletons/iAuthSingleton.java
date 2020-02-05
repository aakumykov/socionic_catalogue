package ru.aakumykov.me.sociocat.singletons;

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

    interface LoginCallbacks {
        void onLoginSuccess(String userId);
        void onLoginError(String errorMsg);
    }

    interface EmailLinkSignInCallbacks extends LoginCallbacks {
        void onLoginLinkHasExpired();
    }

    interface CheckEmailExistsCallbacks {
        void onEmailExists();
        void onEmailNotExists();
        void onEmailCheckError(String errorMsg);
    }

    interface ChangePasswordCallbacks {
        void onChangePasswordSuccess();
        void onChangePasswordError(String errorMsg);
    }


    class iAuthSingletonException extends Exception {
        public iAuthSingletonException(String message) {
            super(message);
        }
    }
}
