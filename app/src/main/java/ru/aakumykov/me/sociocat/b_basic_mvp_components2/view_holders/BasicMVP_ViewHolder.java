package ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_ListItem;

public abstract class BasicMVP_ViewHolder extends RecyclerView.ViewHolder {

    protected iBasicMVP_ItemClickListener mItemClickListener;

    public BasicMVP_ViewHolder(@NonNull View itemView) {
        super(itemView);
    }


    public abstract void fillWithData(BasicMVP_ListItem basicListItem);

    public void setItemClickListener(iBasicMVP_ItemClickListener clickListener) {
        mItemClickListener = clickListener;
    }
    public iBasicMVP_ItemClickListener getItemClickListener() {
        return mItemClickListener;
    }

}
