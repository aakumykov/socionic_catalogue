package ru.aakumykov.me.sociocat.card_show2.view_holders;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show2.iCardShow2;
import ru.aakumykov.me.sociocat.card_show2.list_items.LoadMore_Item;
import ru.aakumykov.me.sociocat.card_show2.list_items.iList_Item;

public class LoadMore_ViewHolder extends Base_ViewHolder {

    @BindView(R.id.loadMoreTextView) TextView loadMoreTextView;

    private final static String TAG = "LoadMore_ViewHolder";
    private iCardShow2.iPresenter presenter;
    private int position = -1;


    public LoadMore_ViewHolder(@NonNull View itemView, iCardShow2.iPresenter presenter) {
        super(itemView);
        this.presenter = presenter;
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void initialize(iList_Item listItem) {
        //Log.d(TAG, "initialize()");
        LoadMore_Item loadMoreItem = (LoadMore_Item) listItem;
        loadMoreTextView.setText(loadMoreItem.getTextId());
    }

    @OnClick(R.id.loadMoreView)
    void onLoadMoreClicked() {
        presenter.onLoadMoreClicked(this.position);
    }

    public void setPosition(int position) {
        //Log.d(TAG, "setPosition("+position+")");
        this.position = position;
    }
}
