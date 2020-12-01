package ru.aakumykov.me.sociocat.cards_list2.view_holders;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.models.Card;

public class CardViewHolder_Grid extends CardViewHolder {

    @BindView(R.id.cardTypeImageView) ImageView cardTypeImageView;

    public CardViewHolder_Grid(@NonNull View itemView) {
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
        displatCardType(card);
    }

    @Override
    protected void showNoCardError() {

    }


    private void displatCardType(@NonNull Card card) {
        if (card.isTextCard())
            cardTypeImageView.setImageResource(R.drawable.ic_card_type_text_list_mode);
        else if (card.isImageCard())
            cardTypeImageView.setImageResource(R.drawable.ic_card_type_image_list_mode);
        else if (card.isAudioCard())
            cardTypeImageView.setImageResource(R.drawable.ic_card_type_audio_list);
        else if (card.isVideoCard())
            cardTypeImageView.setImageResource(R.drawable.ic_card_type_video_list);
    }
}
