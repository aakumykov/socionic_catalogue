package ru.aakumykov.me.sociocat.cards_list2.view_states;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicViewState;

public class LoadingAllCardsViewState implements iBasicViewState {

    private boolean mWithBackButton = false;

    public LoadingAllCardsViewState(boolean withBackButton) {
        mWithBackButton = withBackButton;
    }

    public boolean isWithBackButton() {
        return mWithBackButton;
    }
}
