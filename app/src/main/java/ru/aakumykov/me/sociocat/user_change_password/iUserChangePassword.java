package ru.aakumykov.me.sociocat.user_change_password;

import android.content.Intent;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.user_change_password.models.Item;
import ru.aakumykov.me.sociocat.interfaces.iBaseView;

public interface iUserChangePassword {

    interface iView extends iBaseView {

    }

    interface iPresenter {
        void linkView(iView view);
        void unlinkView();

        boolean hasItem();

        void onFirstOpen(@Nullable Intent intent);

        void onConfigChanged();

        void onRefreshRequested();

        void onButtonClicked();

        void onBackPressed();

        boolean onHomePressed();
    }

}
