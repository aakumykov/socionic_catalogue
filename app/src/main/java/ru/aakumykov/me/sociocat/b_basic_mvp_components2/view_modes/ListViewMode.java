package ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes;

import androidx.annotation.Nullable;

public class ListViewMode extends BasicViewMode {
    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof ListViewMode;
    }
}
