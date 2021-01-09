package ru.aakumykov.me.sociocat.b_comments_list.list_items;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.models.Comment;

public class CommentsList_Item extends BasicMVPList_DataItem {

    public CommentsList_Item(Comment comment) {
        setPayload(comment);
    }

    @Override
    public String getTitle() {
        return getComment().getName();
    }

    @NonNull
    @Override
    public String toString() {
        String text = getComment().getText();
        return "Comment { " +
                getComment().getText().substring(Math.min(text.length(), 20)) +
                " }";
    }

    private Comment getComment() {
        return (Comment) getPayload();
    }
}
