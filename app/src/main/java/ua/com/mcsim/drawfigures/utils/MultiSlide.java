package ua.com.mcsim.drawfigures.utils;


import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;

import static java.lang.Math.abs;

public class MultiSlide implements OnTouchListener, AnimatorUpdateListener, AnimatorListener {
    private View view;
    private float touchableArea;
    private int autoSlideDuration = 300;
    private int positionOnScreen;
    private MultiSlide.SlideListener slideListener;
    private ValueAnimator valueAnimator;
    private float slideAnimationTo;
    private float startPositionY, startPositionX;
    private float viewStartPositionY, viewStartPositionX;
    private boolean canSlide = true;
    private float density;
    private float lowerPosition;
    private float viewHeight, viewWidth;
    private boolean hiddenInit;
    public static final int BOTTOM = 0;
    public static final int TOP = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    private static final String LOG = "MultiSlide";


    public MultiSlide(final View view, int pos) {
        this.positionOnScreen = pos;
        this.view = view;
        this.density = view.getResources().getDisplayMetrics().density;
        this.touchableArea = 300.0F * this.density;
        view.setOnTouchListener(this);
        view.setPivotY(0.0F);
        view.setPivotX(0.0F);
        this.createAnimation();
        view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if(MultiSlide.this.hiddenInit) {
                    MultiSlide.this.viewHeight = (float)view.getHeight();
                    MultiSlide.this.viewWidth = (float)view.getWidth();
                    MultiSlide.this.hideImmediately();
                }

                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }


    public boolean isVisible() {
        return this.view.getVisibility() == View.VISIBLE;
    }

    public void setSlideListener(MultiSlide.SlideListener slideListener) {
        this.slideListener = slideListener;
    }

    public void setAutoSlideDuration(int autoSlideDuration) {
        this.autoSlideDuration = autoSlideDuration;
    }

    public float getAutoSlideDuration() {
        return (float)this.autoSlideDuration;
    }

    public void setTouchableArea(float touchableArea) {
        this.touchableArea = touchableArea * this.density;
    }

    public float getTouchableArea() {
        return this.touchableArea / this.density;
    }

    public boolean isAnimationRunning() {
        return this.valueAnimator != null && this.valueAnimator.isRunning();
    }

    public void animateIn() {
        float from = 0.0F;
        this.slideAnimationTo = 0.0F;
        switch (this.positionOnScreen){
            case TOP:
                from = -this.viewHeight;
                break;
            case BOTTOM:
                from = this.viewHeight;
                break;
            case LEFT:
                from = -this.viewWidth;
                break;
            case RIGHT:
                from = this.viewWidth;
                break;
        }
        this.valueAnimator.setFloatValues(new float[]{from, this.slideAnimationTo});
        this.valueAnimator.start();
    }

    public void animateOut() {
        float from = 0.0F;
        switch (this.positionOnScreen){
            case TOP:
                this.slideAnimationTo = -(float)this.view.getHeight();
                from = this.view.getTranslationY();
                break;
            case BOTTOM:
                this.slideAnimationTo = (float)this.view.getHeight();
                from = this.view.getTranslationY();
                break;
            case LEFT:
                this.slideAnimationTo = -(float)this.view.getWidth();
                from = this.view.getTranslationX();
                break;
            case RIGHT:
                this.slideAnimationTo = (float)this.view.getWidth();
                from = this.view.getTranslationX();
                break;
        }
        this.valueAnimator.setFloatValues(new float[]{from, this.slideAnimationTo});
        this.valueAnimator.start();
    }

    public void hideImmediately() {
        if(this.view.getHeight() > 0) {
            switch (this.positionOnScreen){
                case TOP:
                    this.view.setTranslationY(-this.viewHeight);
                    break;
                case BOTTOM:
                    this.view.setTranslationY(this.viewHeight);
                    break;
                case LEFT:
                    this.view.setTranslationX(-this.viewWidth);
                    break;
                case RIGHT:
                    this.view.setTranslationX(this.viewWidth);
                    break;
            }
            this.view.setVisibility(View.GONE);
            this.notifyVisibilityChanged(View.GONE);
        } else {
            this.hiddenInit = true;
        }

    }

    private void createAnimation() {
        this.valueAnimator = ValueAnimator.ofFloat(new float[0]);
        this.valueAnimator.setDuration((long)this.autoSlideDuration);
        this.valueAnimator.setInterpolator(new DecelerateInterpolator());
        this.valueAnimator.addUpdateListener(this);
        this.valueAnimator.addListener(this);
    }

    public boolean onTouch(View v, MotionEvent event) {
        float touchedAreaY = event.getRawY() - (float)this.view.getTop();
        float touchedAreaX = event.getRawX() - (float)this.view.getRight();
        if(this.isAnimationRunning()) {
            return false;
        } else {
            switch (this.positionOnScreen) {
                case TOP:
                    switch(event.getActionMasked()) {
                        case 0: //ACTION_DOWN
                            this.viewHeight = (float)this.view.getHeight();
                            this.startPositionY = event.getRawY();
                            this.viewStartPositionY = this.view.getTranslationY();
                            if(this.touchableArea < touchedAreaY) {
                                this.canSlide = false;
                            }
                            break;
                        case 1: //ACTION_UP
                            float slideAnimationFrom = this.view.getTranslationY();
                            boolean mustSlideUp = this.lowerPosition < event.getRawY();
                            boolean scrollableAreaConsumed = abs(this.view.getTranslationY()) > (float)(this.view.getHeight() / 5);
                            if(scrollableAreaConsumed && !mustSlideUp) {
                                this.slideAnimationTo = -(float)this.view.getHeight();
                            } else {
                                this.slideAnimationTo = 0.0F;
                            }

                            this.valueAnimator.setFloatValues(new float[]{slideAnimationFrom, this.slideAnimationTo});
                            this.valueAnimator.start();
                            this.canSlide = true;
                            this.lowerPosition = 0.0F;
                            break;
                        case 2: //ACTION_MOVE
                            float difference = event.getRawY() - this.startPositionY;
                            float moveTo = this.viewStartPositionY + difference;
                            float percents = moveTo * 100.0F / (float)this.view.getHeight();
                            if(moveTo < 0.0F && this.canSlide) {
                                this.notifyPercentChanged(percents);
                                this.view.setTranslationY(moveTo);
                            }

                            if(event.getRawY() > this.lowerPosition) {
                                this.lowerPosition = event.getRawY();
                            }
                    }
                    break;
                case BOTTOM:
                    switch(event.getActionMasked()) {
                        case 0: //ACTION_DOWN
                            this.viewHeight = (float)this.view.getHeight();
                            this.startPositionY = event.getRawY();
                            this.viewStartPositionY = this.view.getTranslationY();
                            if(this.touchableArea < touchedAreaY) {
                                this.canSlide = false;
                            }
                            break;
                        case 1: //ACTION_UP
                            float slideAnimationFrom = this.view.getTranslationY();
                            boolean mustSlideUp = this.lowerPosition > event.getRawY();
                            boolean scrollableAreaConsumed = abs(this.view.getTranslationY()) > (float)(this.view.getHeight() / 5);
                            if(scrollableAreaConsumed && !mustSlideUp) {
                                this.slideAnimationTo = (float)this.view.getHeight();
                            } else {
                                this.slideAnimationTo = 0.0F;
                            }

                            this.valueAnimator.setFloatValues(new float[]{slideAnimationFrom, this.slideAnimationTo});
                            this.valueAnimator.start();
                            this.canSlide = true;
                            this.lowerPosition = 0.0F;
                            break;
                        case 2: //ACTION_MOVE
                            float difference = event.getRawY() - this.startPositionY;
                            float moveTo = this.viewStartPositionY + difference;
                            float percents = moveTo * 100.0F / (float)this.view.getHeight();
                            if(moveTo > 0.0F && this.canSlide) {
                                this.notifyPercentChanged(percents);
                                this.view.setTranslationY(moveTo);
                            }

                            if(event.getRawY() > this.lowerPosition) {
                                this.lowerPosition = event.getRawY();
                            }
                    }
                    break;
                case LEFT:
                    switch(event.getActionMasked()) {
                        case 0: //ACTION_DOWN
                            this.viewWidth = (float)this.view.getWidth();
                            this.startPositionX = event.getRawX();
                            this.viewStartPositionX = this.view.getTranslationX();
                            if(this.touchableArea < touchedAreaX) {
                                this.canSlide = false;
                            }
                            break;
                        case 1: //ACTION_UP
                            float slideAnimationFrom = this.view.getTranslationX();
                            boolean mustSlideUp = this.lowerPosition < event.getRawX();
                            boolean scrollableAreaConsumed = abs(this.view.getTranslationX()) > (float)(this.view.getWidth() / 5);
                            if(scrollableAreaConsumed && !mustSlideUp) {
                                this.slideAnimationTo = -(float)this.view.getWidth();
                            } else {
                                this.slideAnimationTo = 0.0F;
                            }

                            this.valueAnimator.setFloatValues(new float[]{slideAnimationFrom, this.slideAnimationTo});
                            this.valueAnimator.start();
                            this.canSlide = true;
                            this.lowerPosition = 0.0F;
                            break;
                        case 2: //ACTION_MOVE
                            float difference = event.getRawX() - this.startPositionX;
                            float moveTo = this.viewStartPositionX + difference;
                            float percents = moveTo * 100.0F / (float)this.view.getWidth();
                            if(moveTo < 0.0F && this.canSlide) {
                                this.notifyPercentChanged(percents);
                                this.view.setTranslationX(moveTo);
                            }

                            if(event.getRawX() > this.lowerPosition) {
                                this.lowerPosition = event.getRawX();
                            }
                    }
                    break;
                case RIGHT:
                    switch(event.getActionMasked()) {
                        case 0: //ACTION_DOWN
                            this.viewWidth = (float)this.view.getWidth();
                            this.startPositionX = event.getRawX();
                            this.viewStartPositionX = this.view.getTranslationX();
                            if(this.touchableArea < touchedAreaX) {
                                this.canSlide = false;
                            }
                            break;
                        case 1: //ACTION_UP
                            float slideAnimationFrom = this.view.getTranslationX();
                            boolean mustSlideUp = this.lowerPosition > event.getRawX();
                            boolean scrollableAreaConsumed = abs(this.view.getTranslationX()) > (float)(this.view.getWidth() / 5);
                            if(scrollableAreaConsumed && !mustSlideUp) {
                                this.slideAnimationTo = (float)this.view.getWidth();
                            } else {
                                this.slideAnimationTo = 0.0F;
                            }

                            this.valueAnimator.setFloatValues(new float[]{slideAnimationFrom, this.slideAnimationTo});
                            this.valueAnimator.start();
                            this.canSlide = true;
                            this.lowerPosition = 0.0F;
                            break;
                        case 2: //ACTION_MOVE
                            float difference = event.getRawX() - this.startPositionX;
                            float moveTo = this.viewStartPositionX + difference;
                            float percents = moveTo * 100.0F / (float)this.view.getWidth();
                            if(moveTo > 0.0F && this.canSlide) {
                                this.notifyPercentChanged(percents);
                                this.view.setTranslationX(moveTo);
                            }

                            if(event.getRawX() > this.lowerPosition) {
                                this.lowerPosition = event.getRawX();
                            }
                    }
                    break;
            }

            return true;
        }
    }

    public void onAnimationUpdate(ValueAnimator animation) {
        float val = ((Float)animation.getAnimatedValue()).floatValue();

        switch (this.positionOnScreen){
            case TOP:
            case BOTTOM:
                this.view.setTranslationY(val);
                float percentsY = (this.view.getY() - (float)this.view.getTop()) * 100.0F / this.viewHeight;
                this.notifyPercentChanged(percentsY);
                break;
            case LEFT:
            case RIGHT:
                this.view.setTranslationX(val);
                float percentsX = (this.view.getX() - (float)this.view.getRight()) * 100.0F / this.viewWidth;
                this.notifyPercentChanged(percentsX);
                break;
        }


    }

    private void notifyPercentChanged(float percent) {
        if(this.slideListener != null) {
            this.slideListener.onSlideDown(percent);
        }

    }

    private void notifyVisibilityChanged(int visibility) {
        if(this.slideListener != null) {
            this.slideListener.onVisibilityChanged(visibility);
        }

    }

    public void onAnimationStart(Animator animator) {
        this.view.setVisibility(View.VISIBLE);
        this.notifyVisibilityChanged(View.VISIBLE);
    }

    public void onAnimationEnd(Animator animator) {

        switch (this.positionOnScreen) {
            case TOP:
            case LEFT:
                if (this.slideAnimationTo < 0.0F) {
                    this.view.setVisibility(View.GONE);
                    this.notifyVisibilityChanged(View.GONE);
                }
                break;

            case RIGHT:
            case BOTTOM:
                if (this.slideAnimationTo > 0.0F) {
                    this.view.setVisibility(View.GONE);
                    this.notifyVisibilityChanged(View.GONE);
                }
                break;
        }
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public interface SlideListener {
        void onSlideDown(float var1);

        void onVisibilityChanged(int var1);
    }
}