package ru.aakumykov.me.sociocat.tags_list.view_states;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_states.ProgressViewState;

public class CancelableProgress extends ProgressViewState {

    public CancelableProgress(int messageId) {
        super(messageId);
    }

    public CancelableProgress(String messageString) {
        super(messageString);
    }
}
