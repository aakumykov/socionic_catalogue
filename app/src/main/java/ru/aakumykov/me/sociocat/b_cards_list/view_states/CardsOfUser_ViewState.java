package ru.aakumykov.me.sociocat.b_cards_list.view_states;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicViewState;

public class CardsOfUser_ViewState implements iBasicViewState {

    private final String mUserName;

    public CardsOfUser_ViewState(String userName) {
        mUserName = userName;
    }

    public String getUserName() {
        return mUserName;
    }
}
