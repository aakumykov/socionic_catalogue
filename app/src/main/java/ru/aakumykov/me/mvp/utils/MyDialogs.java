package ru.aakumykov.me.mvp.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iMyDialogs;

public class MyDialogs {

    public static void cardDeleteDialog(Activity activity, String message, iMyDialogs.Delete callbacks) {

        String title = activity.getString(R.string.DIALOG_delete_card);

        basicDialog(
                activity,
                title,
                message,
                null,
                R.string.yes,
                R.string.no,
                callbacks
        ).show();
    }

    public static void commentEditDialog(
            Activity activity,
            String initialText,
            final iMyDialogs.StringInputCallback callbacks
    ) {
        String title = activity.getString(R.string.DIALOG_edit_comment);

        final AlertDialog alertDialog = basicDialog(
                activity,
                title,
                null,
                null,
                null,
                null,
                null
        );

        View view = activity.getLayoutInflater().inflate(R.layout.edit_dialog, null);
        EditText editText = view.findViewById(R.id.editText);
        editText.setText(initialText);
        alertDialog.setView(view);

        alertDialog.setButton(
                DialogInterface.BUTTON_POSITIVE,
                activity.getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = alertDialog.findViewById(R.id.editText);
                        String text = editText.getText().toString();
                        callbacks.onDialogWithStringYes(text);
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
            @Nullable Integer iconId,
//            @Nullable Integer layoutId,
            @Nullable Integer yesButtonId, // Для варианта
            @Nullable Integer noButtonId,  // с одной кнопкой
            final iMyDialogs.StandardCallbacks callbacks
    )
    {
        final View dialogView;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity)
                .setTitle(title);

        dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d("TAG", "диалог ОТМЕНЁН");
            }
        });

        if (null != message)
                dialogBuilder.setMessage(message);

        if (null != iconId)
            dialogBuilder.setIcon(iconId);

//        if (null != layoutId) {
//            dialogView = activity.getLayoutInflater().inflate(layoutId, null);
//            dialogBuilder.setView(dialogView);
//        }

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

        return dialogBuilder.create();
    }
}
