package io.gitlab.aakumykov.sociocat.d_login2.page_states;

import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.interfaces.iMessageHolder;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_state.ErrorPageState;

public class ErrorLoadingUserPageState extends ErrorPageState {

    public ErrorLoadingUserPageState(@NonNull String tag, int userMessageId, String debugMessage) {
        super(tag, userMessageId, debugMessage);
    }

    public ErrorLoadingUserPageState(@NonNull String tag, int userMessageId, @NonNull iMessageHolder messageHolder) {
        super(tag, userMessageId, messageHolder);
    }
}
