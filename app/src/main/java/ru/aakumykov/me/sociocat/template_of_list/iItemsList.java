package ru.aakumykov.me.sociocat.template_of_list;

import android.content.Intent;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.template_of_list.model.Item;

public interface iItemsList {

    interface iPageView extends iBaseView {
        void hideRefreshThrobber();
    }

    interface iDataAdapter {
        boolean isVirgin();
        void deflorate();

        void setList(List<Item> inputList);
        void appendList(List<Item> inputList);

        Item getItem(int position);
        void removeItem(Item item);

        int getListSize();
    }

    interface iPresenter {
        void linkViewAndAdapter(iPageView pageView, iDataAdapter dataAdapter);
        void unlinkView();

        void onFirstOpen(@Nullable Intent intent);
        void onConfigurationChanged();
        void onPageRefreshRequested();
        void onItemClicked(Item item);

    }
}
