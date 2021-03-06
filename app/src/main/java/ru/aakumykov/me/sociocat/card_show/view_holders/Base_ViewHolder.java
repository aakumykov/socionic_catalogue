package ru.aakumykov.me.sociocat.card_show.view_holders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.card_show.list_items.iList_Item;

public abstract class Base_ViewHolder extends RecyclerView.ViewHolder {

    Base_ViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void initialize(iList_Item listItem);
}
