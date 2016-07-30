package com.j1adong.meizi.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewTreeObserver;

import com.j1adong.meizi.util.MyUtil;

/**
 * Created by J1aDong on 16/7/29.
 */
public class CircleDrawable extends Drawable {

    private Context mContext;
    private int mWidth;
    private Paint mPaint;

    public CircleDrawable(final View view) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mContext = view.getContext();
        mPaint.setColor(mContext.getResources().getColor(android.R.color.white));
        mPaint.setStyle(Paint.Style.FILL);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                mWidth = view.getWidth();

            }
        });
    }

    @Override
    public void draw(Canvas canvas) {

        canvas.drawCircle(mWidth / 2, mWidth / 2, mWidth / 2, mPaint);
    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
