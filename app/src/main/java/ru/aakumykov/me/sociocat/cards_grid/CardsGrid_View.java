package ru.aakumykov.me.sociocat.cards_grid;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
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
        iCardsGrid.iGridItemClickListener,
        iCardsGrid.iLoadMoreClickListener,
        SearchView.OnQueryTextListener,
        SearchView.OnCloseListener,
        SearchView.OnFocusChangeListener,
        View.OnClickListener
{
    private static final String TAG = "CardsGrid_View";

    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.tagsContainer) TagContainerLayout tagsContainer;
    @BindView(R.id.speedDialView) SpeedDialView speedDialView;
    private SearchView searchView;

    private CardsGrid_Adapter dataAdapter;
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
        setContentView(R.layout.cards_grid_activity2);
        ButterKnife.bind(this);

        setPageTitle(R.string.CARDS_GRID_page_title);

        presenter = new CardsGrid_Presenter();
        dataAdapter = new CardsGrid_Adapter(this, this, this);

        int colsNum = MyUtils.isPortraitOrientation(this) ?
                Config.CARDS_GRID_COLUMNS_COUNT_PORTRAIT : Config.CARDS_GRID_COLUMNS_COUNT_LANDSCAPE;
        layoutManager = new StaggeredGridLayoutManager(colsNum, StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setAdapter(dataAdapter);
        recyclerView.setLayoutManager(layoutManager);

        configureSwipeRefresh();

        configureTagsContainer();

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
            presenter.processInputIntent(getIntent());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        saveListState();
        dataAdapter.disableFiltering();
        unbindComponents();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(Constants.FILTER_KEY, searchView.getQuery().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String filterKey = savedInstanceState.getString(Constants.FILTER_KEY, "");
        if (filterKey.isEmpty()) {
            dataAdapter.restoreOriginalList();
        } else {
            searchView.setQuery(filterKey, true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search, menu);

        super.onCreateOptionsMenu(menu);

        configureSearchWidget(menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            dataAdapter.disableFiltering();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }


    // View.OnClickListener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionSearch:
                Log.d(TAG, "onClick(): R.id.actionSearch");
                dataAdapter.enableFiltering();
                break;
        }
    }


    // SearchView.OnQueryTextListener
    @Override
    public boolean onQueryTextSubmit(String queryText) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (dataAdapter.filterIsEnabled())
            dataAdapter.applyFilterToGrid(newText);
        return false;
    }


    // SearchView.OnFocusChangeListener
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Log.d(TAG, "View: "+v+", has focus: "+hasFocus);
        if (v.getId() == R.id.actionSearch && hasFocus) {
            dataAdapter.enableFiltering();
        }
    }


    // SearchView.OnCloseListener
    @Override
    public boolean onClose() {
        Log.d(TAG, "onClose()");

        dataAdapter.disableFiltering();

        searchView.clearFocus();
//        dataAdapter.restoreOriginalList();
        return false;
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

    @Override
    public void goCardsGrid() {
        Intent intent = new Intent(this, CardsGrid_View.class);
        startActivity(intent);
    }

    @Override
    public String getFilterString() {
        return searchView.getQuery() + "";
    }

    @Override
    public void showFilteringTag(String tagName) {
        tagsContainer.addTag(tagName);
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


    // iLoadMoreClickListener
    @Override
    public void onLoadMoreClicked(View view) {
        int position = recyclerView.getChildAdapterPosition(view);
        presenter.onLoadMoreClicked(position);
    }


    // Внутренние методы
    private void bindComponents() {
        presenter.linkViews(this, dataAdapter);
        dataAdapter.linkPresenter(presenter);
    }

    private void unbindComponents() {
        // В обратном порядке
        dataAdapter.unlinkPresenter();
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

    private void configureTagsContainer() {
        tagsContainer.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {

            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onSelectedTagDrag(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {
                presenter.onFilteringTagDiscardClicked();
            }
        });
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

    private void configureSearchWidget(Menu menu) {
        // Ассоциируем настройку поиска с SearchView
        try {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            searchView = (SearchView) menu.findItem(R.id.actionSearch).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setMaxWidth(Integer.MAX_VALUE);

            searchView.setOnQueryTextListener(this);
            searchView.setOnSearchClickListener(this);
            searchView.setOnCloseListener(this);
            searchView.setOnQueryTextFocusChangeListener(this);

        } catch (Exception e) {
            showErrorMsg(R.string.error_configuring_page, e.getMessage());
            e.printStackTrace();
        }
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
                    addItem(card);
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
                dataAdapter.updateItem(positionInWork, gridItem);
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

    private void addItem(Card card) {
        int num = new Random().nextInt();

        iGridItem gridItem = new GridItem_Card();
        gridItem.setPayload(card);

        dataAdapter.addItem(gridItem);
    }


}
