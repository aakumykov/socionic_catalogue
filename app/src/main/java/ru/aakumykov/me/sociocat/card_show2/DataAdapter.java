package ru.aakumykov.me.sociocat.card_show2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show2.list_items.List_Item;
import ru.aakumykov.me.sociocat.card_show2.list_items.iList_Item;
import ru.aakumykov.me.sociocat.card_show2.view_holders.Card_ViewHolder;
import ru.aakumykov.me.sociocat.card_show2.view_holders.Comment_ViewHolder;
import ru.aakumykov.me.sociocat.models.Card;

public class DataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
    iCardShow2.iDataAdapter
{
    private final static int CARD_INDEX = 0;
    private List<iList_Item> itemsList = new ArrayList<>();


    @Override
    public int getItemViewType(int position) {
        iList_Item listItem = itemsList.get(position);
        return listItem.getItemType();
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
        iList_Item listItem = itemsList.get(position);

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


    @Override
    public void setCard(Card card) {
        iList_Item listItem = new List_Item(card);

        if (0 == itemsList.size())
            itemsList.add(listItem);
        else {
            if (itemsList.get(CARD_INDEX).isCardItem())
                itemsList.set(CARD_INDEX, listItem);
            else {
                itemsList.add(CARD_INDEX, listItem);
            }
        }
    }
}
