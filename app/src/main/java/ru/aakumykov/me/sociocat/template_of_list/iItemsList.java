package ru.aakumykov.me.sociocat.template_of_list;

import android.content.Intent;
import android.widget.Filterable;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.base_view.iBaseView;
import ru.aakumykov.me.sociocat.template_of_list.model.Item;

public interface iItemsList {

    interface iPageView extends iBaseView {
        void showRefreshThrobber();
        void hideRefreshThrobber();

        void startActionMode();
        void finishActionMode();

        void showSelectedItemsCount(int count);
    }

    interface iDataAdapter extends Filterable, iSelectableAdapter {
        boolean isVirgin();

        void setList(List<Item> inputList);
        void setList(List<Item> inputList, CharSequence filterQuery);
        void appendList(List<Item> inputList);

        Item getItem(int position);
        void removeItem(Item item);

        int getListSize();

        void sortByName(SortingListener sortingListener);
        void sortByCount(SortingListener sortingListener);
        SortingMode getSortingMode();

        int getPositionOf(Item item);
    }

    interface iPresenter {
        void linkViewAndAdapter(iPageView pageView, iDataAdapter dataAdapter);
        void unlinkView();

        void onFirstOpen(@Nullable Intent intent);
        void onConfigurationChanged();
        void onPageRefreshRequested();

        void onItemClicked(Item item);
        void onItemLongClicked(Item item);

        void onListFiltered(CharSequence filterText, List<Item> filteredList);

        boolean hasFilterText();
        CharSequence getFilterText();

        void onSortByNameClicked();
        void onSortByCountClicked();

        void onSelectedItemsDeleteClicked();
    }


    enum SortingMode {
        ORDER_NAME_DIRECT,
        ORDER_NAME_REVERSED,
        ORDER_COUNT_DIRECT,
        ORDER_COUNT_REVERSED
    }

    interface SortingListener {
        void onSortingComplete();
    }
}
