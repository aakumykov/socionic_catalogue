package ru.aakumykov.me.mvp.cards_list;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.card_edit.CardEdit_View;
import ru.aakumykov.me.mvp.card_view.CardView_View;
import ru.aakumykov.me.mvp.interfaces.iCardsService;
import ru.aakumykov.me.mvp.interfaces.iDialogCallbacks;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.services.TagsSingleton;
import ru.aakumykov.me.mvp.tags.list.TagsList_View;
import ru.aakumykov.me.mvp.users.list.UsersList_View;
import ru.aakumykov.me.mvp.utils.YesNoDialog;

// TODO: Пункт "обновить" в меню панели.

public class CardsList_View extends BaseView implements
        iCardsList.View,
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener,
        PopupMenu.OnMenuItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        iCardsService.ListCallbacks
{

    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.listView) ListView listView;

    private final static String TAG = "CardsList_View";
    private CardsArrayList cardsList;
    private CardsListAdapter cardsListAdapter;
    private boolean firstRun = true;
    private Card currentCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_list_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.CARDS_LIST_page_title);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_swipe, R.color.green_swipe, R.color.orange_swipe, R.color.red_swipe);

        listView.setOnItemClickListener(this);
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(this);

        cardsList = new CardsArrayList();
        cardsListAdapter = new CardsListAdapter(this, R.layout.cards_list_item, cardsList);
        listView.setAdapter(cardsListAdapter);
    }

    @Override
    public void onServiceBounded() {
        if (firstRun) {
            loadList(false);
            firstRun = false;
        }
    }

    @Override
    public void onServiceUnbounded() {

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick(..., position: "+position+", id: "+id+")");
        Card card = cardsList.get(position);
        viewCard(card);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        currentCard = cardsList.get(position);

        Drawable oldBackground = view.getBackground();
        view.setBackgroundColor(getResources().getColor(R.color.selected_list_item_bg));

        showPopupMenu(view, oldBackground);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.actionEdit:
                editCard(currentCard);
                currentCard = null; // Надо же, editCard() так работает!
                return true;

            case R.id.actionDelete:
                deleteCard(currentCard);
//                currentCard = null; // Попробовать (ещё не пробовал)
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh()");
        hideMsg();
        showInfoMsg(R.string.refreshing_list);
        loadList(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.cards_list_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.actionTags:
                goToPage(TagsList_View.class);
                break;

            case R.id.actionUsers:
                onUsersButton();
                break;

            case R.id.actionCreateTextCard:
                onAddCardButton(Constants.TEXT_CARD);
                break;

            case R.id.actionCreateImageCard:
                onAddCardButton(Constants.IMAGE_CARD);
                break;

            case android.R.id.home:
                this.finish();
                break;

            default:
                super.onOptionsItemSelected(item);
        }

        return true;
    }


    private void onAddCardButton(String cardType) {

        // А ведь можно перенести эту проверку в модель Card...
        switch (cardType) {
            case Constants.TEXT_CARD:
                break;
            case Constants.IMAGE_CARD:
                break;
            default:
                Log.e(TAG, "Wrong card type: "+cardType);
                return;
        }

        Card cardDraft = new Card();
        cardDraft.setType(cardType);

        Intent intent = new Intent(this, CardEdit_View.class);
        intent.setAction(Constants.ACTION_CREATE);
        intent.putExtra(Constants.CARD, cardDraft);

        startActivity(intent);
    }

    private void onUsersButton() {
        Intent intent = new Intent(this, UsersList_View.class);
        startActivity(intent);
    }

    private void loadList(boolean manualRefresh) {
        Log.d(TAG, "loadList(manualRefresh: "+manualRefresh+")");

        if (!manualRefresh) showLoadingMessage();

        Intent intent = getIntent();
        String tagFilter = intent.getStringExtra(Constants.TAG_FILTER);

        getCardsService().loadList(tagFilter, this);
    }

    private void showLoadingMessage() {
        messageView.setText(R.string.loading_cards_list);
        messageView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoadingMessage() {
        progressBar.setVisibility(View.GONE);
        messageView.setVisibility(View.GONE);
    }


    private void showPopupMenu(final View v, final Drawable oldBackground) {

        PopupMenu popupMenu = new PopupMenu(this, v);

        popupMenu.inflate(R.menu.card_actions_menu);

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


    // "Методы карточки" (в списке)
    private void viewCard(Card card) {
        Log.d(TAG, "viewCard()");
        Intent intent = new Intent();
        intent.setClass(this, CardView_View.class);
        intent.putExtra(Constants.CARD_KEY, card.getKey());
        startActivity(intent);
    }

    private void editCard(final Card card) {
        Log.d(TAG, "editCard()");
        Intent intent = new Intent(this, CardEdit_View.class);
        intent.setAction(Intent.ACTION_EDIT);
        intent.putExtra(Constants.CARD, card);
        startActivityForResult(intent, Constants.CODE_EDIT_CARD);
    }

    private void deleteCard(final Card card) {
        Log.d(TAG, "deleteCard(), "+card.getTitle());

        YesNoDialog yesNoDialog = new YesNoDialog(this, R.string.card_deletion,
                R.string.really_delete_card,
                new iDialogCallbacks.onCheck() {
                    @Override
                    public boolean doCheck() {
                        return true;
                    }
                },
                new iDialogCallbacks.onYes() {
                    @Override
                    public void yesAction() {
                        getCardsService().deleteCard(card, CardsList_View.this);
                    }
                },
                null
        );

        yesNoDialog.show();
    }


    // Методы обратнаго вызова
    @Override
    public void onListLoadSuccess(List<Card> list) {
        Log.d(TAG, "onListLoadSuccess()");

        hideLoadingMessage();
        swipeRefreshLayout.setRefreshing(false);

        cardsList.clear();
        cardsList.addAll(list);
        cardsListAdapter.notifyDataSetChanged();

        if (0 == list.size()) showInfoMsg(R.string.list_is_empty);
    }

    @Override
    public void onListLoadFail(String errorMessage) {
        Log.d(TAG, "onListLoadFail()");
        showErrorMsg(R.string.error_loading_list, errorMessage);
    }

    @Override
    public void onDeleteSuccess(Card deletedCard) {
        Log.d(TAG, "onDeleteSuccess(), "+deletedCard);

        String oldCardKey = deletedCard.getKey();
        Card oldCard = cardsList.findCardByKey(oldCardKey);
        cardsList.remove(oldCard);
        cardsListAdapter.remove(oldCard);

        HashMap<String,Boolean> oldTags = deletedCard.getTags();
        TagsSingleton.getInstance().updateCardTags(oldCardKey, oldTags, null);
    }

    @Override
    public void onDeleteError(String msg) {
        Log.d(TAG, "onDeleteError(), "+msg);
        showErrorMsg(R.string.error_deleting_card);
    }


    // Внутренние методы
    private void goToPage(Class<?> activityClass) {
        Log.d(TAG, "goToPage("+activityClass+")");
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}
