package ru.aakumykov.me.sociocat.template_of_list;

import android.content.Intent;
import android.widget.Filterable;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.base_view.iBaseView;
import ru.aakumykov.me.sociocat.template_of_list.model.Item;

public interface iItemsList {

    enum ViewState {
        PROGRESS,
        SUCCESS,
        ERROR
    }

    interface iPageView extends iBaseView {
        void setState(ViewState viewState, Integer messageId, @Nullable String messageDetails);

        void showRefreshThrobber();
        void hideRefreshThrobber();

        void startActionMode();
        void finishActionMode();
        boolean actionModeIsActive();
        void refreshActionMode();

        void showSelectedItemsCount(int count);
    }

    interface iDataAdapter extends Filterable, iSelectableAdapter {
        boolean isVirgin();

        void setList(List<Item> inputList);
        void setList(List<Item> inputList, CharSequence filterQuery);
        void appendList(List<Item> inputList);

        Item getItem(int position);
        List<Item> getAllItems();

        void removeItem(Item item);

        int getListSize();

        void sortByName(SortingListener sortingListener);
        void sortByCount(SortingListener sortingListener);
        SortingMode getSortingMode();

        int getPositionOf(Item item);

        boolean allItemsAreSelected();
    }

    interface iPresenter {
        void linkViewAndAdapter(iPageView pageView, iDataAdapter dataAdapter);
        void unlinkViewAndAdapter();

        void onFirstOpen(@Nullable Intent intent);
        void onConfigurationChanged();

        void storeViewState(ViewState viewState, Integer messageId, String messageDetails);

        void onRefreshRequested();

        void onItemClicked(Item item);
        void onItemLongClicked(Item item);

        void onListFiltered(CharSequence filterText, List<Item> filteredList);

        boolean hasFilterText();
        CharSequence getFilterText();

        boolean canEditSelectedItem();
        boolean canDeleteSelectedItem();

        void onSelectAllClicked();
        void onClearSelectionClicked();
        void onEditSelectedItemClicked();
        void onDeleteSelectedItemsClicked();
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
