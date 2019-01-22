package ru.aakumykov.me.mvp.dynamic_link_processor;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;

import ru.aakumykov.me.mvp.iBaseView;

interface iDLP {

    interface View extends iBaseView {
        void showHomeButton();
    }

    interface Presenter {
        void processDynamicLink(Activity activity, @Nullable Intent intent);

        void linkView(View view);
        void unlinkView();
    }
}
