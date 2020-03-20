package ru.aakumykov.me.sociocat.template_of_list.view_holders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.template_of_list.iItemsList;

public abstract class BasicViewHolder extends RecyclerView.ViewHolder {

    private iItemsList.iPresenter presenter;

    public BasicViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void initialize(Object payload);

    public abstract void setSelected(boolean isSelected);

    public void setPresenter(iItemsList.iPresenter presenter) {
        this.presenter = presenter;
    }
}