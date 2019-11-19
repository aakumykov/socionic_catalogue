package ru.aakumykov.me.sociocat.card_show2.view_holders;

import android.view.View;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.card_show2.list_items.iList_Item;
import ru.aakumykov.me.sociocat.models.Comment;

public class Comment_ViewHolder extends Base_ViewHolder {

    public Comment_ViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initialize(iList_Item listItem) {
        Comment comment = (Comment) listItem;
    }
}
