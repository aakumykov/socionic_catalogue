package ru.aakumykov.me.sociocat.b_cards_list2.stubs;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.stubs.BasicMVPList_ViewStub;
import ru.aakumykov.me.sociocat.b_cards_list2.interfaces.iCardsList2_View;
import ru.aakumykov.me.sociocat.eCardType;
import ru.aakumykov.me.sociocat.models.Card;

public class CardsList2_ViewStub extends BasicMVPList_ViewStub implements iCardsList2_View {

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
