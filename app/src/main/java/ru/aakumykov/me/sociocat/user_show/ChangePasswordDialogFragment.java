package ru.aakumykov.me.sociocat.user_show;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class ChangePasswordDialogFragment extends DialogFragment implements iChangePasswordDialog {

    public interface ChangePasswordDialogListener {
        void onChangePasswordSaveClicked();
        void onChangePasswordCancelClicked();
        void onFragmentAttached(iChangePasswordDialog changePasswordDialog);
        void onFragmentDetached();
    }

    private View layout;
    private EditText currentPasswordInput;
    private EditText newPasswordInput;
    private EditText newPasswordConfirmationInput;
    private ProgressBar progressBar;

    private ChangePasswordDialogListener listener;


    // DialogFragment
    @NonNull @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.USER_CHANGE_PASSWORD_page_title);

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        layout = layoutInflater.inflate(R.layout.user_change_password_layout, null);
        builder.setView(layout);

        configureLayout();

        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //listener.onChangePasswordSaveClicked();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //listener.onChangePasswordCancelClicked();
            }
        });

        Dialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                AlertDialog alertDialog = (AlertDialog) dialog;

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listener.onChangePasswordSaveClicked();
                            }
                        });

                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listener.onChangePasswordCancelClicked();
                            }
                        });

            }
        });

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (ChangePasswordDialogListener) context;
        listener.onFragmentAttached(this);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        listener.onChangePasswordCancelClicked();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener.onFragmentDetached();
        this.listener = null;
    }


    // iChangePasswordDialog
    @Override
    public void disableForm() {
        MyUtils.show(progressBar);
        MyUtils.disable(currentPasswordInput);
        MyUtils.disable(newPasswordInput);
        MyUtils.disable(newPasswordConfirmationInput);
    }

    @Override
    public void enableForm() {
        MyUtils.hide(progressBar, true);
        MyUtils.enable(currentPasswordInput);
        MyUtils.enable(newPasswordInput);
        MyUtils.enable(newPasswordConfirmationInput);
    }


    // Внутренние
    private void configureLayout() {
        progressBar = layout.findViewById(R.id.progressBar);
        currentPasswordInput = layout.findViewById(R.id.currentPasswordInput);
        newPasswordInput = layout.findViewById(R.id.newPasswordInput);
        newPasswordConfirmationInput= layout.findViewById(R.id.newPasswordConfirmationInput);
    }
}
