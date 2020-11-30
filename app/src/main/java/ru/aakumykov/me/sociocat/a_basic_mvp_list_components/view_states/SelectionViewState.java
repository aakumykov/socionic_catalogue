package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states;


import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicViewState;

public class SelectionViewState implements iBasicViewState {

    private int mSelectedItemsCount = -1;

    public SelectionViewState(int selectedItemsCount) {
        mSelectedItemsCount = selectedItemsCount;
    }

    public int getSelectedItemsCount() {
        return mSelectedItemsCount;
    }
}
