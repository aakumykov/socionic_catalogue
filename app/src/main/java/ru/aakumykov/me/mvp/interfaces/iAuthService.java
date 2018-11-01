package ru.aakumykov.me.mvp.interfaces;

import ru.aakumykov.me.mvp.models.User;

public interface iAuthService {

    boolean isAuthorized();
    boolean isAdmin();

    void registerWithEmail(
            String email,
            String password,
            RegisterCallbacks callbacks
    );

    void createUser(String uid, String name, CreateUserCallbacks callbacks);
//    void getUser(String uid);
//    void updateUser(String uid, String name);
//    void deleteUser(String uid)

    interface RegisterCallbacks {
        void onRegSucsess(String userId);
        void onRegFail(String errorMessage);
    }

    interface CreateUserCallbacks {
        void onCreateSuccess(User user);
        void onCreateFail(String errorMessage);
    }
}
