package ru.aakumykov.me.sociocat.cards_list;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.cards_grid.CardsGrid_View;
import ru.aakumykov.me.sociocat.interfaces.iMyDialogs;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.utils.MyDialogs;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CardsList_View extends BaseView implements
        iCardsList.View,
        SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener,
        PopupMenu.OnMenuItemClickListener
{
    @BindView(R.id.swiperefresh) SwipeRefreshLayout swiperefreshLayout;
    @BindView(R.id.filterView) ConstraintLayout filterView;
    @BindView(R.id.filterCloser) ImageView filterCloser;
    @BindView(R.id.filterName) TextView filterName;
    @BindView(R.id.listView) ListView listView;
    @BindView(R.id.floatingActionButton) FloatingActionButton floatingActionButton;

    public final static String TAG = "CardsList_Fragment";
    private iCardsList.Presenter presenter;
    private Card currentCard;
    private boolean firstRun = true;
    private String tagFilter;

    private List<Card> cardsList;
    private CardsListAdapter cardsListAdapter;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_list_activity);
        ButterKnife.bind(this);

        setTitle(R.string.CARDS_LIST_page_title);

        Intent intent = getIntent();
        if (null != intent) {
            this.tagFilter = intent.getStringExtra(Constants.TAG_FILTER);
            if (null != this.tagFilter) {
                activateUpButton();
            }
        }
        
        swiperefreshLayout.setOnRefreshListener(this);
        swiperefreshLayout.setColorSchemeResources(R.color.blue_swipe, R.color.green_swipe, R.color.orange_swipe, R.color.red_swipe);

        cardsList = new ArrayList<>();
        cardsListAdapter = new CardsListAdapter(this, R.layout.cards_list_item, cardsList);
        listView.setAdapter(cardsListAdapter);

        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listView.setLongClickable(true);

        presenter = new CardsList_Presenter();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        presenter.linkView(this);

        if (firstRun) {
            loadList(true);
            firstRun = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

//        menuInflater.inflate(R.menu.create_card, menu);
        menuInflater.inflate(R.menu.grid_view, menu);
        menuInflater.inflate(R.menu.tags, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionGridView:
                goGridView();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {

            case Constants.CODE_CREATE_CARD:
                onCardCreated(resultCode, data);
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Обязательные методы
    @Override
    public void onUserLogin() {
        MyUtils.show(floatingActionButton);
    }

    @Override
    public void onUserLogout() {
        MyUtils.hide(floatingActionButton);
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
        this.tagFilter = null;
        presenter.loadList(null);
    }

    @OnClick(R.id.floatingActionButton)
    void addCard() {
        goCreateCard();
    }

    // Нажатия в списке
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Card card = cardsList.get(position);
        Intent intent = new Intent(this, CardShow_View.class);
        intent.putExtra(Constants.CARD_KEY, card.getKey());
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//        Log.d(TAG, "onItemLongClick(pos: "+position+", id: "+id+")");
        currentCard = cardsList.get(position);

        if (AuthSingleton.getInstance().isUserLoggedIn()) {
            Drawable oldBackground = view.getBackground();
            view.setBackgroundColor(getResources().getColor(R.color.selected_list_item_bg));
            showPopupMenu(view, currentCard, oldBackground);
        }

        return true;
    }


    // Выслывающее меню
    private void showPopupMenu(final View view, final Card card, final Drawable oldBackground) {

        PopupMenu popupMenu = new PopupMenu(this, view);

        if (AuthSingleton.getInstance().isAdmin() || AuthSingleton.getInstance().isCardOwner(card)) {
            popupMenu.inflate(R.menu.edit);
            popupMenu.inflate(R.menu.delete);
        }

        popupMenu.inflate(R.menu.share);

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu popupMenu) {
                view.setBackground(oldBackground);
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
                deleteCardQuestion();
                return true;

            default:
                return false;
        }
    }


    // Внутренние методы
    private void editCard() {
        Intent intent = new Intent(this, CardEdit_View.class);
        intent.setAction(Constants.ACTION_EDIT);
        intent.putExtra(Constants.CARD, currentCard);
        intent.putExtra(Constants.CARD_KEY, currentCard.getKey());
        startActivityForResult(intent, Constants.CODE_EDIT_CARD);
    }

    private void deleteCardQuestion() {
        String cardName = currentCard.getTitle();
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
                presenter.deleteCard(currentCard);
            }
        });
    }

    private void loadList(boolean showProgressBar) {
        if (showProgressBar) {
            showInfoMsg(R.string.CARDS_LIST_loading_list);
            showProgressBar();
        }
        presenter.loadList(tagFilter);
    }

    @Override
    public void processCardCreationResult(Intent data) {

        if (null != data) {

            Card card = data.getParcelableExtra(Constants.CARD);

            if (null != card) {
                showToast(R.string.INFO_card_created);
                addListItem(card);
//                int position = cardsListAdapter.getPosition(card_edit);
//                int count = cardsListAdapter.getCount();
//                int size = listView.getChildCount();
//                listView.smoothScrollToPosition(position);

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

    private void onCardCreated(int resultCode, @Nullable Intent data) {

        switch (resultCode) {
            case RESULT_OK:
                showToast(R.string.INFO_card_created);
                if (null != data) {
                    Card card = data.getParcelableExtra(Constants.CARD);
                    if (null != card) {
                        cardsList.add(card);
                        cardsListAdapter.notifyDataSetChanged();
                    }
                }
                break;

            case RESULT_CANCELED:
                showToast(R.string.INFO_operation_cancelled);
                break;

            default:
                showErrorMsg(R.string.ERROR_creating_card);
                Log.d(TAG, "data: "+data);
                break;
        }
    }

    private void goGridView() {
        Intent intent = new Intent(this, CardsGrid_View.class);
        startActivity(intent);
    }
}
