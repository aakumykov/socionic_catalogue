package ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_states;

public class CancelableProgressViewState extends ProgressViewState {

    public CancelableProgressViewState(int messageId) {
        super(messageId);
    }

    public CancelableProgressViewState(String messageString) {
        super(messageString);
    }
}
