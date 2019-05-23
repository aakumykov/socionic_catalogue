package ru.aakumykov.me.sociocat.card_show.adapter;

import java.util.List;

import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;

public class ListAdapter_Stub implements
    iListAdapter_Card,
    iListAdapter_Comments
{
    @Override
    public void showCardThrobber() {

    }

    @Override
    public void hideCardThrobber() {

    }

    @Override
    public void showCardError(int errorMsgId, String errorMsg) {

    }

    @Override
    public void hideCardError() {

    }

    @Override
    public void setCard(Card card) {

    }

    @Override
    public void showCommentsThrobber() {

    }

    @Override
    public void hideCommentsThrobber() {

    }

    @Override
    public void showCommentsError(int errorMsgId, String consoleErrorMsg) {

    }

    @Override
    public void hideCommentsError() {

    }

    @Override
    public void setList(List<Comment> itemsList) {

    }

    @Override
    public void addList(List<Comment> list) {

    }

    @Override
    public void addComment(Comment comment, boolean scrollToAddedComment) {

    }

    @Override
    public void scrollToComment(String commentKey) {

    }
}
