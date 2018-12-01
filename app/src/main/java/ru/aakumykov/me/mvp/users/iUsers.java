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
        byte[] getImageData() throws Exception;

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

        void prepareUserEdit(String userId) throws Exception;

        void setImageSelected(boolean isSelected);

        void loadUser(String userId, iUsersSingleton.ReadCallbacks callbacks) throws Exception;

        void processSelectedImage(@Nullable Intent intent);

        void loadList(iUsersSingleton.ListCallbacks callbacks);

        void listItemClicked(String key);

        void saveProfile() throws Exception;

        void cancelButtonClicked();
    }

}
