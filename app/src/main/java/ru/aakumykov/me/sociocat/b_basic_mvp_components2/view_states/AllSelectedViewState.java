package ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_states;


import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicViewState;

public class AllSelectedViewState implements iBasicViewState {

    private int mSelectedItemsCount = -1;

    public AllSelectedViewState(int selectedItemsCount) {
        mSelectedItemsCount = selectedItemsCount;
    }

    public int getSelectedItemsCount() {
        return mSelectedItemsCount;
    }
}
