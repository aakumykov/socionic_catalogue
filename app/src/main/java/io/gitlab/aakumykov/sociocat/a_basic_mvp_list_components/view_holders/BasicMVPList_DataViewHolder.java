package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_holders;


import android.view.View;

import androidx.annotation.NonNull;

public abstract class BasicMVPList_DataViewHolder extends BasicMVPList_ViewHolder {
    
    public BasicMVPList_DataViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void displayIsChecked(boolean selected);

    public abstract void displayIsHighlighted(boolean isHighLighted);
}
