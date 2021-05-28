package com.example.androidchat2.views.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class AnimationUtils {

    public final static long ANIMATION_DURATION = 300L;
    public final static long ANIMATION_DELAY = 100L;

    public final static class Builder {

        private List<Object> objects = new ArrayList<>();

        private boolean animateAlphaIn = false;
        private boolean animateAlphaOut = false;

        private boolean animateScaleUp = false;
        private boolean animateScaleDown = false;

        private long duration = ANIMATION_DURATION;
        private long delay = ANIMATION_DELAY;

        private TimeInterpolator interpolator = null;

        private float translationYBegin = 0f;
        private float translationYEnd = 0f;

        private float translationXBegin = 0f;
        private float translationXEnd = 0f;

        public Builder() {

        }

        public Builder setObjects(List<Object> objects) {
            this.objects = objects;
            return this;
        }

        public Builder setAnimateAlphaIn(boolean animateAlphaIn) {
            this.animateAlphaIn = animateAlphaIn;
            return this;
        }

        public Builder setAnimateAlphaOut(boolean animateAlphaOut) {
            this.animateAlphaOut = animateAlphaOut;
            return this;
        }

        public Builder setAnimateScaleUp(boolean animateScaleUp) {
            this.animateScaleUp = animateScaleUp;
            return this;
        }

        public Builder setAnimateScaleDown(boolean animateScaleDown) {
            this.animateScaleDown = animateScaleDown;
            return this;
        }

        public Builder setDuration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder setDelay(long delay) {
            this.delay = delay;
            return this;
        }

        public Builder setInterpolator(TimeInterpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public Builder setTranslationY(float begin, float end) {
            this.translationYBegin = begin;
            this.translationYEnd = end;
            return this;
        }

        public Builder setTranslationYBegin(float begin) {
            this.translationYBegin = begin;
            return this;
        }

        public Builder setTranslationYEnd(float end) {
            this.translationYEnd = end;
            return this;
        }

        public Builder setTranslationX(float begin, float end) {
            this.translationXBegin = begin;
            this.translationXEnd = end;
            return this;
        }

        public Builder setTranslationXBegin(float translationXBegin) {
            this.translationXBegin = translationXBegin;
            return this;
        }

        public Builder setTranslationXEnd(float translationXEnd) {
            this.translationXEnd = translationXEnd;
            return this;
        }

        public void start() {
            List<Animator> animators = getAnimators();
            animate(animators, duration, delay, interpolator);
        }

        public List<Animator> getAnimators() {
            List<Animator> animators = new ArrayList<>();
            for (Object object : objects) {
                View view = (View) object;

                ObjectAnimator alpha = null;
                float begin = -1f, end = -1f;

                if (animateAlphaIn) {
                    begin = 0f;
                    end = 1f;
                } else if (animateAlphaOut) {
                    begin = 1f;
                    end = 0f;
                }

                if (begin != -1f && end != -1f) {
                    alpha = ObjectAnimator.ofFloat(view, "alpha", begin, end);
                    animators.add(alpha);
                }

                ObjectAnimator scaleX, scaleY;
                begin = -1f;
                end = -1f;
                if (animateScaleUp) {
                    begin = 0f;
                    end = 1f;
                } else if (animateScaleDown) {
                    begin = 1f;
                    end = 0f;
                }

                if (begin != -1f && end != -1f) {
                    scaleX = ObjectAnimator.ofFloat(view, "scaleX", begin, end);
                    scaleY = ObjectAnimator.ofFloat(view, "scaleY", begin, end);
                    animators.add(scaleX);
                    animators.add(scaleY);
                }

                if (translationYBegin != 0f || translationYEnd != 0f) {
                    ObjectAnimator translateY = ObjectAnimator.ofFloat(view, "translationY", translationYBegin, translationYEnd);
                    animators.add(translateY);
                }

                if (translationXBegin != 0f || translationXEnd != 0f) {
                    ObjectAnimator translateX = ObjectAnimator.ofFloat(view, "translationX", translationXBegin, translationXEnd);
                    animators.add(translateX);
                }
            }
            return animators;
        }
    }

    public static void animate(List<Animator> animators, long duration, long delay, TimeInterpolator interpolator) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);
        animatorSet.setInterpolator(interpolator);
        animatorSet.setDuration(duration);
        animatorSet.setStartDelay(delay);
        animatorSet.start();
    }

}