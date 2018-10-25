package ru.aakumykov.me.mvp.user_page;

import android.util.Log;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.services.UsersSingleton;
import ru.aakumykov.me.mvp.users_list.iUsersList;

class UserPage_Presenter implements
        iUserPage.Presenter,
        iUsersSingleton.UserCallbacks
{

    private final static String TAG = "UserPage_Presenter";
    private iUserPage.View view;
    private UsersSingleton usersSingleton = UsersSingleton.getInstance();


    // Системные методы
    @Override
    public void linkView(iUserPage.View view) {
        this.view = view;
    }
    @Override
    public void unlinkView() {
        this.view = null;
    }


    // Пользовательские методы
    @Override
    public void userIdRecieved(String userId) {
        Log.d(TAG, "userIdRecieved("+userId+")");
//        if (null == userId) {
//            view.showErrorMsg(R.string.);
//        }
        usersSingleton.getUser(userId, this);
    }


    // Коллбеки
    @Override
    public void onUserReadSuccess(User user) {
        view.displayUser(user);
    }

    @Override
    public void onUserReadFail(String errorMsg) {
        view.showErrorMsg(R.string.error_displaying_user, errorMsg);
    }
}
