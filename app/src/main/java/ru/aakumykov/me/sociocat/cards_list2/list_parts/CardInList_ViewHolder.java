package ru.aakumykov.me.sociocat.cards_list2.list_parts;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_DataItem;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.ViewUtils;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_DataViewHolder;
import ru.aakumykov.me.sociocat.models.Card;

public class CardInList_ViewHolder extends BasicMVP_DataViewHolder {

    @BindView(R.id.elementView)
    View elementView;

    @BindView(R.id.checkMark)
    View checkMark;

    @BindView(R.id.titleView)
    TextView titleView;

    @BindView(R.id.cardTypeImageView)
    ImageView cardTypeImageView;

    @BindView(R.id.highlightingOverlay)
    View highlightingOverlay;


    public CardInList_ViewHolder(@NonNull View itemView) {
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
//        Card_ListItem cardListItem = (Card_ListItem) dataItem.getPayload();
        Card card = (Card) dataItem.getPayload();

        titleView.setText(card.getTitle());

        displayCardType(card);

        displayIsChecked(dataItem.isSelected());
        displayIsHighlighted(dataItem.isHighLighted());
    }

    private void displayCardType(@NonNull Card card) {
        switch (card.getType()) {
            case Card.TEXT_CARD:
                cardTypeImageView.setImageResource(R.drawable.ic_card_type_text_list);
                break;
            case Card.IMAGE_CARD:
                cardTypeImageView.setImageResource(R.drawable.ic_card_type_image_list);
                break;
            case Card.VIDEO_CARD:
                cardTypeImageView.setImageResource(R.drawable.ic_card_type_video_list);
                break;
            case Card.AUDIO_CARD:
                cardTypeImageView.setImageResource(R.drawable.ic_card_type_audio_list);
                break;
            default:
                break;
        }
    }
}
