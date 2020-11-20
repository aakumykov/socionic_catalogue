package ru.aakumykov.me.sociocat.cards_list2.adapter_utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewHolderCreator;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.data_types.BasicMVP_ItemTypes;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.exceptions.UnknownViewModeException;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.FeedViewMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.GridViewMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.ListViewMode;
import ru.aakumykov.me.sociocat.cards_list2.view_holders.CardViewHolder;
import ru.aakumykov.me.sociocat.cards_list2.view_holders.CardViewHolder_Feed;
import ru.aakumykov.me.sociocat.cards_list2.view_holders.CardViewHolder_Grid;
import ru.aakumykov.me.sociocat.cards_list2.view_holders.CardViewHolder_List;

public class CardsList2_ViewHolderCreator extends BasicMVP_ViewHolderCreator {

    public CardsList2_ViewHolderCreator(iBasicMVP_ItemClickListener itemClickListener) {
        super(itemClickListener);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(@NonNull ViewGroup parent, int viewType, BasicViewMode viewMode) {

        if (BasicMVP_ItemTypes.CARD_ITEM == viewType) {
            return createCardViewHolder(parent, viewMode);
        }
        return super.createViewHolder(parent, viewType, viewMode);
    }


    private RecyclerView.ViewHolder createCardViewHolder(@NonNull ViewGroup parent, BasicViewMode viewMode) {

        View itemView = inflateItemView(parent, viewMode);

        CardViewHolder viewHolder = prepareViewHolder(itemView, viewMode);

        viewHolder.setItemClickListener(mItemClickListener);

        return viewHolder;
    }

    private View inflateItemView(ViewGroup parent, BasicViewMode viewMode) {

        int layoutId = -1;

        if (viewMode instanceof ListViewMode)
            layoutId = R.layout.cards_list2_card_item_list;
        else if (viewMode instanceof GridViewMode)
            layoutId = R.layout.cards_list2_card_item_grid;
        else if (viewMode instanceof FeedViewMode)
            layoutId = R.layout.cards_list2_card_item_feed;
        else
            throw new UnknownViewModeException(viewMode);

        return LayoutInflater.from(parent.getContext()).inflate(layoutId,parent,false);
    }

    private CardViewHolder prepareViewHolder(View itemView, BasicViewMode viewMode) {

        if (viewMode instanceof ListViewMode)
            return new CardViewHolder_List(itemView);
        else if (viewMode instanceof GridViewMode)
            return new CardViewHolder_Grid(itemView);
        else if (viewMode instanceof FeedViewMode)
            return new CardViewHolder_Feed(itemView);
        else
            throw new UnknownViewModeException(viewMode);
    }
}
