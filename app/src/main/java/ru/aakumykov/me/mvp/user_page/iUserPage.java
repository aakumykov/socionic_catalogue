package ru.aakumykov.me.mvp.user_page;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.models.User;


public interface iUserPage {

    interface View extends iBaseView {
        void displayUser(User user);
    }

    // TODO: вынести (un)linkView в интерфейс

    interface Presenter {
        void linkView(iUserPage.View view);
        void unlinkView();
        void userIdRecieved(String userId) throws Exception;
    }
}
