package ru.aakumykov.me.sociocat.user_edit;

import android.content.Intent;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.user_edit.stubs.UserEdit_ViewStub;
import ru.aakumykov.me.sociocat.utils.ImageType;
import ru.aakumykov.me.sociocat.utils.ImageUtils;


class UserEdit_Presenter implements iUserEdit.iPresenter {

    private iUserEdit.iView view;
    private User editedUser;
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private ImageType avatarImageType;
    private Bitmap avatarBitmap;


    @Override
    public void linkView(iUserEdit.iView view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = new UserEdit_ViewStub();
    }

    @Override
    public boolean hasUser() {
        return null != editedUser;
    }

    @Override
    public void onFirstOpen(@Nullable Intent intent) {
        if (isGuest()) {
            view.requestLogin(intent);
            return;
        }

        // Не буду проверять Intent на null, так как страница используется внутри приложения.

        String userId = intent.getStringExtra(Constants.USER_ID);
        loadAndShowUser(userId);
    }

    @Override
    public void onConfigChanged() {
        if (isGuest()) {
            Intent intent = new Intent();
            intent.putExtra(Constants.USER_ID, editedUser.getKey());
            view.requestLogin(intent);
            return;
        }

        view.fillEditForm(editedUser, editedUser.getAvatarURL());
    }

    @Override
    public void onImageSelected(Bitmap bitmap, ImageType imageType) {
        this.avatarImageType = imageType;

        Bitmap bitmapCopy = bitmap.copy(bitmap.getConfig(), true);
        avatarBitmap = ImageUtils.scaleDownBitmap(bitmapCopy, Config.AVATAR_MAX_SIZE);

        view.displayAvatar(avatarBitmap);
    }

    @Override
    public void onUserLoggedOut() {

    }

    @Override
    public void onSaveUserClicked() {

    }

    @Override
    public void onCancelButtonClicked() {

    }

    @Override
    public void onBackPressed() {

    }


    // Внутренние методы
    private boolean isGuest() {
        return !AuthSingleton.isLoggedIn();
    }

    private void loadAndShowUser(String userId) {
        view.disableEditForm();
        view.showProgressBar();

        usersSingleton.getUserById(userId, new iUsersSingleton.ReadCallbacks() {
            @Override
            public void onUserReadSuccess(User user) {
                editedUser = user;
                view.hideProgressBar();
                view.fillEditForm(user, user.getAvatarURL());
                view.enableEditForm();
            }

            @Override
            public void onUserReadFail(String errorMsg) {
                view.showErrorMsg(R.string.USER_EDIT_error_loading_data, errorMsg);
                // TODO: показать кнопку "Попробовать ещё" или закрыть страницу, показав Toast?
            }
        });
    }
}
