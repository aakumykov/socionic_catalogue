package ru.aakumykov.me.sociocat.cards_list.stubs;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.CardType;
import ru.aakumykov.me.sociocat.base_view.BaseView_Stub;
import ru.aakumykov.me.sociocat.cards_list.iCardsList;
import ru.aakumykov.me.sociocat.models.Card;

@SuppressLint("Registered")
public class CardsList_ViewStub
        extends BaseView_Stub
        implements iCardsList.iPageView
{
    @Override
    public void changeLayout(@Nullable iCardsList.LayoutMode layoutMode) {

    }

    @Override
    public void setViewState(iCardsList.PageViewState pageViewState, Integer messageId, @Nullable Object messageDetails) {

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
    public void goCreateCard(CardType cardType) {

    }
}
