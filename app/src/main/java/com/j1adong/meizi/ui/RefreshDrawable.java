package com.j1adong.meizi.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewTreeObserver;

import com.j1adong.meizi.R;
import com.j1adong.meizi.util.MyUtil;

/**
 * Created by J1aDong on 16/7/24.
 */
public class RefreshDrawable extends Drawable {
    private Paint mPaint;
    private Paint mStrokePaint;
    private int mWidth;
    private RectF mRectF = new RectF();
    private int mStrokeWidth;

    public RefreshDrawable(final View view) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setColor(view.getResources().getColor(R.color.red));
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(MyUtil.dp2px(view.getContext(), 5));
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mWidth = view.getWidth();
                mStrokeWidth = MyUtil.dp2px(view.getContext(), 12);
                mRectF.set(mWidth / 2 - mStrokeWidth, mWidth / 2 - mStrokeWidth, mWidth / 2 + mStrokeWidth, mWidth / 2 + mStrokeWidth);
            }
        });
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mWidth / 2, mWidth / 2, mWidth / 2, mPaint);
        canvas.drawArc(mRectF, 30.f, 290.f, false, mStrokePaint);
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
