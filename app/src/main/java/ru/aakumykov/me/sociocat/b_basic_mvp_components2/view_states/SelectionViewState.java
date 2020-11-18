package ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_states;


import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicViewState;

public class SelectionViewState implements iBasicViewState {

    private int mSelectedItemsCount = -1;

    public SelectionViewState(int selectedItemsCount) {
        mSelectedItemsCount = selectedItemsCount;
    }

    public int getSelectedItemsCount() {
        return mSelectedItemsCount;
    }
}
