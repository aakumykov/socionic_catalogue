package ru.aakumykov.me.sociocat.cards_list.utils;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.cards_list.iCardsList;
import ru.aakumykov.me.sociocat.cards_list.list_items.DataItem;
import ru.aakumykov.me.sociocat.cards_list.list_items.ListItem;
import ru.aakumykov.me.sociocat.models.Card;

public class ItemsFilter extends Filter {

    private iCardsList.iPresenter presenter;
    private List<ListItem> originalItemsList = new ArrayList<>();


    public ItemsFilter(iCardsList.iPresenter presenter) {
        this.presenter = presenter;
    }

    public void setList(List<ListItem> list) {
        this.originalItemsList.clear();
        this.originalItemsList.addAll(list);
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        FilterResults filterResults = new FilterResults();

        // Если текст фильтра пустой, возвращаю исходный список
        if (null == constraint && constraint.length() == 0) {
            filterResults.count = originalItemsList.size();
            filterResults.values = originalItemsList;
            return filterResults;
        }


        List<DataItem> filteredList = new ArrayList<>();
        String query = constraint.toString().toLowerCase();

        for (ListItem item : originalItemsList) {
            if (item instanceof DataItem) {
                Card card = (Card) ((DataItem) item).getPayload();
                String title = card.getTitle().toLowerCase();

                if (title.contains(query))
                    filteredList.add((DataItem) item);
            }
        }

        filterResults.count = filteredList.size();
        filterResults.values = filteredList;

        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        presenter.onListFiltered(constraint, (ArrayList<DataItem>) results.values);
    }
}
