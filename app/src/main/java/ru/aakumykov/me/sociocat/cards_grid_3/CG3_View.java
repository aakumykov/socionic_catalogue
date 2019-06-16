package ru.aakumykov.me.sociocat.cards_grid_3;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;

public class CG3_View extends BaseView implements
        iCG3.iPageView
{
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    private StaggeredGridLayoutManager layoutManager;
    private CG3_Adapter adapter;
    private List<Card> cardsList = new ArrayList<>();

    private iCG3.iPresenter iPresenter;
    private boolean firstRun = true;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cg3_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.CARDS_GRID_page_title);

        iPresenter = new CG3_Presenter();
        adapter = new CG3_Adapter();
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        iPresenter.linkView(this, adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        iPresenter.linkView(this, adapter);

        if (firstRun) {
            firstRun = false;
            iPresenter.onWorkBegins();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        iPresenter.unlinkView();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }


    // iPage
    @Override
    public <T> void setTitle(T title) {
//        String titleString = "";
//        if (title instanceof Integer) {
//            titleString = getResources().getString((Integer)title);
//        }
//        else if (title instanceof String) {
//            titleString = (String) title;
//        }
//        setPageTitle(titleString);
    }
}
