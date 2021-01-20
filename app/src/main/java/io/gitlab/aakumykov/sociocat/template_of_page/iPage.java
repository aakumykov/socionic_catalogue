package io.gitlab.aakumykov.sociocat.template_of_page;

import android.content.Intent;

import androidx.annotation.Nullable;

import io.gitlab.aakumykov.sociocat.template_of_page.models.Item;
import io.gitlab.aakumykov.sociocat.z_base_view.iBaseView;

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
