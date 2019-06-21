package ru.aakumykov.me.sociocat.cards_grid_3.view_holders;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.myimageloader.MyImageLoader;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid_3.iCG3;
import ru.aakumykov.me.sociocat.cards_grid_3.items.iGridItem;
import ru.aakumykov.me.sociocat.models.Card;

public class Card_ViewHolder extends BaseViewHolder implements
    View.OnClickListener,
    View.OnLongClickListener
{
    private final static String TAG = "Card_ViewHolder";

    @BindView(R.id.cardView) CardView mCardView;
    @BindView(R.id.titleView) TextView mTitleView;
    @Nullable @BindView(R.id.imageContainer) ViewGroup mImageContainer;

    private iCG3.iPresenter mPresenter;
    private int mPosition;
    private iGridItem mGridItem;
    private int mOriginalBackground = -1;


    public Card_ViewHolder(@NonNull View itemView, iCG3.iPresenter presenter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mPresenter = presenter;
    }

    public void initialize(iGridItem gridItem, int position, Object payload) {
        this.mPosition = position;
        this.mGridItem = gridItem;

        mCardView.setOnClickListener(this);
        mCardView.setOnLongClickListener(this);

        Card card = (Card) payload;

        commonCardInit(card);

        if (card.isTextCard()) {
            initTextCard(card);
        }
        else if (card.isImageCard()) {
            initImageCard(card);
        }
        else if (card.isAudioCard()) {
            initAudioCard(card);
        }
        else if (card.isVideoCard()) {
            initVideoCard(card);
        }
        else {
            Log.e(TAG, "Unknown card type: "+ payload);
        }
    }

//    public void fade() {
//        mOriginalBackground = mCardView.getCardBackgroundColor().getDefaultColor();
//
//        int color = mCardView.getResources().getColor(R.color.cards_grid_pressed_background_color);
//        mCardView.setCardBackgroundColor(color);
//    }
//
//    public void unfade() {
//        mCardView.setCardBackgroundColor(mOriginalBackground);
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardView:
                mPresenter.onCardClicked(mPosition);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        mPresenter.onCardLongClicked(mPosition, view, this);
        return true;
    }


    // Внутренние методы
    private void commonCardInit(Card card) {
        mTitleView.setText(card.getTitle());

        if (mGridItem.isPressed()) {
            mOriginalBackground = mCardView.getCardBackgroundColor().getDefaultColor();

            int color = mCardView.getResources().getColor(R.color.cards_grid_pressed_background_color);
            mCardView.setCardBackgroundColor(color);
        }
        else {
            if (mOriginalBackground > -1)
                mCardView.setCardBackgroundColor(mOriginalBackground);
        }
    }

    private void initTextCard(Card card) {

    }

    private void initImageCard(Card card) {
        MyImageLoader.loadImageToContainer(
                mImageContainer.getContext(),
                mImageContainer,
                card.getImageURL()
            );
    }

    private void initAudioCard(Card card) {

    }

    private void initVideoCard(Card card) {

    }

    private void initUnknownCard(Card card) {

    }
}
