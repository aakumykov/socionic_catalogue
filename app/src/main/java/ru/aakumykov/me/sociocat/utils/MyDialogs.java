package ru.aakumykov.me.sociocat.utils;

import android.app.Activity;
import android.content.DialogInterface;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.interfaces.iMyDialogs;
import ru.aakumykov.me.sociocat.utils.MVPUtils.MVPUtils;

public class MyDialogs {

    public static final String TAG = "MyDialogs";

    // Возобновление редактирования карточки
    public static void resumeCardEditDialog(Activity activity, iMyDialogs.StandardCallbacks callbacks) {
        String title = activity.getString(R.string.CARD_EDIT_resume_dialog_title);
        String message = activity.getString(R.string.CARD_EDIT_resume_dialog_message);

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

    // Диалог для проверок
    public static void dummyDialog(Activity activity, String title, String message) {
        basicDialog(
                activity,
                title,
                message,
                R.string.yes,
                null,
                null,
                null,
                null,
                null
        ).show();
    }

    // Отмена редактирования
    public static void cancelEditDialog(Activity activity, int titleId, int messageId, iMyDialogs.StandardCallbacks callbacks) {

        String title = activity.getString(titleId);
        String message = activity.getString(messageId);

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

    // Удаление карточки
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

    // Необходимость входа
    public static void loginRequiredDialog(Activity activity, iMyDialogs.StandardCallbacks callbacks) {
        loginRequiredDialog(activity, R.string.DIALOG_login_required, null, callbacks);
    }

    public static <T> void loginRequiredDialog(Activity activity, T titleId, T messageId, iMyDialogs.StandardCallbacks callbacks) {

        String title = null;
        if (titleId instanceof Integer) {
            title =activity.getString((Integer)titleId);
        } else if (titleId instanceof String) {
            title = (String)titleId;
        } else {
            Log.e(TAG, "Unknown type of 'titleId' argument: "+titleId);
        }

        String message = null;
        if (messageId instanceof Integer) {
            message =activity.getString((Integer)messageId);
        } else if (messageId instanceof String) {
            message = (String)messageId;
        } else {
            Log.e(TAG, "Unknown type of 'messageId' argument: "+messageId);
        }

        AlertDialog alertDialog = basicDialog(
                activity,
                title,
                message,
                R.string.DIALOG_do_login,
                R.string.DIALOG_do_not_login,
                null,
                R.drawable.ic_login, // TODO: не видать
                null,
                callbacks
        );

        alertDialog.show();
    }

    // Необходимость установки имени пользователя
    public static void userNameRequiredDialog(Activity activity, iMyDialogs.StandardCallbacks callbacks) {
        String title = activity.getResources().getString(R.string.DIALOG_user_name_required_title);
        String message = activity.getResources().getString(R.string.DIALOG_user_name_required_message);
        AlertDialog alertDialog = basicDialog(
                activity,
                title,
                message,
                R.string.yes,
                R.string.no,
                null,
                R.drawable.ic_person,
                null,
                callbacks
        );
        alertDialog.show();
    }

    public static <T> void stringInputDialog(
            Activity activity,
            int titleId,
            T message,
            T hint,
            final iMyDialogs.StringInputCallback callback
    )
    {
        String titleString = activity.getString(titleId);

        String messageString = null;
        if (null != message)
            messageString = (message instanceof Integer) ? activity.getString((Integer)message) : String.valueOf(message);

        String hintString = null;
        if (null != hint)
            hintString = (hint instanceof Integer) ? activity.getString((Integer)hint) : String.valueOf(hint);

        final View dialogLayout = activity.getLayoutInflater().inflate(R.layout.dialog_text_input, null);
        final EditText editText = dialogLayout.findViewById(R.id.editText);
        if (null != hintString)
            editText.setHint(hintString);
        final TextView dialogErrorView = dialogLayout.findViewById(R.id.dialogErrorView);

        AlertDialog alertDialog = basicDialog(
                activity,
                titleString,
                messageString,
                R.string.yes,
                null,
                R.string.cancel,
                null,
                dialogLayout,
                null
        );

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                String clipboardString = callback.onPrepareText();
                editText.setText(clipboardString);

                Button yesButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);

                yesButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String inputString = editText.getText().toString();
                        String errorMsg = callback.onYesClicked(inputString);
                        if (null == errorMsg) {
                            dialog.dismiss();
                            callback.onSuccess(inputString);
                        } else {
                            showErrorMessage(dialogLayout, errorMsg);
                        }
                    }

                });
            }
        });

        alertDialog.show();
    }

    // TODO: сделать единый диалог ввода строки
    // Добавление кода видео
    public static void addYoutubeVideoDialog(Activity activity, final iMyDialogs.StringInputCallback callbacks) {
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
                            showErrorMessage(view, R.string.DIALOG_MESSAGE_cannot_be_empty);
                            return;
                        }

                        if (!MVPUtils.isYoutubeLink(newText)) {
                            showErrorMessage(view, R.string.DIALOG_MESSAGE_incorrect_youtube_video_link);
                            return;
                        }

                        else {
                            dialog.dismiss();
                            callbacks.onYesClicked(newText);
                        }
                    }
                });
            }
        });

        alertDialog.show();
    }

    // Правка комментария
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
                            dialogErrorView.setText(view.getResources().getString(R.string.DIALOG_MESSAGE_cannot_be_empty));
                        } else {
                            dialog.dismiss();
                            callbacks.onYesClicked(newText);
                        }
                    }
                });

            }
        });

        alertDialog.show();
    }

    // Удаление комментария
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

    // Переход на страницу
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

    // Вопрос о недобавленной метке
    public static void forgottenTagDialog(Activity activity, String message, iMyDialogs.StandardCallbacks callbacks) {
        String title = activity.getString(R.string.CARD_EDIT_forgotten_tag_dialog_title);

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

    // Сообщение о завершении регистрации
    public static void registrationCompleteDialog(Activity activity, final iMyDialogs.StandardCallbacks callbacks) {
        String title = activity.getResources().getString(R.string.REGISTER1_complete_dialog_title);
        String message = activity.getResources().getString(R.string.REGISTER1_complete_dialog_message);
        AlertDialog alertDialog = basicDialog(
                activity,
                title,
                message,
                R.string.REGISTER1_ok,
                null,
                null,
                null,
                null,
                callbacks
        );

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                callbacks.onCancelInDialog();
            }
        });

        alertDialog.show();
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

//        dialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                Log.d("DISMISS","ИСЧЕЗНОВЕНИЕ");
//            }
//        });

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

    private static <T> void showErrorMessage(View view, T msg) {

        TextView dialogErrorView = view.findViewById(R.id.dialogErrorView);

        String message = MyUtils.object2string(view.getContext(), msg);

        if (null != dialogErrorView) {
            dialogErrorView.setText(message);
        }
    }
}
