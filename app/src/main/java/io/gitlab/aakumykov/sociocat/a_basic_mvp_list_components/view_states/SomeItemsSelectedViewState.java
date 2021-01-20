package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_states;


import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iViewState;

public class SomeItemsSelectedViewState implements iViewState {

    private int mSelectedItemsCount = -1;

    public SomeItemsSelectedViewState(int selectedItemsCount) {
        mSelectedItemsCount = selectedItemsCount;
    }

    public int getSelectedItemsCount() {
        return mSelectedItemsCount;
    }
}
