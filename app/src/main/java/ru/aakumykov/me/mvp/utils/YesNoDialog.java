package ru.aakumykov.me.mvp.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iDialogCallbacks;


public class YesNoDialog {

    private final static String TAG = "YesNoDialog";

    private AlertDialog alertDialog;

    private Context context;
    private String title;
    private String message;
    private iDialogCallbacks.onCheck checkCallback;
    private iDialogCallbacks.onYes yesCallback;
    private iDialogCallbacks.onNo noCallback;


    public <T> YesNoDialog (
            final Context context,
            T title,
            @Nullable T msg,
            final iDialogCallbacks.onCheck checkCallback,
            final iDialogCallbacks.onYes yesCallback,
            @Nullable final iDialogCallbacks.onNo noCallback
            ) {

        this.context = context;

        // Хотел перенести это во внутренний метод, да не смог
        this.title = context.getResources().getString(R.string.DIALOG_card_deletion);
        if (title instanceof String) this.title = (String) title;
        else if (title instanceof Integer) this.title = context.getResources().getString((Integer)title);

        // Хотел перенести это во внутренний метод, да не смог
        if (null != msg) {
            this.message = context.getResources().getString(R.string.DIALOG_really_delete_card);
            if (msg instanceof String) this.message = (String) msg;
            else if (msg instanceof Integer)
                this.message = context.getResources().getString((Integer) msg);
        }

        this.checkCallback = checkCallback;
        this.yesCallback = yesCallback;
        this.noCallback = noCallback;

        this.create();
    }

    private void create() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkCallback.doCheck()) {
                            yesCallback.yesAction();
                        }
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (null != noCallback) noCallback.noAction();
                    }
                });

        alertDialog = dialogBuilder.create();
    }

    public void show() {
        alertDialog.show();
    }
}
