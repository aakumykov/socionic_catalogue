package ru.aakumykov.me.sociocat.user_show;

import android.content.Intent;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.user_show.models.Item;

public interface iUserShow {

    interface iView extends iBaseView {
        void displayUser(User user);

        void hideRefreshThrobber();

        void showAvatarTrobber();
        void hideAvatarThrobber();
    }

    interface iPresenter {
        void linkView(iView view);
        void unlinkView();

        boolean hasUser();

        void onFirstOpen(@Nullable Intent intent);

        void onConfigChanged();

        void onRefreshRequested();

        boolean canEditUser();
    }

}