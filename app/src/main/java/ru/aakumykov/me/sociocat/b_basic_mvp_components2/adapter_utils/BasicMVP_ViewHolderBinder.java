package ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_LoadmoreViewHolder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_ThrobberViewHolder;

public class BasicMVP_ViewHolderBinder {

    public void bindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, BasicMVP_ListItem listItem) {

        if (holder instanceof BasicMVP_LoadmoreViewHolder)
        {
            BasicMVP_LoadmoreViewHolder loadmoreItemViewHolder = ((BasicMVP_LoadmoreViewHolder) holder);
            loadmoreItemViewHolder.initialize(listItem);
        }
        else if (holder instanceof BasicMVP_ThrobberViewHolder) {
            // Ничего не делаю
        }
        else {
            throw new RuntimeException("Неизвестный тип view holder-а: "+holder);
        }
    }

}