package ru.aakumykov.me.sociocat.card_show2.view_holders;

import android.view.View;

import androidx.annotation.NonNull;

import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.card_show2.list_items.iList_Item;

public class CommentThrobber_ViewHolder extends Base_ViewHolder {

    public CommentThrobber_ViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void initialize(iList_Item listItem) {

    }
}