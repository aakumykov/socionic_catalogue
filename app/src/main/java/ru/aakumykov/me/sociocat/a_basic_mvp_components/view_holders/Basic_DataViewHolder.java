package ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders;

import android.view.View;

import androidx.annotation.NonNull;

public abstract class Basic_DataViewHolder extends Basic_ViewHolder {
    
    public Basic_DataViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void displayIsChecked(boolean selected);
}
