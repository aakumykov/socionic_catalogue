package ru.aakumykov.me.sociocat.cards_grid.view_holders;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid.iCardsGrid;
import ru.aakumykov.me.sociocat.cards_grid.items.iGridItem;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.CardUtils;
import ru.aakumykov.me.sociocat.utils.ImageLoader;
import ru.aakumykov.me.sociocat.utils.ImageUtils;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class Card_ViewHolder extends BaseViewHolder
{
    private final static String TAG = "Card_ViewHolder";

    @BindView(R.id.cardView) CardView mCardView;
    @BindView(R.id.titleView) TextView mTitleView;
    @Nullable @BindView(R.id.imageView) ImageView mImageView;

    private iCardsGrid.iPresenter mPresenter;


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
        if (null != mImageView) {

            Glide.with(mImageView)
                    .load(card.getImageURL())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.ic_image_placeholder_monochrome)
                    .error(R.drawable.ic_image_error)
                    .into(mImageView);
//                    .into(new CustomTarget<Drawable>() {
//                        @Override
//                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                            //CardUtils.smartDisplayImage(mImageView, resource);
//                            mImageView.setImageDrawable(resource);
//                        }
//
//                        @Override
//                        public void onLoadCleared(@Nullable Drawable placeholder) {
//                            mImageView.setImageResource(R.drawable.ic_image_placeholder_monochrome);
//                        }
//                    });

            /*ImageLoader.loadImage(mImageView.getContext(), card.getImageURL(), new ImageLoader.LoadImageCallbacks() {
                @Override
                public void onImageLoadSuccess(Bitmap imageBitmap) {
                    mImageView.setImageBitmap(imageBitmap);

                    CardUtils.smartDisplayImage(mImageView, imageBitmap);

//                    if (1 == MyUtils.random(1,2)) {
//                        mImageView.setAdjustViewBounds(true);
//                        mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//                        mImageView.setBackgroundResource(R.drawable.shape_green_border);
//                    }
//                    else {
//                        mImageView.setAdjustViewBounds(false);
//                        mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//                        mImageView.setBackgroundResource(R.drawable.shape_transparent_border);
//                    }
                }

                @Override
                public void onImageLoadError(String errorMsg) {
                    showImageError(errorMsg);
                }
            });*/
        }
    }

    private void initAudioCard(Card card) {

    }

    private void initVideoCard(Card card) {

    }

    private void initUnknownCard(Card card) {

    }

    private void showImageError(String errorMsg) {
        if (null != mImageView)
            mImageView.setBackgroundResource(R.drawable.ic_image_error);
        //MyUtils.showCustomToast(mImageView.getContext(), errorMsg);
    }
}
