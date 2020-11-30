package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.enums;


import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicViewState;

public enum eBasicViewStates implements iBasicViewState {
    NEUTRAL,
    REFRESHING,
    PROGRESS,
    ERROR,
    SELECTION,
    SELECTION_ALL,

}
