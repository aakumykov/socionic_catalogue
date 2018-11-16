package ru.aakumykov.me.mvp.utils;

import android.content.Context;
import android.widget.Toast;

public class MyToast {

    public static void show(Context context, String msg) {
        showToast(context, msg, Toast.LENGTH_SHORT);
    }

    private static void showToast(Context context, String msg, int length) {
        Toast toast = Toast.makeText(context, msg, length);
        toast.show();
    }
}
