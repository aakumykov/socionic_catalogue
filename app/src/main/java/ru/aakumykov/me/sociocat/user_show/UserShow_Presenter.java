package ru.aakumykov.me.sociocat.user_show;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.user_show.stubs.UserShow_ViewStub;


class UserShow_Presenter implements iUserShow.iPresenter {

    private final static String TAG = "UserShow_Presenter";
    private iUserShow.iView view;
    private User displayedUser;
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
        return null != displayedUser;
    }

    @Override
    public void onFirstOpen(@Nullable Intent intent) {
        if (isGuest()) {
            view.requestLogin(intent);
            return;
        }

        if (null == intent) {
            view.showErrorMsg(R.string.USER_SHOW_error_displaying_user, "Intent is null");
            return;
        }

        String userId = intent.hasCategory(Constants.USER_ID) ?
                intent.getStringExtra(Constants.USER_ID) :
                AuthSingleton.currentUserId();

        if (null == userId) {
            view.showErrorMsg(R.string.USER_SHOW_error_displaying_user, "There is no userId in Intent");
            return;
        }

        loadAndShowUser(userId);
    }

    @Override
    public void onConfigChanged() {
        Intent intent = new Intent();
        intent.putExtra(Constants.USER_ID, displayedUser.getKey());

        if (isGuest()) {
            view.requestLogin(intent);
            return;
        }

        view.displayUser(displayedUser);
    }

    @Override
    public void onRefreshRequested() {
        Intent intent = new Intent();
        intent.putExtra(Constants.USER_ID, displayedUser.getKey());

        if (isGuest()) {
            view.requestLogin(intent);
            return;
        }

        view.hideRefreshThrobber();

        loadAndShowUser(displayedUser.getKey());
    }

    @Override
    public void onUserLoggedOut() {
        view.closePage();
    }

    @Override
    public boolean canEditUser() {
        String currentUserId = AuthSingleton.currentUserId();

        if (null == currentUserId)
            return false;

        if (null == displayedUser)
            return false;

        return usersSingleton.currentUserIsAdmin() || currentUserId.equals(displayedUser.getKey());
    }

    @Override
    public void onEditClicked() {
        if (!isGuest()) {
            view.goUserEdit(displayedUser.getKey());
        } else {
            view.showToast(R.string.action_denied);
        }
    }


    // Внутренние методы
    private boolean isGuest() {
        return !AuthSingleton.isLoggedIn();
    }

    private void loadAndShowUser(String userId) {
        view.showProgressBar();

        usersSingleton.getUserById(userId, new iUsersSingleton.ReadCallbacks() {
            @Override
            public void onUserReadSuccess(User user) {
                displayedUser = user;
                view.hideProgressBar();
                view.hideRefreshThrobber();
                view.displayUser(displayedUser);
                view.refreshMenu();
            }

            @Override
            public void onUserReadFail(String errorMsg) {
                view.showErrorMsg(R.string.USER_SHOW_error_displaying_user, errorMsg);
            }
        });
    }
}
