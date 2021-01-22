package io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_state;

import androidx.annotation.NonNull;

public class ProgressPageState extends BasicPageState {

    public ProgressPageState(@NonNull String messageString) {
        super(messageString);
    }

    public ProgressPageState(int messageId) {
        super(messageId);
    }
}
