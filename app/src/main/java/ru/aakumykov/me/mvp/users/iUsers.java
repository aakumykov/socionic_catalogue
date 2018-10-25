package ru.aakumykov.me.mvp.users;

import java.util.List;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.models.User;

public interface iUsers {

    interface ListView  extends iBaseView {
        void showPageProgressBar();
        void hidePageProgressBar();
        void hideSwipeProgressBar();
        void displayList(List<User> list);
        void goUserPage(String userId);
    }

    interface ShowView  extends iBaseView {
        void displayUser(User user);
    }

    interface EditView  extends iBaseView {

    }

    interface Presenter {
        void linkView(ShowView showView);
        void linkView(ListView listView);
        void unlinkView();

        void loadList();
        void listItemClicked(String key);

        void loadUser(String userId) throws Exception;
    }

}
