package ru.aakumykov.me.sociocat.card_edit;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;

public class DraftRestoreFragment extends DialogFragment {

    public interface Callbacks {
        void onDraftRestoreConfirmed();
        void onDraftRestoreCanceled();
    }

    private Callbacks callbacks;


    public DraftRestoreFragment(){}

    // TODO: опасно?
    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    public static DraftRestoreFragment getInstance(Card card) {
        DraftRestoreFragment fragment = new DraftRestoreFragment();

        Bundle arguments = new Bundle();
        arguments.putString(Constants.CARD_DRAFT, new Gson().toJson(card));

        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.draft_restore_dialog, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        if (null != arguments) {

            TextView titleView = view.findViewById(R.id.titleView);
            TextView quoteView = view.findViewById(R.id.quoteView);
            TextView descriptionView = view.findViewById(R.id.descriptionView);

            Card cardDraft = new Gson().fromJson(arguments.getString(Constants.CARD_DRAFT), Card.class);
            if (null != cardDraft) {
                titleView.setText(cardDraft.getTitle());
                quoteView.setText(cardDraft.getQuote());
                descriptionView.setText(cardDraft.getDescription());
            }


            Button confirmButton = view.findViewById(R.id.drafConfirmButton);
            Button discardButton = view.findViewById(R.id.draftDiscardButton);

            //Callbacks callbacks = (Callbacks)getActivity();

            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callbacks.onDraftRestoreConfirmed();
                    dismiss();
                }
            });

            discardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callbacks.onDraftRestoreCanceled();
                    dismiss();
                }
            });

            Dialog dialog = getDialog();
            if (null != dialog) {
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
            }
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        callbacks.onDraftRestoreCanceled();
    }
}
