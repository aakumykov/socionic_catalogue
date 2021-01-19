package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.exceptions;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;

public class UnknownViewModeException extends RuntimeException {

    public UnknownViewModeException(BasicViewMode viewMode) {
        super("Неизвестный viewMode: "+viewMode);
    }
}
