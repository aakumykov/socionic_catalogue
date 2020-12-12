package ru.aakumykov.me.sociocat.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.ArrayMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import java.util.List;
import java.util.Map;

import ru.aakumykov.me.sociocat.R;

public class CanonicalTagsHelper {

    private static CanonicalTagsHelper sInstance;

    public static synchronized CanonicalTagsHelper getInstance(@NonNull Context context) {
        if (null == sInstance)
            sInstance = new CanonicalTagsHelper(context);
        return sInstance;
    }


    private final Map<String, Integer> mTagDrawableMap = new ArrayMap<>();
    private final Map<String, Integer> mTagIdMap = new ArrayMap<>();

    private CanonicalTagsHelper(@NonNull Context context) {
        mTagDrawableMap.put("БЛ", R.drawable.aspect_logic);
        mTagDrawableMap.put("ЧЭ", R.drawable.aspect_emotion);
        mTagDrawableMap.put("БС", R.drawable.aspect_sense);
        mTagDrawableMap.put("ЧИ", R.drawable.aspect_intuition);
        mTagDrawableMap.put("ЧС", R.drawable.aspect_force);
        mTagDrawableMap.put("БИ", R.drawable.aspect_time);
        mTagDrawableMap.put("БЭ", R.drawable.aspect_relation);
        mTagDrawableMap.put("ЧЛ", R.drawable.aspect_practice);

        mTagIdMap.put("БЛ", R.id.logic);
        mTagIdMap.put("ЧЭ", R.id.emotion);
        mTagIdMap.put("БС", R.id.sense);
        mTagIdMap.put("ЧИ", R.id.intuition);
        mTagIdMap.put("ЧС", R.id.force);
        mTagIdMap.put("БИ", R.id.time);
        mTagIdMap.put("БЭ", R.id.relation);
        mTagIdMap.put("ЧЛ", R.id.practice);
    }


    public boolean containsCanonicalTag(@NonNull List<String> tagsList) {
        for (String tagName : tagsList) {
            if (mTagDrawableMap.containsKey(tagName))
                return true;
        }
        return false;
    }

    public boolean isCanonicalTag(@Nullable String tagName) {
        return mTagDrawableMap.containsKey(tagName);
    }

    public Drawable getTagIcon(@NonNull Context context, @NonNull String tagName) {
        if (!isCanonicalTag(tagName))
            return null;

        //noinspection ConstantConditions
        return ResourcesCompat.getDrawable(context.getResources(), mTagDrawableMap.get(tagName), null);
    }

    public int getTagId(@NonNull String tagName) {
        if (mTagIdMap.containsKey(tagName))
            return mTagIdMap.get(tagName);

        return -1;
    }
}
