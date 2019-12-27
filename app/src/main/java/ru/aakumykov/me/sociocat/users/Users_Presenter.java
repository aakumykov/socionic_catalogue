package ru.aakumykov.me.sociocat.users;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.util.List;

import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.StorageSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iStorageSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.users.stubs.UserEdit_ViewStub;
import ru.aakumykov.me.sociocat.users.stubs.UserShow_ViewStub;
import ru.aakumykov.me.sociocat.users.stubs.UsersList_ViewStub;
import ru.aakumykov.me.sociocat.users.stubs.Users_ViewStub;
import ru.aakumykov.me.sociocat.utils.ImageUtils;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class Users_Presenter implements
        iUsers.Presenter,
        iUsersSingleton.ReadCallbacks,
        iUsersSingleton.SaveCallbacks,
        iStorageSingleton.FileUploadCallbacks,
        iCardsSingleton.ListCallbacks
{
    private final static String TAG = "Users_Presenter";

    private iUsers.ViewMode viewMode;

    private iUsers.View view;
    private iUsers.ShowView showView;
    private iUsers.ListView listView;
    private iUsers.EditView editView;
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private iStorageSingleton storageSingleton = StorageSingleton.getInstance();

    private String currentUserId;
    private User currentUser;
    private String editedUserId;
    private boolean imageSelected = false;
    private String imageType;
    private Bitmap avatarBitmap;


    // Системные методы
    @Override
    public void linkView(iUsers.View view) throws IllegalArgumentException {

        this.view = view;

        if (view instanceof iUsers.ListView) {
            this.listView = (iUsers.ListView) view;
            viewMode = iUsers.ViewMode.LIST;
        }
        else if (view instanceof iUsers.ShowView) {
            this.showView = (iUsers.ShowView) view;
            viewMode = iUsers.ViewMode.SHOW;
        }
        else if (view instanceof iUsers.EditView) {
            this.editView = (iUsers.EditView) view;
            viewMode = iUsers.ViewMode.EDIT;
        }
        else {
            throw new IllegalArgumentException("Unknown type of View '"+view.getClass()+"'");
        }
    }

    @Override
    public void unlinkView() {
        this.view =     new Users_ViewStub();
        this.showView = new UserShow_ViewStub();
        this.editView = new UserEdit_ViewStub();
        this.listView = new UsersList_ViewStub();
    }

    @Override
    public boolean hasUser() {
        return null != currentUser;
    }

    // TODO: кидать исключения
    @Override
    public void onFirstOpen() {
        if (checkAuthorization())
            loadUser();
    }

    @Override
    public void onConfigurationChanged() {
        if (checkAuthorization())
            displayUser();
    }


    // Пользовательские методы
    @Override
    public void setImageSelected(boolean isSelected) {
        imageSelected = isSelected;
    }

    @Override
    public void cancelButtonClicked() {
        Log.d(TAG, "cancelButtonClicked()");
        editView.closePage();
    }

    @Override
    public void onUserLoggedOut() {
        view.showToast(R.string.you_are_logged_out);
        view.closePage();
    }

    @Override
    public void onImageSelected(Bitmap bitmap) {
        this.avatarBitmap = ImageUtils.scaleDownBitmap(bitmap, Config.AVATAR_MAX_SIZE);
        editView.displayAvatar(avatarBitmap);
    }

    @Override
    public void onSaveUserClicked() {
        try {
            saveProfile();
        }
        catch (Exception e) {
            editView.enableEditForm();
            editView.showErrorMsg(R.string.USER_EDIT_error_saving_profile, e.getMessage());
            MyUtils.printError(TAG, e);
        }
    }

    @Override
    public void loadList(iUsersSingleton.ListCallbacks callbacks) {
        Log.d(TAG, "loadList()");
        usersSingleton.listUsers(callbacks);
    }

    @Override
    public void listItemClicked(String key) {
        Log.d(TAG, "listItemClicked("+key+")");
        listView.goUserPage(key);
    }


    // Методы обратного вызова
    @Override
    public void onUserReadSuccess(final User user) {
        currentUser = user;
        editedUserId = user.getKey();

        editView.hideProgressMessage();
        editView.displayUser(user);
    }

    @Override
    public void onUserReadFail(String errorMsg) {
        editView.hideProgressMessage();
        editView.hideProgressMessage();
        editView.showErrorMsg(R.string.USER_EDIT_error_loading_data, errorMsg);
    }

    @Override
    public void onFileUploadProgress(int progress) {

    }

    @Override
    public void onFileUploadSuccess(String fileName, String downloadURL) {
        editView.hideAvatarThrobber();

        // Не очень красивое решение обновлять пользователя здесь, а потом
        // отдельным методом сохранять. А следующий блок с refreshUserFromServer()
        // вообще не нужен.
        currentUser.setAvatarFileName(fileName);
        currentUser.setAvatarURL(downloadURL);

        /*try {
            usersSingleton.refreshUserFromServer(currentUser.getKey(), new iUsersSingleton.RefreshCallbacks() {
                @Override
                public void onUserRefreshSuccess(User user) {
                    currentUser = user;
                }

                @Override
                public void onUserRefreshFail(String errorMsg) {
                    editView.showErrorMsg(R.string.USER_EDIT_error_updating_user, errorMsg);
                }
            });
        }
        catch (Exception e) {
            e.printError();
            editView.showErrorMsg(R.string.USER_EDIT_error_updating_user, e.getMessage());
        }*/

        saveUser();
    }

    @Override
    public void onFileUploadFail(String errorMsg) {
        editView.showErrorMsg(R.string.USER_EDIT_error_saving_avatar, errorMsg);
        editView.hideAvatarThrobber();
        editView.enableEditForm();
    }

    @Override
    public void onFileUploadCancel() {
        editView.hideAvatarThrobber();
        editView.enableEditForm();
    }

    @Override
    public void onUserSaveSuccess(User user) {
        try {
            usersSingleton.storeCurrentUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            editView.showErrorMsg(R.string.USER_EDIT_error_updating_user, e.getMessage());
        }

        editView.hideProgressMessage();
        editView.finishEdit(user, true);
    }

    @Override
    public void onUserSaveFail(String errorMsg) {
        editView.hideProgressMessage();
        editView.enableEditForm();
        editView.showErrorMsg(R.string.USER_EDIT_error_saving_profile, errorMsg);
    }

    @Override
    public void onListLoadSuccess(List<Card> list) {

    }

    @Override
    public void onListLoadFail(String errorMessage) {
        showView.showErrorMsg(R.string.USER_SHOW_error_loading_cards_lsit, errorMessage);
    }


    // Внутренние методы
    private boolean checkAuthorization() {
        this.currentUserId = AuthSingleton.currentUserId();
        if (null == currentUserId) {
            view.showToast(R.string.not_authorized);
            view.closePage();
            return false;
        }
        return true;
    }

    private void loadUser() {
        view.showProgressMessage(R.string.USER_SHOW_loading_user_info);

        usersSingleton.getUserById(currentUserId, new iUsersSingleton.ReadCallbacks() {
            @Override
            public void onUserReadSuccess(User user) {
                view.hideProgressMessage();
                currentUser = user;
                displayUser();
            }

            @Override
            public void onUserReadFail(String errorMsg) {
                view.showErrorMsg(R.string.USER_SHOW_error_displaying_user, errorMsg);
            }
        });
    }

    private void displayUser() {
        switch (viewMode) {
            case SHOW:
                showView.displayUser(currentUser);
                break;

            case EDIT:
                editView.displayUser(currentUser, avatarBitmap);
                break;

            case LIST:
                break;

            default:
                throw new RuntimeException("Unknown videMode: "+viewMode);
        }
    }

    private void saveProfile() throws Exception {

        if (!AuthSingleton.isLoggedIn()) {
            throw new IllegalAccessException("You is not logged in.");
        }

        if (!AuthSingleton.currentUserId().equals(editedUserId)) {
            throw new IllegalAccessException("Cannot save profile of another user.");
        }

        /*String name = editView.getName();
        if (TextUtils.isEmpty(name)) {
            editView.showErrorMsg(R.string.USER_EDIT_name_cannot_be_empty, "");
            return;
        }

        currentUser.setName(name);
        currentUser.setAbout(editView.getAbout());

        if (!currentUser.hasAvatar() && imageSelected) {
            try {
                Bitmap imageBitmap = editView.getImageBitmap();
                String fileName = AuthSingleton.currentUserId();

                editView.showAvatarThrobber();
                editView.disableEditForm();
                editView.showToast(R.string.USER_EDIT_saving_avatar);

                storageSingleton.uploadAvatar(imageBitmap, imageType, fileName, this);

            } catch (Exception e) {
                onFileUploadFail(e.getMessage());
                e.printStackTrace();
            }
        }
        else {
            saveUser();
        }*/
    }

    private void saveUser() {
        editView.disableEditForm();
        editView.showProgressMessage(R.string.USER_EDIT_saving_profile);

        try {
            usersSingleton.saveUser(currentUser, new iUsersSingleton.SaveCallbacks() {
                @Override
                public void onUserSaveSuccess(User user) {
                    editView.showToast(R.string.USER_EDIT_profile_saved);
                    editView.finishEdit(user, true);

                    usersSingleton.storeCurrentUser(currentUser);
                }

                @Override
                public void onUserSaveFail(String errorMsg) {
                    editView.showErrorMsg(R.string.USER_EDIT_error_saving_user, errorMsg);
                    editView.enableEditForm();
                }
            });
        } catch (Exception e) {
            onUserSaveFail(e.getMessage());
            e.printStackTrace();
        }
    }

    private void detectImageWidthAndHeight(Uri imageURI) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

//        BitmapFactory.decodeFile(imageURI)
    }


}
