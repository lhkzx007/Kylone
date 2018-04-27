package com.kylone.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Created by zack
 */
public class AniUtils {
    public static Interpolator DEFAULT_INTERPOLATOR = new AccelerateDecelerateInterpolator();

    public static void aniScale(final View view, float from, float to, long duration) {
        aniScale(view, from, to, duration, DEFAULT_INTERPOLATOR, null);
    }

    public static Animator aniScale(final View view, float from, float to, long duration, Interpolator interpolator) {
        return aniScale(view, from, to, duration, interpolator, null);
    }

    public static Animator aniScale(final View view, float from, float to, long duration, Interpolator interpolator, final ValueAnimator.AnimatorUpdateListener listener) {
        ValueAnimator ani = ValueAnimator.ofFloat(from, to);
        ani.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                view.setScaleX(value);
                view.setScaleY(value);
                if (listener != null) {
                    listener.onAnimationUpdate(animation);
                }
            }
        });
        ani.setInterpolator(interpolator);
        ani.setDuration(duration);
        ani.start();
        return ani;
    }

    public static void scaleAnimation(View v, float fromX, float toX, float fromY, float toY, int duration, Interpolator interpolator) {
        v.clearAnimation();
        ScaleAnimation animation = new ScaleAnimation(fromX, toX, fromY, toY, v.getWidth() / 2, v.getHeight() / 2);
        animation.setDuration(duration);
        animation.setInterpolator(interpolator);
        animation.setFillAfter(true);
        v.setAnimation(animation);
        animation.start();
    }

    public static void aniProperty(View v, String property, float value, int duration, Animator.AnimatorListener listener) {
        ObjectAnimator ani = ObjectAnimator.ofFloat(v, property, value);
        if (listener != null) {
            ani.addListener(listener);
        }
        ani.setDuration(duration).start();
    }

    public static ObjectAnimator aniShake(final View v, final String property, ObjectAnimator ani) {
        if (ani != null && ani.isRunning()) return ani;
        ani = ObjectAnimator.ofFloat(v, property, 12);
        ani.setInterpolator(new CycleInterpolator(2.0f));
        ani.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator.ofFloat(v, property, 0).setDuration(0).start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        ani.setDuration(450).start();
        return ani;
    }

    public static void aniShakeX(View v) {
        v.clearAnimation();
        TranslateAnimation animation = new TranslateAnimation(0, 12, 0, 0);
        animation.setInterpolator(new CycleInterpolator(2.0f));
        animation.setDuration(450);
        v.setAnimation(animation);
        animation.start();
    }

    public static void aniShakeY(View v) {
        v.clearAnimation();
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 12);
        animation.setInterpolator(new CycleInterpolator(2.0f));
        animation.setDuration(450);
        v.setAnimation(animation);
        animation.start();
    }
}
