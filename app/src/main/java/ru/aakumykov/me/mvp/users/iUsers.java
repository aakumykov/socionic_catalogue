package ru.aakumykov.me.mvp.users;

import java.util.List;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.models.User;

public interface iUsers {

    interface View {} // Это объединение нужно для работы linkView / unlinkView

    interface ListView  extends iBaseView, View {
        void displayList(List<User> list);
        void goUserPage(String userId);
    }

    interface ShowView  extends iBaseView, View {
        void displayUser(User user);
    }

    interface EditView  extends iBaseView, View {

    }

    interface Presenter {
        void linkView(View view);
        void unlinkView();

        void loadList();
        void listItemClicked(String key);

        void loadUser(String userId) throws Exception;
    }

}
