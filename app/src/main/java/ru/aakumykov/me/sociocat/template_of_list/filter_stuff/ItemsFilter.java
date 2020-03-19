package ru.aakumykov.me.sociocat.template_of_list.filter_stuff;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.template_of_list.iItemsList;
import ru.aakumykov.me.sociocat.template_of_list.model.DataItem;

public class ItemsFilter extends Filter {

    private iItemsList.iPresenter presenter;
    private List<DataItem> originalItemsList = new ArrayList<>();


    public ItemsFilter(List<DataItem> tagsList, iItemsList.iPresenter presenter) {
        this.presenter = presenter;
        this.originalItemsList.addAll(tagsList);
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        FilterResults filterResults = new FilterResults();

        if (null != constraint && constraint.length() > 0)
        {
            List<DataItem> filteredItemsList = new ArrayList<>();
            String query = constraint.toString().toLowerCase();

            for (DataItem tag : originalItemsList) {
                String tagName = tag.getName().toLowerCase();
                if (tagName.contains(query))
                    filteredItemsList.add(tag);
            }

            filterResults.count = filteredItemsList.size();
            filterResults.values = filteredItemsList;
        }
        else
        {
            filterResults.count = originalItemsList.size();
            filterResults.values = originalItemsList;
        }

        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        presenter.onListFiltered(constraint, (ArrayList<DataItem>) results.values);
    }
}
