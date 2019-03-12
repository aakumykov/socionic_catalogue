package ru.aakumykov.me.sociocat.dynamic_link_processor;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;

interface iDLP {

    interface View extends iBaseView {
        void showHomeButton();
        void goHomePage();
    }

    interface Presenter {
        void processDynamicLink(Activity activity, @Nullable Intent intent);

        void linkView(View view);
        void unlinkView();
    }
}
