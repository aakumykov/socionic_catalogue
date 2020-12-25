package ru.aakumykov.me.sociocat.b_cards_list.view_states;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iViewState;

public class LoadingCards_ViewState implements iViewState {

    private final boolean mHasParent;

    public LoadingCards_ViewState(boolean hasParent) {
        mHasParent = hasParent;
    }

    public boolean isHasParent() {
        return mHasParent;
    }
}
