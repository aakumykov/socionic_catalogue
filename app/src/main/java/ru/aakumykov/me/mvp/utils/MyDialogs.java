package ru.aakumykov.me.mvp.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iMyDialogs;

public class MyDialogs {

    public static void cardDeleteDialog(Activity activity, String message, iMyDialogs.Delete callbacks) {

        String title = activity.getString(R.string.DIALOG_delete_card);

        basicDialog(
                activity,
                title,
                message,
                R.string.yes,
                R.string.no,
                R.string.cancel,
                null,
                null,
                callbacks
        ).show();
    }

    public static void commentEditDialog(
            Activity activity,
            String initialText,
            final iMyDialogs.StringInputCallback callbacks
    ) {
        String title = activity.getString(R.string.DIALOG_edit_comment);

        View view = activity.getLayoutInflater().inflate(R.layout.edit_dialog, null);
         EditText editText = view.findViewById(R.id.editText);
         editText.setText(initialText);

        final AlertDialog alertDialog = basicDialog(
                activity,
                title,
                null,
                null,
                null,
                null,
                null,
                view,
                null
        );

        alertDialog.setButton(
                DialogInterface.BUTTON_POSITIVE,
                activity.getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            EditText editText = alertDialog.findViewById(R.id.editText);
                            String text = editText.getText().toString();
                            callbacks.onDialogWithStringYes(text);
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    }
                }
        );

        alertDialog.setButton(
                DialogInterface.BUTTON_NEGATIVE,
                activity.getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }
        );

        alertDialog.show();
    }

    private static AlertDialog basicDialog(
            final Activity activity,
            String title,
            @Nullable String message,
            @Nullable Integer yesButtonId, // Для варианта
            @Nullable Integer noButtonId,  // с одной кнопкой
            @Nullable Integer cancelButtonId,  // с одной кнопкой
            @Nullable Integer iconId,
            final @Nullable View view,
            final iMyDialogs.StandardCallbacks callbacks
    )
    {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity)
                .setTitle(title);

        // Кнопка "Да"
        if (null != yesButtonId) {
            dialogBuilder.setPositiveButton(yesButtonId, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (null != callbacks) {
                        if (null != view) {
                            TextView errorView = view.findViewById(R.id.dialogErrorView);
                            errorView.setText("Неправильно!");
                        }
//                        if (callbacks.onCheckInDialog()) {
//                            callbacks.onYesInDialog();
//                        }
                    }
                }
            });
        }

        // Кнопка "Нет"
        if (null != noButtonId) {
            dialogBuilder.setNegativeButton(noButtonId, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (null != callbacks) {
                        callbacks.onNoInDialog();
                    }
                }
            });
        }

        // Кнопка "Отмена"
        if (null != cancelButtonId) {
            dialogBuilder.setNeutralButton(cancelButtonId, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (null != callbacks) {
                        callbacks.onCancelInDialog();
                    }
                }
            });
        }

        // Реакция на отмену (кнопкой Отмена или только стрелкой?)
        dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d("TAG", "диалог ОТМЕНЁН");
            }
        });

        // Сообщение
        if (null != message)
                dialogBuilder.setMessage(message);

        // Картинка
        if (null != iconId)
            dialogBuilder.setIcon(iconId);

        // Пользовательская разметка
        if (null != view) {
            dialogBuilder.setView(view);
        }

        return dialogBuilder.create();
    }
}
