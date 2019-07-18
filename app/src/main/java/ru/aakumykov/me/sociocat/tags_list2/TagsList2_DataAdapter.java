package ru.aakumykov.me.sociocat.tags_list2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Tag;

public class TagsList2_DataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
    iTagsList2.iTagsView
{
    private List<Tag> tagsList = new ArrayList<>();


    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.tags_list2_item, parent, false);
        TagsList2_ViewHolder viewHolder = new TagsList2_ViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Tag tag = tagsList.get(position);
        TagsList2_ViewHolder viewHolder = (TagsList2_ViewHolder) holder;
        viewHolder.initialize(tag);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return tagsList.size();
    }


    // iTagsView
    @Override
    public void displayList(List<Tag> inputList) {
        tagsList.clear();
        tagsList.addAll(inputList);
        notifyDataSetChanged();
    }
}
