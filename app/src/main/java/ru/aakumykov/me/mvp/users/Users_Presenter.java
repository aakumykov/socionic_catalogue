package ru.aakumykov.me.mvp.users;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.services.UsersSingleton;
import ru.aakumykov.me.mvp.users.list.UsersList_View;
import ru.aakumykov.me.mvp.users.show.UserShow_View;

public class Users_Presenter implements
        iUsers.Presenter
{

    private final static String TAG = "Users_Presenter";
    private iUsers.ShowView showView;
    private iUsers.ListView listView;
    private iUsers.EditView editView;
    private UsersSingleton usersSingleton = UsersSingleton.getInstance();
    private User currentUser;

    // Системные методы
    @Override
    public void linkView(iUsers.View view) {

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
            throw new IllegalArgumentException("Unknown type of View");
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
        user.setEmail(editView.getEmail());

        editView.showInfoMsg(R.string.saving_user);
        editView.showProgressBar();
        editView.disableEditForm();

        usersSingleton.saveUser(user, callbacks);
    }

    @Override
    public void cancelButtonClicked() {
        Log.d(TAG, "cancelButtonClicked()");
        editView.closePage();
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

    @Override
    public void loadUser(String userId, iUsersSingleton.UserCallbacks callbacks) throws Exception {
        Log.d(TAG, "loadUser("+userId+")");
        if (null == userId) {
            throw new Exception("userId == null");
        }
        usersSingleton.getUser(userId, callbacks);
    }

    @Override
    public void prepareUserEdit(String userId, iUsersSingleton.UserCallbacks callbacks) throws Exception {
        Log.d(TAG, "prepareUserEdit("+userId+")");
        usersSingleton.getUser(userId, callbacks);
    }

    @Override
    public void saveUser(User user) {
        Log.d(TAG, "saveUser(), "+user);

    }
}
