package ru.aakumykov.me.sociocat.user_show.stubs;

import android.annotation.SuppressLint;

import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.BaseView_Stub;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.user_show.iUserShow;
import ru.aakumykov.me.sociocat.user_show.models.Item;

@SuppressLint("Registered")
public class UserShow_ViewStub extends BaseView_Stub implements iUserShow.iView {

    @Override
    public void displayUser(User user) {

    }

    @Override
    public void hideRefreshThrobber() {

    }

    @Override
    public void showAvatarTrobber() {

    }

    @Override
    public void hideAvatarThrobber() {

    }
}
