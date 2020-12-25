package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iViewState;

public class ListFilteredViewState implements iViewState {

    private String mFilterText;

    public ListFilteredViewState(String filterText) {
        mFilterText = filterText;
    }

    public String getFilterText() {
        return mFilterText;
    }
}
