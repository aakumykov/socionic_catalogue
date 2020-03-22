package ru.aakumykov.me.sociocat.cards_list.filter_stuff;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.cards_list.iCardsList;
import ru.aakumykov.me.sociocat.cards_list.list_items.DataItem;
import ru.aakumykov.me.sociocat.cards_list.list_items.ListItem;

public class ItemsFilter extends Filter {

    private iCardsList.iPresenter presenter;
    private List<ListItem> originalItemsList = new ArrayList<>();


    public ItemsFilter(List<ListItem> tagsList, iCardsList.iPresenter presenter) {
        this.presenter = presenter;
        this.originalItemsList.addAll(tagsList);
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        FilterResults filterResults = new FilterResults();

        /*if (null != constraint && constraint.length() > 0)
        {
            List<DataItem> filteredItemsList = new ArrayList<>();
            String query = constraint.toString().toLowerCase();

            for (ListItem item : originalItemsList) {
                String tagName = item.getName().toLowerCase();
                if (tagName.contains(query))
                    filteredItemsList.add(item);
            }

            filterResults.count = filteredItemsList.size();
            filterResults.values = filteredItemsList;
        }
        else
        {
            filterResults.count = originalItemsList.size();
            filterResults.values = originalItemsList;
        }*/

        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        presenter.onListFiltered(constraint, (ArrayList<DataItem>) results.values);
    }
}
