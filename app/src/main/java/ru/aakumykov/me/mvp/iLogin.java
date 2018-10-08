package ru.aakumykov.me.mvp;

public interface iLogin {

    interface View {
        void showInfo(String message);
        void showInfo(int messageId);
        void hideInfo();

        void showError(String message);
        void showError(int messageId);
        void hideError();

        void showProgressBar();
        void hideProgressBar();

        String getEmail();
        String getPassword();

        void disableLoginForm();
        void enableLoginForm();
    }

    interface Presenter {
        void linkView(iLogin.View v);
        void unlinkView();
        void loginButtonClicked();
        void logoutButtonClicked();
    }

    interface Model {

    }
}
