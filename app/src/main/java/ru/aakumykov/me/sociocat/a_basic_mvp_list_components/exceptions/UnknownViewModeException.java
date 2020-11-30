package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.exceptions;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;

public class UnknownViewModeException extends RuntimeException {

    public UnknownViewModeException(BasicViewMode viewMode) {
        super("Неизвестный viewMode: "+viewMode);
    }
}
