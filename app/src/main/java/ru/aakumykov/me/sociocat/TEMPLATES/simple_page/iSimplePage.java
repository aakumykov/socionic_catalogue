package ru.aakumykov.me.sociocat.TEMPLATES.simple_page;

import android.content.Intent;
import android.support.annotation.Nullable;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;

interface iSimplePage {

    interface View extends iBaseView {
        void showNeedsConfirmationMessage();
        void showConfirmationSuccessMessage();
        void showConfirmationErrorMessage();
    }

    interface Presenter {
        void processInputIntent(@Nullable Intent intent);

        void linkView(View view);
        void unlinkView();
    }
}
