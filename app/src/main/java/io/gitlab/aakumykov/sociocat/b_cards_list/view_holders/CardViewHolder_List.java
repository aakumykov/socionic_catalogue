package io.gitlab.aakumykov.sociocat.b_cards_list.view_holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import butterknife.BindView;
import io.gitlab.aakumykov.sociocat.BuildConfig;
import io.gitlab.aakumykov.sociocat.R;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.utils.ViewUtils;
import io.gitlab.aakumykov.sociocat.models.Card;
import io.gitlab.aakumykov.sociocat.utils.CanonicalTagsHelper;

public class CardViewHolder_List extends CardViewHolder {

    @BindView(R.id.mediaView) ImageView cardTypeImageView;
    @BindView(R.id.canonicalTagsContainer) LinearLayout canonicalTagsContainer;
    @BindView(R.id.cardKeyView) TextView cardKeyView;


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

        displayCommonParts(card);
//        displayCardIcons(card);
        displayCardType(card);
        displayCardKey(card);
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

    private void displayCardKey(Card card) {
        if (BuildConfig.DEBUG)
            cardKeyView.setText(card.getKey());
        ViewUtils.setVisibility(cardKeyView, BuildConfig.DEBUG);
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
