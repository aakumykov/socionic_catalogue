package ru.aakumykov.me.sociocat.users;

import android.graphics.Bitmap;
import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.utils.ImageType;
import ru.aakumykov.me.sociocat.utils.ImageUtils;

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
        void hideSwipeRefresh();
    }

    interface EditView  extends View {
        <T> void displayUser(User user);
        <T> void displayUser(User user, @Nullable T avatar);
        <T> void displayAvatar(T avatar);

        void showAvatarThrobber();
        void hideAvatarThrobber();

        void enableEditForm();
        void disableEditForm();

        void finishEdit(User user, boolean isSuccessfull);

        String getName();
        String getEmail();
        String getAbout();
    }

    interface Presenter {
        void linkView(View view) throws IllegalArgumentException;
        void unlinkView();

        boolean hasShownUser();
        boolean hasEditedUSer();

        void onFirstOpen();
        void onConfigurationChanged();

        void onSaveUserClicked();
        void cancelButtonClicked();

        void loadList(iUsersSingleton.ListCallbacks callbacks);
        void listItemClicked(String key);

        void onUserLoggedOut();

        void onImageSelected(Bitmap bitmap, ImageType imageType);

        void onRefreshRequested();
    }

}
