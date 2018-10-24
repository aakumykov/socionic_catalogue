package ru.aakumykov.me.mvp.users_list;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iUsers;
import ru.aakumykov.me.mvp.models.User;

public class UsersList_View extends BaseView implements
        iUsersList.View
{
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.listView) ListView listView;

    private final static String TAG = "UsersList_Presenter";
    private UsersListAdapter usersListAdapter;
    private ArrayList<User> usersList;
    private iUsersList.Presenter presenter;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_list_activity);
        ButterKnife.bind(this);

        usersList = new ArrayList<>();
        usersListAdapter = new UsersListAdapter(this, R.layout.users_list_item, usersList);
        listView.setAdapter(usersListAdapter);

        presenter = new UsersList_Presenter();
        presenter.loadList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    public void onServiceBounded() {

    }
    @Override
    public void onServiceUnbounded() {

    }


    // Методы интерфейса
    @Override
    public void showProgressBar() {
        MyUtils.show(progressBar);
    }

    @Override
    public void hideProgressBar() {
        MyUtils.hide(progressBar);
    }

    @Override
    public void displayList(List<User> list) {
        Log.d(TAG, "displayList()");
        hideProgressBar();
        usersList.addAll(list);
        usersListAdapter.notifyDataSetChanged();
    }
}
