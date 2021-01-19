package io.gitlab.aakumykov.sociocat.template_of_page.stubs;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import io.gitlab.aakumykov.sociocat.template_of_page.iPage;
import io.gitlab.aakumykov.sociocat.template_of_page.models.Item;
import io.gitlab.aakumykov.sociocat.z_base_view.BaseView_Stub;

@SuppressLint("Registered")
public class Page_ViewStub extends BaseView_Stub implements iPage.iView {

    @Override
    public void setState(iPage.ViewState state, int messageId) {

    }

    @Override
    public void setState(iPage.ViewState state, int messageId, @Nullable String messagePayload) {

    }

    @Override
    public String getText() {
        return null;
    }


    @Override
    public void displayItem(Item item) {

    }

    @Override
    public void hideRefreshThrobber() {

    }

    @Override
    public void goCardsGrid() {

    }
}
