package ru.aakumykov.me.sociocat.users;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.User;

public interface iUsers {

    interface View {

    } // Это объединение нужно для работы linkViews / unlinkViews

    interface ListView  extends iBaseView, View {
        void displayList(List<User> list);
        void goUserPage(String userId);
    }

    interface ShowView  extends iBaseView, View {
        void displayUser(User user);
        void displayCardsList(List<Card> list);
        void goUserEdit();
    }

    interface EditView  extends iBaseView, View {
        void fillUserForm(User user);
        void displayAvatar(String imageURL, boolean justSelected);

        String getName();
        String getAbout();
        Bitmap getImageBitmap();

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

        void processInputIntent(Intent intent) throws Exception;

        void prepareUserEdit(String userId) throws Exception;

        void setImageSelected(boolean isSelected);

        void loadUser(String userId);

        void processSelectedImage(@Nullable Intent intent);

        void loadList(iUsersSingleton.ListCallbacks callbacks);

        void loadCardsOfUser(String userId);

        void listItemClicked(String key);

        void saveProfile() throws Exception;

        void cancelButtonClicked();
    }

}
