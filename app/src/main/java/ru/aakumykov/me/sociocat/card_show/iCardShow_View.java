package ru.aakumykov.me.sociocat.card_show;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;
import ru.aakumykov.me.sociocat.interfaces.iBaseView;

public interface iCardShow_View extends iBaseView {

    // TODO: кажется, ему здесь не место
    void scrollListToPosition(int position);
}
