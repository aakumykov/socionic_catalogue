package io.gitlab.aakumykov.sociocat.user_show;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.gitlab.aakumykov.sociocat.models.User;
import io.gitlab.aakumykov.sociocat.z_base_view.iBaseView;

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

        void goShowUserCards(@NonNull User user);
        void goShowUserComments(@NonNull User user);
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

        void onUserEdited(@Nullable Intent data);

        boolean canEditUser();

        void onEditClicked();

        void onShowUserCardsClicked();
        void onShowUserCommentsClicked();
    }

}
