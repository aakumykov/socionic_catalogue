package ru.aakumykov.me.mvp.register;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.models.User;

public interface iRegister {

    interface View extends iBaseView {
        void disableForm();
        void enableForm();
        void goUserEditPage(User user);
    }

    interface Presenter {
        void regUserWithEmail(String email, String password);

        // TODO: вынести в общий интерфейс
        void linkView(iRegister.View view);
        void unlinkView();
    }
}
