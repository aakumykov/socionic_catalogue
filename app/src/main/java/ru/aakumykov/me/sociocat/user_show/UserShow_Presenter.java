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
    private User mCurrentUser;
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
        return null != mCurrentUser;
    }

    @Override
    public void onFirstOpen(@Nullable Intent intent) {

        if (null == intent) {
            view.setState(iUserShow.ViewState.ERROR, R.string.USER_SHOW_data_error, "Intent is null");
            return;
        }

        String userId = intent.hasExtra(Constants.USER_ID) ?
                intent.getStringExtra(Constants.USER_ID) :
                AuthSingleton.currentUserId();

        if (null == userId) {
            view.setState(iUserShow.ViewState.ERROR, R.string.USER_SHOW_data_error, "There is no userId in Intent");
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
        if (null != mCurrentUser)
            loadAndShowUser(mCurrentUser.getKey());
        else
            view.setState(currentViewState, currentMessageId, currentMessagePayload);
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
            mCurrentUser = null;
            return;
        }

        mCurrentUser = data.getParcelableExtra(Constants.USER);

        showUserProfile();
    }

    @Override
    public boolean canEditUser() {
        String currentUserId = AuthSingleton.currentUserId();

        if (null == currentUserId)
            return false;

        if (null == mCurrentUser)
            return false;

        return usersSingleton.currentUserIsAdmin() || currentUserId.equals(mCurrentUser.getKey());
    }

    @Override
    public void onEditClicked() {
        if (!isGuest()) {
            view.goUserEdit(mCurrentUser.getKey());
        } else {
            view.showToast(R.string.not_authorized);
        }
    }

    @Override
    public void onShowUserCardsClicked() {
        view.goShowUserCards(mCurrentUser.getKey());
    }

    @Override
    public void onShowUserCommentsClicked() {
        view.goShowUserComments(mCurrentUser.getKey());
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
                mCurrentUser = user;
                showUserProfile();
            }

            @Override
            public void onUserReadFail(String errorMsg) {
                view.setState(iUserShow.ViewState.ERROR, R.string.USER_SHOW_data_error, errorMsg);
            }
        });
    }

    private void showUserProfile() {

        if (null != mCurrentUser) {

            String profileUserId = mCurrentUser.getKey();
            String currentUserId = AuthSingleton.currentUserId();

            boolean isPrivateMode =
                    profileUserId.equals(currentUserId) ||
                            UsersSingleton.getInstance().currentUserIsAdmin();

            if (isPrivateMode)
                view.setState(iUserShow.ViewState.SHOW_PRIVATE, -1, mCurrentUser);
            else
                view.setState(iUserShow.ViewState.SHOW_PUBLIC, -1, mCurrentUser);

        }
    }
}
