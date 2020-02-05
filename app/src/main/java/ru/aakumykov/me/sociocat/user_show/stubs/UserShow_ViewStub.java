package ru.aakumykov.me.sociocat.user_show.stubs;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.BaseView_Stub;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.user_show.iUserShow;

@SuppressLint("Registered")
public class UserShow_ViewStub extends BaseView_Stub implements iUserShow.iView {

    @Override
    public void goUserEdit(String userId) {

    }

    @Override
    public void displayUser(User user, boolean isPrivateMode) {

    }

    @Override
    public void setState(iUserShow.ViewState viewState, int messageId) {

    }

    @Override
    public void setState(iUserShow.ViewState viewState, int messageId, @Nullable Object payload) {

    }

    @Override
    public void showRefreshThrobber() {

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
