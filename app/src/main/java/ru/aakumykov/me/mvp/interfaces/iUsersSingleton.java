package ru.aakumykov.me.mvp.interfaces;

import java.util.List;

import ru.aakumykov.me.mvp.models.User;

// TODO: глобальный goOffline() для Firebase

public interface iUsersSingleton {

    void createUser(String userId, CreateCallbacks callbacks);
    void getUser(String id, ReadCallbacks callbacks);
    void saveUser(User user, SaveCallbacks callbacks);
    void deleteUser(User user, DeleteCallbacks callbacks);
    void listUsers(ListCallbacks callbacks);


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
}
