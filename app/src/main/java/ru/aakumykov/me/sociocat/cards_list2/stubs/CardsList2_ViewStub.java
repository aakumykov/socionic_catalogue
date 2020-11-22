package ru.aakumykov.me.sociocat.cards_list2.stubs;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.stubs.BasicMVP_ViewStub;
import ru.aakumykov.me.sociocat.cards_list2.interfaces.iCardsList2_View;
import ru.aakumykov.me.sociocat.eCardType;
import ru.aakumykov.me.sociocat.models.Card;

public class CardsList2_ViewStub extends BasicMVP_ViewStub implements iCardsList2_View {

    @Override
    public void goShowingCard(@NonNull Card card) {

    }

    @Override
    public void showAddNewCardMenu() {

    }

    @Override
    public void goCreateCard(eCardType cardType) {

    }

    @Override
    public void goShowAllCards() {

    }
}
