package ru.aakumykov.me.mvp.interfaces;

import ru.aakumykov.me.mvp.models.User;

public interface iAuthSingleton {

    void registerWithEmail(String email, String password, RegisterCallbacks callbacks) throws Exception;
    void login(String email, String password, LoginCallbacks callbacks) throws Exception;
    void logout();
    void cancelLogin(); // TODO: сделать при нажатии кнопки отмена, уходе со страницы

    User currentUser();
    String currentUserName();
    String currentUserId()/* throws Exception*/;
    boolean isUserLoggedIn();
    boolean userIsAdmin(String userId);

    void storeCurrentUser(User user);
    void clearCurrentUser();


    interface RegisterCallbacks {
        void onRegSucsess(String userId, String email);
        void onRegFail(String errorMessage);
    }

    interface LoginCallbacks {
        void onLoginSuccess();
        void onLoginFail(String errorMsg);
    }

}
