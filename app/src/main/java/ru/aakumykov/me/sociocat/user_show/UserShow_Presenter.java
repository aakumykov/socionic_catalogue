package ru.aakumykov.me.sociocat.user_show;

import android.content.Intent;

import androidx.annotation.Nullable;

import java.util.Date;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.user_show.models.Item;
import ru.aakumykov.me.sociocat.user_show.stubs.UserShow_ViewStub;


class UserShow_Presenter implements iUserShow.iPresenter {

    private final static String TAG = "UserShow_Presenter";
    private iUserShow.iView view;
    private User currentUser;
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();


    @Override
    public void linkView(iUserShow.iView view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = new UserShow_ViewStub();
    }

    @Override
    public boolean hasUser() {
        return null != currentUser;
    }

    @Override
    public void onFirstOpen(@Nullable Intent intent) {
        if (null == intent) {
            view.showErrorMsg(R.string.USER_SHOW_error_displaying_user, "Intent is null");
            return;
        }

        String userId = AuthSingleton.currentUserId();

        view.showProgressBar();

        usersSingleton.getUserById(userId, new iUsersSingleton.ReadCallbacks() {
            @Override
            public void onUserReadSuccess(User user) {
                view.hideProgressBar();
                view.displayUser(user);
            }

            @Override
            public void onUserReadFail(String errorMsg) {
                view.showErrorMsg(R.string.USER_SHOW_error_displaying_user, errorMsg);
            }
        });
    }

    @Override
    public void onConfigChanged() {
        view.displayUser(currentUser);
    }

    @Override
    public void onRefreshRequested() {

    }

    // Внутренние методы
    private Item getItem() {
        String timeString = new Date().toString();
        return new Item("Объект-"+timeString, "Привет, я Объект-"+timeString);
    }
}
