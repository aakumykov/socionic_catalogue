package ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils;


import android.view.View;

import androidx.annotation.Nullable;

public class ViewUtils {

    public static void show(@Nullable View view) {
        if (null != view)
            view.setVisibility(View.VISIBLE);
    }

    public static void hide(@Nullable View view) {
        if (null != view)
            view.setVisibility(View.GONE);
    }

    public static void setVisibility(@Nullable View view, boolean isVivible) {
        if (null != view) {
            if (isVivible)
                show(view);
            else
                hide(view);
        }
    }
}
