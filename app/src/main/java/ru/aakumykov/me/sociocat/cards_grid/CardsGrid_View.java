package ru.aakumykov.me.sociocat.cards_grid;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import ru.aakumykov.me.sociocat.AppConfig;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.push_notifications.NewCardsCounter;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.base_view.BaseView;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.cards_grid.items.GridItem_Card;
import ru.aakumykov.me.sociocat.cards_grid.items.iGridItem;
import ru.aakumykov.me.sociocat.cards_grid.view_holders.iGridViewHolder;
import ru.aakumykov.me.sociocat.cards_grid.view_model.CardsGrid_ViewModel;
import ru.aakumykov.me.sociocat.cards_grid.view_model.CardsGrid_ViewModel_Factory;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CardsGrid_View extends BaseView implements
        iCardsGrid.iPageView,
        iCardsGrid.iGridItemClickListener,
        iCardsGrid.iLoadMoreClickListener,

        SearchView.OnQueryTextListener,
        SearchView.OnCloseListener,
        SearchView.OnFocusChangeListener,

        View.OnClickListener,
        SpeedDialView.OnActionSelectedListener
{
    private static final String TAG = "CardsGrid_View";
    private static final int NEW_CARDS_AVAILABLE = 10;

    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.newCardsNotification) TextView newCardsNotification;
    @BindView(R.id.newCardsThrobber) View newCardsThrobber;
    @BindView(R.id.tagsParentContainer) LinearLayout tagsParentContainer;
    @BindView(R.id.tagsContainer) TagContainerLayout tagsContainer;
    @BindView(R.id.speedDialView) SpeedDialView fabSpeedDialView;

    private SearchView searchView;
    private MenuItem searchWidget;

    private CardsGrid_ViewModel viewModel;
    private iCardsGrid.iDataAdapter dataAdapter;
    private iCardsGrid.iPresenter presenter;

    private RecyclerView.LayoutManager layoutManager;
    private int positionInWork = -1;
    private Bundle listStateStorage;
    private final static String KEY_LIST_STATE = "LIST_STATE";
    private int backPressedCount = 0;
    private Menu menu;
    private TimerTask newCardsCheckingTimerTask;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_grid_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.CARDS_GRID_page_title);

        viewModel = new ViewModelProvider(this, new CardsGrid_ViewModel_Factory()).get(CardsGrid_ViewModel.class);

        this.presenter = viewModel.getPresenter();
        if (null == this.presenter) {
            this.presenter = new CardsGrid_Presenter();
            viewModel.storePresenter(this.presenter);
        }

        this.dataAdapter = viewModel.getDataAdapter();
        if (null == this.dataAdapter) {
            this.dataAdapter = new CardsGrid_DataAdapter(this, this, this);
            viewModel.storeDataAdapter(this.dataAdapter);
        }

        int colsNum = MyUtils.isPortraitOrientation(this) ?
                AppConfig.CARDS_GRID_COLUMNS_COUNT_PORTRAIT : AppConfig.CARDS_GRID_COLUMNS_COUNT_LANDSCAPE;
        layoutManager = new StaggeredGridLayoutManager(colsNum, StaggeredGridLayoutManager.VERTICAL);
//        layoutManager = new LinearLayoutManager(this);
        layoutManager = new GridLayoutManager(this, 2);

        recyclerView.setAdapter((RecyclerView.Adapter) dataAdapter);
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
            case Constants.CODE_SHOW_CARD:
                processCardShowResult(resultCode, data);
                break;

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

        if (!dataAdapter.hasData())
            presenter.processInputIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        scheduleNewCardsChecking();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unScheduleNewCardsChecking();
    }

    @Override
    protected void onStop() {
        super.onStop();

        dataAdapter.disableFiltering();

        unbindComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.search_widget, menu);
        menuInflater.inflate(R.menu.search, menu);
        configureSearchWidget(menu);

        menuInflater.inflate(R.menu.tags, menu);

        if (AuthSingleton.isLoggedIn()) {
            menuInflater.inflate(R.menu.profile_in, menu);
            menuInflater.inflate(R.menu.preferences, menu);
        }

        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.actionSearch:
                showSwarchWidget();
                break;

            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        backPressedCount += 1;

        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            dataAdapter.disableFiltering();
        }
        else {
            if (2 == backPressedCount)
                super.onBackPressed();
            else {
                showToast(R.string.press_again_to_exit);
            }
        }
    }

    @Override
    public boolean onSearchRequested() {
        super.onSearchRequested();
        showSwarchWidget();
        return true;
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
        dataAdapter.disableFiltering();
        hideSearchWidget();
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
    public void goShowCard(Card card, int position) {
        this.positionInWork = position;

        Intent intent = new Intent(this, CardShow_View.class);
        intent.putExtra(Constants.CARD, card);

        startActivityForResult(intent, Constants.CODE_SHOW_CARD);
    }

    @Override
    public void goCreateCard(Constants.CardType cardType) {
        Intent intent = new Intent(this, CardEdit_View.class);
        intent.setAction(Constants.ACTION_CREATE);
        intent.putExtra(Constants.CARD_TYPE, cardType.name());
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
        intent.putExtra(Constants.BACK_BUTTON_ENABLED, true);
        startActivity(intent);
    }

    @Override
    public String getCurrentFilterWord() {
        return (null != searchView) ? searchView.getQuery() + "" : "";
    }

    @Override
    public String getCurrentFilterTag() {
        int tagsCount = tagsContainer.getTags().size();
        switch (tagsCount) {
            case 0:
                return null;
            case 1:
                return tagsContainer.getTagText(0);
            default:
                throw new RuntimeException("Cards grid page must have only one filter tag!");
        }
    }

    @Override
    public void showTagFilter(String tagName) {
        MyUtils.show(tagsParentContainer);
        tagsContainer.removeAllTags();
        tagsContainer.addTag(tagName);
    }

    @Override
    public void showToolbarThrobber() {
        MenuItem menuItem = this.menu.findItem(R.id.actionNewCards);
        if (null != menuItem) {
            menuItem.setActionView(R.layout.progress_bar);
        }
    }

    @Override
    public void hideToolbarThrobber() {
        MenuItem menuItem = this.menu.findItem(R.id.actionNewCards);
        if (null != menuItem) {
            menuItem.setActionView(null);
        }
    }

    @Override
    public void showSwipeThrobber() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideSwipeThrobber() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void scroll2position(int position) {
        recyclerView.scrollToPosition(position);
    }

    @Override
    public void showNewCardsNotification(int count) {
        newCardsNotification.setText(getString(R.string.CARDS_GRID_new_cards_available, count));
        MyUtils.show(newCardsNotification);
    }

    @Override
    public void hideNewCardsNotification() {
        MyUtils.hide(newCardsNotification);
    }

    @Override
    public void showLoadingNewCardsThrobber() {
        MyUtils.show(newCardsThrobber);
    }

    @Override
    public void hideLoadingNewCardsThrobber() {
        MyUtils.hide(newCardsThrobber);
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


    // SpeedDialView.OnActionSelectedListener
    @Override
    public boolean onActionSelected(SpeedDialActionItem actionItem) {
        switch (actionItem.getId()) {

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


    // Нажатия
    @OnClick(R.id.newCardsNotification)
    void onNewCardsAvailableClicked() {
        presenter.onNewCardsAvailableClicked();
    }


    // Внутренние методы
    private void bindComponents() {
        presenter.bindComponents(this, dataAdapter);
        dataAdapter.linkPresenter(presenter);
    }

    private void unbindComponents() {
        // В обратном порядке
        dataAdapter.unlinkPresenter();
        presenter.unbindComponents();
    }

    private void configureSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                presenter.onRefreshRequested();
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

        fabSpeedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_audio, R.drawable.ic_fab_audio)
                        .setFabBackgroundColor(resources.getColor(R.color.audio_mode))
                        .setLabel(R.string.FAB_subitem_audio)
                        .setLabelColor(resources.getColor(R.color.white))
                        .setLabelBackgroundColor(resources.getColor(R.color.audio_mode))
                        .create()
        );

        fabSpeedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_video, R.drawable.ic_fab_video)
                        .setFabBackgroundColor(getResources().getColor(R.color.video_mode))
                        .setLabel(R.string.FAB_subitem_video)
                        .setLabelColor(resources.getColor(R.color.white))
                        .setLabelBackgroundColor(resources.getColor(R.color.video_mode))
                        .create()
        );

        fabSpeedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_image, R.drawable.ic_fab_image)
                        .setFabBackgroundColor(resources.getColor(R.color.image_mode))
                        .setLabel(R.string.FAB_subitem_image)
                        .setLabelColor(resources.getColor(R.color.white))
                        .setLabelBackgroundColor(resources.getColor(R.color.image_mode))
                        .create()
        );

        fabSpeedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_quote, R.drawable.ic_fab_text)
                        .setFabBackgroundColor(resources.getColor(R.color.text_mode))
                        .setLabel(R.string.FAB_subitem_text)
                        .setLabelColor(resources.getColor(R.color.white))
                        .setLabelBackgroundColor(resources.getColor(R.color.text_mode))
                        .create()
        );

        fabSpeedDialView.setOnActionSelectedListener(this);

    }

    private void configureSearchWidget(Menu menu) {
        // Ассоциируем настройку поиска с SearchView
        try {
            searchWidget = menu.findItem(R.id.searchWidget);

            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            searchView = (SearchView) menu.findItem(R.id.searchWidget).getActionView();
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

    private void processCardShowResult(int resultCode, @Nullable Intent data) {
        if (null != data) {

            String action = data.getAction();
            if (null == action) action = "";

            Card card = data.getParcelableExtra(Constants.CARD);

            switch (action) {
                case Constants.ACTION_DELETE:
                    dataAdapter.removeItem(positionInWork);
                    positionInWork = -1;
                    break;

                /*case Constants.ACTION_EDIT:
                    dataAdapter.updateItem(positionInWork, card);
                    positionInWork = -1;
                    break;*/

                default:
                    dataAdapter.updateItem(positionInWork, card);
                    positionInWork = -1;
                    break;
            }

        }
    }

    private void processCardCreationResult(int resultCode, @Nullable Intent data) {
        try {
            switch (resultCode) {
                case RESULT_OK:
                    presenter.processCardCreationResult(data);
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
                if (null != data) {
                    Card card = data.getParcelableExtra(Constants.CARD);
                    dataAdapter.updateItem(positionInWork, card);
                }
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

    private void showSwarchWidget() {
        searchWidget.setVisible(true);
        searchView.setVisibility(View.VISIBLE);
        searchView.setIconified(false);
    }

    private void hideSearchWidget() {
        searchView.clearFocus();
        searchView.setVisibility(View.GONE);
        searchWidget.setVisible(false);
    }

    private List<iGridItem> getSavedCardsList() {
        try {
            List<iGridItem> gridItemsList = new ArrayList<>();

            SharedPreferences sharedPreferences = getSharedPrefs(AppConfig.SAVED_CARDS_LIST);

            if (sharedPreferences.contains(AppConfig.CARDS_LIST)) {

                Set<String> cardsSet = sharedPreferences.getStringSet(AppConfig.CARDS_LIST, null);

                for (String cardString : cardsSet) {
                    Card card = new Gson().fromJson(cardString, Card.class);
                    GridItem_Card cardGridItem = new GridItem_Card();
                    cardGridItem.setPayload(card);
                    gridItemsList.add(cardGridItem);
                }

                return (0 == gridItemsList.size()) ? null : gridItemsList;
            }
            else
                return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveCardsList(List<iGridItem> gridItemsList) {
        try {
            SharedPreferences sharedPreferences = getSharedPrefs(AppConfig.SAVED_CARDS_LIST);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            Set<String> cardsSet = new HashSet<>();

            for (iGridItem gridItem : dataAdapter.getList()) {
                if (gridItem instanceof GridItem_Card) {
                    Card card = (Card) gridItem.getPayload();
                    String cardString = new Gson().toJson(card);
                    cardsSet.add(cardString);
                }
            }

            editor.putStringSet(AppConfig.CARDS_LIST, cardsSet);

            editor.apply();
        }
        catch (Exception e) {
            showToast(R.string.CARDS_GRID_error_storing_cards_list);
            e.printStackTrace();
        }
    }

    private void scheduleNewCardsChecking() {
        Handler newCardsCheckingHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case NEW_CARDS_AVAILABLE:
                        presenter.onNewCardsAvailable((Integer) msg.obj);
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };

        newCardsCheckingTimerTask = new TimerTask() {
            @Override
            public void run() {
                checkForNewCards(newCardsCheckingHandler);
            }
        };

        new Timer().schedule(
                newCardsCheckingTimerTask,
                AppConfig.NEW_CARDS_CHECK_DELAY,
                AppConfig.NEW_CARDS_CHECK_INTERVAL
        );
    }

    private void unScheduleNewCardsChecking() {
        newCardsCheckingTimerTask.cancel();
    }

    private void checkForNewCards(Handler handler) {
        int newCardsCount = NewCardsCounter.getCount();
        if (newCardsCount > 0) {
            Message message = handler.obtainMessage(NEW_CARDS_AVAILABLE, newCardsCount);
            handler.sendMessage(message);
        }
    }


}
