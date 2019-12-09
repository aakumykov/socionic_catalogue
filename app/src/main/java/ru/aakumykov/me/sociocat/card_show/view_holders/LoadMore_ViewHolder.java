package ru.aakumykov.me.sociocat.card_show.view_holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.iCardShow;
import ru.aakumykov.me.sociocat.card_show.list_items.LoadMore_Item;
import ru.aakumykov.me.sociocat.card_show.list_items.iList_Item;

public class LoadMore_ViewHolder extends Base_ViewHolder {

    @BindView(R.id.loadMoreTextView) TextView loadMoreTextView;

    private final static String TAG = "LoadMore_ViewHolder";
    private iCardShow.iPresenter presenter;
    private iList_Item currentItem;

    public LoadMore_ViewHolder(@NonNull View itemView, iCardShow.iPresenter presenter) {
        super(itemView);
        this.presenter = presenter;
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void initialize(iList_Item listItem) {
        this.currentItem = listItem;
        LoadMore_Item loadMoreItem = (LoadMore_Item) listItem;
        loadMoreTextView.setText(loadMoreItem.getTextId());
    }

    @OnClick(R.id.loadMoreView)
    void onLoadMoreClicked() {
        presenter.onLoadMoreClicked(this.currentItem);
    }

}
