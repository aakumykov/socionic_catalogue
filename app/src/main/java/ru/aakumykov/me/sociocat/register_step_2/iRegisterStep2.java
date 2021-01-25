package ru.aakumykov.me.sociocat.register_step_2;


import android.content.Intent;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.z_base_view.iBaseView;

public interface iRegisterStep2 {

    enum ViewState {
        INITIAL,
        PROGRESS,
        SUCCESS,
        ERROR,
        FATAL_ERROR
    }

    interface View extends iBaseView {
        void setState(ViewState state, int messageId);
        void setState(ViewState state, int messageId, @Nullable String messageDetails);

        String getPassword();

        void disableForm();
        void enableForm();

        void confirmPageLeave();
    }

    interface Presenter {
        void linkView(View view);
        void unlinkView();

        void storeViewState(ViewState viewState, int messageId, String messageDetails);

        boolean isVirgin();
        void processInputIntent(@Nullable Intent intent);
        void onConfigChanged();

        void onFormIsValid();

        void onCancelRequested();
    }
}
