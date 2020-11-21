package ru.aakumykov.me.sociocat.cards_list.stubs;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.eCardType;
import ru.aakumykov.me.sociocat.base_view.BaseView_Stub;
import ru.aakumykov.me.sociocat.cards_list.iCardsList;
import ru.aakumykov.me.sociocat.models.Card;

@SuppressLint("Registered")
public class CardsList_ViewStub
        extends BaseView_Stub
        implements iCardsList.iPageView
{
    @Override
    public void changeViewMode(@Nullable iCardsList.ViewMode viewMode) {

    }

    @Override
    public void setViewState(iCardsList.ViewState viewState, Integer messageId, @Nullable Object messageDetails) {

    }

    @Override
    public void setToolbarState(iCardsList.ToolbarState toolbarState) {

    }

    @Override
    public boolean actionModeIsActive() {
        return false;
    }

    @Override
    public void finishActionMode() {

    }

    @Override
    public void scrollToPosition(int position) {

    }

    @Override
    public void goShowCard(Card card) {

    }

    @Override
    public void goEditCard(Card card) {

    }

    @Override
    public void showAddNewCardMenu() {

    }

    @Override
    public void goCreateCard(eCardType cardType) {

    }

    @Override
    public void goUserProfile(String userId) {

    }

    @Override
    public void go2cardComments(Card card) {

    }

}
