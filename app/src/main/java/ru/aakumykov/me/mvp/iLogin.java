package ru.aakumykov.me.mvp;

public interface iLogin {

    public interface View {
        void showInfo(String message);
        void hideInfo();

        void showError(String message);
        void hideError();

        String getName();

        void disableLoginButton();
        void enableLoginButton();
    }

    public interface Presenter {
        void linkView(iLogin.View v);
        void unlinkView();
        void loginButtonClicked();
    }

    public interface Model {

    }
}
