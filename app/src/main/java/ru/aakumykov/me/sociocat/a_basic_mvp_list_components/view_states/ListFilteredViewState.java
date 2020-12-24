package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicViewState;

public class ListFilteredViewState implements iBasicViewState {

    private String mFilterText;

    public ListFilteredViewState(String filterText) {
        mFilterText = filterText;
    }

    public String getFilterText() {
        return mFilterText;
    }
}
