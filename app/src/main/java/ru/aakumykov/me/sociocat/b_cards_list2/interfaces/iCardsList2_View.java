package ru.aakumykov.me.sociocat.b_cards_list2.interfaces;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicList_Page;
import ru.aakumykov.me.sociocat.eCardType;
import ru.aakumykov.me.sociocat.models.Card;

public interface iCardsList2_View extends iBasicList_Page {
    void goShowingCard(@NonNull Card card);
    void showAddNewCardMenu();
    void goCreateCard(eCardType cardType);
    void goShowAllCards();
}
