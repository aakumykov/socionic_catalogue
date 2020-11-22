package ru.aakumykov.me.sociocat.cards_list2.view_states;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicViewState;

public class LoadingCardsWithoutTag_ViewState implements iBasicViewState {

    private final boolean mDisplayBackButton;

    public LoadingCardsWithoutTag_ViewState(boolean displayBackButton) {
        mDisplayBackButton = displayBackButton;
    }

    public boolean isDisplayBackButton() {
        return mDisplayBackButton;
    }
}
