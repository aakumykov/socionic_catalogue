package ru.aakumykov.me.sociocat.users;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton_CF;
import ru.aakumykov.me.sociocat.singletons.StorageSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton_CF;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iStorageSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.utils.MVPUtils.MVPUtils;

public class Users_Presenter implements
        iUsers.Presenter,
        iUsersSingleton.ReadCallbacks,
        iUsersSingleton.SaveCallbacks,
        iStorageSingleton.FileUploadCallbacks,
        iCardsSingleton.ListCallbacks
{

    private final static String TAG = "Users_Presenter";
    private iUsers.ShowView showView;
    private iUsers.ListView listView;
    private iUsers.EditView editView;
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private iUsersSingleton usersSingleton_CF = UsersSingleton_CF.getInstance();
    private iCardsSingleton cardsSingleton = CardsSingleton_CF.getInstance();
    private iAuthSingleton authSingleton = AuthSingleton.getInstance();
    private iStorageSingleton storageSingleton = StorageSingleton.getInstance();

    private User currentUser;
    private String editedUserId;
    private boolean imageSelected = false;
    private String imageType;

    // Системные методы
    @Override
    public void linkView(iUsers.View view) throws IllegalArgumentException {

        if (view instanceof iUsers.ListView) {
            Log.d(TAG, "linkViews(ListAdapter)");
            this.listView = (iUsers.ListView) view;
        }
        else if (view instanceof iUsers.ShowView) {
            Log.d(TAG, "linkViews(ShowView)");
            this.showView = (iUsers.ShowView) view;
        }
        else if (view instanceof iUsers.EditView) {
            Log.d(TAG, "linkViews(EditView)");
            this.editView = (iUsers.EditView) view;
        }
        else {
            throw new IllegalArgumentException("Unknown type of View '"+view.getClass()+"'");
        }
    }

    @Override
    public void unlinkView() {
        Log.d(TAG, "unlinkViews()");
        this.listView = null;
        this.showView = null;
        this.editView = null;
    }

    @Override
    public void processInputIntent(Intent intent) throws Exception {
       String userId = intent.getStringExtra(Constants.USER_ID);

       if (null != userId) {
           loadUser(userId);
       }
       else {
           if (AuthSingleton.isLoggedIn()) {
               userId = AuthSingleton.currentUserId();
               loadUser(userId);
           }
           else {
               showView.requestLogin(Constants.CODE_USER_SHOW, null);
           }
       }
    }


    // Пользовательские методы
    @Override
    public void prepareUserEdit(String userId) throws Exception {
        if (AuthSingleton.isLoggedIn())
        {
            if (AuthSingleton.currentUserId().equals(userId))
            {
                usersSingleton.getUserById(userId, this);
            }
            else {
                throw new IllegalAccessException("Cannot edit profile of another user.");
            }
        }
        else {
            throw new IllegalAccessException("You must be logged in.");
        }
    }

    @Override
    public void setImageSelected(boolean isSelected) {
        imageSelected = isSelected;
    }

    @Override
    public void loadUser(String userId) {

        showView.showProgressMessage(R.string.USER_SHOW_loading_user_info);

        usersSingleton.getUserById(userId, new iUsersSingleton.ReadCallbacks() {
            @Override
            public void onUserReadSuccess(User user) {
                if (null != user) {
                    currentUser = user;
                    showView.hideProgressMessage();
                    showView.displayUser(user);
                }
                else
                    showView.showErrorMsg(R.string.USER_SHOW_error_displaying_user, "User is NULL");
            }

            @Override
            public void onUserReadFail(String errorMsg) {
                showView.showErrorMsg(R.string.USER_SHOW_error_displaying_user, errorMsg);
            }
        });
    }

    @Override
    public void cancelButtonClicked() {
        Log.d(TAG, "cancelButtonClicked()");
        editView.closePage();
    }

    @Override
    public void onTransferUserClicked() {
        showView.showProgressMessage(R.string.USER_SHOW_transferring_user);

        usersSingleton_CF.saveUser(currentUser, new iUsersSingleton.SaveCallbacks() {
            @Override
            public void onUserSaveSuccess(User user) {
                showView.showDebugMsg(R.string.USER_SHOW_user_transfer_success);
            }

            @Override
            public void onUserSaveFail(String errorMsg) {
                showView.showErrorMsg(R.string.USER_SHOW_user_transfer_error, errorMsg);
            }
        });
    }

    @Override
    public void loadList(iUsersSingleton.ListCallbacks callbacks) {
        Log.d(TAG, "loadList()");
        usersSingleton.listUsers(callbacks);
    }

    @Override
    public void loadCardsOfUser(String userId) {
        cardsSingleton.loadListForUser(userId, this);
    }

    @Override
    public void listItemClicked(String key) {
        Log.d(TAG, "listItemClicked("+key+")");
        listView.goUserPage(key);
    }

    @Override
    public void saveProfile() throws Exception {

        if (!AuthSingleton.isLoggedIn()) {
            throw new IllegalAccessException("You is not logged in.");
        }

        if (!AuthSingleton.currentUserId().equals(editedUserId)) {
            throw new IllegalAccessException("Cannot save profile of another user.");
        }

        String name = editView.getName();
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
        }
    }

    @Override
    public void processSelectedImage(@Nullable Intent data) {

        if (null == data) {
            editView.showErrorMsg(R.string.USER_EDIT_error_selecting_image, "");
            return;
        }

        // Первый способ получить содержимое
        Uri imageURI = data.getParcelableExtra(Intent.EXTRA_STREAM);

        // Второй способ получить содержимое
        if (null == imageURI) {
            imageURI = data.getData();
            if (null == imageURI) {
                editView.showErrorMsg(R.string.USER_EDIT_no_image_data, "imageURI == NULL");
                return;
            }
        }

        setImageSelected(true);
        currentUser.setAvatarURL("");
        imageType = MVPUtils.detectImageType(editView.getApplicationContext(), imageURI);
        editView.displayAvatar(imageURI.toString(), true);
    }


    // Методы обратного вызова
    @Override
    public void onUserReadSuccess(final User user) {
        currentUser = user;
        editedUserId = user.getKey();

        editView.hideProgressMessage();
        editView.fillUserForm(user);
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
            e.printStackTrace();
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
        showView.displayCardsList(list);
    }

    @Override
    public void onListLoadFail(String errorMessage) {
        showView.showErrorMsg(R.string.USER_SHOW_error_loading_cards_lsit, errorMessage);
    }


    // Внутренние методы
    private void saveUser() {
        editView.disableEditForm();
        editView.showProgressMessage(R.string.USER_EDIT_saving_profile);

        try {
            usersSingleton.saveUser(currentUser, new iUsersSingleton.SaveCallbacks() {
                @Override
                public void onUserSaveSuccess(User user) {
                    editView.showToast(R.string.USER_EDIT_profile_saved);
                    editView.finishEdit(user, true);
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
