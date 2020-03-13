package ru.aakumykov.me.sociocat.template_of_page;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.base_view.BaseView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid.CardsGrid_View;
import ru.aakumykov.me.sociocat.template_of_page.models.Item;
import ru.aakumykov.me.sociocat.template_of_page.view_model.Page_ViewModel;
import ru.aakumykov.me.sociocat.template_of_page.view_model.Page_ViewModelFactory;

public class Page_View extends BaseView implements
        iPage.iView,
        Validator.ValidationListener
{
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.nameView) TextView nameView;
    @BindView(R.id.descriptionView) TextView descriptionView;

    @NotEmpty(messageResId = R.string.cannot_be_empty)
    @BindView(R.id.textInput) EditText textInput;
    private iPage.iPresenter presenter;

    private Validator validator;


    // Activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.template_of_page_activity);
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

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.linkView(this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                return presenter.onHomePressed();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }


    // BaseView
    @Override
    public void onUserLogin() {
        presenter.onUserLoggedIn();
    }

    @Override
    public void onUserLogout() {
        presenter.onUserLoggedOut();
    }


    @Override
    public void setState(iPage.ViewState state, int messageId) {
        setState(state, messageId, null);
    }

    @Override
    public void setState(iPage.ViewState state, int messageId, @Nullable String messageDetails) {
        presenter.storeViewState(state, messageId, messageDetails);
    }

    @Override
    public String getText() {
        return textInput.getText().toString();
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

    @Override
    public void goCardsGrid() {
        startActivity(new Intent(this, CardsGrid_View.class));
    }


    // Validator.ValidationListener
    @Override
    public void onValidationSucceeded() {
        presenter.onFormIsValid();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                showToast(message);
            }
        }
    }


    // Нажатия
    @OnClick(R.id.button)
    void onButtonClicked() {
        validator.validate();
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
