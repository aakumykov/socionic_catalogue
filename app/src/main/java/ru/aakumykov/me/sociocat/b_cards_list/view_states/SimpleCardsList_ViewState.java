package ru.aakumykov.me.sociocat.b_cards_list.view_states;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iViewState;

public class SimpleCardsList_ViewState implements iViewState {

    private final boolean mDisplayBackButton;

    public SimpleCardsList_ViewState(boolean displayBackButton) {
        mDisplayBackButton = displayBackButton;
    }

    public boolean isDisplayBackButton() {
        return mDisplayBackButton;
    }
}
