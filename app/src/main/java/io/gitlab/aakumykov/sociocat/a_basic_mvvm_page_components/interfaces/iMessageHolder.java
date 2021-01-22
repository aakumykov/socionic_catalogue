package io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.interfaces;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface iMessageHolder {

    @Nullable
    String getMessage(@NonNull Context context);
}
