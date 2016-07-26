package com.j1adong.meizi.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.j1adong.meizi.R;
import com.j1adong.meizi.util.MyUtil;

/**
 * TODO: document your custom view class.
 */
public class UITextView extends TextView {

    //是否是初次绘制
    private boolean mIsFirstDraw = true;

    //是否能按下变色
    private boolean mPressable;
    //按下背景色的画笔
    private Paint mPressPaint;
    private int mWidth; //控件的宽
    private int mHeight; //控件的高
    private RectF mRectF;
    private String mFontFamily; //字体路径,在assets文件夹下
    private int mRadius;

    public UITextView(Context context) {
        super(context);
        init(null, 0);
    }

    public UITextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public UITextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.UITextView, defStyle, 0);

        // 获得参数
        mPressable = a.getBoolean(R.styleable.UITextView_pressable, false);
        mFontFamily = a.getString(R.styleable.UITextView_fontFamily);

        a.recycle();

        // Set up a default TextPaint object
        //如果按下可变色,则设置可点击
        if (mPressable) {
            this.setClickable(true);
        }

        // 初始化画笔
        mPressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPressPaint.setColor(getResources().getColor(android.R.color.black));

        mRectF = new RectF();

        mRadius = MyUtil.dp2px(getContext(), 1);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //设置字体
        if (!TextUtils.isEmpty(mFontFamily)) {
            MyUtil.setCusFont(this, mFontFamily);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        if (mPressable && !mIsFirstDraw) {
            //为了能够实时的拿到当前的宽高,在此处设置
            mRectF.set(0, 0, mWidth, mHeight);
            canvas.drawRoundRect(mRectF, mRadius, mRadius, mPressPaint);
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
