package ru.aakumykov.me.sociocat.utils;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import ru.aakumykov.me.sociocat.R;

public class SimpleYesNoDialog {

    /* Context должен быть контекстом страницы (Activity),
    а не приложения (т.е. ApplicationContext).
    Иначе не определяется тема и вываливается исключение. */
    public static void show(
            @NonNull Context context,
            @Nullable Integer titleId,
            @Nullable Integer messageId,
            Callbacks callbacks
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (null != titleId)
            builder.setTitle(titleId);

        if (null != messageId)
            builder.setMessage(messageId);

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
