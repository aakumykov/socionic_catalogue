package ru.aakumykov.me.sociocat.card_show;

import ru.aakumykov.me.sociocat.card_show.list_items.iTextItem;
import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;

public interface iPageView extends iBaseView {

    void refreshMenu();

    void goEditCard(Card card);

    void showCommentForm(iTextItem repliedItem, boolean editMode);
    void hideCommentForm(boolean withQuestion);
}
