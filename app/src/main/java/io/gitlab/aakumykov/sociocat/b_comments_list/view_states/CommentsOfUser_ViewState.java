package io.gitlab.aakumykov.sociocat.b_comments_list.view_states;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iViewState;

public class CommentsOfUser_ViewState implements iViewState {

    private String userName;

    public CommentsOfUser_ViewState(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
