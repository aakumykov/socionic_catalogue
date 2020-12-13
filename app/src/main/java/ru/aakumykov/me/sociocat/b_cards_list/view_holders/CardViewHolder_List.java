package ru.aakumykov.me.sociocat.b_cards_list.view_holders;

import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Flow;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

import butterknife.BindView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils.ViewUtils;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.CanonicalTagsHelper;
import ru.aakumykov.me.sociocat.utils.DisplayUtils;

public class CardViewHolder_List extends CardViewHolder {

    @BindView(R.id.cardTypeImageView) ImageView cardTypeImageView;
    @BindView(R.id.canonicalTagsFlowHelper) Flow canonicalTagsFlowHelper;

    private final static int ASPECT_ICON_SIZE = 12;
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

        removeOldTags();

        if (! mCanonicalTagsHelper.containsCanonicalTag(tagsList)) {
            ViewUtils.hide(canonicalTagsFlowHelper);
            return;
        }

        for (String tagName : tagsList) {

            ImageView imageView = new ImageView(elementView.getContext());

            float density = Resources.getSystem().getDisplayMetrics().density;

            imageView.setLayoutParams(new ConstraintLayout.LayoutParams(
                    DisplayUtils.dpToPx(ASPECT_ICON_SIZE),
                    DisplayUtils.dpToPx(ASPECT_ICON_SIZE)
            ));

            imageView.setId(mCanonicalTagsHelper.getTagId(tagName));
            imageView.setImageResource(mCanonicalTagsHelper.getIconId(tagName));
            imageView.setAdjustViewBounds(true);

            elementView.addView(imageView);
            canonicalTagsFlowHelper.addView(imageView);
        }

        ViewUtils.show(canonicalTagsFlowHelper);
    }

    private void removeOldTags() {

        canonicalTagsFlowHelper.setReferencedIds(new int[]{});

        for (String tagName : mCanonicalTagsHelper.getTagNames())
        {
            int tagId = mCanonicalTagsHelper.getTagId(tagName);
            View tagView = elementView.findViewById(tagId);
            while (null != tagView) {
                elementView.removeView(tagView);
                tagView = elementView.findViewById(tagId);
            }
        }
    }
}
