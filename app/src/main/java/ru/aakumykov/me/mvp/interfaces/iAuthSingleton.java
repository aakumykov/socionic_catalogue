package ru.aakumykov.me.mvp.interfaces;

import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.models.User;

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


    interface RegisterCallbacks {
        void onRegSucsess(String userId, String email);
        void onRegFail(String errorMessage);
    }

    interface LoginCallbacks {
        void onLoginSuccess(User user);
        void onLoginFail(String errorMsg);
    }

    interface UserRestoreCallbacks {
        void onUserRestoreSuccess();
        void onUserRestoreFail(String errorMsg);
    }
}
