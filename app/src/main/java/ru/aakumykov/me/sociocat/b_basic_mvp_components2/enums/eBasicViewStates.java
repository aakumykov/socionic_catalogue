package ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums;


import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicViewState;

public enum eBasicViewStates implements iBasicViewState {
    NEUTRAL,
    REFRESHING,
    PROGRESS,
    ERROR,
    SELECTION,
    SELECTION_ALL,

}
