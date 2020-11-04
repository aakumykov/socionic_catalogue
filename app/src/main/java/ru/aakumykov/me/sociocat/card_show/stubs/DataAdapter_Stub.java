package ru.aakumykov.me.sociocat.card_show.stubs;

import androidx.annotation.NonNull;

import java.util.List;

import ru.aakumykov.me.sociocat.card_show.iCardShow;
import ru.aakumykov.me.sociocat.card_show.list_items.iList_Item;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;

public class DataAdapter_Stub implements iCardShow.iDataAdapter {
    @Override
    public boolean notYetFilled() {
        return false;
    }

    @Override
    public void showCardThrobber() {

    }

    @Override
    public void showCard(Card card) {

    }

    @Override
    public int appendOneComment(Comment comment) {
        return 0;
    }

    @Override
    public void removeComment(iList_Item listItem) {

    }

    @Override
    public void updateComment(iList_Item listItem, Comment newComment) {

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

    @Override
    public void addCommentsList(List<Comment> list) {

    }

    @Override
    public void addCommentsList(List<Comment> list, int position) {

    }

    @Override
    public void replaceComments(List<Comment> list) {

    }

    @Override
    public void showCommentsThrobber2() {

    }

    @Override
    public void hideCommentsThrobber2(int position) {

    }

    @Override
    public void clearCommentsList() {

    }

    @Override
    public int getCommentPositionByKey(@NonNull String commentKey) {
        return 0;
    }

    @Override
    public void highlightComment(int position) {

    }

    @Override
    public void showCommentsThrobber2(int position) {

    }

    @Override
    public void hideCommentsThrobber2() {

    }
}
