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

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseClass;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.card_edit.CardEdit_View;
import ru.aakumykov.me.mvp.card_view.CardView_View;
import ru.aakumykov.me.mvp.interfaces.iCardsService;
import ru.aakumykov.me.mvp.interfaces.iDialogCallbacks;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.utils.YesNoDialog;

// TODO: Пункт "обновить" в меню панели.

public class CardsList_View extends BaseClass implements
        iCardsList.View,
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener,
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_list_activity);
        ButterKnife.bind(this);

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
        loadList(false);
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
        Card card = cardsList.get(position);

        Drawable oldBackground = view.getBackground();
        view.setBackgroundColor(getResources().getColor(R.color.selected_list_item_bg));

        showPopupMenu(view, oldBackground,  card);
        return true;
    }

    // Зачем, если список живой?
    @Override
    public void onRefresh() {
//        Log.d(TAG, "onRefresh()");
        cardsList.clear();
        cardsListAdapter.notifyDataSetChanged();
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

    @Override
    public void onAddCardButton(String cardType) {
//        Log.d(TAG, "onAddCardButton("+cardType+")");

        switch (cardType) {
            case Constants.TEXT_CARD:
                break;
            case Constants.IMAGE_CARD:
                break;
            default:
                Log.e(TAG, "Wrong card type: "+cardType);
                return;
        }

        Intent intent = new Intent();
        intent.setClass(this, CardEdit_View.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(Constants.CARD_TYPE, cardType);
        startActivity(intent);
    }


    private void loadList(boolean manualRefresh) {
        Log.d(TAG, "loadList(manualRefresh: "+manualRefresh+")");
        if (!manualRefresh) showLoadingMessage();
        getCardsService().loadList(this);
    }

    private void showLoadingMessage() {
        messageView.setText(R.string.getting_cards_list);
        messageView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoadingMessage() {
        progressBar.setVisibility(View.GONE);
        messageView.setVisibility(View.GONE);
    }


    private void showPopupMenu(final View v, final Drawable oldBackground, final Card card) {

        PopupMenu popupMenu = new PopupMenu(this, v);

        popupMenu.inflate(R.menu.card_actions_menu);

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu popupMenu) {
                v.setBackground(oldBackground);
            }
        });

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.actionEdit:
                        editCard(card);
//                        try { editCard(CardsListActivity.this, baseCard.getType(), baseCard.getKey()); }
//                        catch (Exception e) { msg.error(getString(R.string.error_editing_card), e); }
                        return true;

                    case R.id.actionDelete:
                        deleteCard(card);
                        return true;

                    default:
                        return false;
                }
            }
        });

//        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
//            @Override
//            public void onDismiss(PopupMenu menu) {
//                Toast.makeText(getApplicationContext(), "onDismiss", Toast.LENGTH_SHORT).show();
//            }
//        });

        popupMenu.setGravity(Gravity.END);

        popupMenu.show();
    }


    public void showInfoMsg(int messageId) {
        showMsg(getResources().getString(messageId), getResources().getColor(R.color.info));
    }

    public void showErrorMsg(int messageId) {
        showErrorMsg(getResources().getString(messageId));
    }

    public void showErrorMsg(String message) {
        showMsg(message, getResources().getColor(R.color.error));
    }

    private void showMsg(String text, int color) {
        messageView.setText(text);
        messageView.setTextColor(color);
        MyUtils.show(messageView);
    }

    public void hideMsg() {
        MyUtils.hide(messageView);
    }


    // "Методы карточки" (в списке)
    private void viewCard(Card card) {
        Log.d(TAG, "viewCard()");
        Intent intent = new Intent();
        intent.setClass(this, CardView_View.class);
        intent.putExtra(Constants.CARD_KEY, card.getKey());
        startActivity(intent);
    }

    private void editCard(Card card) {
        Log.d(TAG, "editCard()");
        Intent intent = new Intent(this, CardEdit_View.class);
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
                        // Правильно: CardsList_View.this ?
                        getCardsService().deleteCard(card, CardsList_View.this);
                    }
                },
                null
        );

        yesNoDialog.show();
    }


    // Методы обратнаго вызова
    @Override
    public void onChildAdded(Card card) {
        Log.d(TAG, "onChildAdded()");
        hideLoadingMessage();
        swipeRefreshLayout.setRefreshing(false);
        cardsList.add(card);
        cardsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChildChanged(Card card, String previousCardName) {
        Log.d(TAG, "onChildChanged(), "+card);
        String changedCardKey = card.getKey();
        Card oldCard = cardsList.findCardByKey(changedCardKey);
        // TODO: где обрабатывать ошибки?
//        if (null != oldCard) {
            int cardArrayIndex = cardsList.indexOf(oldCard);
            cardsList.set(cardArrayIndex, card);
            cardsListAdapter.notifyDataSetChanged();
//        } else {
//            showErrorMsg(R.string.error_updating_list);
//            Log.e(TAG, "Ошибка обновления списка после изменения карточки "+card);
//        }
    }


    // Блять, это же не нужно на живом списке (нужно на обычном)
    // Пора отдыхать!
//    @Override
//    public void onUpdateSuccess(Card card) {
//        Log.d(TAG, "onUpdateSuccess()");
//        // TODO: переделать на Toast
//        showInfoMsg(R.string.card_update_success);
//    }
//
//    @Override
//    public void onUpdateError(String msg) {
//        Log.d(TAG, "onUpdateError()");
//        showErrorMsg(R.string.card_update_error);
//    }

    @Override
    public void onDeleteSuccess(Card card) {
        Log.d(TAG, "onDeleteSuccess(), "+card);
        String changedCardKey = card.getKey();
        Card oldCard = cardsList.findCardByKey(changedCardKey);
        cardsList.remove(oldCard);
        cardsListAdapter.remove(oldCard);
    }

    @Override
    public void onDeleteError(String msg) {
        Log.d(TAG, "onDeleteError(), "+msg);
        showErrorMsg(R.string.error_deleting_card);
    }


    @Override
    public void onChildMoved(Card card, String previousCardName) {

    }

    @Override
    public void onCancelled(String errorMessage) {

    }

    @Override
    public void onBadData(String errorMsg) {

    }
}
