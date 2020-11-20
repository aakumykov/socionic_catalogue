package ru.aakumykov.me.sociocat.cards_list2.view_holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_DataItem;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.ViewUtils;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_DataViewHolder;
import ru.aakumykov.me.sociocat.models.Card;

public class CardViewHolder extends BasicMVP_DataViewHolder {

    @BindView(R.id.elementView)
    View elementView;

    @BindView(R.id.checkMark)
    View checkMark;

    @BindView(R.id.titleView)
    TextView titleView;

    @BindView(R.id.highlightingOverlay)
    View highlightingOverlay;

    protected Card mCurrentCard;


    public CardViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void displayIsChecked(boolean isChecked) {
        ViewUtils.setVisibility(checkMark, isChecked);
    }

    @Override
    public void displayIsHighlighted(boolean isHighLighted) {
        ViewUtils.setVisibility(highlightingOverlay, isHighLighted);
    }

    @Override
    public void fillWithData(BasicMVP_ListItem basicListItem) {

        BasicMVP_DataItem dataItem = (BasicMVP_DataItem) basicListItem;

        mCurrentCard = extractCardFromListItem(basicListItem);

        titleView.setText(mCurrentCard.getTitle());

        displayIsChecked(dataItem.isSelected());

        displayIsHighlighted(dataItem.isHighLighted());
    }

    protected Card extractCardFromListItem(BasicMVP_ListItem basicListItem) {
        BasicMVP_DataItem dataItem = (BasicMVP_DataItem) basicListItem;
        return (Card) dataItem.getPayload();
    }


    @OnClick({R.id.elementView})
    void onCardClicked() {
        mItemClickListener.onItemClicked(this);
    }

}
