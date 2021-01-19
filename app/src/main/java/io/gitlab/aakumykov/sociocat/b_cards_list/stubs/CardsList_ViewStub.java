package io.gitlab.aakumykov.sociocat.b_cards_list.stubs;

import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.stubs.BasicMVPList_ViewStub;
import io.gitlab.aakumykov.sociocat.b_cards_list.interfaces.iCardsList_View;
import io.gitlab.aakumykov.sociocat.eCardType;
import io.gitlab.aakumykov.sociocat.models.Card;

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
