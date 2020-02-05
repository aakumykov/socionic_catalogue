package ru.aakumykov.me.sociocat.user_show;

import android.content.Intent;

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
    private User profileUser;
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private iUserShow.ViewState currentViewState;
    private int currentMessageId;
    private Object currentMessagePayload;


    @Override
    public void linkView(iUserShow.iView view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = new UserShow_ViewStub();
    }

    @Override
    public void storeViewState(iUserShow.ViewState viewState, int messageId, Object messagePayload) {
        currentViewState = viewState;
        currentMessageId = messageId;
        currentMessagePayload = messagePayload;
    }

    @Override
    public boolean hasUser() {
        return null != profileUser;
    }

    @Override
    public void onFirstOpen(@Nullable Intent intent) {

        if (null == intent) {
            view.setState(iUserShow.ViewState.ERROR, R.string.USER_SHOW_error_displaying_user, "Intent is null");
            return;
        }

        String userId = intent.hasExtra(Constants.USER_ID) ?
                intent.getStringExtra(Constants.USER_ID) :
                AuthSingleton.currentUserId();

        if (null == userId) {
            view.setState(iUserShow.ViewState.ERROR, R.string.USER_SHOW_error_displaying_user, "There is no userId in Intent");
            return;
        }

        loadAndShowUser(userId);
    }

    @Override
    public void onConfigChanged() {
        showUserProfile();
    }

    @Override
    public void onRefreshRequested() {
        loadAndShowUser(profileUser.getKey());
    }

    @Override
    public void onUserLoggedOut() {
        showUserProfile();
    }

    @Override
    public void onUserLoggedIn() {
        showUserProfile();
    }

    @Override
    public void onUserEdited(@Nullable Intent data) {
        if (null == data) {
            view.setState(iUserShow.ViewState.ERROR, R.string.data_error, "There is no User in intent");
            profileUser = null;
            return;
        }

        profileUser = data.getParcelableExtra(Constants.USER);

        showUserProfile();
    }

    @Override
    public boolean canEditUser() {
        String currentUserId = AuthSingleton.currentUserId();

        if (null == currentUserId)
            return false;

        if (null == profileUser)
            return false;

        return usersSingleton.currentUserIsAdmin() || currentUserId.equals(profileUser.getKey());
    }

    @Override
    public void onEditClicked() {
        if (!isGuest()) {
            view.goUserEdit(profileUser.getKey());
        } else {
            view.showToast(R.string.not_authorized);
        }
    }


    // Внутренние методы
    private boolean isGuest() {
        return !AuthSingleton.isLoggedIn();
    }

    private void loadAndShowUser(String userId) {

        view.setState(iUserShow.ViewState.PROGRESS, -1, null);

        usersSingleton.getUserById(userId, new iUsersSingleton.ReadCallbacks() {
            @Override
            public void onUserReadSuccess(User user) {
                profileUser = user;
                showUserProfile();
            }

            @Override
            public void onUserReadFail(String errorMsg) {
                view.setState(iUserShow.ViewState.ERROR, R.string.USER_SHOW_error_displaying_user, errorMsg);
            }
        });
    }

    private void showUserProfile() {

        String profileUserId = profileUser.getKey();
        String currentUserId = AuthSingleton.currentUserId();

        boolean isPrivateMode =
                profileUserId.equals(currentUserId) ||
                        UsersSingleton.getInstance().currentUserIsAdmin();

        if (isPrivateMode)
            view.setState(iUserShow.ViewState.SHOW_PRIVATE, -1, profileUser);
        else
            view.setState(iUserShow.ViewState.SHOW_PUBLIC, -1, profileUser);
    }
}
