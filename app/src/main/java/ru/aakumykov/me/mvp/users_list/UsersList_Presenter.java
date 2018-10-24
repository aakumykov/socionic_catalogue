package ru.aakumykov.me.mvp.users_list;

import android.util.Log;

import java.util.List;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iUsers;
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.services.UsersSingleton;

public class UsersList_Presenter implements
    iUsersList.Presenter,
    iUsers.ListCallbacks
{

    private final static String TAG = "UsersList_Presenter";
    private iUsersList.View view;
    private UsersSingleton usersSingleton = UsersSingleton.getInstance();

    @Override
    public void linkView(iUsersList.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }

    @Override
    public void loadList() {
        Log.d(TAG, "loadList()");
        usersSingleton.listUsers(this);
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
}
