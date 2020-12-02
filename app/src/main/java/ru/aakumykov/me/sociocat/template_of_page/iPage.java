package ru.aakumykov.me.sociocat.template_of_page;

import android.content.Intent;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.template_of_page.models.Item;
import ru.aakumykov.me.sociocat.z_base_view.iBaseView;

public interface iPage {

    enum ViewState {
        PROGRESS,
        SUCCESS,
        ERROR
    }

    interface iView extends iBaseView {

        void setState(ViewState state, int messageId);
        void setState(ViewState state, int messageId, @Nullable String messagePayload);

        String getText();

        void displayItem(Item item);
        void hideRefreshThrobber();

        void goCardsGrid();
    }

    interface iPresenter {
        void linkView(iView view);
        void unlinkView();

        void onUserLoggedIn();
        void onUserLoggedOut();

        void storeViewState(ViewState viewState, int messageId, String messageDetails);

        boolean hasItem();

        void onFirstOpen(@Nullable Intent intent);
        void onConfigChanged();

        void onRefreshRequested();

        void onButtonClicked();
        void onFormIsValid();

        void onBackPressed();
        boolean onHomePressed();
    }

}
