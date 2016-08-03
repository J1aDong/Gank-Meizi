package com.j1adong.meizi.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;

import com.facebook.rebound.SpringUtil;
import com.j1adong.meizi.R;
import com.j1adong.meizi.util.MyUtil;
import com.socks.library.KLog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by J1aDong on 16/7/27.
 */
public class HamburgerDrawable extends Drawable {
    private View view;
    private int mWidth;
    private Paint mPaint;
    private Paint mStrokePaint;
    private Paint mStrokePaint2;

    @TYPE
    private int mType = MENU;

    public static final int MENU = 0;
    public static final int ERROR = 1;
    public static final int LEFT = 2;

    @IntDef({MENU, ERROR, LEFT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TYPE {
    }

    //Menu图案
    private PointF mPoint1 = new PointF();
    private PointF mPoint2 = new PointF();
    private PointF mPoint3 = new PointF();
    private PointF mPoint4 = new PointF();
    private PointF mPoint5 = new PointF();
    private PointF mPoint6 = new PointF();

    public HamburgerDrawable(final View view) {
        this.view = view;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(view.getContext().getResources().getColor(android.R.color.white));
        mPaint.setStyle(Paint.Style.FILL);

        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setColor(view.getContext().getResources().getColor(R.color.red));
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeJoin(Paint.Join.ROUND);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        mStrokePaint.setStrokeWidth(MyUtil.dp2px(view.getContext(), 3));

        mStrokePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint2.setColor(view.getContext().getResources().getColor(R.color.red));
        mStrokePaint2.setStyle(Paint.Style.STROKE);
        mStrokePaint2.setStrokeJoin(Paint.Join.ROUND);
        mStrokePaint2.setStrokeCap(Paint.Cap.ROUND);
        mStrokePaint2.setStrokeWidth(MyUtil.dp2px(view.getContext(), 3));

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mWidth = view.getWidth();
                Context context = view.getContext();

                mPoint3.x = MyUtil.dp2px(context, 12);
                mPoint3.y = mWidth / 2;
                mPoint4.x = mWidth - MyUtil.dp2px(context, 12);
                mPoint4.y = mWidth / 2;

                mPoint1.x = MyUtil.dp2px(context, 12);
                mPoint1.y = mWidth / 2 - MyUtil.dp2px(context, 8);
                mPoint2.x = mWidth - MyUtil.dp2px(context, 12);
                mPoint2.y = mWidth / 2 - MyUtil.dp2px(context, 8);

                mPoint5.x = MyUtil.dp2px(context, 12);
                mPoint5.y = mWidth / 2 + MyUtil.dp2px(context, 8);
                mPoint6.x = mWidth - MyUtil.dp2px(context, 12);
                mPoint6.y = mWidth / 2 + MyUtil.dp2px(context, 8);
            }
        });
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mWidth / 2, mWidth / 2, mWidth / 2, mPaint);

        canvas.drawLine(mPoint1.x, mPoint1.y, mPoint2.x, mPoint2.y, mStrokePaint);
        canvas.drawLine(mPoint3.x, mPoint3.y, mPoint4.x, mPoint4.y, mStrokePaint2);
        canvas.drawLine(mPoint5.x, mPoint5.y, mPoint6.x, mPoint6.y, mStrokePaint);
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

    public void setType(@TYPE int type) {
        if (type == ERROR) {
            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(500);
            animator.setInterpolator(new DecelerateInterpolator(2.f));
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float value = (float) valueAnimator.getAnimatedValue();
                    mPoint2.y = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, mWidth / 2 - MyUtil.dp2px(view.getContext(), 8), mWidth / 2 + MyUtil.dp2px(view.getContext(), 8));
                    mStrokePaint2.setAlpha((int) SpringUtil.mapValueFromRangeToRange(value, 0, 1, 255, 0));
                    mPoint6.y = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, mWidth / 2 + MyUtil.dp2px(view.getContext(), 8), mWidth / 2 - MyUtil.dp2px(view.getContext(), 8));
                    invalidateSelf();
                }
            });
            animator.start();
            mType = ERROR;
        } else if (type == MENU) {
            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(500);
            animator.setInterpolator(new DecelerateInterpolator(2.f));
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float value = (float) valueAnimator.getAnimatedValue();
                    mPoint2.y = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, mWidth / 2 + MyUtil.dp2px(view.getContext(), 8), mWidth / 2 - MyUtil.dp2px(view.getContext(), 8));
                    mStrokePaint2.setAlpha((int) SpringUtil.mapValueFromRangeToRange(value, 0, 1, 0, 255));
                    mPoint6.y = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, mWidth / 2 - MyUtil.dp2px(view.getContext(), 8), mWidth / 2 + MyUtil.dp2px(view.getContext(), 8));
                    invalidateSelf();
                }
            });
            animator.start();
            mType = MENU;
        }
    }

    public int getType() {
        return mType;
    }
}
