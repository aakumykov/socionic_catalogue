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

    enum ViewMode {
        SHOW, EDIT, LIST
    }

    interface View extends iBaseView {

    } // Это объединение нужно для работы linkViews / unlinkViews

    interface ListView extends View {
        void displayList(List<User> list);
        void goUserPage(String userId);
    }

    interface ShowView  extends View {
        void displayUser(User user);
        void goUserEdit();
        void setPageTitle(String userName);
    }

    interface EditView  extends View {

        void displayUser(User user);
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

        boolean hasUser();

        void onFirstOpen();

        void onConfigurationChanged();


        void prepareUserEdit(String userId) throws Exception;

        void setImageSelected(boolean isSelected);

        void processSelectedImage(@Nullable Intent intent);

        void loadList(iUsersSingleton.ListCallbacks callbacks);

        void loadCardsOfUser(String userId);

        void listItemClicked(String key);

        void saveProfile() throws Exception;

        void cancelButtonClicked();

        void onTransferUserClicked();


        void onUserLoggedOut();
    }

}
