package ru.aakumykov.me.sociocat.user_edit_email;

import android.content.Intent;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.User;

public interface iUserEditEmail {

    interface iView extends iBaseView {
        void displayCurrentEmail(User user);
    }

    interface iPresenter {
        void linkView(iView view);
        void unlinkView();

        void onFirstOpen(@Nullable Intent intent);
        void onConfigChanged();

        void onSaveButtonClicked();
        void onCancelButtonClicked();

        void onBackPressed();
        boolean onHomePressed();
    }

}
