package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.adapter_utils;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_LoadmoreViewHolder;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_ThrobberViewHolder;

public class BasicMVPList_ViewHolderBinder {

    public void bindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, BasicMVPList_ListItem listItem) {

        if (holder instanceof BasicMVPList_LoadmoreViewHolder)
        {
            BasicMVPList_LoadmoreViewHolder loadmoreItemViewHolder = ((BasicMVPList_LoadmoreViewHolder) holder);
            loadmoreItemViewHolder.initialize(listItem);
        }
        else if (holder instanceof BasicMVPList_ThrobberViewHolder) {
            // Ничего не делаю
        }
        else {
            throw new RuntimeException("Неизвестный тип view holder-а: "+holder);
        }
    }

}