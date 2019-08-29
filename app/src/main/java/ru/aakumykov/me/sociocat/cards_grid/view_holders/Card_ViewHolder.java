package ru.aakumykov.me.sociocat.cards_grid.view_holders;

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
import ru.aakumykov.me.sociocat.cards_grid.iCardsGrid;
import ru.aakumykov.me.sociocat.cards_grid.items.iGridItem;
import ru.aakumykov.me.sociocat.models.Card;

public class Card_ViewHolder extends BaseViewHolder
{
    private final static String TAG = "Card_ViewHolder";

    @BindView(R.id.cardView) CardView mCardView;
    @BindView(R.id.titleView) TextView mTitleView;
    @Nullable @BindView(R.id.imageContainer) ViewGroup mImageContainer;

    private iCardsGrid.iPresenter mPresenter;
//    private iGridItem mGridItem;


    public Card_ViewHolder(@NonNull View itemView, iCardsGrid.iPresenter presenter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mPresenter = presenter;
    }

    public void initialize(iGridItem gridItem, int position, Object payload) {
//        this.mGridItem = gridItem;

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

    public void bindClickListener(iCardsGrid.iGridItemClickListener gridItemClickListener) {
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridItemClickListener.onGridItemClicked(v);
            }
        });
    }

    public void bindLongClickListener(iCardsGrid.iGridItemClickListener gridItemClickListener) {
        mCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                gridItemClickListener.onGridItemLongClicked(v);
                return true;
            }
        });
    }


    // Внутренние методы
    private void commonCardInit(Card card) {
        mTitleView.setText(card.getTitle());
    }

    private void initTextCard(Card card) {

    }

    private void initImageCard(Card card) {
        MyImageLoader.loadImageToContainer(
                mImageContainer.getContext(),
                card.getImageURL(),
                mImageContainer
            );
    }

    private void initAudioCard(Card card) {

    }

    private void initVideoCard(Card card) {

    }

    private void initUnknownCard(Card card) {

    }
}
