package ru.aakumykov.me.sociocat.user_email_change;

import android.content.Intent;

import androidx.annotation.Nullable;

import java.util.Date;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.user_email_change.models.Item;
import ru.aakumykov.me.sociocat.user_email_change.stubs.Page_ViewStub;


class Page_Presenter implements iPage.iPresenter {

    private iPage.iView view;
    private Item currentItem;


    @Override
    public void linkView(iPage.iView view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = new Page_ViewStub();
    }

    @Override
    public boolean hasItem() {
        return null != currentItem;
    }

    @Override
    public void onFirstOpen(@Nullable Intent intent) {
        currentItem = getItem();
        view.displayItem(currentItem);
    }

    @Override
    public void onConfigChanged() {
        view.displayItem(currentItem);
    }

    @Override
    public void onRefreshRequested() {
        currentItem = getItem();
        view.displayItem(currentItem);
        view.hideRefreshThrobber();
    }

    @Override
    public void onButtonClicked() {
        view.showToast(R.string.PAGE_TEMPLATE_button_clicked);
    }

    @Override
    public void onBackPressed() {
        view.goCardsGrid();
    }

    @Override
    public boolean onHomePressed() {
        view.goCardsGrid();
        return true;
    }


    // Внутренние методы
    private Item getItem() {
        String timeString = new Date().toString();
        return new Item("Объект-"+timeString, "Привет, я Объект-"+timeString);
    }
}
