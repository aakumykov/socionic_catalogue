package ru.aakumykov.me.sociocat.cards_list2.adapter_utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewHolderCreator;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.data_types.BasicMVP_ItemTypes;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eBasicViewMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iViewMode;
import ru.aakumykov.me.sociocat.cards_list2.view_holders.CardInFeed_ViewHolder;
import ru.aakumykov.me.sociocat.cards_list2.view_holders.CardInGrid_ViewHolder;
import ru.aakumykov.me.sociocat.cards_list2.view_holders.CardInList_ViewHolder;
import ru.aakumykov.me.sociocat.cards_list2.view_holders.Card_ViewHolder;

public class CardsList2_ViewHolderCreator extends BasicMVP_ViewHolderCreator {

    public CardsList2_ViewHolderCreator(iViewMode initialViewMode, iBasicMVP_ItemClickListener itemClickListener) {
        super(initialViewMode, itemClickListener);
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

        View itemView = inflateItemView(parent);

        Card_ViewHolder viewHolder;

        eBasicViewMode viewMode = (eBasicViewMode) getCurrentViewMode();

        switch (viewMode) {
            case LIST:
                viewHolder = new CardInList_ViewHolder(itemView);
                break;
            case FEED:
                viewHolder = new CardInFeed_ViewHolder(itemView);
                break;
            case GRID:
                viewHolder = new CardInGrid_ViewHolder(itemView);
                break;
            default:
                throw new RuntimeException("Неизвестный viewMode: "+viewMode);
        }

        viewHolder.setItemClickListener(mItemClickListener);

        return viewHolder;
    }

    private View inflateItemView(@NonNull ViewGroup parent) {

        eBasicViewMode viewMode = (eBasicViewMode) getCurrentViewMode();

        int layoutId = -1;

        switch (viewMode) {
            case LIST:
                layoutId = R.layout.cards_list2_card_list_item;
                break;
            case GRID:
                layoutId = R.layout.cards_list2_card_grid_item;
                break;
            case FEED:
                layoutId = R.layout.cards_list2_card_feed_item;
                break;
            default:
                throw new RuntimeException("Неизвестный viewMode: "+viewMode);
        }

        return LayoutInflater.from(parent.getContext()).inflate(layoutId,parent,false);
    }
}
