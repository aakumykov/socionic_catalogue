package ru.aakumykov.me.sociocat.users.view_model;

import androidx.lifecycle.ViewModel;

import ru.aakumykov.me.sociocat.users.iUsers;
import ru.aakumykov.me.sociocat.users.list.UsersListAdapter;

public class Users_ViewModel extends ViewModel {

    private iUsers.Presenter presenter;
    private UsersListAdapter listAdapter;

    public boolean hasPresenter() {
        return null != presenter;
    }
    public void storePresenter(iUsers.Presenter presenter) {
        this.presenter = presenter;
    }
    public iUsers.Presenter getPresenter() {
        return presenter;
    }

    public boolean hasListAdapter() {
        return null != listAdapter;
    }
    public void storeListAdapter(UsersListAdapter listAdapter) {
        this.listAdapter = listAdapter;
    }
    public UsersListAdapter getListAdapter() {
        return listAdapter;
    }
}
