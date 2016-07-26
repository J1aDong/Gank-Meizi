package com.j1adong.meizi.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.widget.TextView;

import com.j1adong.meizi.GankDate;
import com.j1adong.meizi.bean.GankData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by J1aDong on 16/6/17.
 */
public class MyUtil {
    private static Typeface font;

    /**
     * 设置字体
     *
     * @param tv
     * @param path
     */
    public static void setCusFont(TextView tv, String path) {
        if (null == font) {
            font = Typeface.createFromAsset(tv.getContext().getAssets(), path);
        }
        tv.setTypeface(font);
    }

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    public static Drawable bitmap2drawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    public static int dp2px(Context context, int dpval) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpval, context.getResources().getDisplayMetrics());
    }

    /**
     * 字符串转换成日期
     * <p>
     * 2015-10-27T02:43:16.906000
     *
     * @param s
     * @return
     */
    public static GankDate string2GankDate(String s) {
        String regEx = "([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(s);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            result.append(matcher.group());
        }
        String s1 = result.toString();
        String[] dates = s1.split("-");
        GankDate gankDate = new GankDate(Integer.valueOf(dates[0]), Integer.valueOf(dates[1]), Integer.valueOf(dates[2]));
        return gankDate;
    }

    /**
     * 获取今天的日期
     *
     * @return
     */
    public static GankDate getToadyGankDate() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = format.format(date);
        String[] dates = dateString.split("-");
        GankDate gankDate = new GankDate(Integer.valueOf(dates[0]), Integer.valueOf(dates[1]), Integer.valueOf(dates[2]));
        return gankDate;
    }
}
