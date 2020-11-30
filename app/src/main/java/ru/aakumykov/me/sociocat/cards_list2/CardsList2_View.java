package ru.aakumykov.me.sociocat.cards_list2;

import android.content.Intent;
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
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.BasicMVP_DataAdapter;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.BasicMVP_Presenter;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.BasicMVP_View;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eBasicSortingMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.helpers.SortingMenuItemConstructor;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicViewState;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iDataAdapterPreparationCallback;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iPresenterPreparationCallback;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.BasicMVPUtils;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.TextUtils;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.ViewUtils;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.FeedViewMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_states.ProgressViewState;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.cards_list2.enums.eCardsList2_SortingMode;
import ru.aakumykov.me.sociocat.cards_list2.interfaces.iCardsList2_View;
import ru.aakumykov.me.sociocat.cards_list2.view_states.CardsWithTag_ViewState;
import ru.aakumykov.me.sociocat.cards_list2.view_states.CardsWithoutTag_ViewState;
import ru.aakumykov.me.sociocat.cards_list2.view_states.LoadingCardsWithTag_ViewState;
import ru.aakumykov.me.sociocat.cards_list2.view_states.LoadingCards_ViewState;
import ru.aakumykov.me.sociocat.eCardType;
import ru.aakumykov.me.sociocat.models.Card;

public class CardsList2_View extends BasicMVP_View implements iCardsList2_View {

    private static final BasicViewMode DEFAULT_VIEW_MODE = new FeedViewMode();

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.tagFilter) Chip tagFilterChip;

    private static final String TAG = CardsList2_View.class.getSimpleName();
    private BottomSheetListener mBottomSheetListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configureBottomSheetListener();

        configureTagFilter();
    }

    @Override
    protected void setActivityView() {
        setContentView(R.layout.cards_list2_activity);
        ButterKnife.bind(this);
    }

    @Override
    protected void processActivityResult() {

        switch (mActivityRequestCode) {
            case Constants.CODE_CREATE_CARD:
                processCardCreationResult();
                break;

            case Constants.CODE_SHOW_CARD:
                processCardShowResult();
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (R.id.actionSortByComments == id) {
            mPresenter.onSortMenuItemClicked(eCardsList2_SortingMode.BY_COMMENTS);
        }
        /*else if (R.id.actionSortBy == id || R.id.actionSortByCommentsReverse == id) {

        }
        else if (R.id.actionSortByCommentsDirect == id || R.id.actionSortByCommentsReverse == id) {

        }*/
        else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void assembleMenu() {
        Log.d("assembleMenu", "assembleMenu()");

        addSearchView();

        inflateMenu(R.menu.change_view_mode);
        inflateMenu(R.menu.tags);

        addSortByNameMenu();
        addSortByCommentsMenu();
        addSortByRatingMenu();
        addSortByAuthorMenu();

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
    protected BasicMVP_Presenter preparePresenter() {
        return BasicMVPUtils.prepPresenter(mViewModel, new iPresenterPreparationCallback() {
            @Override
            public BasicMVP_Presenter onPresenterPrepared() {
                return new CardsList2_Presenter(DEFAULT_VIEW_MODE, eBasicSortingMode.BY_NAME);
            }
        });
    }

    @Override
    protected BasicMVP_DataAdapter prepareDataAdapter() {
        return BasicMVPUtils.prepDataAdapter(mViewModel, new iDataAdapterPreparationCallback() {
            @Override
            public BasicMVP_DataAdapter onDataAdapterPrepared() {
                return new CardsList2_DataAdapter(mPresenter.getCurrentViewMode(), mPresenter);
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
        Intent intent = new Intent(this, CardsList2_View.class);
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);
    }

    @Override
    public void setViewState(iBasicViewState viewState) {

        if (viewState instanceof LoadingCards_ViewState) {
            setLoadingCardsViewState((LoadingCards_ViewState) viewState);
        }
        else if (viewState instanceof CardsWithoutTag_ViewState) {
            setCardsWithoutTagViewState((CardsWithoutTag_ViewState) viewState);
        }
        else if (viewState instanceof LoadingCardsWithTag_ViewState) {
            setLoadingCardsWithTagViewState((LoadingCardsWithTag_ViewState) viewState);
        }
        else if (viewState instanceof CardsWithTag_ViewState) {
            setCardsWithTagViewState((CardsWithTag_ViewState) viewState);
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

    private void setCardsWithoutTagViewState(CardsWithoutTag_ViewState cardsWithoutTagViewState) {
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


    protected void addSortByCommentsMenu() {

        addSortingMenuRootIfNotExists();

        new SortingMenuItemConstructor()
                .addMenuInflater(mMenuInflater)
                .addTargetMenu(mSortingSubmenu)
                .addMenuResource(R.menu.menu_sort_by_comments)
                .addDirectOrderMenuItemId(R.id.actionSortByComments)
                .addReverseOrderMenuItemId(R.id.actionSortByCommentsReverse)
                .addDirectOrderActiveIcon(R.drawable.ic_menu_sort_by_comments_count_direct)
                .addReverseOrderActiveIcon(R.drawable.ic_menu_sort_by_comments_count_reverse_active)
                .addDirectOrderInactiveIcon(R.drawable.ic_menu_sort_by_comments_count_direct)
                .addSortingModeParamsCallback(new SortingMenuItemConstructor.iSortingModeParamsCallback() {
                    @Override
                    public boolean isSortingModeComplains(iSortingMode sortingMode) {
                        return sortingMode instanceof eCardsList2_SortingMode;
                    }

                    @Override
                    public boolean isSortingModeActive(iSortingMode sortingMode) {
                        switch ((eCardsList2_SortingMode) sortingMode) {
                            case BY_COMMENTS:
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public boolean isDirectOrder(eSortingOrder sortingOrder) {
                        return sortingOrder.isDirect();
                    }
                })
                .makeMenuItem(mPresenter.getCurrentSortingMode(), mPresenter.getCurrentSortingOrder());
    }

    protected void addSortByRatingMenu() {

    }

    protected void addSortByAuthorMenu() {

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
                ((CardsList2_Presenter) mPresenter).onAddNewCardClicked(menuItem.getItemId());
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
                ((CardsList2_Presenter) mPresenter).onCloseTagFilterClicked();
            }
        });
    }

    private void processCardCreationResult() {
        if (RESULT_OK == mActivityResultCode) {
            if (null != mActivityResultData) {
                Card newCard = mActivityResultData.getParcelableExtra(Constants.CARD);

                ((CardsList2_Presenter) mPresenter).onCardCreated(newCard);
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

        ((CardsList2_Presenter) mPresenter).onCardEdited(oldCard, newCard);
    }

    private void processCardDeletion() {
        Card card = mActivityResultData.getParcelableExtra(Constants.CARD);

        ((CardsList2_Presenter) mPresenter).onCardDeleted(card);
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
        ((CardsList2_Presenter) mPresenter).onFABClicked();
    }

}
