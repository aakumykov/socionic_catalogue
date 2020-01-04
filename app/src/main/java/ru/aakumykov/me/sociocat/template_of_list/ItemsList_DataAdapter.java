package ru.aakumykov.me.sociocat.template_of_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.template_of_list.model.Item;
import ru.aakumykov.me.sociocat.template_of_list.view_holders.Row_ViewHolder;

public class ItemsList_DataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements iItemsList.iDataAdapter {

    private iItemsList.iPresenter presenter;

    private List<Item> itemsList = new ArrayList<>();
    private boolean isVirgin = true;


    // Конструктор
    public ItemsList_DataAdapter(iItemsList.iPresenter presenter) {

        if (null == presenter)
            throw new IllegalArgumentException("Presenter passed as argument cannot be null");

        this.presenter = presenter;
    }


    // RecyclerView.Adapter
    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.items_list_row_item, parent, false);
        return new Row_ViewHolder(itemView, presenter);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Item item = itemsList.get(position);
        Row_ViewHolder tagRowViewHolder = (Row_ViewHolder) holder;
        tagRowViewHolder.initialize(item);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    // iItemsList.iDataAdapter
    @Override
    public boolean isVirgin() {
        return this.isVirgin;
    }
    @Override
    public void deflorate() {
        this.isVirgin = false;
    }

    @Override
    public void setList(List<Item> inputList) {
        itemsList.clear();
        itemsList.addAll(inputList);
        notifyDataSetChanged();
    }

    @Override
    public void appendList(List<Item> inputList) {
        int startIndex = maxIndex();
        itemsList.addAll(inputList);
        notifyItemRangeChanged(startIndex, inputList.size());
    }

    @Override
    public Item getItem(int position) {
        if (position >= 0 && position <= maxIndex()) {
            return itemsList.get(position);
        }
        else {
            return null;
        }
    }

    @Override
    public void removeItem(Item item) {
        int index = itemsList.indexOf(item);
        if (index >= 0) {
            itemsList.remove(index);
            notifyItemRemoved(index);
        }
    }

    @Override
    public int getListSize() {
        return itemsList.size();
    }


    // Внутренние методы
    private int maxIndex() {
        return itemsList.size() - 1;
    }

}
