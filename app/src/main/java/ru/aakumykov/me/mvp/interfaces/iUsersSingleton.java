package ru.aakumykov.me.mvp.interfaces;

import android.content.Context;

import java.util.List;

import ru.aakumykov.me.mvp.models.User;

// TODO: глобальный goOffline() для Firebase

public interface iUsersSingleton {

    void createUser(String userId, String userName, String email, CreateCallbacks callbacks);
    void getUser(String id, ReadCallbacks callbacks);
    void saveUser(User user, SaveCallbacks callbacks);
    void deleteUser(User user, DeleteCallbacks callbacks);
    void listUsers(ListCallbacks callbacks);

    void checkNameExists(String name, CheckExistanceCallbacks callbacks);
    void checkEmailExists(String email, CheckExistanceCallbacks callbacks);

    void sendEmailVerificationLink(Context context, SendEmailVerificationLinkCallbacks callbacks);


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

    interface SendEmailVerificationLinkCallbacks {
        void onEmailVerificationLinkSendSuccess();
        void onEmailVerificationLinkSendFail(String errorMsg);
    }
}
