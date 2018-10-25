package ru.aakumykov.me.mvp.users_list;

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
import ru.aakumykov.me.mvp.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.user_page.UserPage_View;

public class UsersList_View extends BaseView implements
        iUsersList.View,
        SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener
{
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
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

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_swipe, R.color.green_swipe, R.color.orange_swipe, R.color.red_swipe);

        usersList = new ArrayList<>();
        usersListAdapter = new UsersListAdapter(this, R.layout.users_list_item, usersList);
        listView.setAdapter(usersListAdapter);
        listView.setOnItemClickListener(this);

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
    public void onRefresh() {
        Log.d(TAG, "onRefresh()");
        presenter.loadList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick(..., position: "+position+", id: "+id);
        String userId = usersList.get(position).getKey();
        presenter.listItemClicked(userId);
    }

    @Override
    public void onServiceBounded() {

    }
    @Override
    public void onServiceUnbounded() {

    }


    // Методы интерфейса
    @Override
    public void showPageProgressBar() {
        MyUtils.show(progressBar);
    }

    @Override
    public void hidePageProgressBar() {
        MyUtils.hide(progressBar);
    }

    @Override
    public void hideSwipeProgressBar() {
        swipeRefreshLayout.setRefreshing(false);
    }


    // Пользовательские методы
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
        Intent intent = new Intent(this, UserPage_View.class);
        intent.putExtra(Constants.USER_ID, userId);
        startActivity(intent);
    }
}
