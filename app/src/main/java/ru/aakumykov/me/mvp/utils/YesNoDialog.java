package ru.aakumykov.me.mvp.utils;

import android.content.Context;
import android.content.DialogInterface;
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

    public YesNoDialog (
            final Context context,
            int titleId,
            int messageId,
            final iDialogCallbacks.onCheck checkCallback,
            final iDialogCallbacks.onYes yesCallback,
            final iDialogCallbacks.onNo noCallback
            ) {

        this.context = context;
        this.title = context.getResources().getString(titleId);
        this.message = context.getResources().getString(messageId);
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