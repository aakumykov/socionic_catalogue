package ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils;


import android.content.Context;
import android.widget.Toast;

public class PageUtils {

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
