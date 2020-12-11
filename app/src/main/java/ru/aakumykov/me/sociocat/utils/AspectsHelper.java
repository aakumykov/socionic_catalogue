package ru.aakumykov.me.sociocat.utils;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

public class AspectsHelper {
// БЛ,БС,БЭ,БИ,ЧЭ,ЧС,ЧИ,ЧЛ
    public static final String LOGIC = "БЛ";
    public static final String PRACTIVE = "ЧЛ";
    public static final String EMOTION = "ЧЭ";
    public static final String RELATION = "БЭ";
    public static final String FORCE = "ЧС";
    public static final String INTUITION = "ЧИ";
    public static final String SENCE = "БС";
    public static final String TIME = "БИ";

    public enum ASPECTS {
        LOGIC,
        PRACTIVE,
        EMOTION,
        RELATION,
        FORCE,
        INTUITION,
        SENCE,
        TIME
    }

    private final static List<String> TAGS_LIST = Arrays.asList("БЛ","ЧЭ","БС","ЧИ","БЭ","ЧЛ","БИ","ЧС");

    public static boolean isAspectTag(@Nullable String tagName) {
        return TAGS_LIST.contains(tagName);
    }

    public static boolean containsAspectTag(List<String> tagsList) {
        for (String tagName : TAGS_LIST) {
            if (tagsList.contains(tagName))
                return true;
        }
        return false;
    }
}
