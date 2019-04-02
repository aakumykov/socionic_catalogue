package ru.aakumykov.me.sociocat.interfaces;

import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.User;

public interface iAuthSingleton {

    void registerWithEmail(String email, String password, RegisterCallbacks callbacks) throws Exception;
    void login(String email, String password, LoginCallbacks callbacks) throws Exception;
    void logout();
    void cancelLogin(); // TODO: сделать при нажатии кнопки отмена, уходе со страницы

    void restoreCurrentUser(UserRestoreCallbacks callbacks);
    User currentUser();
    String currentUserName();
    String currentUserId()/* throws Exception*/;
    boolean isUserLoggedIn();
    boolean isAdmin();
    boolean userIsAdmin(String userId);
    boolean isCardOwner(Card card);

    void storeCurrentUser(User user);
    void clearCurrentUser();

    void sendSignInLinkToEmail(String email, SendSignInLinkToEmailCallbacks callbacks);
    void sendEmailVerificationLink(String packageName, SendEmailVerificationLinkCallbacks callbacks);
    void resetPasswordEmail(String email, ResetPasswordCallbacks callbacks);


    interface RegisterCallbacks {
        void onRegSucsess(String userId, String email);
        void onRegFail(String errorMessage);
    }

    interface LoginCallbacks {
        void onLoginSuccess(String userId);
        void onLoginFail(String errorMsg);
    }

    interface UserRestoreCallbacks {
        void onUserRestoreSuccess();
        void onUserRestoreFail(String errorMsg);
    }

    interface SendSignInLinkToEmailCallbacks {
        void onSendSignInLinkToEmailSuccess();
        void onSendSignInLinkToEmailFail(String errorMsg);
    }

    interface SendEmailVerificationLinkCallbacks {
        void onEmailVerificationLinkSendSuccess();
        void onEmailVerificationLinkSendFail(String errorMsg);
    }

    interface ResetPasswordCallbacks {
        void onEmailSendSuccess();
        void onEmailSendFail(String errorMsg);
    }
}
