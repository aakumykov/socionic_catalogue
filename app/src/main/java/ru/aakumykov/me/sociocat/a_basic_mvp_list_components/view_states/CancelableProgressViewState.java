package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states;

public class CancelableProgressViewState extends ProgressViewState {

    public CancelableProgressViewState(int messageId) {
        super(messageId);
    }

    public CancelableProgressViewState(String messageString) {
        super(messageString);
    }
}
