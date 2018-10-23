package ru.aakumykov.me.mvp;

import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.google.firebase.database.DataSnapshot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.aakumykov.me.mvp.models.Card;

public final class MyUtils {
    private MyUtils() {}

    public static void show(View view) {
        view.setVisibility(View.VISIBLE);
    }

    public static void hide(View view) {
        view.setVisibility(View.GONE);
    }

    public static String mime2ext(String mimeType) {
        Pattern pattern = Pattern.compile("^image/([a-z]+)$");
        Matcher matcher = pattern.matcher(mimeType);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public static Card snapshot2card(DataSnapshot dataSnapshot) throws IllegalArgumentException {
        Card card = dataSnapshot.getValue(Card.class);
        // TODO: протестировать с card == null
        if (null != card) {
            card.setKey(dataSnapshot.getKey());
        } else {
            throw new IllegalArgumentException("Card object is null");
        }
        return card;
    }

//    public static String getMimeType(String url) {
//        String type = null;
//        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
//        if (extension != null) {
//            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//        }
//        return type;
//    }
}
