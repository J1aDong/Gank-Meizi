package com.j1adong.meizi.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;

/**
 * Created by J1aDong on 16/7/24.
 */
public class RefreshImageView extends ImageView {

    private Spring mScaleSpring;
    private static final SpringConfig ORIGAMI_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(80, 4);
    private ScaleSpringListener mScaleSpringListener;

    public RefreshImageView(Context context) {
        this(context, null);
    }

    public RefreshImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        RefreshDrawable refreshDrawable = new RefreshDrawable(this);
        setBackgroundDrawable(refreshDrawable);
        mScaleSpringListener = new ScaleSpringListener(this);
        mScaleSpring = SpringSystem.create().createSpring().setSpringConfig(ORIGAMI_SPRING_CONFIG);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mScaleSpring.setEndValue(1);
                break;
            case MotionEvent.ACTION_UP:
                mScaleSpring.setEndValue(0);
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mScaleSpring.addListener(mScaleSpringListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mScaleSpring.removeListener(mScaleSpringListener);
    }

    public void refresh() {
        final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f);
        objectAnimator.setDuration(800);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.setRepeatMode(ValueAnimator.INFINITE);
        objectAnimator.start();
    }

    private class ScaleSpringListener extends SimpleSpringListener {
        View view;

        public ScaleSpringListener(View view) {
            this.view = view;
        }

        @Override
        public void onSpringUpdate(Spring spring) {
            float mappedvalue = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 1, 0.8);
            view.setScaleX(mappedvalue);
            view.setScaleY(mappedvalue);
        }
    }
}
