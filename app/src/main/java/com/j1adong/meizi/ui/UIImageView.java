package com.j1adong.meizi.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.j1adong.meizi.R;
import com.j1adong.meizi.util.MyUtil;

/**
 * Created by J1aDong on 16/6/17.
 */
public class UIImageView extends ImageView {

    /**
     * 图片的类型，圆形or圆角
     */
    private int mType;
    private static final int TYPE_CIRCLE = 0;
    private static final int TYPE_ROUND = 1;

    /**
     * 圆角大小的默认值
     */
    private static final int BODER_RADIUS_DEFAULT = 1;

    //圆角的大小
    private int mBorderRadius;

    /**
     * 绘图的Paint
     */
    private Paint mBitmapPaint;

    /**
     * 圆角的半径
     */
    private int mRadius;

    /**
     * 3x3 矩阵，主要用于缩小放大
     */
    private Matrix mMatrix;

    /**
     * 渲染图像，使用图像为绘制图形着色
     */
    private BitmapShader mBitmapShader;

    /**
     * View的宽度
     */
    private int mLength;
    private RectF mRoundRect;

    private static final String STATE_INSTANCE = "state_instance";
    private static final String STATE_TYPE = "state_type";
    private static final String STATE_BORDER_RADIUS = "state_border_radius";

    //是否是初次绘制
    private boolean mIsFirstDraw = true;

    //是否能按下变色
    private boolean mPressable;
    //按下背景色的画笔
    private Paint mPressPaint;
    private int mWidth; //控件的宽
    private int mHeight; //控件的高
    private Bitmap mBitmap;

    public UIImageView(Context context) {
        super(context);
        init(null, 0);
    }

    public UIImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public UIImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.UIImageView);
        mBorderRadius = array.getDimensionPixelSize(R.styleable.UIImageView_borderRadius, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BODER_RADIUS_DEFAULT, getResources().getDisplayMetrics()));//默认为10dp
        mType = array.getInt(R.styleable.UIImageView_type, TYPE_ROUND);
        mPressable = array.getBoolean(R.styleable.UIImageView_pressable, false);
        array.recycle();

        mMatrix = new Matrix();
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (mPressable) {
            this.setClickable(true);
        }

        mPressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPressPaint.setColor(getResources().getColor(android.R.color.black));

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_INSTANCE, super.onSaveInstanceState());
        bundle.putInt(STATE_TYPE, mType);
        bundle.putInt(STATE_BORDER_RADIUS, mBorderRadius);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(((Bundle) state)
                    .getParcelable(STATE_INSTANCE));
            this.mType = bundle.getInt(STATE_TYPE);
            this.mBorderRadius = bundle.getInt(STATE_BORDER_RADIUS);
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (TYPE_CIRCLE == mType) {
            mLength = Math.min(getMeasuredWidth(), getMeasuredHeight());
            mRadius = mLength / 2;
            setMeasuredDimension(mLength, mLength);
        }
    }

    private void setUpShader() {
        Drawable drawable = getDrawable();
        if (null == drawable) {
            return;
        }

        mBitmap = MyUtil.drawableToBitmap(drawable);
        //将bmp作为着色器,就是在指定区域内绘制bmp
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        float scale = 1.0f;
        if (TYPE_CIRCLE == mType) {
            //拿到bitmap宽或者高的小值
            int bSize = Math.min(mBitmap.getWidth(), mBitmap.getHeight());
            scale = mLength * 1.0f / bSize;
        } else if (TYPE_ROUND == mType) {
            //如果图片的宽或者高与view的宽高不匹配,计算出需要缩放的比例;缩放后的图片的宽高,一定要大于我们view的宽高;所以我们这里取大值
            scale = Math.max(getWidth() * 1.0f / mBitmap.getWidth(), getHeight() * 1.0f / mBitmap.getHeight());
        }
        //shader的变形矩阵,我们这里主要用于放大或者缩小
        mMatrix.setScale(scale, scale);
        //设置变换矩阵
        mBitmapShader.setLocalMatrix(mMatrix);
        //设置shader
        mBitmapPaint.setShader(mBitmapShader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (null == getDrawable()) {
            return;
        }
        setUpShader();

        if (TYPE_ROUND == mType) {
            canvas.drawRoundRect(mRoundRect, mBorderRadius, mBorderRadius, mBitmapPaint);
        } else {
            canvas.drawCircle(mRadius, mRadius, mRadius, mBitmapPaint);
        }

        if (mPressable && !mIsFirstDraw) {
            if (TYPE_ROUND == mType) {
                canvas.drawRoundRect(mRoundRect, mBorderRadius, mBorderRadius, mPressPaint);
            } else {
                canvas.drawCircle(mRadius, mRadius, mRadius, mPressPaint);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

        //圆角图片的范围
        if (TYPE_ROUND == mType) {
            mRoundRect = new RectF(0, 0, getWidth(), getHeight());
        }
    }

    public void setBorderRadius(int borderRadius) {
        int pxVal = MyUtil.dp2px(getContext(), borderRadius);
        if (this.mBorderRadius != pxVal) {
            this.mBorderRadius = pxVal;
            invalidate();
        }
    }

    public void setType(int type) {
        if (this.mType != type) {
            this.mType = type;
            if (this.mType != TYPE_ROUND && this.mType != TYPE_CIRCLE) {
                this.mType = TYPE_CIRCLE;
            }
            requestLayout();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPressPaint.setAlpha(50);
                mIsFirstDraw = false;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPressPaint.setAlpha(0);
                mIsFirstDraw = false;
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }
}
