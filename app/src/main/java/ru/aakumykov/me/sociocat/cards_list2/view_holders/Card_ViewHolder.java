package ru.aakumykov.me.sociocat.cards_list2.view_holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_DataItem;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.ViewUtils;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_DataViewHolder;
import ru.aakumykov.me.sociocat.models.Card;

public abstract class Card_ViewHolder extends BasicMVP_DataViewHolder {

    @BindView(R.id.elementView) View elementView;
    @BindView(R.id.checkMark) View checkMark;
    @BindView(R.id.titleView) TextView titleView;
    @Nullable @BindView(R.id.cardTypeImageView) ImageView cardTypeImageView;
    @BindView(R.id.highlightingOverlay) View highlightingOverlay;

    protected Card mCurrentCard;
    protected BasicMVP_DataItem mCurrentDataItem;


    public Card_ViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }


    @Override
    public void fillWithData(BasicMVP_ListItem basicListItem) {

        mCurrentDataItem = (BasicMVP_DataItem) basicListItem;
        mCurrentCard = (Card) mCurrentDataItem.getPayload();

        titleView.setText(mCurrentCard.getTitle());

        displayIsChecked(mCurrentDataItem.isSelected());
        displayIsHighlighted(mCurrentDataItem.isHighLighted());
    }

    @Override
    public void displayIsChecked(boolean isChecked) {
        ViewUtils.setVisibility(checkMark, isChecked);
    }

    @Override
    public void displayIsHighlighted(boolean isHighLighted) {
        ViewUtils.setVisibility(highlightingOverlay, isHighLighted);
    }

}
