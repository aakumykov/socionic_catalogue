package ru.aakumykov.me.sociocat.cards_list.view_holders;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_list.iCardsList;
import ru.aakumykov.me.sociocat.cards_list.list_items.DataItem;
import ru.aakumykov.me.sociocat.cards_list.list_items.ListItem;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class DataItem_ViewHolder
        extends BasicViewHolder
        implements View.OnLongClickListener
{
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


    // BasicViewHolder
    @Override
    public void initialize(ListItem listItem) {
        this.dataItem = (DataItem) listItem;
        this.currentCard = (Card) dataItem.getPayload();

        elementView.setOnLongClickListener(this);
        titleView.setOnLongClickListener(this);

        initializeCommonParts();

        switch (currentViewMode) {
            case FEED:
                initializeInFeedMode();
                break;
            case LIST:
                initializeInListMode();
                break;
            case GRID:
                initializeInGridMode();
                break;
            default:
                throw new RuntimeException("Unknown view mode: "+currentViewMode);
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


    // View.OnLongClickListener
    @Override
    public boolean onLongClick(View view) {
        if (presenter.canSelectItem()) {
            presenter.onDataItemLongClicked(dataItem);
        }
        return presenter.canSelectItem();
    }


    // Нажатия
    @Optional
    @OnClick({ R.id.titleView, R.id.imageView, R.id.quoteView, R.id.dateView })
    void onItemClicked() {
        presenter.onDataItemClicked(this.dataItem);
    }

    @Optional
    @OnClick({R.id.authorView})
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



    // Внутренние
    private void initializeCommonParts() {
        titleView.setText(currentCard.getTitle());
    }

    private void initializeInFeedMode() {
        if (currentCard.isImageCard()) {
            MyUtils.show(imageView);

            Glide.with(imageView).load(currentCard.getImageURL())
                    .error(R.drawable.ic_image_error)
                    .into(imageView);
        }
        else
            MyUtils.hide(imageView);

        if (currentCard.isTextCard()) {
            MyUtils.hide(titleView);

            quoteView.setText(currentCard.getQuote());
            MyUtils.show(quoteView);
        }
        else {
            MyUtils.show(titleView);
            MyUtils.hide(quoteView);
        }


        authorView.setText(currentCard.getUserName());
        commentsCountView.setText( String.valueOf(currentCard.getCommentsKeys().size()) );

        displayDate();

        ratingView.setText(String.valueOf(currentCard.getRating()));
    }

    private void initializeInListMode() {
        displayDate();
    }

    private void initializeInGridMode() {
        if (currentCard.isImageCard()) {
            imageView.setImageResource(R.drawable.ic_card_type_image);
        }
        else if (currentCard.isTextCard()) {
            imageView.setImageResource(R.drawable.ic_card_type_text);
        }
        else if (currentCard.isAudioCard()) {
            imageView.setImageResource(R.drawable.ic_card_type_audio);
        }
        else if (currentCard.isVideoCard()) {
            imageView.setImageResource(R.drawable.ic_card_type_video);
        }
        else {
            imageView.setImageResource(R.drawable.ic_card_type_unknown);
        }
    }

    private void displayDate() {
        Long cTime = currentCard.getCTime();
        Long mTime = currentCard.getMTime();

        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        String formatterDate = dateFormat.format((cTime > 0) ? cTime : mTime);

        dateView.setText(formatterDate);
    }


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
