package ru.aakumykov.me.sociocat.b_cards_list.view_states;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicViewState;

public class CardsList_ViewState implements iBasicViewState {

    private final boolean mDisplayBackButton;

    public CardsList_ViewState(boolean displayBackButton) {
        mDisplayBackButton = displayBackButton;
    }

    public boolean isDisplayBackButton() {
        return mDisplayBackButton;
    }
}
