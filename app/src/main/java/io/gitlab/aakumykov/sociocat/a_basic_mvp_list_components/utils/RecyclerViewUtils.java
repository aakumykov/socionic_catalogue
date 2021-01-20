package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.utils;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import io.gitlab.aakumykov.sociocat.R;

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

    public static void setFullSpanIfSupported(RecyclerView.ViewHolder viewHolder) {

        ViewGroup.LayoutParams lp = viewHolder.itemView.getLayoutParams();

        boolean complaintLayoutManager =
                (lp instanceof StaggeredGridLayoutManager.LayoutParams) ||
                        (lp instanceof GridLayoutManager.LayoutParams);

        if (complaintLayoutManager)
            ((StaggeredGridLayoutManager.LayoutParams) lp).setFullSpan(true);
    }
}
