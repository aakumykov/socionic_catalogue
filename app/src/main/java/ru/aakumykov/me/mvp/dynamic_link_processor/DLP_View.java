package ru.aakumykov.me.mvp.dynamic_link_processor;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.cards_grid.CardsGrid_View;
import ru.aakumykov.me.mvp.utils.MyDialogs;
import ru.aakumykov.me.mvp.utils.MyUtils;

public class DLP_View extends BaseView implements
        iDLP.View
{
    @BindView(R.id.homeButton) Button homeButton;
    private iDLP.Presenter presenter;
    private boolean firstRun = true;

    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dlp_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.DLP_page_title);

        presenter = new DLP_Presenter();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);
        if (firstRun) {
            firstRun = false;
            presenter.processDynamicLink(this, getIntent());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    // Интерфейсные методы
    @Override public void showHomeButton() {
        MyUtils.show(homeButton);
    }

    // Нажатия
    @OnClick(R.id.homeButton)
    void homeButtonClicked() {
        Intent intent = new Intent(this, CardsGrid_View.class);
        startActivity(intent);
//        goToPage(CardsGrid_View.class, this);
//        MyUtils.openPage(CardsGrid_View.class, this);
    }
}
