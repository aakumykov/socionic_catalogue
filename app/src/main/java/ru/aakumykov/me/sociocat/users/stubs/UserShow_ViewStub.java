package ru.aakumykov.me.sociocat.users.stubs;

import android.annotation.SuppressLint;

import java.util.List;

import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.users.iUsers;

@SuppressLint("Registered")
public class UserShow_ViewStub extends Users_ViewStub implements iUsers.ShowView {

    @Override
    public void displayUser(User user) {

    }

    @Override
    public void goUserEdit() {

    }

    @Override
    public void setPageTitle(String userName) {

    }
}
