package ru.aakumykov.me.sociocat.b_cards_list;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.kennyc.bottomsheet.BottomSheetListener;
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.BasicMVPList_DataAdapter;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.BasicMVPList_Presenter;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.BasicMVPList_View;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.enums.eBasicSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicViewState;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iDataAdapterPreparationCallback;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iPresenterPreparationCallback;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils.BasicMVPList_Utils;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils.TextUtils;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils.ViewUtils;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils.builders.SortingMenuItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.ListViewMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states.ItemsSelectedViewState;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states.ProgressViewState;
import ru.aakumykov.me.sociocat.b_cards_list.enums.eCardsList_SortingMode;
import ru.aakumykov.me.sociocat.b_cards_list.interfaces.iCardsList_View;
import ru.aakumykov.me.sociocat.b_cards_list.view_states.CardsList_ViewState;
import ru.aakumykov.me.sociocat.b_cards_list.view_states.CardsWithTag_ViewState;
import ru.aakumykov.me.sociocat.b_cards_list.view_states.LoadingCardsWithTag_ViewState;
import ru.aakumykov.me.sociocat.b_cards_list.view_states.LoadingCards_ViewState;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.eCardType;
import ru.aakumykov.me.sociocat.models.Card;

public class CardsList_View extends BasicMVPList_View implements iCardsList_View {

    private static final BasicViewMode DEFAULT_VIEW_MODE = new ListViewMode();

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.tagFilter) Chip tagFilterChip;

    private static final String TAG = CardsList_View.class.getSimpleName();
    private BottomSheetListener mBottomSheetListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configureBottomSheetListener();

        configureTagFilter();
    }

    @Override
    protected void setActivityView() {
        setContentView(R.layout.cards_list_activity);
        ButterKnife.bind(this);
    }

    @Override
    public void processActivityResult() {

        switch (mActivityRequestCode) {

            case Constants.CODE_CREATE_CARD:
                processCardCreationResult();
                break;

            case Constants.CODE_SHOW_CARD:
                processCardShowResult();
                break;

            default:
                super.processActivityResult();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (R.id.actionSortByComments == id) {
            mPresenter.onSortMenuItemClicked(eCardsList_SortingMode.BY_COMMENTS);
        }
        else if (R.id.actionSortByAuthor == id) {
            mPresenter.onSortMenuItemClicked(eCardsList_SortingMode.BY_AUTHOR);
        }
        else if (R.id.actionSortByRating == id) {
            mPresenter.onSortMenuItemClicked(eCardsList_SortingMode.BY_RATING);
        }
        else if (R.id.actionEdit == id) {
            ((CardsList_Presenter) mPresenter).onEditCardClicked();
        }
        else if (R.id.actionDelete == id) {
            ((CardsList_Presenter) mPresenter).onDeleteMenuItemClicked();
        }
        else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void assembleMenu() {
        Log.d("assembleMenu", "assembleMenu()");

        addSearchView();

        addSingleItemMenu(R.menu.tags, R.id.actionTags, MenuItem.SHOW_AS_ACTION_NEVER);

        addSortByNameMenu();
        addSortByDateMenu();
        addSortByCommentsMenu();
        addSortByRatingMenu();
        addSortByAuthorMenu();

        inflateMenu(R.menu.change_view_mode);

        addProfileMenuItem();
        addPreferencesMenuItem();

        addAuthorizationMenu();
    }

    @Override
    public RecyclerView.ItemDecoration prepareItemDecoration(BasicViewMode viewMode) {
        return createItemDecoration(viewMode);
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    protected int getColumnsCountForGridLayout(int orientation) {
        return (Configuration.ORIENTATION_PORTRAIT == orientation) ? 3 : 5;
    }

    @Override
    protected BasicMVPList_Presenter preparePresenter() {
        return BasicMVPList_Utils.prepPresenter(mViewModel, new iPresenterPreparationCallback() {
            @Override
            public BasicMVPList_Presenter onPresenterPrepared() {
                return new CardsList_Presenter(DEFAULT_VIEW_MODE, eBasicSortingMode.BY_DATE);
            }
        });
    }

    @Override
    protected BasicMVPList_DataAdapter prepareDataAdapter() {
        return BasicMVPList_Utils.prepDataAdapter(mViewModel, new iDataAdapterPreparationCallback() {
            @Override
            public BasicMVPList_DataAdapter onDataAdapterPrepared() {
                return new CardsList_DataAdapter(mPresenter.getCurrentViewMode(), mPresenter);
            }
        });
    }

    @Override
    public void setDefaultPageTitle() {
        setPageTitle(R.string.CARDS_LIST_page_title);
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    public void goShowingCard(@NonNull Card card) {
        Intent intent = new Intent(this, CardShow_View.class);
        intent.putExtra(Constants.CARD_KEY, card.getKey());
        intent.setAction(Intent.ACTION_VIEW);
        startActivityForResult(intent, Constants.CODE_SHOW_CARD);
    }

    @Override
    public void showAddNewCardMenu() {
        new BottomSheetMenuDialogFragment.Builder(this, R.style.MyBottomSheetMenuStyleForLight)
                .setSheet(R.menu.add_new_card_bottom_sheet)
                .setTitle(R.string.add_new_card_bottom_sheet_title)
                .setListener(mBottomSheetListener)
                .show(getSupportFragmentManager());
    }

    @Override
    public void goCreateCard(eCardType cardType) {
        Intent intent = new Intent(this, CardEdit_View.class);
        intent.setAction(Intent.ACTION_CREATE_DOCUMENT);
        intent.putExtra(Constants.CARD_TYPE, cardType.name());

        startActivityForResult(intent, Constants.CODE_CREATE_CARD);
    }

    @Override
    public void goShowAllCards() {
        Intent intent = new Intent(this, CardsList_View.class);
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);
    }

    @Override
    public void setViewState(iBasicViewState viewState) {

        if (viewState instanceof LoadingCards_ViewState) {
            setLoadingCardsViewState((LoadingCards_ViewState) viewState);
        }
        else if (viewState instanceof CardsList_ViewState) {
            setCardsWithoutTagViewState((CardsList_ViewState) viewState);
        }
        else if (viewState instanceof LoadingCardsWithTag_ViewState) {
            setLoadingCardsWithTagViewState((LoadingCardsWithTag_ViewState) viewState);
        }
        else if (viewState instanceof CardsWithTag_ViewState) {
            setCardsWithTagViewState((CardsWithTag_ViewState) viewState);
        }
        else if (viewState instanceof ItemsSelectedViewState) {
            setItemSelectedViewState((ItemsSelectedViewState) viewState);
        }
        else {
            super.setViewState(viewState);
        }
    }

    @Override
    public int getListScrollOffset() {
        return mRecyclerView.computeVerticalScrollOffset();
    }

    @Override
    public void setListScrollOffset(int verticalOffset) {
        mRecyclerView.scrollBy(0, verticalOffset);
    }

    @Override
    protected void setNeutralViewState() {
        super.setNeutralViewState();
        hideTagFilter();
    }


    private void setLoadingCardsViewState(LoadingCards_ViewState loadingCardsWithoutTagViewState) {

        setRefreshingViewState();

        if (loadingCardsWithoutTagViewState.isHasParent())
            activateUpButton();
    }

    private void setCardsWithoutTagViewState(CardsList_ViewState cardsWithoutTagViewState) {
        setNeutralViewState();
        if (cardsWithoutTagViewState.isDisplayBackButton())
            activateUpButton();
    }

    protected void setLoadingCardsWithTagViewState(LoadingCardsWithTag_ViewState loadingCardsWithTagViewState) {
        activateUpButton();

        String tagName = loadingCardsWithTagViewState.getTagName();
        String msg = TextUtils.getText(this, R.string.CARDS_LIST_loading_cards_with_tag, tagName);
        setProgressViewState(new ProgressViewState(msg));
    }

    protected void setCardsWithTagViewState(CardsWithTag_ViewState cardsWithTagViewState) {
        setNeutralViewState();
        activateUpButton();

        String tagName = cardsWithTagViewState.getTagName();
        String msg = TextUtils.getText(this, R.string.CARDS_LIST_cards_with_tag, tagName);
        setPageTitle(msg);

        showTagFilter(msg);
    }

    @Override
    protected void setItemSelectedViewState(ItemsSelectedViewState selectionViewState) {
        super.setItemSelectedViewState(selectionViewState);

        boolean canEditCard = ((CardsList_Presenter) mPresenter).canEditCard();
        boolean canDeleteCard = ((CardsList_Presenter) mPresenter).canDeleteCard();
        boolean cardsAreSelected = selectionViewState.getSelectedItemsCount() > 0;
        boolean singleCardSelected = 1 == selectionViewState.getSelectedItemsCount();

        if (canEditCard || canDeleteCard) {

            if (singleCardSelected && canEditCard) {
                inflateMenu(R.menu.edit);
                makeMenuItemVisible(R.id.actionEdit, R.drawable.ic_edit);
            }

            if (cardsAreSelected && canDeleteCard) {
                inflateMenu(R.menu.delete);
                makeMenuItemVisible(R.id.actionDelete, R.drawable.ic_delete);
            }

            refreshMenu();
        }
        else {
            super.setItemSelectedViewState(selectionViewState);
        }
    }


    protected void addSortByCommentsMenu() {

        addSortingMenuRootIfNotExists();

        new SortingMenuItem.Builder()
                .addMenuInflater(mMenuInflater)
                .addRootMenu(mSortingSubmenu)
                .addInflatedMenuResource(R.menu.sort_by_comments)
                .addInflatedMenuItemId(R.id.actionSortByComments)
                .addSortingMode(mPresenter.getCurrentSortingMode())
                .addSortingOrder(mPresenter.getCurrentSortingOrder())
                .addSortingModeParamsCallback(new SortingMenuItem.iSortingModeParamsCallback() {
                    @Override
                    public boolean isSortingModeComplains(iSortingMode sortingMode) {
                        return sortingMode instanceof eCardsList_SortingMode;
                    }

                    @Override
                    public boolean isSortingModeActive(iSortingMode sortingMode) {
                        return eCardsList_SortingMode.BY_COMMENTS.equals(sortingMode);
                    }
                })
                .create();
    }

    protected void addSortByRatingMenu() {

        addSortingMenuRootIfNotExists();

        new SortingMenuItem.Builder()
                .addMenuInflater(mMenuInflater)
                .addRootMenu(mSortingSubmenu)
                .addInflatedMenuResource(R.menu.sort_by_rating)
                .addInflatedMenuItemId(R.id.actionSortByRating)
                .addSortingMode(mPresenter.getCurrentSortingMode())
                .addSortingOrder(mPresenter.getCurrentSortingOrder())
                .addSortingModeParamsCallback(new SortingMenuItem.iSortingModeParamsCallback() {
                    @Override
                    public boolean isSortingModeComplains(iSortingMode sortingMode) {
                        return sortingMode instanceof eCardsList_SortingMode;
                    }

                    @Override
                    public boolean isSortingModeActive(iSortingMode sortingMode) {
                        return eCardsList_SortingMode.BY_RATING.equals(sortingMode);
                    }
                })
                .create();
    }

    protected void addSortByAuthorMenu() {

        addSortingMenuRootIfNotExists();

        new SortingMenuItem.Builder()
                .addMenuInflater(mMenuInflater)
                .addRootMenu(mSortingSubmenu)
                .addInflatedMenuResource(R.menu.sort_by_author)
                .addInflatedMenuItemId(R.id.actionSortByAuthor)
                .addSortingMode(mPresenter.getCurrentSortingMode())
                .addSortingOrder(mPresenter.getCurrentSortingOrder())
                .addSortingModeParamsCallback(new SortingMenuItem.iSortingModeParamsCallback() {
                    @Override
                    public boolean isSortingModeComplains(iSortingMode sortingMode) {
                        return sortingMode instanceof eCardsList_SortingMode;
                    }

                    @Override
                    public boolean isSortingModeActive(iSortingMode sortingMode) {
                        return eCardsList_SortingMode.BY_AUTHOR.equals(sortingMode);
                    }
                })
                .create();
    }



    private void configureBottomSheetListener() {

        mBottomSheetListener = new BottomSheetListener() {
            @Override
            public void onSheetShown(@NotNull BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, @org.jetbrains.annotations.Nullable Object o) {

            }

            @Override
            public void onSheetItemSelected(
                    @NotNull BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment,
                    @NotNull MenuItem menuItem,
                    @Nullable Object o
            ) {
                ((CardsList_Presenter) mPresenter).onAddNewCardClicked(menuItem.getItemId());
            }

            @Override
            public void onSheetDismissed(@NotNull BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, @org.jetbrains.annotations.Nullable Object o, int i) {

            }
        };
    }

    private void configureTagFilter() {
        tagFilterChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CardsList_Presenter) mPresenter).onCloseTagFilterClicked();
            }
        });
    }

    private void processCardCreationResult() {
        if (RESULT_OK == mActivityResultCode) {
            if (null != mActivityResultData) {
                Card newCard = mActivityResultData.getParcelableExtra(Constants.CARD);

                ((CardsList_Presenter) mPresenter).onCardCreated(newCard);
            }
        }
    }

    private void processCardShowResult() {
        if (RESULT_OK != mActivityResultCode)
            return;

        if (null == mActivityResultData)
            return;

        String action = mActivityResultData.getAction();
        if (null == action)
            return;

        switch (action) {
            case Intent.ACTION_VIEW:
            case Constants.ACTION_EDIT:
                processCardShowOrEdition();
                break;

            case Intent.ACTION_DELETE:
                processCardDeletion();
                return;

            default:
                break;
        }
    }

    private void processCardShowOrEdition() {
        Card oldCard = mActivityResultData.getParcelableExtra(Constants.OLD_CARD);
        Card newCard = mActivityResultData.getParcelableExtra(Constants.NEW_CARD);

        ((CardsList_Presenter) mPresenter).onCardEdited(oldCard, newCard);
    }

    private void processCardDeletion() {
        Card card = mActivityResultData.getParcelableExtra(Constants.CARD);

        ((CardsList_Presenter) mPresenter).onCardDeleted(card);
    }

    private void showTagFilter(String tagName) {
        tagFilterChip.setText(tagName);
        ViewUtils.show(tagFilterChip);
    }

    private void hideTagFilter() {
        tagFilterChip.setText("");
        ViewUtils.hide(tagFilterChip);
    }


    @OnClick(R.id.floatingActionButton)
    void onFABClicked() {
        ((CardsList_Presenter) mPresenter).onFABClicked();
    }

}
