package ru.aakumykov.me.sociocat.cards_list2.view_states;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicViewState;

public class CardsWithTag_ViewState implements iBasicViewState {

    private final String mTagName;

    public CardsWithTag_ViewState(String tagName) {
        mTagName = tagName;
    }

    public String getTagName() {
        return mTagName;
    }
}
