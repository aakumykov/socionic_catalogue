package ru.aakumykov.me.mvp.interfaces;

import ru.aakumykov.me.mvp.models.User;

public interface iAuthSingleton {

    boolean isUserLoggedIn();
    String currentUid();
    boolean isAdmin(String userId);

    void registerWithEmail(String email, String password, RegisterCallbacks callbacks) throws Exception;
    void createUser(String uid, User userDraft, CreateUserCallbacks callbacks) throws Exception;
    void login(String email, String password, LoginCallbacks callbacks) throws Exception;
    void logout();
    void cancelLogin();


    interface RegisterCallbacks {
        void onRegSucsess(String userId);
        void onRegFail(String errorMessage);
    }

    interface CreateUserCallbacks {
        void onCreateSuccess(User user);
        void onCreateFail(String errorMessage);
    }

    interface LoginCallbacks {
        void onLoginSuccess();
        void onLoginFail(String errorMsg);
    }

//    interface LogoutCallbacks {
//        void onLogoutSuccess();
//        void onLogoutFail(String errorMsg);
//    }
}
