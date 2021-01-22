package io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.gitlab.aakumykov.sociocat.R;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.live_data_observers.PageEventObserver;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.live_data_observers.PageStateObserver;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_event.BasicPageEvent;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_event.ToastPageEvent;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_state.BasicPageState;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_state.ErrorPageState;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_state.NeutralPageState;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_state.ProgressPageState;
import io.gitlab.aakumykov.sociocat.z_base_view.BaseView;

public abstract class BasicMVVMPage_View extends BaseView {

    @BindView(R.id.messageView)
    TextView messageView;

    private static final String TAG = BasicMVVMPage_View.class.getSimpleName();

    protected BasicMVVMPage_ViewModel mPageViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setView();

        ButterKnife.bind(this);

        configureView();

        configurePageViewModel();

        configureLifecycleObserver();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mPageViewModel.getPageStateLiveData().observe(
                this,
                new PageStateObserver(this::onNewPageState)
        );

        mPageViewModel.getPageEventLiveData().observe(
                this,
                new PageEventObserver(new PageEventObserver.PageEventChangeCallback() {
                    @Override
                    public void onPageEventOccurred(BasicPageEvent pageEvent) {
                        onNewPageEvent(pageEvent);
                    }
                })
        );
    }


    protected abstract void setView();
    protected abstract void configureView();
    protected abstract BasicMVVMPage_ViewModel createPageViewModel();
    protected abstract void onNewPageState(@NonNull BasicPageState pageState);
    protected abstract void onNewPageEvent(@NonNull BasicPageEvent pageEvent);


    protected ViewModelProvider getViewModelProvider(ViewModelStoreOwner viewModelStoreOwner) {
        return new ViewModelProvider(viewModelStoreOwner, new ViewModelProvider.NewInstanceFactory());
    }


    // Обработка состояния страницы
    protected void processPageState(@NonNull BasicPageState pageState) {

        if (pageState instanceof NeutralPageState) {
            setNeutralPageState();
        }
        else if (pageState instanceof ProgressPageState) {
            setProgressPageState((ProgressPageState) pageState);
        }
        else if (pageState instanceof ErrorPageState) {
            setErrorPageState((ErrorPageState) pageState);
        }
        else {
            throw new RuntimeException("Unknown page state: "+pageState);
        }
    }

    protected void setNeutralPageState() {
        hideMessage();
    }

    protected void setProgressPageState(@NonNull ProgressPageState progressPageState) {
        hideMessage();
        showProgressMessage(progressPageState.getMessage());
    }

    protected void setErrorPageState(@NonNull ErrorPageState errorPageState) {
        hideMessage();
        showErrorMsg(
                errorPageState.getUserMessageId(),
                errorPageState.getDebugMessage()
        );
        Log.e(errorPageState.getTag(), errorPageState.getDebugMessage());
    }


    // Обработка событий страницы
    protected void processPageEvent(@NonNull BasicPageEvent pageEvent) {
        if (pageEvent instanceof ToastPageEvent) {
            processToastPageEvent((ToastPageEvent) pageEvent);
        }
        else {
            throw new RuntimeException("Неизвестный тип pageEvent: " + pageEvent);
        }
    }


    // Внутренние методы
    private void configurePageViewModel() {
        mPageViewModel = createPageViewModel();
    }

    private void configureLifecycleObserver() {
        getLifecycle().addObserver(mPageViewModel);
    }


    private void processToastPageEvent(@NonNull ToastPageEvent toastPageEvent) {
        showToast(toastPageEvent.getMessage(this));
    }
}
