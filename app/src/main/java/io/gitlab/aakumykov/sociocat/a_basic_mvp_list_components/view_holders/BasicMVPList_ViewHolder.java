package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_holders;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iBasicMVP_ItemClickListener;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;

public abstract class BasicMVPList_ViewHolder extends RecyclerView.ViewHolder {

    protected iBasicMVP_ItemClickListener mItemClickListener;

    public BasicMVPList_ViewHolder(@NonNull View itemView) {
        super(itemView);
    }


    public abstract void initialize(BasicMVPList_ListItem basicListItem);

    public void setItemClickListener(iBasicMVP_ItemClickListener clickListener) {
        mItemClickListener = clickListener;
    }
    public iBasicMVP_ItemClickListener getItemClickListener() {
        return mItemClickListener;
    }

}
