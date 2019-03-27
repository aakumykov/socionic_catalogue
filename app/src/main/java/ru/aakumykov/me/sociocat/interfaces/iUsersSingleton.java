package ru.aakumykov.me.sociocat.interfaces;

import java.util.List;

import ru.aakumykov.me.sociocat.models.User;

// TODO: глобальный goOffline() для Firebase

public interface iUsersSingleton {

    public final static String PUSH_TOKEN_NAME = "push_token";

    void createUser(String userId, String userName, String email, CreateCallbacks callbacks);
    void getUserById(String id, ReadCallbacks callbacks);
    void getUserByEmail(String email, ReadCallbacks callbacks);
    void saveUser(User user, SaveCallbacks callbacks);
    void deleteUser(User user, DeleteCallbacks callbacks);
    void listUsers(ListCallbacks callbacks);
    void checkNameExists(String name, CheckExistanceCallbacks callbacks);
    void checkEmailExists(String email, CheckExistanceCallbacks callbacks);
    void setEmailVerified(String userId, boolean isVerified, final EmailVerificationCallbacks callbacks);
    void updatePushToken(String token, PushTokenCallbacks callbacks);

    interface CreateCallbacks {
        void onUserCreateSuccess(User user);
        void onUserCreateFail(String errorMsg);
    }

    interface ReadCallbacks {
        void onUserReadSuccess(User user);
        void onUserReadFail(String errorMsg);
    }

    interface SaveCallbacks {
        void onUserSaveSuccess(User user);
        void onUserSaveFail(String errorMsg);
    }

    interface DeleteCallbacks {
        void onUserDeleteSuccess(User user);
        void onUserDeleteFail(String errorMsg);
    }

    interface ListCallbacks {
        void onListRecieved(List<User> usersList);
        void onListFail(String errorMsg);
    }

    interface CheckExistanceCallbacks {
        void onCheckComplete();
        void onExists();
        void onNotExists();
        void onCheckFail(String errorMsg);
    }

    interface EmailVerificationCallbacks {
        void OnEmailVerificationSuccess();
        void OnEmailVerificationFail(String errorMsg);
    }

    interface PushTokenCallbacks {
        void onPushTokenUpdateSuccess(String token);
        void onPushTokenUpdateError(String errorMsg);
    }
}
