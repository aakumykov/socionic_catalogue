package ru.aakumykov.me.mvp.cards_list;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.card.edit.CardEdit2_View;
import ru.aakumykov.me.mvp.card_show.CardShow_View;
import ru.aakumykov.me.mvp.interfaces.iDialogCallbacks;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.utils.MyUtils;
import ru.aakumykov.me.mvp.utils.YesNoDialog;

// Построен по принципу Active View

// TODO: не исчезает крутилка пальцевого обновления

public class CardsList_View extends BaseView implements
        iCardsList.View,
        ListView.OnItemClickListener,
        ListView.OnItemLongClickListener,
        PopupMenu.OnMenuItemClickListener
{
    @BindView(R.id.tagFilterView) TextView tagFilterView;
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
        setContentView(R.layout.cards_list_activity);
        ButterKnife.bind(this);

        setPageTitle(getResources().getString(R.string.CARDS_LIST_page_title));

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

            String tagFilter = null;

            try {
                tagFilter = getIntent().getStringExtra(Constants.TAG_FILTER);
            } catch (Exception e) {}

            presenter.loadList(tagFilter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        presenter.linkView(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (userLoggedIn()) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.create_card, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            default:
                super.onOptionsItemSelected(item);
        }

        return true;
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

        cardsList.clear();
        cardsList.addAll(list);
        cardsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void displayTagFilter(String tagName) {
        String text = getResources().getString(R.string.CARDS_LIST_tag_filter, tagName);
        tagFilterView.setText(text);
        MyUtils.show(tagFilterView);

        activateUpButton();
    }

    @Override
    public void deleteCardRequest(iDialogCallbacks.Delete callbacks) {
        Log.d(TAG, "deleteCardRequest()");

        String cardName = currentCard.getTitle();

        YesNoDialog yesNoDialog = new YesNoDialog(
                this,
                getResources().getString(R.string.DIALOG_deleted_card_name, cardName),
                null,
                callbacks
        );

        yesNoDialog.show();
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

        if (userLoggedIn()) {
            Drawable oldBackground = view.getBackground();
            view.setBackgroundColor(getResources().getColor(R.color.selected_list_item_bg));
            showPopupMenu(view, oldBackground);
        }

        return true;
    }


    // Выслывающее меню
    private void showPopupMenu(final View v, final Drawable oldBackground) {

        PopupMenu popupMenu = new PopupMenu(this, v);

        popupMenu.inflate(R.menu.edit_delete);

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
                presenter.deleteCard(currentCard);
                return true;

            default:
                return false;
        }
    }


    // Внутренние методы
    private void editCard() {
        Log.d(TAG, "editCard()");

        Intent intent = new Intent(this, CardEdit2_View.class);
        intent.setAction(Constants.ACTION_EDIT);
        intent.putExtra(Constants.CARD_KEY, currentCard.getKey());
        startActivity(intent);

        currentCard = null;
    }

}
