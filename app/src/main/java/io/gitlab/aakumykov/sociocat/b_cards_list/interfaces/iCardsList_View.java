package io.gitlab.aakumykov.sociocat.b_cards_list.interfaces;

import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iBasicList_Page;
import io.gitlab.aakumykov.sociocat.eCardType;
import io.gitlab.aakumykov.sociocat.models.Card;

public interface iCardsList_View extends iBasicList_Page {
    void goShowingCard(@NonNull Card card);
    void showAddNewCardMenu();
    void goCreateCard(eCardType cardType);
    void goShowAllCards();
}
