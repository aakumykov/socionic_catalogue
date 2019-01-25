package ru.aakumykov.me.mvp.TEMPLATES.email_input;

import ru.aakumykov.me.mvp.iBaseView;

public interface iEmailInput {

    interface View extends iBaseView {
        String getEmail();
        void disableForm();
        void enableForm();
        void showEmailError(String msgId);
    }

    interface Presenter {
//        void processInputIntent(@Nullable Intent intent);
        void sendRegistrationEmail(String email);

        void linkView(View view);
        void unlinkView();
    }
}
