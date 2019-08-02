package ru.aakumykov.me.sociocat.card_show;

import ru.aakumykov.me.sociocat.BaseView_Stub;
import ru.aakumykov.me.sociocat.card_show.list_items.iTextItem;
import ru.aakumykov.me.sociocat.models.Card;

public class PageView_Stub extends BaseView_Stub implements iCardShow.iPageView {

    @Override
    public void refreshMenu() {

    }

    @Override
    public void scrollListToPosition(int position) {

    }

    @Override
    public void goEditCard(Card card) {

    }

    @Override
    public void goShowCardsWithTag(String tagName) {

    }

    @Override
    public void showCommentForm(iTextItem repliedItem, boolean editMode) {

    }

    @Override
    public void hideCommentForm(boolean withQuestion) {

    }

    @Override
    public void hideSwipeRefreshThrobber() {

    }
}
