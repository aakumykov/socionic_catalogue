package ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders;


import android.view.View;

import androidx.annotation.NonNull;

public abstract class BasicMVP_DataViewHolder extends BasicMVP_ViewHolder {
    
    public BasicMVP_DataViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void displayIsChecked(boolean selected);

    public abstract void displayIsHighlighted(boolean isHighLighted);
}
