package ru.aakumykov.me.sociocat.tags_lsit3;

import android.content.Intent;
import android.widget.Filterable;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Tag;

public interface iTagsList3 {

    interface iPageView extends iBaseView {
        void showRefreshThrobber();
        void hideRefreshThrobber();

        void showCardsWithTag(Tag tag);
    }

    interface iDataAdapter extends Filterable {
        boolean isVirgin();
        void deflorate();

        void setList(List<Tag> inputList);
        void appendList(List<Tag> inputList);
        int getListSize();

        Tag getTag(int position);
        void removeTag(Tag tag);

        void sortByName(SortingListener sortingListener);
        void sortByCount(SortingListener sortingListener);
        SortOrder getSortingMode();
    }

    interface iPresenter {
        void linkViewAndAdapter(iPageView pageView, iDataAdapter dataAdapter);
        void unlinkView();

        void onFirstOpen(@Nullable Intent intent);
        void onConfigurationChanged();
        void onPageRefreshRequested();

        void onTagClicked(Tag tag);

        void onSortByNameClicked();
        void onSortByCountClicked();

        void onListFiltered(CharSequence filterText, List<Tag> filteredList);

        CharSequence getFilterText();
    }

    enum SortOrder {
        ORDER_NAME_DIRECT,
        ORDER_NAME_REVERSED,
        ORDER_COUNT_DIRECT,
        ORDER_COUNT_REVERSED
    }

    interface SortingListener {
        void onSortingComplete();
    }
}
