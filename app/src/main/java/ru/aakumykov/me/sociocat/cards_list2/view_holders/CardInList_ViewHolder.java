package ru.aakumykov.me.sociocat.cards_list2.view_holders;

import android.view.View;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.models.Card;

public class CardInList_ViewHolder extends Card_ViewHolder {

    public CardInList_ViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void fillWithData(BasicMVP_ListItem basicListItem) {
        super.fillWithData(basicListItem);
        displayCardType(mCurrentCard);
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
