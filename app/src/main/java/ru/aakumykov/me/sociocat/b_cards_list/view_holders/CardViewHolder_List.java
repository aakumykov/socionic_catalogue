package ru.aakumykov.me.sociocat.b_cards_list.view_holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.util.List;

import butterknife.BindView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils.ViewUtils;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.CanonicalTagsHelper;

public class CardViewHolder_List extends CardViewHolder {

    @BindView(R.id.cardTypeImageView) ImageView cardTypeImageView;
    @BindView(R.id.canonicalTagsContainer) LinearLayout canonicalTagsContainer;

    private final static int ASPECT_ICON_SIZE = 24;
    private CanonicalTagsHelper mCanonicalTagsHelper;

    public CardViewHolder_List(@NonNull View itemView) {
        super(itemView);
        mCanonicalTagsHelper = CanonicalTagsHelper.getInstance(elementView.getContext());
    }

    @Override
    public void initialize(BasicMVPList_ListItem basicListItem) {
        super.initialize(basicListItem);

        Card card = extractCardFromListItem(basicListItem);
        if (null == card) {
            showNoCardError();
            return;
        }

        displayMainContent(card);
        displayCardIcons(card);
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

    private void displayCardIcons(@NonNull Card card) {

        List<String> tagsList = card.getTags();

        canonicalTagsContainer.removeAllViews();

        if (! mCanonicalTagsHelper.containsCanonicalTag(tagsList)) {
            ViewUtils.hide(canonicalTagsContainer);
            return;
        }

        for (String tagName : tagsList) {

            ImageView imageView = new ImageView(elementView.getContext());

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ASPECT_ICON_SIZE,
                    ASPECT_ICON_SIZE
            );

//            layoutParams.setMargins(
//                    DisplayUtils.dpToPx(4),
//                    DisplayUtils.dpToPx(4),
//                    DisplayUtils.dpToPx(4),
//                    DisplayUtils.dpToPx(4)
//            );

            imageView.setLayoutParams(layoutParams);

            imageView.setId(mCanonicalTagsHelper.getTagId(tagName));
            imageView.setImageResource(mCanonicalTagsHelper.getIconId(tagName));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            imageView.setAdjustViewBounds(true);

            elementView.addView(imageView);
            canonicalTagsContainer.addView(imageView);
        }

        ViewUtils.show(canonicalTagsContainer);
    }
}
