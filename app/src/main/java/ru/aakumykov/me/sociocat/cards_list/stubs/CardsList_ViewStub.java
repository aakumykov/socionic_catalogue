package ru.aakumykov.me.sociocat.cards_list.stubs;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.base_view.BaseView_Stub;
import ru.aakumykov.me.sociocat.cards_list.iCardsList;
import ru.aakumykov.me.sociocat.models.Card;

@SuppressLint("Registered")
public class CardsList_ViewStub
        extends BaseView_Stub
        implements iCardsList.iPageView
{
    @Override
    public void applyViewMode() {

    }

    @Override
    public void setViewState(iCardsList.ViewState viewState, Integer messageId, @Nullable Object messageDetails) {

    }

    @Override
    public boolean actionModeIsActive() {
        return false;
    }

    @Override
    public void scrollToPosition(int position) {

    }

    @Override
    public void goShowCard(Card card) {

    }
}
