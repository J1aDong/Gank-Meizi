package com.j1adong.meizi.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewTreeObserver;

import com.j1adong.meizi.R;
import com.j1adong.meizi.util.MyUtil;

/**
 * Created by J1aDong on 16/7/25.
 */
public class LeftDrawable extends Drawable {
    private Paint mStrokePaint;
    private int mWidth;
    private int mStrokeWidth;
    private RectF mRectF = new RectF();
    private Paint mPaint;
    private PointF mPointF1 = new PointF();
    private PointF mPointF2 = new PointF();
    private PointF mPointF3 = new PointF();
    private PointF mPointF4 = new PointF();

    public LeftDrawable(final View view) {

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);

        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setColor(view.getContext().getResources().getColor(R.color.red));
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(MyUtil.dp2px(view.getContext(), 4));
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                                                 @Override
                                                                 public void onGlobalLayout() {
                                                                     view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                                                     mWidth = view.getWidth();
                                                                     mStrokeWidth = MyUtil.dp2px(view.getContext(), 12);
                                                                     mRectF.set(mWidth / 2 - mStrokeWidth, mWidth / 2 - mStrokeWidth, mWidth / 2 + mStrokeWidth, mWidth / 2 + mStrokeWidth);

                                                                     mPointF1.x = MyUtil.dp2px(view.getContext(), 12) * 1f;
                                                                     mPointF1.y = mWidth / 2 * 1f;
                                                                     mPointF2.x = mWidth / 2 * 1f;
                                                                     mPointF2.y = MyUtil.dp2px(view.getContext(), 12) * 1f;
                                                                     mPointF3.x = mWidth - MyUtil.dp2px(view.getContext(), 12);
                                                                     mPointF3.y = mWidth / 2;
                                                                     mPointF4.x = mWidth / 2 * 1f;
                                                                     mPointF4.y = mWidth - MyUtil.dp2px(view.getContext(), 12) * 1f;
                                                                 }
                                                             }

        );

    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mWidth / 2, mWidth / 2, mWidth / 2, mPaint);
        canvas.drawLine(mPointF1.x, mPointF1.y, mPointF2.x, mPointF2.y, mStrokePaint);
        canvas.drawLine(mPointF1.x, mPointF1.y, mPointF3.x, mPointF3.y, mStrokePaint);
        canvas.drawLine(mPointF1.x, mPointF1.y, mPointF4.x, mPointF4.y, mStrokePaint);
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
