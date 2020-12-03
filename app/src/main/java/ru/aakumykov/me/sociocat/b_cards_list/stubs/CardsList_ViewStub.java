package ru.aakumykov.me.sociocat.b_cards_list.stubs;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.stubs.BasicMVPList_ViewStub;
import ru.aakumykov.me.sociocat.b_cards_list.interfaces.iCardsList_View;
import ru.aakumykov.me.sociocat.eCardType;
import ru.aakumykov.me.sociocat.models.Card;

public class CardsList_ViewStub extends BasicMVPList_ViewStub implements iCardsList_View {

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
