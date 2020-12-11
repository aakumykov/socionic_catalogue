package ru.aakumykov.me.sociocat.b_cards_list.view_holders;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Flow;

import java.util.List;

import butterknife.BindView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils.ViewUtils;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.AspectsHelper;

public class CardViewHolder_List extends CardViewHolder {

    @BindView(R.id.cardTypeImageView) ImageView cardTypeImageView;
    @BindView(R.id.aspectsContainer) ViewGroup aspectsContainer;
    @BindView(R.id.aspectsContainerFlowHelper) Flow aspectsContainerFlowHelper;

    private final static int ASPECT_ICON_SIZE = 12;

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

        if (!AspectsHelper.containsAspectTag(tagsList)) {
            ViewUtils.hide(aspectsContainer);
            ViewUtils.hide(aspectsContainerFlowHelper);
            return;
        }

        aspectsContainer.removeAllViews();
        aspectsContainerFlowHelper.setReferencedIds(new int[]{});

        for (String tagName : tagsList) {

            if (AspectsHelper.isAspectTag(tagName)) {

                ImageView imageView = new ImageView(elementView.getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ASPECT_ICON_SIZE, ASPECT_ICON_SIZE));
                imageView.setAdjustViewBounds(true);

                switch (tagName) {
                    case AspectsHelper.LOGIC:
                        imageView.setImageResource(R.drawable.aspect_logic);
                        break;

                    case AspectsHelper.EMOTION:
                        imageView.setImageResource(R.drawable.aspect_emotion);
                        break;

                    case AspectsHelper.INTUITION:
                        imageView.setImageResource(R.drawable.aspect_intuition);
                        break;

                    case AspectsHelper.SENCE:
                        imageView.setImageResource(R.drawable.aspect_sence);
                        break;

                    case AspectsHelper.FORCE:
                        imageView.setImageResource(R.drawable.aspect_force);
                        break;

                    case AspectsHelper.TIME:
                        imageView.setImageResource(R.drawable.aspect_time);
                        break;

                    case AspectsHelper.RELATION:
                        imageView.setImageResource(R.drawable.aspect_relation);
                        break;

                    case AspectsHelper.PRACTIVE:
                        imageView.setImageResource(R.drawable.aspect_practice);
                        break;

                    default:
                        throw new RuntimeException("Не найдено соответствие инфоаспекту для метки '"+tagName+"'");
                }

                imageView.setId(View.generateViewId());

                aspectsContainer.addView(imageView);
                aspectsContainerFlowHelper.addView(imageView);
            }
        }

        ViewUtils.show(aspectsContainer);
        ViewUtils.show(aspectsContainerFlowHelper);
    }

    private void addTagImageToContainer(@NonNull String tagName) {
        ImageView imageView = new ImageView(elementView.getContext());
//        imageView.setLayoutParams();
    }
}
