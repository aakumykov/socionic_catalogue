package ru.aakumykov.me.sociocat.cards_list2.adapter_utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewHolderCreator;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.data_types.BasicMVP_ItemTypes;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.cards_list2.list_parts.CardInList_ViewHolder;

public class CardsList2_ViewHolderCreator extends BasicMVP_ViewHolderCreator {

    public CardsList2_ViewHolderCreator(iBasicMVP_ItemClickListener itemClickListener) {
        super(itemClickListener);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case BasicMVP_ItemTypes.CARD_ITEM:
                return createCardViewHolder(parent);
            default:
                return super.createViewHolder(parent, viewType);
        }
    }

    private RecyclerView.ViewHolder createCardViewHolder(@NonNull ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_list2_card_list_item,parent,false);

        CardInList_ViewHolder viewHolder = new CardInList_ViewHolder(itemView);

        viewHolder.setItemClickListener(mItemClickListener);

        return new CardInList_ViewHolder(itemView);
    }
}
