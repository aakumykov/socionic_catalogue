package ru.aakumykov.me.sociocat.cards_list2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.BasicMVP_DataAdapter;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.BasicMVP_Presenter;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.BasicMVP_View;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eBasicSortingMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iDataAdapterPreparationCallback;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iPresenterPreparationCallback;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.BasicMVPUtils;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.ListViewMode;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.cards_list2.interfaces.iCardsList2_View;
import ru.aakumykov.me.sociocat.models.Card;

public class CardsList2_View extends BasicMVP_View implements iCardsList2_View {

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_list2_activity);
        ButterKnife.bind(this);
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
    protected void processActivityResult() {

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
}
