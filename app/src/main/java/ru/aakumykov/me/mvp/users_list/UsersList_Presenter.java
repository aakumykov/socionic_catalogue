package ru.aakumykov.me.mvp.users_list;

import android.util.Log;

import ru.aakumykov.me.mvp.interfaces.iUsers;
import ru.aakumykov.me.mvp.services.UsersSingleton;

public class UsersList_Presenter implements
    iUsersList.Presenter
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
    public void loadList(iUsers.ListCallbacks callbacks) {
        Log.d(TAG, "loadList()");
        usersSingleton.listUsers(callbacks);
    }

}
