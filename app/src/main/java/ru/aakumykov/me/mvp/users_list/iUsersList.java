package ru.aakumykov.me.mvp.users_list;

import ru.aakumykov.me.mvp.interfaces.iUsers;

public interface iUsersList {

    interface View {
        void showProgressBar();
        void hideProgressBar();
    }

    interface Presenter {
        void linkView(iUsersList.View view);
        void unlinkView();

        void loadList(iUsers.ListCallbacks callbacks);
    }
}
