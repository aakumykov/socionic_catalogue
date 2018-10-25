package ru.aakumykov.me.mvp.users_list;

import android.util.Log;

import java.util.List;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.services.UsersSingleton;

public class UsersList_Presenter implements
    iUsersList.Presenter,
    iUsersSingleton.ListCallbacks
{

    private final static String TAG = "UsersList_Presenter";
    private iUsersList.View view;
    private UsersSingleton usersSingleton = UsersSingleton.getInstance();

    // Системные методы
    @Override
    public void linkView(iUsersList.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }


    // Пользовательские методы
    @Override
    public void loadList() {
        Log.d(TAG, "loadList()");
        usersSingleton.listUsers(this);
    }

    @Override
    public void listItemClicked(String key) {
        Log.d(TAG, "listItemClicked("+key+")");
        view.goUserPage(key);
    }


    // Коллбеки
    @Override
    public void onListRecieved(List<User> list) {
        Log.d(TAG, "onListRecieved()");
        view.displayList(list);
    }

    @Override
    public void onListFail(String errorMsg) {
        Log.d(TAG, "onListFail()");
        view.showErrorMsg(R.string.error_loading_list, errorMsg);
    }

//    @Override
//    public void onUserReadSuccess(User user) {
//        Log.d(TAG, "onUserReadSuccess("+user+")");
//        view.displayUser(user);
//    }
//
//    @Override
//    public void onUserReadFail(String errorMsg) {
//        Log.d(TAG, "onUserReadFail("+errorMsg+")");
//        view.showErrorMsg(R.string.error_reading_user);
//    }


    // Внутренние методы
    void loadUser(String userId) {

    }
}
