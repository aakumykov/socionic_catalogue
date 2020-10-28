package ru.aakumykov.me.sociocat.utils;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

public class AnimationUtils {

    public static AlphaAnimation createFadeInOutAnimation(long duration, boolean fadeOutFirst) {
        float startAlphaValue = (fadeOutFirst) ? 1f : 0f;
        float endAlphaValue = (fadeOutFirst) ? 0f : 1f;

        AlphaAnimation alphaAnimation = new AlphaAnimation(startAlphaValue, endAlphaValue);

        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        alphaAnimation.setDuration(duration);

        alphaAnimation.setInterpolator(
//                new AccelerateInterpolator()
                new DecelerateInterpolator()
//                new AccelerateDecelerateInterpolator()

//                new LinearInterpolator()

//                new AnticipateInterpolator()
//                new AnticipateOvershootInterpolator()

//                new FastOutSlowInInterpolator()
//                new FastOutLinearInInterpolator()
//                new LinearOutSlowInInterpolator()

//                new BounceInterpolator()
//                new CycleInterpolator(2f)
//                new OvershootInterpolator()
        );

        return alphaAnimation;
    }


}
