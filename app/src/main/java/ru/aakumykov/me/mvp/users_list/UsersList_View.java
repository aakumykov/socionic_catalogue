package ru.aakumykov.me.mvp.users_list;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iUsers;
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.services.UsersSingleton;

public class UsersList_View extends BaseView implements
        iUsers.ListCallbacks
{
    @BindView(R.id.listView) ListView listView;

    private final static String TAG = "UsersList_Presenter";
    private UsersSingleton usersSingleton;
    private UsersListAdapter usersListAdapter;
    private ArrayList<User> usersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list__view);
        ButterKnife.bind(this);

        usersSingleton = UsersSingleton.getInstance();

        usersListAdapter = new UsersListAdapter(this, R.layout.users_list_item, usersList);
        listView.setAdapter(usersListAdapter);

        loadList();
    }

    @Override
    public void onServiceBounded() {

    }

    @Override
    public void onServiceUnbounded() {

    }

    private void loadList() {
        Log.d(TAG, "loadList()");

        usersSingleton.listUsers(this);


    }

    @Override
    public void onListRecieved(List<User> list) {
        Log.d(TAG, "onListRecieved()");
        usersList.addAll(list);
//        Log.d(TAG, "usersList: "+usersList);
        usersListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListFail(String errorMsg) {
        Log.d(TAG, "onListFail()");
        showErrorMsg(R.string.error_loading_list, errorMsg);
    }
}
