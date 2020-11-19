package ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_LoadmoreViewHolder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_ThrobberViewHolder;

public class BasicMVP_ViewHolderBinder {

//    private iBasicMVP_ItemClickListener clickListener;

    public void bindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, BasicMVP_ListItem listItem) {

        if (holder instanceof BasicMVP_LoadmoreViewHolder) {
            BasicMVP_LoadmoreViewHolder loadmoreItemViewHolder = ((BasicMVP_LoadmoreViewHolder) holder);
            loadmoreItemViewHolder.fillWithData(listItem);
        }
        else if (holder instanceof BasicMVP_ThrobberViewHolder) {

        }
        else {
            throw new RuntimeException("Неизвестный тип view holder-а: "+holder);
        }
    }


//    public iBasicMVP_ItemClickListener getItemClickListener() {
//        return this.clickListener;
//    }

    // TODO: а не сделать ли просто свойство публичным, как RV.itemView ?
    public void setClickListener(iBasicMVP_ItemClickListener clickListener) {
//        this.clickListener = clickListener;
    }
}