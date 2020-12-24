package ru.aakumykov.me.sociocat.b_cards_list.view_states;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicViewState;

public class CardsWithTag_ViewState implements iBasicViewState {

    private final String mTagName;

    public CardsWithTag_ViewState(String tagName) {
        mTagName = tagName;
    }

    public String getTagName() {
        return mTagName;
    }
}
