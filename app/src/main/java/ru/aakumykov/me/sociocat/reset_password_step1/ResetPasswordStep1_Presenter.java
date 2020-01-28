package ru.aakumykov.me.sociocat.reset_password_step1;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.interfaces.iMyDialogs;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.utils.MyDialogs;


public class ResetPasswordStep1_Presenter implements iResetPasswordStep1.Presenter {

    private static final String TAG = "ResetPasswordStep1_Presenter";
    private iResetPasswordStep1.View view;
    private boolean isVirgin = true;
    private iResetPasswordStep1.ViewState currentViewSate;
    private int currentMessageId;
    private String currentMessageDetails;


    @Override
    public void linkView(iResetPasswordStep1.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = new ResetPasswordStep1_ViewStub();
    }

    @Override
    public boolean isVirgin() {
        return isVirgin;
    }

    @Override
    public void onFirstOpen() {
        isVirgin = false;
        view.setState(iResetPasswordStep1.ViewState.INITIAL, -1);
    }

    @Override
    public void storeViewState(iResetPasswordStep1.ViewState state, int messageId, String messageDetails) {
        currentViewSate = state;
        currentMessageId = messageId;
        currentMessageDetails = messageDetails;
    }

    @Override
    public void onFormIsValid() {

        String email = view.getEmail();

        view.setState(iResetPasswordStep1.ViewState.CHECKING_EMAIL, R.string.RESET_PASSWORD_checking_emai);

        UsersSingleton.getInstance().checkEmailExists(email, new iUsersSingleton.CheckExistanceCallbacks() {
            @Override
            public void onCheckComplete() {
                view.setState(iResetPasswordStep1.ViewState.SUCCESS, -1);
            }

            @Override
            public void onExists() {
                sendResetPasswordEmail();
            }

            @Override
            public void onNotExists() {
                view.setState(iResetPasswordStep1.ViewState.EMAIL_ERROR, R.string.RESET_EMAIL_error_email_not_found);
            }

            @Override
            public void onCheckFail(String errorMsg) {
                view.setState(iResetPasswordStep1.ViewState.COMMON_ERROR, R.string.RESET_PASSWORD_error_checking_email);
            }
        });

    }

    @Override
    public void onConfigChanged() {
        view.setState(currentViewSate, currentMessageId, currentMessageDetails);
    }


    // Внутренние методы
    private void sendResetPasswordEmail() {

        String email = view.getEmail();

        view.setState(iResetPasswordStep1.ViewState.PROGRESS, R.string.RESET_PASSWORD_sending_email);

        AuthSingleton.resetPasswordEmail(email, new iAuthSingleton.ResetPasswordCallbacks() {
            @Override
            public void onEmailSendSuccess() {
                view.setState(iResetPasswordStep1.ViewState.SUCCESS, -1);
            }

            @Override
            public void onEmailSendFail(String errorMsg) {
                view.setState(iResetPasswordStep1.ViewState.COMMON_ERROR, R.string.RESET_PASSWORD_error_sending_email);
            }
        });
    }
}
