package ru.aakumykov.me.sociocat.cards_list2.view_holders;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.models.Card;

public class CardViewHolder_Grid extends CardViewHolder {

    @BindView(R.id.cardTypeImageView) ImageView cardTypeImageView;

    public CardViewHolder_Grid(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initialize(BasicMVP_ListItem basicListItem) {
        super.initialize(basicListItem);

        Card card = extractCardFromListItem(basicListItem);
        if (null == card) {
            showNoCardError();
            return;
        }

        if (card.isTextCard())
            cardTypeImageView.setImageResource(R.drawable.ic_card_type_text_list);
        else if (card.isImageCard())
            cardTypeImageView.setImageResource(R.drawable.ic_card_type_image_list);
        else if (card.isAudioCard())
            cardTypeImageView.setImageResource(R.drawable.ic_card_type_audio_list);
        else if (card.isVideoCard())
            cardTypeImageView.setImageResource(R.drawable.ic_card_type_video_list);
    }

    @Override
    protected void showNoCardError() {

    }
}
