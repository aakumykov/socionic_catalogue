package ru.aakumykov.me.sociocat.cards_list.view_states;

import ru.aakumykov.me.sociocat.basic_view_states.iBasicViewState;

public class FilteredListViewState implements iBasicViewState {

    private final String mTagName;

    public FilteredListViewState(String tagName) {
        mTagName = tagName;
    }

    public String getTagName() {
        return mTagName;
    }
}
