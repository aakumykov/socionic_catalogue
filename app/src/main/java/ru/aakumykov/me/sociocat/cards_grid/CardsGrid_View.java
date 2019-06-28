package ru.aakumykov.me.sociocat.cards_grid;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.cards_grid.items.GridItem_Card;
import ru.aakumykov.me.sociocat.cards_grid.items.iGridItem;
import ru.aakumykov.me.sociocat.cards_grid.view_holders.iGridViewHolder;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CardsGrid_View extends BaseView implements
        iCardsGrid.iPageView,
        iCardsGrid.iGridItemClickListener
{
    private static final String TAG = "CardsGrid_View";

    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.speedDialView) SpeedDialView speedDialView;

    private CardsGrid_Adapter adapter;
    private iCardsGrid.iPresenter presenter;
    private StaggeredGridLayoutManager layoutManager;
    private boolean firstRun = true;
    private int positionInWork = -1;
    private Bundle listStateStorage;

    private final static String KEY_LIST_STATE = "LIST_STATE";


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_grid_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.CARDS_GRID_page_title);

        presenter = new CardsGrid_Presenter();
        adapter = new CardsGrid_Adapter(this, this);

        int colsNum = MyUtils.isPortraitOrientation(this) ?
                Config.CARDS_GRID_COLUMNS_COUNT_PORTRAIT : Config.CARDS_GRID_COLUMNS_COUNT_LANDSCAPE;
        layoutManager = new StaggeredGridLayoutManager(colsNum, StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        configureSwipeRefresh();

        configureFAB();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        bindComponents();

        switch (requestCode) {
            case Constants.CODE_CREATE_CARD:
                processCardCreationResult(resultCode, data);
                break;

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
        else {
            //restoreListState();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveListState();
        unbindComponents();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }


    // iPageView
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
    public void scrollToPosition(Integer position) {
        recyclerView.scrollToPosition(position);
    }

    @Override
    public void goShowCard(Card card) {
        Intent intent = new Intent(this, CardShow_View.class);
        intent.putExtra(Constants.CARD_KEY, card.getKey());
        startActivity(intent);
    }

    @Override
    public void goCreateCard(Constants.CardType cardType) {

        Card card = new Card();

        switch (cardType) {
            case TEXT_CARD:
                card.setType(Constants.TEXT_CARD);
                break;
            case IMAGE_CARD:
                card.setType(Constants.IMAGE_CARD);
                break;
            case AUDIO_CARD:
                card.setType(Constants.AUDIO_CARD);
                break;
            case VIDEO_CARD:
                card.setType(Constants.VIDEO_CARD);
                break;
        }

        Intent intent = new Intent(this, CardEdit_View.class);
        intent.putExtra(Constants.CARD, card);

        startActivityForResult(intent, Constants.CODE_CREATE_CARD);
    }

    @Override
    public void goEditCard(Card card, int position) {
        this.positionInWork = position;

        Intent intent = new Intent(this, CardEdit_View.class);
        intent.putExtra(Constants.CARD, card);
        intent.setAction(Constants.ACTION_EDIT);
        startActivityForResult(intent, Constants.CODE_EDIT_CARD);
    }


    // iGridItemClickListener
    @Override
    public void onGridItemClicked(View view) {
        int position = recyclerView.getChildLayoutPosition(view);
        presenter.onCardClicked(position);
    }

    @Override
    public void onGridItemLongClicked(View view) {
        int position = recyclerView.getChildLayoutPosition(view);
        iGridViewHolder gridViewHolder = (iGridViewHolder) recyclerView.getChildViewHolder(view);
        presenter.onCardLongClicked(position, view, gridViewHolder);
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

    private void configureSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                showToast(R.string.not_implemented_yet);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.blue_swipe, R.color.green_swipe, R.color.orange_swipe, R.color.red_swipe);
    }

    private void configureFAB() {

        Resources resources = getResources();

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_audio, R.drawable.ic_fab_audio)
                        .setFabBackgroundColor(resources.getColor(R.color.audio_mode))
                        .setLabel(R.string.FAB_subitem_audio)
                        .setLabelColor(resources.getColor(R.color.white))
                        .setLabelBackgroundColor(resources.getColor(R.color.audio_mode))
                        .create()
        );

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_video, R.drawable.ic_fab_video)
                        .setFabBackgroundColor(getResources().getColor(R.color.video_mode))
                        .setLabel(R.string.FAB_subitem_video)
                        .setLabelColor(resources.getColor(R.color.white))
                        .setLabelBackgroundColor(resources.getColor(R.color.video_mode))
                        .create()
        );

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_image, R.drawable.ic_fab_image)
                        .setFabBackgroundColor(resources.getColor(R.color.image_mode))
                        .setLabel(R.string.FAB_subitem_image)
                        .setLabelColor(resources.getColor(R.color.white))
                        .setLabelBackgroundColor(resources.getColor(R.color.image_mode))
                        .create()
        );

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_quote, R.drawable.ic_fab_text)
                        .setFabBackgroundColor(resources.getColor(R.color.text_mode))
                        .setLabel(R.string.FAB_subitem_text)
                        .setLabelColor(resources.getColor(R.color.white))
                        .setLabelBackgroundColor(resources.getColor(R.color.text_mode))
                        .create()
        );

        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem speedDialActionItem) {

                switch (speedDialActionItem.getId()) {

                    case R.id.fab_quote:
                        presenter.onCreateCardClicked(Constants.CardType.TEXT_CARD);
                        return false;

                    case R.id.fab_image:
                        presenter.onCreateCardClicked(Constants.CardType.IMAGE_CARD);
                        return false;

                    case R.id.fab_audio:
                        presenter.onCreateCardClicked(Constants.CardType.AUDIO_CARD);
                        return false;

                    case R.id.fab_video:
                        presenter.onCreateCardClicked(Constants.CardType.VIDEO_CARD);
                        return false;

                    default:
                        return false;
                }
            }
        });

    }

    private void saveListState() {
        listStateStorage = new Bundle();
        Parcelable listState = layoutManager.onSaveInstanceState();
        listStateStorage.putParcelable(KEY_LIST_STATE, listState);
    }

    private void restoreListState() {
        if (null != listStateStorage) {
            Parcelable listState = listStateStorage.getParcelable(KEY_LIST_STATE);
            if (null != listState) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (null != layoutManager)
                    layoutManager.onRestoreInstanceState(listState);
            }
        }
    }

    private void processCardCreationResult(int resultCode, @Nullable Intent data) {
        try {

            switch (resultCode) {
                case RESULT_OK:
                    Card card = data.getParcelableExtra(Constants.CARD);
                    addTestItem(card);
                    break;

                case RESULT_CANCELED:
                    showToast(R.string.CARDS_GRID_card_creation_cancelled);
                    break;

                default:
                    throw new Exception("Unknown result code: "+resultCode);
            }

        }
        catch (Exception e) {
            showErrorMsg(R.string.CARDS_GRID_card_creation_error, e.getMessage());
            e.printStackTrace();
        }
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

    private void addTestItem() {
        int num = new Random().nextInt();

        Card card = new Card();
        card.setType(Constants.TEXT_CARD);
        card.setTitle(num+"_карточка");
        card.setQuote(num+" цитата");
        card.setDescription(num+" описание");

        iGridItem gridItem = new GridItem_Card();
        gridItem.setPayload(card);

        adapter.addItem(gridItem);
    }

    private void addTestItem(Card card) {
        int num = new Random().nextInt();

        iGridItem gridItem = new GridItem_Card();
        gridItem.setPayload(card);

        adapter.addItem(gridItem);
    }
}
