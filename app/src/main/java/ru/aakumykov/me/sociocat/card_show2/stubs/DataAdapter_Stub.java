package ru.aakumykov.me.sociocat.card_show2.stubs;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.card_show2.iCardShow2;
import ru.aakumykov.me.sociocat.card_show2.list_items.iList_Item;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;

public class DataAdapter_Stub implements iCardShow2.iDataAdapter {
    @Override
    public void showCardThrobber() {

    }

    @Override
    public void showCommentsThrobber(@Nullable Integer position) {

    }

    @Override
    public void showCard(Card card) {

    }

    @Override
    public void appendComments(List<Comment> commentsList) {

    }

    @Override
    public void insertComments(List<Comment> commentsList, int position) {

    }

    @Override
    public int appendOneComment(Comment comment) {
        return 0;
    }

    @Override
    public void removeComment(iList_Item listItem) {

    }

    @Override
    public Comment getComment(int position) {
        return null;
    }

    @Override
    public Comment getComment(iList_Item listItem) {
        return null;
    }

    @Override
    public int getIndexOf(iList_Item listItem) {
        return 0;
    }
}
