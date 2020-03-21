package ru.aakumykov.me.sociocat.template_of_list.selectable_adapter;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>
        implements iSelectableAdapter
{
    private static final String TAG = SelectableAdapter.class.getSimpleName();
    private Set<Integer> selectedItemsIndexes = new HashSet<>();

    @Override
    public boolean isSelected(Integer index) {
        return selectedItemsIndexes.contains(index);
    }

    @Override
    public int getSelectedItemCount() {
        return selectedItemsIndexes.size();
    }

    @Override
    public List<Integer> getSelectedIndexes() {
        List<Integer> list = new ArrayList<>(selectedItemsIndexes.size());
        list.addAll(selectedItemsIndexes);
        return list;
    }

    @Override
    public void toggleSelection(int itemIndex) {

        if (selectedItemsIndexes.contains(itemIndex))
            selectedItemsIndexes.remove(itemIndex);
        else {
            selectedItemsIndexes.add(itemIndex);
        }

        notifyItemChanged(itemIndex);
    }

    @Override
    public void selectAll(int listSize) {
//        selectedItemsIndexes.clear();
        for (int i=0; i<listSize; i++)
            selectedItemsIndexes.add(i);
        notifyDataSetChanged();
    }

    @Override
    public void clearSelection() {
        Set<Integer> indexes = new HashSet<>(selectedItemsIndexes);

        selectedItemsIndexes.clear();

        for (Integer i : indexes)
            notifyItemChanged(i);
    }

    @Override
    public boolean isMultipleItemsSelected() {
        return selectedItemsIndexes.size() > 1;
    }

    @Override
    public Integer getSingleSelectedItemIndex() {
        if (isMultipleItemsSelected())
            return null;

        if (0 == selectedItemsIndexes.size())
            return null;

        return selectedItemsIndexes.iterator().next();
    }

}
