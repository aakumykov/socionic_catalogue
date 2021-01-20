package io.gitlab.aakumykov.sociocat.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import io.gitlab.aakumykov.sociocat.R;

public class SimpleYesNoDialog {

    /* Context должен быть контекстом страницы (Activity),
    а не приложения (т.е. ApplicationContext).
    Иначе не определяется тема и вываливается исключение. */
    public static void show(
            @NonNull Context context,
            @Nullable Object title,
            @Nullable Object message,
            Callbacks callbacks
    ) {
        Pair<String,String> titleAndMessage = checkTitleAndMessage(context, title, message);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (null != titleAndMessage.first)
            builder.setTitle(titleAndMessage.first);

        if (null != titleAndMessage.second)
            builder.setMessage(titleAndMessage.second);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callbacks.onYes();
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callbacks.onNo();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                callbacks.onCancel();
            }
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    private static Pair<String,String> checkTitleAndMessage(@NonNull Context context, @Nullable Object title, @Nullable Object message) {

        String titleText = getStringFromObject(context, title);
        String messageText = getStringFromObject(context, message);

        if (null == messageText && null == titleText)
            throw new RuntimeException("You must supply title or message, or both");

        return new Pair<>(titleText, messageText);
    }

    private static String getStringFromObject(@NonNull Context context, @Nullable Object title) {
        String text = null;

        if (title instanceof Integer)
            text = MyUtils.getString(context, (int) title);
        else if (title instanceof String)
            text = (String) title;

        return text;
    }


    public interface Callbacks {
        void onYes();
        void onNo();
        void onCancel();
    }

    public static class AbstractCallbacks implements Callbacks {

        @Override
        public void onYes() {

        }

        @Override
        public void onNo() {

        }

        @Override
        public void onCancel() {

        }
    }

}
