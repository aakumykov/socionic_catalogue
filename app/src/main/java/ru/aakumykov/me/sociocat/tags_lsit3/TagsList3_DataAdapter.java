package ru.aakumykov.me.sociocat.tags_lsit3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.tags_lsit3.model.Item;
import ru.aakumykov.me.sociocat.tags_lsit3.view_holders.Tag_ViewHolder;

public class TagsList3_DataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements iTagsList3.iDataAdapter {

    private iTagsList3.iPresenter presenter;

    private List<Item> itemsList = new ArrayList<>();
    private boolean isVirgin = true;


    // Конструктор
    public TagsList3_DataAdapter(iTagsList3.iPresenter presenter) {

        if (null == presenter)
            throw new IllegalArgumentException("Presenter passed as argument cannot be null");

        this.presenter = presenter;
    }


    // RecyclerView.Adapter
    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.tags_list3_item, parent, false);
        return new Tag_ViewHolder(itemView, presenter);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Item item = itemsList.get(position);
        Tag_ViewHolder tagRowViewHolder = (Tag_ViewHolder) holder;
        tagRowViewHolder.initialize(item);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    // iTagsList3.iDataAdapter
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
