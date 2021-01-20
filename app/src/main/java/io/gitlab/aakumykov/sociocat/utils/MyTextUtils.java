package io.gitlab.aakumykov.sociocat.utils;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;

public class MyTextUtils {
    private MyTextUtils() {}

    // Открытые методы
    public static Spanned boldString(String text) {
        return Html.fromHtml(emboldString(text));
    }

    public static Spanned boldPartOfString(Context context, int stringResourceId, String boldedString) {
        String textInsideBTags = emboldString(boldedString);
        String text = context.getResources().getString(stringResourceId, textInsideBTags);
        return Html.fromHtml(text);
    }

    // Закрытые методы
    private static String emboldString(String text) {
        return "<b>" + text + "</b>";
    }
}
