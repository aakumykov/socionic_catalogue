package ru.aakumykov.me.sociocat.cards_list2;

import android.content.Intent;
import android.os.Bundle;
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
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iDataAdapterPreparationCallback;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iPresenterPreparationCallback;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.BasicMVPUtils;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.ViewUtils;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.ListViewMode;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.cards_list2.interfaces.iCardsList2_View;
import ru.aakumykov.me.sociocat.eCardType;
import ru.aakumykov.me.sociocat.models.Card;

public class CardsList2_View extends BasicMVP_View implements iCardsList2_View {

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.tagFilterChip) Chip tagFilterChip;

    private BottomSheetListener mBottomSheetListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_list2_activity);
        ButterKnife.bind(this);

        configureBottomSheetListener();

        configureTagFilter();
    }

    @Override
    protected void processActivityResult() {

        switch (mActivityRequestCode) {
            case Constants.CODE_CREATE_CARD:
                processCardCreationResult();
                break;

            case Constants.CODE_SHOW_CARD:
            case Constants.CODE_EDIT_CARD:
                processCardShowOrEditionResult();
                break;

            default:
                break;
        }
    }

    @Override
    public void assembleMenu() {
        addSearchView();

        inflateMenu(R.menu.change_view_mode);
        inflateMenu(R.menu.tags);

        addSortByNameMenu();
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
                return new CardsList2_Presenter(new ListViewMode(), eBasicSortingMode.BY_NAME);
            }
        });
    }

    @Override
    protected BasicMVP_DataAdapter prepareDataAdapter() {
        return BasicMVPUtils.prepDataAdapter(mViewModel, new iDataAdapterPreparationCallback() {
            @Override
            public BasicMVP_DataAdapter onDataAdapterPrepared() {
                return new CardsList2_DataAdapter(mPresenter);
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
    public void showTagFilter(String tagName) {
        ViewUtils.show(tagFilterChip);
    }

    @Override
    public void hideTagFilter() {
        ViewUtils.hide(tagFilterChip);
    }


    @OnClick(R.id.floatingActionButton)
    void onFABClicked() {
        ((CardsList2_Presenter) mPresenter).onFABClicked();
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

    private void processCardShowOrEditionResult() {
        if (RESULT_OK == mActivityResultCode) {
            if (null != mActivityResultData) {
                Card oldCard = mActivityResultData.getParcelableExtra(Constants.OLD_CARD);
                Card newCard = mActivityResultData.getParcelableExtra(Constants.NEW_CARD);

                ((CardsList2_Presenter) mPresenter).onCardEdited(oldCard, newCard);
            }
        }
    }

    private void processCardCreationResult() {
        if (RESULT_OK == mActivityResultCode) {
            if (null != mActivityResultData) {
                Card newCard = mActivityResultData.getParcelableExtra(Constants.CARD);

                ((CardsList2_Presenter) mPresenter).onCardCreated(newCard);
            }
        }
    }
}
