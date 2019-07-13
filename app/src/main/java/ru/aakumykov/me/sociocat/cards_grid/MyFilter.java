package ru.aakumykov.me.sociocat.cards_grid;

import android.widget.Filter;

import java.util.List;

import ru.aakumykov.me.sociocat.cards_grid.items.iGridItem;

public class MyFilter extends Filter {

    public interface iMyFilterCallbacks {
        void onListFiltered(List<iGridItem> filteredList);
    }

    private List<iGridItem> list;

    public MyFilter(iMyFilterCallbacks myFilterCallbacks) {

    }

    public void filterList(List<iGridItem> gridItemsList, String filterKey) {
        this.list = gridItemsList;
        performFiltering(filterKey);
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        return null;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

    }
}
