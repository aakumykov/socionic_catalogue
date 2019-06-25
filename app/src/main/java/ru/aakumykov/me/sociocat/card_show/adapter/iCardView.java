package ru.aakumykov.me.sociocat.card_show.adapter;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;
import ru.aakumykov.me.sociocat.models.Card;

public interface iCardView {

    // Основной метод
    void displayCard(@Nullable Card card);

    // Вспомогательные методы
    void showCardThrobber();
    void hideCardThrobber();

    void showCardError(int errorMsgId, String errorMsg);
    void hideCardError();

    void showCardDeleteDialog(Card card);
}
