package ru.aakumykov.me.sociocat.tags_list2;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.models.Tag;

public class TagsList2_DataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
    iTagsList2.iTagsView
{
    private List<Tag> tagsList = new ArrayList<>();


    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return 0;
    }


    // iTagsView
    @Override
    public void displayList(List<Tag> tagsList) {

    }
}
