package ru.aakumykov.me.mvp.users_list;

import java.util.List;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.models.User;

public interface iUsersList {

    interface View extends iBaseView {
        void showProgressBar();
        void hideProgressBar();
        void displayList(List<User> list);
    }

    interface Presenter {
        void linkView(iUsersList.View view);
        void unlinkView();

        void loadList();
    }
}
