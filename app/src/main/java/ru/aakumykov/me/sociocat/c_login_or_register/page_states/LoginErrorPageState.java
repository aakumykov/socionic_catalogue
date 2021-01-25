package ru.aakumykov.me.sociocat.c_login_or_register.page_states;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvvm_page_components.interfaces.iMessageHolder;
import ru.aakumykov.me.sociocat.a_basic_mvvm_page_components.page_state.ErrorPageState;

public class LoginErrorPageState extends ErrorPageState {

    public LoginErrorPageState(@NonNull String tag, int userMessageId, String debugMessage) {
        super(tag, userMessageId, debugMessage);
    }

    public LoginErrorPageState(@NonNull String tag, int userMessageId, @NonNull iMessageHolder messageHolder) {
        super(tag, userMessageId, messageHolder);
    }
}
