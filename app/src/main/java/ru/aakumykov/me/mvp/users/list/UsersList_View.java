package ru.aakumykov.me.mvp.users.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.utils.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.users.Users_Presenter;
import ru.aakumykov.me.mvp.users.iUsers;
import ru.aakumykov.me.mvp.users.show.UserShow_View;

public class UsersList_View extends BaseView implements
        iUsers.ListView,
        iUsersSingleton.ListCallbacks,
        SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener
{
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.listView) ListView listView;

    private final static String TAG = "UsersList_View";
    private UsersListAdapter usersListAdapter;
    private ArrayList<User> usersList;
    private iUsers.Presenter presenter;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_list_activity);
        ButterKnife.bind(this);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_swipe, R.color.green_swipe, R.color.orange_swipe, R.color.red_swipe);

        usersList = new ArrayList<>();
        usersListAdapter = new UsersListAdapter(this, R.layout.users_list_item, usersList);
        listView.setAdapter(usersListAdapter);
        listView.setOnItemClickListener(this);

        presenter = new Users_Presenter();

        presenter.loadList(this);
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
    public void onRefresh() {
        Log.d(TAG, "onRefresh()");
        presenter.loadList(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick(..., position: "+position+", id: "+id+")");

        User user = usersList.get(position);
        Log.d(TAG, user.toString());

        String userId = usersList.get(position).getKey();
        presenter.listItemClicked(userId);
    }


    // Обязательные методы
    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }


    // Интерфейсные методы
    @Override
    public void displayList(List<User> list) {
        Log.d(TAG, "displayList()");

        hidePageProgressBar();
        hideSwipeProgressBar();

        usersList.clear();
        usersListAdapter.notifyDataSetChanged();

        usersList.addAll(list);
        usersListAdapter.notifyDataSetChanged();
    }

    @Override
    public void goUserPage(String userId) {
        Log.d(TAG, "goUserEditPage("+userId+")");
        Intent intent = new Intent(this, UserShow_View.class);
        intent.putExtra(Constants.USER_ID, userId);
        startActivity(intent);
    }


    // Методы интерфейса
    private void showPageProgressBar() {
        MyUtils.show(progressBar);
    }
    private void hidePageProgressBar() {
        MyUtils.hide(progressBar);
    }
    private void hideSwipeProgressBar() {
        swipeRefreshLayout.setRefreshing(false);
    }


    // Коллбеки
    @Override
    public void onListRecieved(List<User> usersList) {
        Log.d(TAG, "onListRecieved()");
        displayList(usersList);
    }

    @Override
    public void onListFail(String errorMsg) {
        Log.d(TAG, "onListFail()");
        showErrorMsg(R.string.error_loading_list, errorMsg);
    }

}
