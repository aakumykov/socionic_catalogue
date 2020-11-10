package ru.aakumykov.me.sociocat.utils;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class AnimationUtils {

    public final static long DEFAULT_FADE_IN_OUT_DURATION = 1000L;


    public static AlphaAnimation createFadeInOutAnimation(long duration, boolean fadeOutFirst) {
        float startAlphaValue = (fadeOutFirst) ? 1f : 0f;
        float endAlphaValue = (fadeOutFirst) ? 0f : 1f;

        AlphaAnimation alphaAnimation = new AlphaAnimation(startAlphaValue, endAlphaValue);

        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        alphaAnimation.setDuration(duration);

        alphaAnimation.setInterpolator(
                (fadeOutFirst) ? new DecelerateInterpolator() : new AccelerateInterpolator()
        );

        return alphaAnimation;
    }


    public static AnimatorSet animateFadeInOut(View view) {
        return animateFadeInOut(view, DEFAULT_FADE_IN_OUT_DURATION, true);
    }

    public static AnimatorSet animateFadeInOut(View view, long duration, boolean startFromFadeOut) {

        float startAlphaValue = (startFromFadeOut) ? view.getAlpha() : 0f;
        float endAlphaValue = (startFromFadeOut) ? 0f : view.getAlpha();

        ValueAnimator fadeInOutAnimator = ValueAnimator.ofFloat(startAlphaValue, endAlphaValue);
        fadeInOutAnimator.setDuration(duration);
        fadeInOutAnimator.setRepeatMode(ValueAnimator.REVERSE);
        fadeInOutAnimator.setRepeatCount(ValueAnimator.INFINITE);

        if (startFromFadeOut)
            fadeInOutAnimator.setInterpolator(new DecelerateInterpolator());
        else
            fadeInOutAnimator.setInterpolator(new AccelerateInterpolator());

        fadeInOutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setAlpha((float) valueAnimator.getAnimatedValue());
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(fadeInOutAnimator);
        animatorSet.start();

        return animatorSet;
    }


    public static void revealFromCurrentAlphaState(View view, @Nullable AnimatorSet currentAnimatorSet) {
        revealFromCurrentAlphaState(view, DEFAULT_FADE_IN_OUT_DURATION, 1.0f, currentAnimatorSet);
    }

    public static void revealFromCurrentAlphaState(View view, long currentAnimationDuration, float endAlphaValue, @Nullable AnimatorSet runningAnimatorSetToStop) {

        float currentImageAlpha = view.getAlpha();
        float alphaDiff = (endAlphaValue - currentImageAlpha);
        long durationDiff = Math.round(alphaDiff * currentAnimationDuration);

        ValueAnimator revealFromAlphaAnimation = ValueAnimator.ofFloat(currentImageAlpha, endAlphaValue);
        revealFromAlphaAnimation.setDuration(durationDiff / 2);
        revealFromAlphaAnimation.setInterpolator(new LinearInterpolator());
        revealFromAlphaAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                view.setAlpha(value);
            }
        });

        if (null != runningAnimatorSetToStop)
            runningAnimatorSetToStop.cancel();

        revealFromAlphaAnimation.start();
    }

}
