package ru.aakumykov.me.sociocat.cards_list2.adapter_utils;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewHolderCreator;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicMVP_ItemClickListener;

public class CardsList2_ViewHolderCreator extends BasicMVP_ViewHolderCreator {

    public CardsList2_ViewHolderCreator(iBasicMVP_ItemClickListener itemClickListener) {
        super(itemClickListener);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(@NonNull ViewGroup parent, int viewType) {
        return super.createViewHolder(parent, viewType);
    }
}
