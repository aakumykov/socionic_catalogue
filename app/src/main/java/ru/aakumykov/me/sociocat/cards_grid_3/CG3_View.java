package ru.aakumykov.me.sociocat.cards_grid_3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.cards_grid_3.items.GridItem_Card;
import ru.aakumykov.me.sociocat.cards_grid_3.items.iGridItem;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CG3_View extends BaseView implements
        iCG3.iPageView
{
    private static final String TAG = "CG3_View";
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    private CG3_Adapter adapter;
    private iCG3.iPresenter presenter;
    private boolean firstRun = true;
    private int positionInWork = -1;

    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cg3_activity_constraint);
        ButterKnife.bind(this);

        setPageTitle(R.string.CARDS_GRID_page_title);

        presenter = new CG3_Presenter();
        adapter = new CG3_Adapter();

        int colsNum = MyUtils.isPortraitOrientation(this) ?
                Config.CARDS_GRID_COLUMNS_COUNT_PORTRAIT : Config.CARDS_GRID_COLUMNS_COUNT_LANDSCAPE;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(colsNum, StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bindComponents();

        switch (requestCode) {
            case Constants.CODE_EDIT_CARD:
                processCardEditResult(resultCode, data);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        bindComponents();

        if (firstRun) {
            firstRun = false;
            presenter.onWorkBegins();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindComponents();

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

    @Override
    public void goShowCard(Card card) {
        Intent intent = new Intent(this, CardShow_View.class);
        intent.putExtra(Constants.CARD_KEY, card.getKey());
        startActivity(intent);
    }

    @Override
    public void goEditCard(Card card, int position) {
        this.positionInWork = position;

        Intent intent = new Intent(this, CardEdit_View.class);
        intent.putExtra(Constants.CARD_KEY, card.getKey());
        intent.setAction(Constants.ACTION_EDIT);
        startActivityForResult(intent, Constants.CODE_EDIT_CARD);
    }


    // Внутренние методы
    private void bindComponents() {
        presenter.linkViews(this, adapter);
        adapter.linkPresenter(presenter);
    }

    private void unbindComponents() {
        // В порядке, обратном bindComponents()
        adapter.unlinkPresenter();
        presenter.unlinkViews();
    }

    private void processCardEditResult(int resultCode, @Nullable Intent data) {
        if (RESULT_OK == resultCode) {
            try {
                Card card = data.getParcelableExtra(Constants.CARD);
                iGridItem gridItem = new GridItem_Card();
                gridItem.setPayload(card);
                adapter.updateItem(positionInWork, gridItem);
                positionInWork = -1;
            }
            catch (Exception e) {
                showErrorMsg(R.string.CARDS_GRID_data_error, e.getMessage());
                e.printStackTrace();
            }
        }
        else if (RESULT_CANCELED == resultCode) {
            showToast(R.string.CARDS_GRID_card_edit_cancelled);
        }
        else {
            Log.w(TAG, "Unknown result code: "+resultCode);
        }
    }
}
