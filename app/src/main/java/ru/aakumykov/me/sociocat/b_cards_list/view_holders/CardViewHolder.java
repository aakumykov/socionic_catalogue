package ru.aakumykov.me.sociocat.b_cards_list.view_holders;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils.ViewUtils;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_DataViewHolder;
import ru.aakumykov.me.sociocat.models.Card;

public abstract class CardViewHolder extends BasicMVPList_DataViewHolder {

    @BindView(R.id.elementView)
    ViewGroup elementView;

    @BindView(R.id.titleView)
    TextView titleView;

    @BindView(R.id.checkingOverlay)
    View checkingOverlay;

    @BindView(R.id.highlightingOverlay)
    View highlightingOverlay;


    public CardViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void displayIsChecked(boolean isChecked) {
        ViewUtils.setVisibility(checkingOverlay, isChecked);
    }

    @Override
    public void displayIsHighlighted(boolean isHighLighted) {
        ViewUtils.setVisibility(highlightingOverlay, isHighLighted);
    }

    @Override
    public void initialize(BasicMVPList_ListItem basicListItem) {

        BasicMVPList_DataItem dataItem = (BasicMVPList_DataItem) basicListItem;

        displayIsChecked(dataItem.isSelected());

        displayIsHighlighted(dataItem.isHighLighted());
    }



    @OnClick(R.id.elementView)
    void onCardClicked() {
        mItemClickListener.onItemClicked(this);
    }

    @OnLongClick(R.id.elementView)
    void onCardLongClicked() {
        mItemClickListener.onItemLongClicked(this);
    }


    protected Card extractCardFromListItem(BasicMVPList_ListItem basicListItem) {
        BasicMVPList_DataItem dataItem = (BasicMVPList_DataItem) basicListItem;
        return (Card) dataItem.getPayload();
    }

    protected abstract void showNoCardError();

    protected void displayCommonParts(@NonNull Card card) {
        titleView.setText(card.getTitle());
    }
}
