package ru.aakumykov.me.mvp.users;

import java.util.List;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.models.User;

public interface iUsers {

    interface View {} // Это объединение нужно для работы linkView / unlinkView

    interface ListView  extends iBaseView, View {
        void displayList(List<User> list);
        void goUserPage(String userId);
    }

    interface ShowView  extends iBaseView, View {
        void displayUser(User user);
        void goUserEdit();
    }

    interface EditView  extends iBaseView, View {
        void fillUserForm(User user);

        String getName();
        String getAbout();

        void enableEditForm();
        void disableEditForm();

        void finishEdit(User user, boolean isSuccessfull);
    }

    interface Presenter {
        void linkView(View view) throws IllegalArgumentException;
        void unlinkView();

        void updateUser(String newName, String newAbout);

        void userEditClicked();
        void userDeleteClicked(String userId);
        void saveButtonClicked(String userId, iUsersSingleton.SaveCallbacks callbacks);
        void cancelButtonClicked();

        void loadList(iUsersSingleton.ListCallbacks callbacks);
        void listItemClicked(String key);

        void loadUser(String userId, iUsersSingleton.ReadCallbacks callbacks) throws Exception;
        void prepareUserEdit(String userId);
        void saveUser(User user);
//        void deleteUser(User user);
    }

}
