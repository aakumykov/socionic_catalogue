package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_modes;

import androidx.annotation.Nullable;

public class ListViewMode extends BasicViewMode {
    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof ListViewMode;
    }
}
