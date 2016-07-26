package com.j1adong.meizi.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;

/**
 * Created by J1aDong on 16/7/25.
 */
public class ReboundImageView extends ImageView {
    private Spring mScaleSpring;
    private static final SpringConfig ORIGAMI_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(80, 4);
    private MySpringListener mScaleSpringListener;

    public ReboundImageView(Context context) {
        this(context, null);
    }

    public ReboundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReboundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mScaleSpringListener = new MySpringListener(this);
        mScaleSpring = SpringSystem.create().createSpring().setSpringConfig(ORIGAMI_SPRING_CONFIG);
        setClickable(true);
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

    public void setDrawable(Drawable drawable) {
        setBackgroundDrawable(drawable);
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

    private class MySpringListener extends SimpleSpringListener {
        View view;

        public MySpringListener(View view) {
            this.view = view;
        }

        @Override
        public void onSpringUpdate(Spring spring) {
            float mappedvalue = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 1, 0.8);
            view.setScaleX(mappedvalue);
            view.setScaleY(mappedvalue);

            float rotation = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 0, 30);
            view.setRotation(rotation);
        }
    }
}
