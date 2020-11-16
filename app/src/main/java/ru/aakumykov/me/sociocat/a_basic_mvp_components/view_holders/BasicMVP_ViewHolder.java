package ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_ListItem;


public abstract class BasicMVP_ViewHolder extends RecyclerView.ViewHolder {

    protected iBasicMVP_ItemClickListener mItemClickListener;

    public BasicMVP_ViewHolder(@NonNull View itemView) {
        super(itemView);
    }


    public abstract void fillWithData(BasicMVP_ListItem basicListItem, int position);

    public void setItemClickListener(iBasicMVP_ItemClickListener clickListener) {
        mItemClickListener = clickListener;
    }
    public iBasicMVP_ItemClickListener getItemClickListener() {
        return mItemClickListener;
    }

}
