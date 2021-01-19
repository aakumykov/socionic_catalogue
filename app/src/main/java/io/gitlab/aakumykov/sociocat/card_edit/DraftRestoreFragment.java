package io.gitlab.aakumykov.sociocat.card_edit;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.gson.Gson;

import io.gitlab.aakumykov.sociocat.R;
import io.gitlab.aakumykov.sociocat.constants.Constants;
import io.gitlab.aakumykov.sociocat.models.Card;
import io.gitlab.aakumykov.sociocat.utils.MVPUtils.MVPUtils;

public class DraftRestoreFragment extends DialogFragment {

    public interface Callbacks {
        void onDraftRestoreConfirmed();
        void onDraftRestoreDeferred();
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

        // TODO: проверить с NULL

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

        }

        Button confirmButton = view.findViewById(R.id.draftConfirmButton);
        Button deferButton = view.findViewById(R.id.draftDeferButton);
        Button discardButton = view.findViewById(R.id.draftDiscardButton);

        // Восстановление
        confirmButton.setOnClickListener(v -> {
            callbacks.onDraftRestoreConfirmed();
            MVPUtils.clearCardDraft(getContext());
            dismiss();
        });

        // "Напомнить позже"
        deferButton.setOnClickListener(v -> {
            callbacks.onDraftRestoreDeferred();
            dismiss();
        });

        // Отказ
        discardButton.setOnClickListener(v -> {
            callbacks.onDraftRestoreCanceled();
            dismiss();
        });

        Dialog dialog = getDialog();
        if (null != dialog) {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
    }
}
