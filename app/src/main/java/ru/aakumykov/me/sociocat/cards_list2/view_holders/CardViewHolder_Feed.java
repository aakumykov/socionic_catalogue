package ru.aakumykov.me.sociocat.cards_list2.view_holders;

import android.animation.AnimatorSet;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import butterknife.BindView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.ViewUtils;
import ru.aakumykov.me.sociocat.utils.AnimationUtils;

public class CardViewHolder_Feed extends CardViewHolder {

    @BindView(R.id.imageView) ImageView mImageView;

    public CardViewHolder_Feed(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void fillWithData(BasicMVP_ListItem basicListItem) {
        super.fillWithData(basicListItem);

        if (mCurrentCard.isImageCard())
            displayImageCard();
        else
            hideImage();
    }

    private void displayImageCard() {

        mImageView.setImageResource(R.drawable.ic_image_placeholder_smaller);
        ViewUtils.show(mImageView);
        AnimatorSet animatorSet = AnimationUtils.animateFadeInOut(mImageView);

        Glide.with(mImageView.getContext())
                .load(mCurrentCard.getImageURL())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        AnimationUtils.revealFromCurrentAlphaState(mImageView, animatorSet);
                        mImageView.setImageResource(R.drawable.ic_image_error);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mImageView.setImageDrawable(resource);
                        AnimationUtils.revealFromCurrentAlphaState(mImageView, animatorSet);
                        return false;
                    }
                })
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        AnimationUtils.revealFromCurrentAlphaState(mImageView, animatorSet);
                    }
                });
    }

    private void hideImage() {
        ViewUtils.hide(mImageView);
    }
}
