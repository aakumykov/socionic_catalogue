package ru.aakumykov.me.sociocat.tags_list.interfaces;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.models.Tag;

public interface iTagsList_View {

    void goShowCardsWithTag(@NonNull Tag tag);
    void goEditTag(@NonNull Tag tag);
}
