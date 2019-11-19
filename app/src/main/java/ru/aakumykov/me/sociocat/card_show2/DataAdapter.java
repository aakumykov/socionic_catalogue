package ru.aakumykov.me.sociocat.card_show2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show2.list_items.Card_Item;
import ru.aakumykov.me.sociocat.card_show2.list_items.Comment_Item;
import ru.aakumykov.me.sociocat.card_show2.list_items.iList_Item;
import ru.aakumykov.me.sociocat.card_show2.view_holders.Card_ViewHolder;
import ru.aakumykov.me.sociocat.card_show2.view_holders.Comment_ViewHolder;

public class DataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
    iCardShow2.iDataAdapter
{
    private List<iList_Item> list = new ArrayList<>();

    @Override
    public int getItemViewType(int position) {
        iList_Item listItem = list.get(position);

        if (listItem instanceof Card_Item) {
            return iList_Item.CARD;
        }
        else if (listItem instanceof Comment_Item) {
            return iList_Item.COMMENT;
        }
        else {
            return super.getItemViewType(position);
        }
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case iList_Item.CARD:
                itemView = layoutInflater.inflate(R.layout.card_show_card, parent, false);
                return new Card_ViewHolder(itemView);

            case iList_Item.COMMENT:
                itemView = layoutInflater.inflate(R.layout.card_show_comment, parent, false);
                return new Comment_ViewHolder(itemView);

            default:
                throw new RuntimeException("Unknown vew type: "+viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        iList_Item listItem = list.get(position);

        int itemType = listItem.getItemType();
        switch (itemType) {
            case iList_Item.CARD:
                ((Card_ViewHolder) holder).initialize(listItem);
                break;

            case iList_Item.COMMENT:
                ((Comment_ViewHolder) holder).initialize(listItem);
                break;

            default:
                throw new RuntimeException("Unknown item type: "+itemType);
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
