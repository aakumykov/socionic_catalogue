package ru.aakumykov.me.sociocat.a_basic_mvp_components.enums;


import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iViewState;

public enum eBasicViewStates implements iViewState {
    NEUTRAL,
    REFRESHING,
    PROGRESS,
    ERROR,
    SELECTION,
    SELECTION_ALL,

}
