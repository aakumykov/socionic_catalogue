package ru.aakumykov.me.sociocat.page_template;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.page_template.models.Item;
import ru.aakumykov.me.sociocat.page_template.view_model.Page_ViewModel;
import ru.aakumykov.me.sociocat.page_template.view_model.Page_ViewModelFactory;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class Page_View extends BaseView implements iPage.iView {

    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.nameView) TextView nameView;
    @BindView(R.id.descriptionView) TextView descriptionView;

    private iPage.iPresenter presenter;

    // Activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_template_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.PAGE_TEMPLATE_page_title);
        activateUpButton();

        Page_ViewModel viewModel = new ViewModelProvider(this, new Page_ViewModelFactory())
                .get(Page_ViewModel.class);

        if (viewModel.hasPresenter()) {
            this.presenter = viewModel.getPresenter();
        } else {
            this.presenter = new Page_Presenter();
            viewModel.storePresenter(this.presenter);
        }

        configureSwipeRefresh();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

        if (presenter.hasItem())
            presenter.onConfigChanged();
        else
            presenter.onFirstOpen(getIntent());
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    // BaseView
    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    // iPage.iView
    @Override
    public void displayItem(Item item) {
        nameView.setText(item.getName());
        descriptionView.setText(item.getDescription());
    }

    @Override
    public void hideRefreshThrobber() {
        swipeRefreshLayout.setRefreshing(false);
    }

    // Нажатия
    @OnClick(R.id.button)
    void onButtonClicked() {
        presenter.onButtonClicked();
    }

    // Внутренние методы
    private void configureSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                presenter.onRefreshRequested();
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.blue_swipe, R.color.green_swipe, R.color.orange_swipe, R.color.red_swipe);
    }
}
