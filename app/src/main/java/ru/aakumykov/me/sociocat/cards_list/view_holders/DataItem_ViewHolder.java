package ru.aakumykov.me.sociocat.cards_list.view_holders;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Optional;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_list.iCardsList;
import ru.aakumykov.me.sociocat.cards_list.list_items.DataItem;
import ru.aakumykov.me.sociocat.cards_list.list_items.ListItem;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class DataItem_ViewHolder extends BasicViewHolder {

    @BindView(R.id.elementView) ViewGroup elementView;
    @BindView(R.id.titleView) TextView titleView;
    @Nullable @BindView(R.id.imageView) ImageView imageView;
    @Nullable @BindView(R.id.quoteView) TextView quoteView;
    @Nullable @BindView(R.id.authorView) TextView authorView;
    @Nullable @BindView(R.id.dateView) TextView dateView;
    @Nullable @BindView(R.id.commentsCountView) TextView commentsCountView;
    @Nullable @BindView(R.id.ratingView) TextView ratingView;

    private static final String TAG = DataItem_ViewHolder.class.getSimpleName();
    private DataItem dataItem;
    private iCardsList.ViewMode currentViewMode;
    private int neutralStateColor = -1;
    private Card currentCard;


    // Конструктор
    public DataItem_ViewHolder(View itemView, iCardsList.ViewMode viewMode) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        currentViewMode = viewMode;
    }


    // Заполнение данными
    public void initialize(ListItem listItem) {
        this.dataItem = (DataItem) listItem;
        this.currentCard = (Card) dataItem.getPayload();

        titleView.setText(currentCard.getTitle());

        switch (currentViewMode) {
            case FEED:
                initializeInFeedMode(currentCard);
                break;
            case LIST:
                initializeInListMode(currentCard);
                break;
            case GRID:
                initializeInGridMode(currentCard);
                break;
            default:
                throw new RuntimeException("Unknown view mode: "+currentViewMode);
        }
    }

    private void initializeInFeedMode(Card card) {
        if (card.isImageCard()) {
            MyUtils.show(imageView);

            Glide.with(imageView).load(card.getImageURL())
                    .error(R.drawable.ic_image_error)
                    .into(imageView);
        }
        else
            MyUtils.hide(imageView);

        if (card.isTextCard()) {
            quoteView.setText(card.getQuote());
            MyUtils.show(quoteView);
            MyUtils.hide(titleView);
        }
        else
            MyUtils.hide(quoteView);


        authorView.setText(card.getUserName());
        commentsCountView.setText( String.valueOf(card.getCommentsKeys().size()) );

        Long cTime = card.getCTime();
        Long mTime = card.getMTime();
        String formatterDate = SimpleDateFormat.getDateInstance().format((cTime > 0) ? cTime : mTime);
        dateView.setText(formatterDate);

        ratingView.setText(String.valueOf(card.getRating()));
    }

    private void initializeInListMode(Card card) {
        titleView.setText(card.getTitle());
    }

    private void initializeInGridMode(Card card) {
        if (card.isImageCard()) {
            imageView.setImageResource(R.drawable.ic_card_type_image);
        }
        else if (card.isTextCard()) {
            imageView.setImageResource(R.drawable.ic_card_type_text);
        }
        else if (card.isAudioCard()) {
            imageView.setImageResource(R.drawable.ic_card_type_audio);
        }
        else if (card.isVideoCard()) {
            imageView.setImageResource(R.drawable.ic_card_type_video);
        }
        else {
            imageView.setImageResource(R.drawable.ic_card_type_unknown);
        }
    }

    @Override
    public void setViewState(iCardsList.ItemState itemState) {
        switch (itemState) {
            case NEUTRAL:
                applyNeutralState();
                break;
            case SELECTED:
                applySelectedState();
                break;
            case DELETING:
                applyDeletingState();
                break;
            default:
                Log.e(TAG, "Unknown eViewHolderState: "+ itemState);
        }
    }


    // Нажатия
    @Optional
    @OnClick({R.id.titleView, R.id.imageView, R.id.quoteView})
    void onItemClicked() {
        presenter.onDataItemClicked(this.dataItem);
    }

    @Optional
    @OnClick({R.id.authorView, R.id.dateView})
    void onAuthorClicked() {
        presenter.onCardAuthorClicked(currentCard.getUserId());
    }

    @Optional
    @OnClick(R.id.commentsInfoContainer)
    void onCommentsClicked() {
        presenter.onCardCommentsClicked(currentCard);
    }

    @Optional
    @OnClick({R.id.rateUpWidget, R.id.rateDownWidget})
    void onRatingWidgetClicked() {
        presenter.onRatingWidgetClicked(currentCard);
    }

    @OnLongClick({R.id.elementView, R.id.titleView})
    void onItemLongClicked() {
        presenter.onDataItemLongClicked(this.dataItem);
    }


    // Внутренние
    private void applySelectedState() {
        int selectedColor = elementView.getResources().getColor(R.color.element_is_selected);

        switch (currentViewMode) {
            case LIST:
                elementView.setBackgroundResource(R.drawable.list_item_selected);
                break;

            default:
                ((CardView) elementView).setCardBackgroundColor(selectedColor);
                break;
        }
    }

    private void applyDeletingState() {
        int deletingStateColor = elementView.getResources().getColor(R.color.element_is_now_deleting);

        switch (currentViewMode) {
            case LIST:
                elementView.setBackgroundResource(R.drawable.list_item_deleting);
                break;

            default:
                ((CardView)elementView).setCardBackgroundColor(deletingStateColor);
                break;
        }
    }

    private void applyNeutralState() {
        switch (currentViewMode) {
            case LIST:
                elementView.setBackgroundResource(R.drawable.list_item_neutral);
                break;

            default:
                ((CardView)elementView).setCardBackgroundColor(neutralStateColor);
                break;
        }
    }

}
