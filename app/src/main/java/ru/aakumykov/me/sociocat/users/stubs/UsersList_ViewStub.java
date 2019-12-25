package ru.aakumykov.me.sociocat.users.stubs;

import android.annotation.SuppressLint;

import java.util.List;

import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.users.iUsers;

@SuppressLint("Registered")
public class UsersList_ViewStub extends Users_ViewStub implements iUsers.ListView {

    @Override
    public void displayList(List<User> list) {

    }

    @Override
    public void goUserPage(String userId) {

    }
}
