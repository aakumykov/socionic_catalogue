package ru.aakumykov.me.mvp.users;

import android.util.Log;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.UsersSingleton;

public class Users_Presenter implements
        iUsers.Presenter,
        iUsersSingleton.ReadCallbacks,
        iUsersSingleton.SaveCallbacks
{

    private final static String TAG = "Users_Presenter";
    private iUsers.ShowView showView;
    private iUsers.ListView listView;
    private iUsers.EditView editView;
    private iUsersSingleton usersService = UsersSingleton.getInstance();
    private iAuthSingleton authService = AuthSingleton.getInstance();
    private User currentUser;

    // Системные методы
    @Override
    public void linkView(iUsers.View view) throws IllegalArgumentException {

        if (view instanceof iUsers.ListView) {
            Log.d(TAG, "linkView(ListView)");
            this.listView = (iUsers.ListView) view;
        }
        else if (view instanceof iUsers.ShowView) {
            Log.d(TAG, "linkView(ShowView)");
            this.showView = (iUsers.ShowView) view;
        }
        else if (view instanceof iUsers.EditView) {
            Log.d(TAG, "linkView(EditView)");
            this.editView = (iUsers.EditView) view;
        }
        else {
            throw new IllegalArgumentException("Unknown type of View '"+view.getClass()+"'");
        }
    }

    @Override
    public void unlinkView() {
        Log.d(TAG, "unlinkView()");
        this.listView = null;
        this.showView = null;
        this.editView = null;
    }


    // Пользовательские методы
    @Override
    public void updateUser(String name, String about) {
        currentUser.setName(name);
        currentUser.setAbout(about);
        usersService.saveUser(currentUser, this);
    }

    @Override
    public void userEditClicked() {
        showView.goUserEdit();
    }

    @Override
    public void userDeleteClicked(String userId) {

    }

    @Override
    public void saveButtonClicked(String userId, iUsersSingleton.SaveCallbacks callbacks) {
        Log.d(TAG, "saveButtonClicked("+userId+", callbacks)");

        // TODO: привязка этого пользователя к пользователю Firebase!
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        String currentUserId = currentUser.getUid();

        // TODO: проверочку бы здесь! Важно ведь.
        User user = new User();
        user.setKey(userId);
        user.setName(editView.getName());
        user.setAbout(editView.getAbout());

        editView.showInfoMsg(R.string.saving_user);
        editView.showProgressBar();
        editView.disableEditForm();

        usersService.saveUser(user, callbacks);
    }

    @Override
    public void cancelButtonClicked() {
        Log.d(TAG, "cancelButtonClicked()");
        editView.closePage();
    }


    @Override
    public void loadList(iUsersSingleton.ListCallbacks callbacks) {
        Log.d(TAG, "loadList()");
        usersService.listUsers(callbacks);
    }

    @Override
    public void listItemClicked(String key) {
        Log.d(TAG, "listItemClicked("+key+")");
        listView.goUserPage(key);
    }

    @Override
    public void loadUser(String userId, iUsersSingleton.ReadCallbacks callbacks) throws Exception {
        if (null == userId) {
            throw new Exception("userId == null");
        }
        usersService.getUser(userId, callbacks);
    }

    @Override
    public void prepareUserEdit(String userId)  {
        Log.d(TAG, "prepareUserEdit("+userId+")");
        usersService.getUser(userId, this);
    }

    @Override
    public void saveUser(User user) {
        Log.d(TAG, "saveUser(), "+user);

    }


    // Методы обратного вызова
    @Override
    public void onUserReadSuccess(final User user) {
        currentUser = user;
        editView.hideProgressBar();
        editView.fillUserForm(user);
    }

    @Override
    public void onUserReadFail(String errorMsg) {
        editView.hideProgressBar();
        editView.showErrorMsg(R.string.USER_EDIT_error_loading_data, errorMsg);
    }

    @Override
    public void onUserSaveSuccess(User user) {
        authService.storeCurrentUser(user);
        editView.hideProgressBar();
        editView.closePage();
    }

    @Override
    public void onUserSaveFail(String errorMsg) {
        editView.hideProgressBar();
        editView.showErrorMsg(R.string.USER_EDIT_user_saving_error, errorMsg);
    }
}
