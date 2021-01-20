package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_states;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iViewState;

public class ListFilteredViewState implements iViewState {

    private String mFilterText;

    public ListFilteredViewState(String filterText) {
        mFilterText = filterText;
    }

    public String getFilterText() {
        return mFilterText;
    }
}
