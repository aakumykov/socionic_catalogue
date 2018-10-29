package ru.aakumykov.me.mvp.tags;

import java.util.List;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iTagsSingleton;
import ru.aakumykov.me.mvp.models.Tag;


public interface iTags {

    interface View {}


    interface ListView extends iBaseView, View {
        void displayTags(List<Tag> list);
    }

    interface ShowView extends iBaseView, View {

    }

    interface EditView extends iBaseView, View {

    }


    interface Presenter {
        void linkView(View view) throws IllegalArgumentException;
        void unlinkView();

        void onPageCreated();
    }
}
