package ru.aakumykov.me.sociocat.card_show.presenters;


import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.card_show.adapter.iCard_ViewAdapter;
import ru.aakumykov.me.sociocat.card_show.iCommentForm;

public interface iCardPresenter extends iPresenter {

    void bindCommentForm(iCommentForm pageView);
    void unbindCommentForm();

    void bindListAdapter(iCard_ViewAdapter listAdapter);
    void unbindListAdapter();

    void onWorkBegins(@Nullable String cardKey, @Nullable String commentKey);
    void onErrorOccurs();

    void onAddCommentClicked();
}
