package ru.aakumykov.me.mvp.login;

public interface iLogin {

    interface View {
        void showInfo(int messageId);
        void hideInfo();

        void showWarning(int messageId);
        void hideWarning();

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
//        iLogin.TemplateModel getInstance();
        void login(String email, String password);
        void logout();
//        void changePassword(String email, String oldPassword, String newPassword);
    }


    interface LoginCallbacks {
        void onAuthSuccess();
        void onAuthFail();
        void onAuthCancel();

        void onLogoutSuccess();
        void onLogoutFail();
    }
}
