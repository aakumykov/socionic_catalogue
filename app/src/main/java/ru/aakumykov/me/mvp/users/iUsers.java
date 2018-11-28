package ru.aakumykov.me.mvp.users;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.util.List;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.models.User;

public interface iUsers {

    interface View {

    } // Это объединение нужно для работы linkView / unlinkView

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
        void displayAvatar(String imageURL, boolean justSelected);

        String getName();
        String getAbout();
        Uri getImageURI();

        void storeImageURI(Uri imageURI);

        void showAvatarThrobber();
        void hideAvatarThrobber();

        void enableEditForm();
        void disableEditForm();

        void finishEdit(User user, boolean isSuccessfull);

        ContentResolver getContentResolver();
        Context getApplicationContext();
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
        void saveProfile();
//        void deleteUser(User user);

        void processSelectedImage(@Nullable Intent intent);
    }

}
