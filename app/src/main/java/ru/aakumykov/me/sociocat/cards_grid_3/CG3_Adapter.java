package ru.aakumykov.me.sociocat.cards_grid_3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid_3.items.LoadMore_Item;
import ru.aakumykov.me.sociocat.cards_grid_3.items.iGridItem;
import ru.aakumykov.me.sociocat.models.Card;

public class CG3_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<iGridItem> list = new ArrayList<>();


    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {

            case iGridItem.CARD_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.cg3_grid_item, null);
                viewHolder = new GridItem_ViewHolder(itemView);
                break;

            case iGridItem.LOAD_MORE_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.cg3_loadmore_item, null);
                viewHolder = new LoadMore_ViewHolder(itemView);
                break;

            default:
                throw new RuntimeException("Unknown item view type: "+viewType);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        iGridItem item = list.get(position);

        if (item instanceof Card) {
            Card card = (Card) item;
            GridItem_ViewHolder gridItemViewHolder = (GridItem_ViewHolder) viewHolder;
            gridItemViewHolder.initialize(card);
        }
        else if (item instanceof LoadMore_Item) {
            LoadMore_ViewHolder loadMoreViewHolder = (LoadMore_ViewHolder) viewHolder;
            loadMoreViewHolder.initialize();
        }
        else {
            throw new RuntimeException("Unknown item type: "+item);
        }

    }

    @Override
    public int getItemViewType(int position) {
        iGridItem item = list.get(position);

        if (item instanceof Card)
            return iGridItem.CARD_VIEW_TYPE;
        else if (item instanceof LoadMore_Item)
            return iGridItem.LOAD_MORE_VIEW_TYPE;
        else
            return -1;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }




    // Внутренние методы
    public void setList(List<iGridItem> itemsList) {
        this.list.clear();
        this.list.addAll(itemsList);
        this.list.add(new LoadMore_Item());
        notifyDataSetChanged();
    }
}
