package ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces;

import androidx.annotation.NonNull;

public interface iBasicData {

    String getName();

    Long getDate();

    @NonNull
    String toString();
}
