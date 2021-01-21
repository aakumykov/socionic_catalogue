package io.gitlab.aakumykov.sociocat.d_login2;

import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.R;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.BasicMVVMPage_View;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.BasicMVVMPage_ViewModel;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_event.BasicPageEvent;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_state.BasicPageState;

public class Login2_View extends BasicMVVMPage_View {

    @Override
    protected void setView() {
        setContentView(R.layout.login2_activity);
    }

    @Override
    protected void configureView() {

    }

    @Override
    protected BasicMVVMPage_ViewModel createPageViewModel() {
        return null;
    }

    @Override
    protected void onNewPageState(@NonNull BasicPageState pageState) {

    }

    @Override
    protected void onNewPageEvent(@NonNull BasicPageEvent pageEvent) {

    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }
}
