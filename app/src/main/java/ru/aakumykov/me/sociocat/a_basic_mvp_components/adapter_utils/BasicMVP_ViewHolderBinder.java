package ru.aakumykov.me.sociocat.a_basic_mvp_components.adapter_utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iBasic_ItemClickListener;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.BasicMVP_LoadmoreViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.BasicMVP_ThrobberViewHolder;


public class BasicMVP_ViewHolderBinder {

//    private iBasic_ItemClickListener clickListener;

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


//    public iBasic_ItemClickListener getItemClickListener() {
//        return this.clickListener;
//    }

    // TODO: а не сделать ли просто свойство публичным, как RV.itemView ?
    public void setClickListener(iBasic_ItemClickListener clickListener) {
//        this.clickListener = clickListener;
    }
}