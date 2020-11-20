package ru.aakumykov.me.sociocat.b_basic_mvp_components2.exceptions;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.BasicViewMode;

public class UnknownViewModeException extends RuntimeException {

    public UnknownViewModeException(BasicViewMode viewMode) {
        super("Неизвестный viewMode: "+viewMode);
    }
}
