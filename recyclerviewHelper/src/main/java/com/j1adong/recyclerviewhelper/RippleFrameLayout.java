package com.j1adong.recyclerviewhelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by J1aDong on 16/7/14.
 */
public class RippleFrameLayout extends FrameLayout {

    private final Drawable drawable;

    public RippleFrameLayout(Context context) {
        this(context, null);
    }

    public RippleFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int[] attr = new int[]{android.R.attr.selectableItemBackground};
        TypedArray array = context.obtainStyledAttributes(attr);
        drawable = array.getDrawable(0);
        array.recycle();

        setBackgroundDrawable(drawable);
        setClickable(true);
    }
}
