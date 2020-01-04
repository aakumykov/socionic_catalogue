package ru.aakumykov.me.sociocat.tags_lsit3;

import android.content.Intent;

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

    interface iDataAdapter {
        boolean isVirgin();
        void deflorate();

        void setList(List<Tag> inputList);
        void appendList(List<Tag> inputList);

        Tag getTag(int position);
        void removeTag(Tag tag);

        int getListSize();
    }

    interface iPresenter {
        void linkViewAndAdapter(iPageView pageView, iDataAdapter dataAdapter);
        void unlinkView();

        void onFirstOpen(@Nullable Intent intent);
        void onConfigurationChanged();
        void onPageRefreshRequested();

        void onTagClicked(Tag tag);

    }
}
