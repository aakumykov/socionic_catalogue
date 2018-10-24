package ru.aakumykov.me.mvp.interfaces;

import ru.aakumykov.me.mvp.models.User;

public interface iUsers {

    void listUsers(ListCallbacks callbacks);
    void createUser(String name, String email);
    void getUser(String id);
    void saveUser(User user);
    void deleteUser(User user);
//    boolean userExists(String id);

    interface ListCallbacks {
        void onListSuccess();
        void onListFail(String errorMsg);
    }
}
