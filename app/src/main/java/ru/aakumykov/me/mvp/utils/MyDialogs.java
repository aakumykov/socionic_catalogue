package ru.aakumykov.me.mvp.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iMyDialogs;

public class MyDialogs {

    // Диалог удаления карточки
    public static void cardDeleteDialog(Activity activity, String message, iMyDialogs.Delete callbacks) {

        String title = activity.getString(R.string.DIALOG_delete_card);

        basicDialog(
                activity,
                title,
                message,
                R.string.yes,
                R.string.no,
                null,
                null,
                null,
                callbacks
        ).show();
    }

    // TODO: сделать единый диалог ввода строки
    // Диалог добавления код видео
    public static void addVideoDialog(Activity activity, final iMyDialogs.StringInputCallback callbacks) {
        String title = activity.getString(R.string.CARD_EDIT_video_code);

        final View view = activity.getLayoutInflater().inflate(R.layout.dialog_text_input, null);
        final EditText editText = view.findViewById(R.id.editText);
        final TextView dialogErrorView = view.findViewById(R.id.dialogErrorView);

        final AlertDialog alertDialog = basicDialog(
                activity,
                title,
                null,
                R.string.yes,
                R.string.no,
                null,
                null,
                view,
                null
        );

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button yesButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);

                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newText = editText.getText().toString();
                        if (TextUtils.isEmpty(newText)) {
                            dialogErrorView.setText(view.getResources().getString(R.string.COMMENT_cannot_be_empty));
                        } else {
                            dialog.dismiss();
                            callbacks.onDialogWithStringYes(newText);
                        }
                    }
                });
            }
        });

        alertDialog.show();
    }

    // Диалог изменения комментария
    public static void commentEditDialog(
            Activity activity,
            String initialText,
            final iMyDialogs.StringInputCallback callbacks
    ) {
        String title = activity.getString(R.string.DIALOG_edit_comment);

        final View view = activity.getLayoutInflater().inflate(R.layout.dialog_text_input, null);
        final EditText editText = view.findViewById(R.id.editText);
        final TextView dialogErrorView = view.findViewById(R.id.dialogErrorView);

        editText.setText(initialText);

        final AlertDialog alertDialog = basicDialog(
                activity,
                title,
                null,
                R.string.yes,
                R.string.no,
                null,
                null,
                view,
                null
        );

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                Button yesButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newText = editText.getText().toString();
                        if (TextUtils.isEmpty(newText)) {
                            dialogErrorView.setText(view.getResources().getString(R.string.COMMENT_cannot_be_empty));
                        } else {
                            dialog.dismiss();
                            callbacks.onDialogWithStringYes(newText);
                        }
                    }
                });

            }
        });

        alertDialog.show();
    }


    // Диалог удаления комментария
    public static void commentDeleteDialog(Activity activity, String message, iMyDialogs.Delete callbacks) {

        String title = activity.getString(R.string.DIALOG_delete_comment);

        basicDialog(
                activity,
                title,
                message,
                R.string.yes,
                R.string.no,
                null,
                null,
                null,
                callbacks
        ).show();
    }


    // Диалог перехода на страницу
    public static void goToPageDialog(Activity activity, String message, iMyDialogs.StandardCallbacks callbacks) {

        basicDialog(
                activity,
                null,
                message,
                R.string.yes,
                R.string.no,
                null,
                null,
                null,
                callbacks
        ).show();

    }


    // Внутренние методы
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
                        if (callbacks.onCheckInDialog()) {
                            callbacks.onYesInDialog();
                        }
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
