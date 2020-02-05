package ru.aakumykov.me.sociocat.change_password;

import android.content.Intent;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.change_password.models.Item;
import ru.aakumykov.me.sociocat.interfaces.iBaseView;

public interface iChangePassword {

    interface iView extends iBaseView {
        void displayItem(Item item);
        void hideRefreshThrobber();

        void goCardsGrid();
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
