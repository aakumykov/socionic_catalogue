package ru.aakumykov.me.sociocat.cards_grid_3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;

public class CG3_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Card> list = new ArrayList<>();

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.cg3_grid_tile, null);
        return new GridItem_ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Card card = list.get(position);
        GridItem_ViewHolder gridItemViewHolder = (GridItem_ViewHolder) viewHolder;
        gridItemViewHolder.initialize(card);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void setList(List<Card> cardsList) {
        this.list.clear();
        this.list.addAll(cardsList);
        notifyDataSetChanged();
    }
}
