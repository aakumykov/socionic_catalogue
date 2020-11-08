package ru.aakumykov.me.sociocat.a_basic_mvp_components.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;

import ru.aakumykov.me.sociocat.R;

public class RecyclerViewUtils {

    public static DividerItemDecoration createSimpleDividerItemDecoration(@NonNull Context context, int drawableResourceId) {

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);

        Drawable drawable = ResourcesCompat.getDrawable(
                context.getResources(),
                R.drawable.simple_list_item_divider,
                null
        );

        if (null != drawable) {
            dividerItemDecoration.setDrawable(drawable);
            return dividerItemDecoration;
        }

        return null;
    }
}
