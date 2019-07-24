package ru.aakumykov.me.sociocat.singletons;

public interface iAuthSingleton {

    void resetPasswordEmail(String email, ResetPasswordCallbacks callbacks);

    interface ResetPasswordCallbacks {
        void onEmailSendSuccess();
        void onEmailSendFail(String errorMsg);
    }

    interface CreateFirebaseCustomToken_Callbacks {
        void onCreateFirebaseCustomToken_Success(String customToken);
        void onCreateFirebaseCustomToken_Error(String errorMsg);
    }
}
