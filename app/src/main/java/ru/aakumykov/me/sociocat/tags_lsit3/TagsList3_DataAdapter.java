package ru.aakumykov.me.sociocat.tags_lsit3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.tags_lsit3.view_holders.Tag_ViewHolder;

public class TagsList3_DataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements iTagsList3.iDataAdapter, Filterable
{
    private iTagsList3.iPresenter presenter;

    private List<Tag> itemsList = new ArrayList<>();
    private boolean isVirgin = true;
    private iTagsList3.SortOrder currentSortOrder = iTagsList3.SortOrder.ORDER_NAME_DIRECT;
    private TagsFilter tagsFilter;

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
        Tag tag = itemsList.get(position);
        Tag_ViewHolder tagRowViewHolder = (Tag_ViewHolder) holder;
        tagRowViewHolder.initialize(tag);
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
    public void setList(List<Tag> inputList) {
        itemsList.clear();
        itemsList.addAll(inputList);
        notifyDataSetChanged();
    }

    @Override
    public void appendList(List<Tag> inputList) {
        int startIndex = maxIndex();
        itemsList.addAll(inputList);
        notifyItemRangeChanged(startIndex, inputList.size());
    }

    @Override
    public Tag getTag(int position) {
        if (position >= 0 && position <= maxIndex()) {
            return itemsList.get(position);
        }
        else {
            return null;
        }
    }

    @Override
    public void removeTag(Tag item) {
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

    @Override
    public iTagsList3.SortOrder getSortingMode() {
        return currentSortOrder;
    }

    @Override
    public void sortByName(iTagsList3.SortingListener sortingListener) {
        switch (currentSortOrder) {
            case ORDER_NAME_DIRECT:
                currentSortOrder = iTagsList3.SortOrder.ORDER_NAME_REVERSED;
                break;
            default:
                currentSortOrder = iTagsList3.SortOrder.ORDER_NAME_DIRECT;
                break;
        }

        Collections.sort(itemsList, new TagsComparator(currentSortOrder));
        notifyDataSetChanged();
        sortingListener.onSortingComplete();
    }

    @Override
    public void sortByCount(iTagsList3.SortingListener sortingListener) {
        switch (currentSortOrder) {
            case ORDER_COUNT_REVERSED:
                currentSortOrder = iTagsList3.SortOrder.ORDER_COUNT_DIRECT;
                break;
            default:
                currentSortOrder = iTagsList3.SortOrder.ORDER_COUNT_REVERSED;
                break;
        }

        Collections.sort(itemsList, new TagsComparator(currentSortOrder));
        notifyDataSetChanged();
        sortingListener.onSortingComplete();
    }


    // Filterable
    @Override
    public Filter getFilter() {
        if (null == tagsFilter)
            tagsFilter = new TagsFilter(itemsList, presenter);
        return tagsFilter;
    }


    // Внутренние методы
    private int maxIndex() {
        return itemsList.size() - 1;
    }

}
