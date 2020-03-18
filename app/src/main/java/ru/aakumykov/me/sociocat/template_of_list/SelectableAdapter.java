package ru.aakumykov.me.sociocat.template_of_list;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.template_of_list.model.Item;

public abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>
        implements iSelectableAdapter
{
    private static final String TAG = SelectableAdapter.class.getSimpleName();
    private List<Item> selectedItems = new ArrayList<>();

    @Override
    public boolean isSelected(Item item) {
        return selectedItems.contains(item);
    }

    @Override
    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    @Override
    public List<Item> getSelectedItems() {
        List<Item> items = new ArrayList<>(selectedItems.size());
        items.addAll(selectedItems);
        return items;
    }

    @Override
    public void toggleSelection(Item item, int itemIndex) {

        if (selectedItems.contains(item))
            selectedItems.remove(item);
        else
            selectedItems.add(item);

        notifyItemChanged(itemIndex);
    }

    @Override
    public void clearSelection() {
        List<Integer> indexes = new ArrayList<>();
        for (Item item : selectedItems)
            indexes.add(selectedItems.indexOf(item));

        selectedItems.clear();

        for (Integer i : indexes)
            notifyItemChanged(i);
    }

}
