package ru.aakumykov.me.sociocat.TEMPLATES.email_input;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;

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
