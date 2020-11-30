package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils;


import android.content.Context;
import android.widget.Toast;

public class PageUtils {

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
