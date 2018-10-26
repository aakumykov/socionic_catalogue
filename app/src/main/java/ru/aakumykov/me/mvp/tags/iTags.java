package ru.aakumykov.me.mvp.tags;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iTagsSingleton;


public interface iTags {

    interface View {}


    interface ListView extends iBaseView, View {

    }

    interface ShowView extends iBaseView, View {

    }

    interface EditView extends iBaseView, View {

    }


    interface Presenter {
        void linkView(View view) throws IllegalArgumentException;
        void unlinkView();

        void listPageCreated(iTagsSingleton.ListCallbacks callbacks);
    }
}
