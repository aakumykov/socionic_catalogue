package ru.aakumykov.me.sociocat.card_show2.view_holders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class Base_ViewHolder extends RecyclerView.ViewHolder {

    public Base_ViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void onAttached();
    public abstract void onDetached();
}

