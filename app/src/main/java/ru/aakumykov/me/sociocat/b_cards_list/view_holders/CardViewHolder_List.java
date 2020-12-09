package ru.aakumykov.me.sociocat.b_cards_list.view_holders;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.models.Card;

public class CardViewHolder_List extends CardViewHolder {

    @BindView(R.id.cardTypeImageView) ImageView cardTypeImageView;

    public CardViewHolder_List(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initialize(BasicMVPList_ListItem basicListItem) {
        super.initialize(basicListItem);

        Card card = extractCardFromListItem(basicListItem);
        if (null == card) {
            showNoCardError();
            return;
        }

        displayCard(card);
        displayCardType(card);
    }

    @Override
    protected void showNoCardError() {

    }

    private void displayCardType(@NonNull Card card) {
        switch (card.getType()) {
            case Card.TEXT_CARD:
                cardTypeImageView.setImageResource(R.drawable.ic_card_type_text_list_mode);
                break;
            case Card.IMAGE_CARD:
                cardTypeImageView.setImageResource(R.drawable.ic_card_type_image_list_mode);
                break;
            case Card.VIDEO_CARD:
                cardTypeImageView.setImageResource(R.drawable.ic_card_type_youtube_video_list_mode);
                break;
            case Card.AUDIO_CARD:
                cardTypeImageView.setImageResource(R.drawable.ic_card_type_audio_list_mode);
                break;
            default:
                break;
        }
    }
}