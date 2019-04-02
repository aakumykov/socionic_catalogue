package ru.aakumykov.me.sociocat.cards_grid;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.appcompat.widget.PopupMenu;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.util.TimeUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.cards_list.CardsList_View;
import ru.aakumykov.me.sociocat.interfaces.iMyDialogs;
import ru.aakumykov.me.sociocat.login.Login_View;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.MyDialogs;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CardsGrid_View extends BaseView implements
        iCardsGrid.View,
        CardsGrid_Adapter.iAdapterUser,
        SwipeRefreshLayout.OnRefreshListener,
        SearchView.OnQueryTextListener,
        SearchView.OnCloseListener,
        SearchView.OnClickListener
{
    private final static int COLUMNS_COUNT_LANDSCAPE = 3;
    private final static int COLUMNS_COUNT_PORTRAIT = 2;

    @BindView(R.id.swiperefresh) SwipeRefreshLayout swiperefreshLayout;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.floatingActionButton) FloatingActionButton floatingActionButton;
    private SearchView searchView;

    public static final String TAG = "CardsGrid_View";
    private iCardsGrid.Presenter presenter;
    private List<Card> cardsList;
    private CardsGrid_Adapter dataAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private LinearLayoutManager linearLayoutManager;

    private boolean gridMode = true;
    private boolean firstRun = true;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_grid_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.CARDS_GRID_page_title);

        swiperefreshLayout.setOnRefreshListener(this);
        swiperefreshLayout.setColorSchemeResources(R.color.blue_swipe, R.color.green_swipe, R.color.orange_swipe, R.color.red_swipe);

        presenter = new CardsGrid_Presenter();

        int colsNum = MyUtils.isPortraitOrientation(this) ? COLUMNS_COUNT_PORTRAIT : COLUMNS_COUNT_LANDSCAPE;
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(colsNum, StaggeredGridLayoutManager.VERTICAL);
        linearLayoutManager = new LinearLayoutManager(this);

        cardsList = new ArrayList<>();
        dataAdapter = new CardsGrid_Adapter(cardsList);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(dataAdapter);

        getFCMToken();
    }

    @Override
    protected void onStart() {
        super.onStart();

        int listSize = cardsList.size();

        presenter.linkView(this);
        dataAdapter.bindView(this);

//        MyDialogs.dummyDialog(this, "Проверка", ":-)");

        if (firstRun) {
            loadList(true);
            firstRun = false;
        }
    }

    @Override
    protected void onStop() {
//        Log.d("QWERTY", "onStop()");
        super.onStop();
        presenter.unlinkView();
        dataAdapter.unbindView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.CODE_CREATE_CARD:
                addCardToList(data);
                break;
            case Constants.CODE_SHOW_CARD:
                processCardShowResult(resultCode, data);
                break;
            case Constants.CODE_EDIT_CARD:
                processCardEditionResult(resultCode, data);
                break;
            default:
                break;
        }
    }

    @Override
    public void onUserLogin() {
        //MyUtils.show(floatingActionButton);
    }

    @Override
    public void onUserLogout() {
        //MyUtils.remove(floatingActionButton);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu()");

        menu.clear();

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.new_cards, menu);

        if (gridMode) menuInflater.inflate(R.menu.list_view, menu);
        else  menuInflater.inflate(R.menu.grid_view, menu);

        menuInflater.inflate(R.menu.tags, menu);

        menuInflater.inflate(R.menu.search, menu);
        initSearchWidget(menu);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.actionListView:
                activateListView();
                break;

            case R.id.actionGridView:
                activateGridView();
                break;

            case R.id.actionNewCards:
                showNewCards();
                break;

            default:
                super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onRefresh() {
        swiperefreshLayout.setRefreshing(true);
        loadList(false);
    }

    @Override public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (Configuration.ORIENTATION_LANDSCAPE == newConfig.orientation) {
            showToast("Альбом");
            staggeredGridLayoutManager.setSpanCount(COLUMNS_COUNT_LANDSCAPE);
        }
        else if (Configuration.ORIENTATION_PORTRAIT == newConfig.orientation) {
            showToast("Портрет");
            staggeredGridLayoutManager.setSpanCount(COLUMNS_COUNT_PORTRAIT);
        }
        else {
            Log.e(TAG, "Unknown orientation '"+newConfig.orientation+"'");
        }
    }


    // Интерфейсные методы
    @Override
    public void displayList(List<Card> list) {
        hideProgressBar();
        hideMsg();
        swiperefreshLayout.setRefreshing(false);

        //if (null != cardsList)
            cardsList.clear();
        cardsList.addAll(list);
        dataAdapter.notifyDataSetChanged();
    }

    @Override
    public void removeGridItem(Card card) {
        int index = cardsList.indexOf(card);
        if (-1 != index) {
            cardsList.remove(index);
            dataAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDataFiltered(List<Card> filteredCardsList) {
        //if (null != filteredCardsList)
            this.cardsList = filteredCardsList;
    }

    @Override
    public void onGridItemClick(int position) {
        Card card = cardsList.get(position);
        if (null != card) {
            showCard(card.getKey());
        } else {
            showErrorMsg(R.string.CARDS_GRID_error_no_such_card);
        }
    }

    @Override
    public void onPopupMenuClick(MenuItem menuItem, int listPosition) {

        Card card = cardsList.get(listPosition);

        switch (menuItem.getItemId()) {

            case R.id.actionEdit:
                editCard(card);
                break;

            case R.id.actionDelete:
                deleteCardQuestion(card);
                break;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        dataAdapter.getFilter().filter(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        dataAdapter.getFilter().filter(s);
        return false;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onClose() {
        dataAdapter.restoreInitialList();
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }


    // Нажатия
    @OnClick(R.id.floatingActionButton)
    void fabClicked() {
        if (auth().isUserLoggedIn()) {
            goCreateCard();
        } else {
            MyDialogs.loginRequiredDialog(
                    this,
                    R.string.CARDS_GRID_authorization,
                    R.string.CARDS_GRID_you_must_login_to_add_card,
                    new iMyDialogs.StandardCallbacks()
                    {
                        @Override public void onCancelInDialog() {

                        }

                        @Override public void onNoInDialog() {

                        }

                        @Override public boolean onCheckInDialog() {
                            return true; // TODO: попробовать false
                        }

                        @Override public void onYesInDialog() {
                            Intent intent = new Intent(CardsGrid_View.this, Login_View.class);
                            intent.setAction(Constants.ACTION_CREATE);
                            startActivity(intent);
                        }
                    }
            );
        }
    }


    // Внутренние методы
    private void initSearchWidget(Menu menu) {
        // Ассоциируем настройку поиска с SearchView
        try {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView = (SearchView) menu.findItem(R.id.actionSearch).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setMaxWidth(Integer.MAX_VALUE);

            searchView.setOnQueryTextListener(this);
            searchView.setOnCloseListener(this);

        } catch (Exception e) {
            showErrorMsg(e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadList(boolean showMessage) {
        if (showMessage) {
            MyUtils.show(progressBar);
            showInfoMsg(R.string.CARDS_GRID_loading_cards);
        }
        presenter.loadCards();
    }

    private void goListView() {
        Intent intent = new Intent(this, CardsList_View.class);
        startActivity(intent);
    }

    private void addCardToList(@Nullable Intent data) {
        if (null != data) {
            Card card = data.getParcelableExtra(Constants.CARD);
            if (null != card) {
                cardsList.add(card);
                dataAdapter.notifyDataSetChanged();
            }
        }
    }

    private void showCard(String cardKey) {
        Intent intent = new Intent(this, CardShow_View.class);
        intent.putExtra(Constants.CARD_KEY, cardKey);
        startActivityForResult(intent, Constants.CODE_SHOW_CARD);
    }

    private void activateListView() {
        gridMode = false;

        recyclerView.setAdapter(null);
        recyclerView.setLayoutManager(null);
        recyclerView.setAdapter(dataAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        dataAdapter.notifyDataSetChanged();

        invalidateOptionsMenu();
    }

    private void activateGridView() {
        gridMode = true;

        recyclerView.setAdapter(null);
        recyclerView.setLayoutManager(null);
        recyclerView.setAdapter(dataAdapter);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        dataAdapter.notifyDataSetChanged();

        invalidateOptionsMenu();
    }

    private void showNewCards() {
//        SharedPreferences sharedPreferences = getSharedPrefs(Constants.SHARED_PREFERENCES_LOGIN);
//        long lastLoginTime = (sharedPreferences.contains(Constants.KEY_LAST_LOGIN))
//                ? sharedPreferences.getLong(Constants.KEY_LAST_LOGIN, 0L)
//                : 0L;
//        presenter.loadNewCards(lastLoginTime);

        presenter.loadNewCards();
    }

    private int findIndexOfCard(Card card) {
        int index = -1;
        String key = card.getKey();
        for (int i=0; i<cardsList.size(); i++) {
            if (key.equals(cardsList.get(i).getKey()))
                return i;
        }
        return index;
    }

    private void processCardShowResult(int resultCode, @Nullable Intent data) {

        if (null != data && RESULT_OK == resultCode) {

            String backAction = data.getAction();

            if (Constants.ACTION_DELETE.equals(backAction)) {

                Card card = data.getParcelableExtra(Constants.CARD);

                if (null != card) {
                    int index = findIndexOfCard(card);
                    if (-1 != index) {
                        cardsList.remove(index);
//                        dataAdapter.notifyItemRemoved(index);
                        dataAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    private void processCardEditionResult(int resultCode, @Nullable Intent data) {
        if (RESULT_OK == resultCode) {
            if (null != data) {
                Card card = data.getParcelableExtra(Constants.CARD);
                int index = findIndexOfCard(card);
                cardsList.set(index, card);
                dataAdapter.notifyItemChanged(index);
            }
        }
    }

    private void deleteCardQuestion(Card card) {

        String cardName = card.getTitle();

        MyDialogs.cardDeleteDialog(this, cardName, new iMyDialogs.Delete() {
            @Override
            public void onCancelInDialog() {

            }

            @Override
            public void onNoInDialog() {

            }

            @Override
            public boolean onCheckInDialog() {
                return true;
            }

            @Override
            public void onYesInDialog() {
                presenter.deleteCard(card);
            }
        });
    }

    private void editCard(Card card) {
        Intent intent = new Intent(this, CardEdit_View.class);
//        intent.putExtra(Constants.CARD, card);
        intent.putExtra(Constants.CARD_KEY, card.getKey());
        intent.setAction(Constants.ACTION_EDIT);
        startActivityForResult(intent, Constants.CODE_EDIT_CARD);
    }

    private void getFCMToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        String token;
                        InstanceIdResult instanceIdResult = task.getResult();
                        if (null != instanceIdResult) {
                            token = instanceIdResult.getToken();
//                            showToast("TOKEN: " + token);
                        }
                    }
                });
    }

}
