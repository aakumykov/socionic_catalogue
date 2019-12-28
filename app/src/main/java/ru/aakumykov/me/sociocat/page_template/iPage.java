package ru.aakumykov.me.sociocat.page_template;

import android.content.Intent;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.page_template.models.Item;

public interface iPage {

    interface iView extends iBaseView {
        void displayItem(Item item);
    }

    interface iPresenter {
        boolean hasItem();

        void onFirstOpen(@Nullable Intent intent);
        void onConfigChanged();

        void onRefreshRequested();

        void onEditClicked();
    }

}