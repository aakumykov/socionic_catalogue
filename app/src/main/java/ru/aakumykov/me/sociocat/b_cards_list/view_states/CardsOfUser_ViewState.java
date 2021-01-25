package ru.aakumykov.me.sociocat.b_cards_list.view_states;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iViewState;

public class CardsOfUser_ViewState implements iViewState {

    private final String mUserName;

    public CardsOfUser_ViewState(String userName) {
        mUserName = userName;
    }

    public String getUserName() {
        return mUserName;
    }
}
