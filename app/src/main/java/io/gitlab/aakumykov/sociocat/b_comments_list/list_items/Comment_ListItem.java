package io.gitlab.aakumykov.sociocat.b_comments_list.list_items;

import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import io.gitlab.aakumykov.sociocat.models.Comment;

public class Comment_ListItem extends BasicMVPList_DataItem {

    public Comment_ListItem(Comment comment) {
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
        return Comment_ListItem.class.getSimpleName() +
                " { " +
                getComment().getText().substring(0, Math.min(text.length(), 20)) +
                " }";
    }

    private Comment getComment() {
        return (Comment) getPayload();
    }
}
