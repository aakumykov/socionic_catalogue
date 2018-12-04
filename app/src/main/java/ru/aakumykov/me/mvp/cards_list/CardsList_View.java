package ru.aakumykov.me.mvp.cards_list;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.card.edit.CardEdit_View;
import ru.aakumykov.me.mvp.card_show.CardShow_View;
import ru.aakumykov.me.mvp.interfaces.iMyDialogs;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.utils.MyDialogs;
import ru.aakumykov.me.mvp.utils.MyUtils;

// Построен по принципу Active View

// TODO: не исчезает крутилка пальцевого обновления

public class CardsList_View extends BaseView implements
        iCardsList.View,
        ListView.OnItemClickListener,
        ListView.OnItemLongClickListener,
        PopupMenu.OnMenuItemClickListener,
        SwipeRefreshLayout.OnRefreshListener
{
    @BindView(R.id.swiperefresh) SwipeRefreshLayout swiperefreshLayout;
    @BindView(R.id.filterView) LinearLayout filterView;
    @BindView(R.id.filterCloser) ImageView filterCloser;
    @BindView(R.id.filterName) TextView filterName;
    @BindView(R.id.listView) ListView listView;

    private final static String TAG = "CardsList_View";
    private iCardsList.Presenter presenter;
    private List<Card> cardsList;
    private CardsListAdapter cardsListAdapter;
    private Card currentCard;
    private boolean firstRun = true;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_list);
        ButterKnife.bind(this);

        setPageTitle(getResources().getString(R.string.CARDS_LIST_page_title));

        swiperefreshLayout.setOnRefreshListener(this);
        swiperefreshLayout.setColorSchemeResources(R.color.blue_swipe, R.color.green_swipe, R.color.orange_swipe, R.color.red_swipe);

        presenter = new CardsList_Presenter();

        cardsList = new ArrayList<>();
        cardsListAdapter = new CardsListAdapter(this, R.layout.cards_list_item, cardsList);
        listView.setAdapter(cardsListAdapter);

        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listView.setLongClickable(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

        if (firstRun) {
            firstRun = false;
            loadList(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        presenter.linkView(this);

        switch (requestCode) {
            case Constants.CODE_CREATE_CARD:
                // TODO: как отображать ошибку?
                if (RESULT_OK==resultCode) processCardCreationResult(data);
                break;
            case Constants.CODE_EDIT_CARD:
                if (RESULT_OK==resultCode) processCardEditionResult(data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (auth().isUserLoggedIn()) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.create_card, menu);
            menuInflater.inflate(R.menu.refresh, menu);
        }

        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.actionRefresh:
                loadList(true);
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


    // Обязательные методы
    @Override
    public void onUserLogin() {
    }

    @Override
    public void onUserLogout() {

    }


    // Интерфейсные методы
    @Override
    public void displayList(final List<Card> list) {
        Log.d(TAG, "displayList()");

        hideProgressBar();
        hideMsg();
        swiperefreshLayout.setRefreshing(false);

        cardsList.clear();
        cardsList.addAll(list);
        cardsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void displayTagFilter(String tagName) {
        String text = getResources().getString(R.string.CARDS_LIST_tag_filter, tagName);
        filterName.setText(text);
        MyUtils.show(filterView);

        activateUpButton();
    }

    @Override
    public void addListItem(Card card) {
        cardsList.add(card);
        cardsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateListItem(int index, Card card) {
        cardsList.set(index, card);
        cardsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void removeListItem(Card card) {
        cardsList.remove(card);
        cardsListAdapter.notifyDataSetChanged();
    }


    // Нажатия
    @OnClick(R.id.filterCloser)
    void clearFilter() {
        MyUtils.hide(filterView);
        presenter.loadList(null);
    }

    // Нажатия в списке
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Log.d(TAG, "onItemClick(pos: "+position+", id: "+id+")");

        // TODO: где контролировать эти данные?
        Card card = cardsList.get(position);

        Intent intent = new Intent(this, CardShow_View.class);
        intent.putExtra(Constants.CARD_KEY, card.getKey());
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//        Log.d(TAG, "onItemLongClick(pos: "+position+", id: "+id+")");
        currentCard = cardsList.get(position);

        if (auth().isUserLoggedIn()) {
            Drawable oldBackground = view.getBackground();
            view.setBackgroundColor(getResources().getColor(R.color.selected_list_item_bg));
            showPopupMenu(view, oldBackground);
        }

        return true;
    }


    // Выслывающее меню
    private void showPopupMenu(final View v, final Drawable oldBackground) {

        PopupMenu popupMenu = new PopupMenu(this, v);

        popupMenu.inflate(R.menu.edit);
        popupMenu.inflate(R.menu.delete);

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu popupMenu) {
                v.setBackground(oldBackground);
            }
        });

        popupMenu.setOnMenuItemClickListener(this);

        popupMenu.setGravity(Gravity.END);

        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.actionEdit:
                editCard();
                return true;

            case R.id.actionDelete:
                deleteCard();
                return true;

            default:
                return false;
        }
    }


    // Внутренние методы
    private void editCard() {
        Intent intent = new Intent(this, CardEdit_View.class);
        intent.setAction(Constants.ACTION_EDIT);
        intent.putExtra(Constants.CARD_KEY, currentCard.getKey());
        startActivityForResult(intent, Constants.CODE_EDIT_CARD);
//        currentCard = null;
    }

    private void deleteCard() {
        String cardName = currentCard.getTitle();
//        currentCard = null;

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
                presenter.deleteCardConfigmed(currentCard);
            }
        });
    }

    private void loadList(boolean showProgressBar) {
        String tagFilter = null;

        try {
            tagFilter = getIntent().getStringExtra(Constants.TAG_FILTER);
        } catch (Exception e) {}

        if (showProgressBar) showProgressBar();
        presenter.loadList(tagFilter);
    }

    @Override
    public void processCardCreationResult(Intent data) {

        if (null != data) {

            Card card = data.getParcelableExtra(Constants.CARD);

            if (null != card) {
                showToast(R.string.INFO_card_created);
                addListItem(card);

            } else {
                showErrorMsg(R.string.CARDS_LIST_error_creating_card);
            }

        } else {
            showErrorMsg(R.string.CARD_SHOW_data_error);
        }
    }

    @Override
    public void processCardEditionResult(Intent data) {

        if (null != data) {

            Card newCard = data.getParcelableExtra(Constants.CARD);

            Integer oldCardIndex = cardsList.indexOf(currentCard);

            updateListItem(oldCardIndex, newCard);

        } else {
            showErrorMsg(R.string.CARD_SHOW_data_error);
        }
    }
}
