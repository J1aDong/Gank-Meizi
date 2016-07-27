package com.j1adong.meizi.util;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by J1aDong on 16/7/24.
 */
public class SnackUtil {

    public static void showShort(Activity activity, String s) {
        Snackbar.make(activity.getWindow().getDecorView(), s, Snackbar.LENGTH_SHORT).show();
    }

    public static void showShortWithAction(Activity activity, String s, String actionString, View.OnClickListener onClickListener) {
        Snackbar.make(activity.getWindow().getDecorView(), s, Snackbar.LENGTH_INDEFINITE).setAction(actionString, onClickListener).show();
    }
}
