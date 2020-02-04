package ru.aakumykov.me.sociocat.user_show;

import android.content.Intent;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.User;

public interface iUserShow {

    enum ViewState {
        PROGRESS,
        SHOW_PRIVATE,
        SHOW_PUBLIC,
        ERROR
    }

    interface iView extends iBaseView {
        void goUserEdit(String userId);

        void displayUser(User user, boolean isPrivateMode);

        void setState(ViewState viewState, int messageId);
        void setState(ViewState viewState, int messageId, @Nullable Object payload);

        void showRefreshThrobber();
        void hideRefreshThrobber();

        void showAvatarTrobber();
        void hideAvatarThrobber();
    }

    interface iPresenter {
        void linkView(iView view);
        void unlinkView();

        void storeViewState(ViewState viewState, int messageId, Object payload);

        boolean hasUser();

        void onFirstOpen(@Nullable Intent intent);

        void onConfigChanged();

        void onRefreshRequested();

        void onUserLoggedOut();
        void onUserLoggedIn();

        boolean canEditUser();

        void onEditClicked();
    }

}
