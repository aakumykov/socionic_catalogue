package ru.aakumykov.me.sociocat.cards_list2.view_states;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicViewState;

public class LoadingCards_ViewState implements iBasicViewState {

    private final boolean mHasParent;

    public LoadingCards_ViewState(boolean hasParent) {
        mHasParent = hasParent;
    }

    public boolean isHasParent() {
        return mHasParent;
    }
}
