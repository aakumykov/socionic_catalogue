package ru.aakumykov.me.sociocat.cards_list.view_states;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicViewState;

public class CardsWithTagViewState implements iBasicViewState {

    private final String mTagName;

    public CardsWithTagViewState(String tagName) {
        mTagName = tagName;
    }

    public String getTagName() {
        return mTagName;
    }
}
