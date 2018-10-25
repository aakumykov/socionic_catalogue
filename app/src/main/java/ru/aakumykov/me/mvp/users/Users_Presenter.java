package ru.aakumykov.me.mvp.users;

import android.util.Log;

import java.util.List;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.services.UsersSingleton;
import ru.aakumykov.me.mvp.users.list.UsersList_View;

public class Users_Presenter implements
        iUsers.Presenter,
        iUsersSingleton.ListCallbacks,
        iUsersSingleton.UserCallbacks
{

    private final static String TAG = "Users_Presenter";
    private iUsers.ShowView showView;
    private iUsers.ListView listView;
    private UsersSingleton usersSingleton = UsersSingleton.getInstance();


    // Системные методы
    @Override
    public void linkView(iUsers.ShowView showView) {
        Log.d(TAG, "linkView()");
        this.showView = showView;

//        listView instanceof iUsers.ListView ?  : null;
    }
    @Override
    public void linkView(iUsers.ListView listView) {
        Log.d(TAG, "linkView()");
        this.listView = listView;
    }
    @Override
    public void unlinkView() {
        Log.d(TAG, "unlinkView()");
        this.listView = null;
        this.showView = null;
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
        listView.goUserPage(key);
    }

    @Override
    public void loadUser(String userId) throws Exception {
        Log.d(TAG, "loadUser("+userId+")");
        if (null == userId) {
            throw new Exception("userId == null");
        }
        usersSingleton.getUser(userId, this);
    }


    // Коллбеки
    @Override
    public void onUserReadSuccess(User user) {
        showView.displayUser(user);
    }

    @Override
    public void onUserReadFail(String errorMsg) {
        showView.showErrorMsg(R.string.error_displaying_user, errorMsg);
    }

    @Override
    public void onListRecieved(List<User> usersList) {
        Log.d(TAG, "onListRecieved()");
        listView.displayList(usersList);
    }

    @Override
    public void onListFail(String errorMsg) {
        Log.d(TAG, "onListFail()");
        listView.showErrorMsg(R.string.error_loading_list, errorMsg);
    }
}
