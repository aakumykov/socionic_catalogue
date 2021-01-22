package io.gitlab.aakumykov.sociocat.d_login2.page_states;

import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.interfaces.iMessageHolder;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_state.ErrorPageState;

public class UserNotExistsPageState extends ErrorPageState {

    public UserNotExistsPageState(@NonNull String tag, int userMessageId, String debugMessage) {
        super(tag, userMessageId, debugMessage);
    }

    public UserNotExistsPageState(@NonNull String tag, int userMessageId, @NonNull iMessageHolder messageHolder) {
        super(tag, userMessageId, messageHolder);
    }
}
