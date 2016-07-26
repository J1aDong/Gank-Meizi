package com.j1adong.progresshud;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by J1aDong on 16/7/13.
 */
public class ProgressDefaultView extends LinearLayout {
    private String TAG = getClass().getSimpleName();

    private int resInfo = R.mipmap.ic_svstatus_info;
    private int resSuccess = R.mipmap.ic_svstatus_success;
    private int resError = R.mipmap.ic_svstatus_error;

    private ImageView ivBigLoading;
    private ImageView ivSmallLoading;
    private TextView tvMsg;
    private MaterialProgressDrawable materialProgressDrawable;
    private int[] colors = {
            0xFFFF0000, 0xFFFF7F00, 0xFFFFFF00, 0xFF00FF00
            , 0xFF00FFFF, 0xFF0000FF, 0xFF8B00FF
    };
    private int CIRCLE_BG_LIGHT = 0xFFFAFAFA;

    public ProgressDefaultView(Context context) {
        super(context);
        initViews();
    }


    private void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_progressdefault, this, true);
        ivBigLoading = (ImageView) findViewById(R.id.ivBigLoading);
        ivSmallLoading = (ImageView) findViewById(R.id.ivSmallLoading);
        tvMsg = (TextView) findViewById(R.id.tvMsg);

        materialProgressDrawable = new MaterialProgressDrawable(getContext(), ivBigLoading);
        materialProgressDrawable.setBackgroundColor(CIRCLE_BG_LIGHT);
        materialProgressDrawable.setColorSchemeColors(colors);
        materialProgressDrawable.updateSizes(MaterialProgressDrawable.LARGE);
        materialProgressDrawable.setAlpha(255);//设置不透明
    }

    public void show() {
        ivBigLoading.setImageDrawable(materialProgressDrawable);
        ivBigLoading.setVisibility(VISIBLE);

        //开启动画
        materialProgressDrawable.start();
    }

    public void showWithStatus(String string) {
        ivBigLoading.setImageDrawable(materialProgressDrawable);
        ivBigLoading.setVisibility(VISIBLE);

        tvMsg.setText(string);
        tvMsg.setVisibility(VISIBLE);

        materialProgressDrawable.start();
    }

    public void showInfoWithStatus(String string) {
        showBaseStatus(resInfo,string);
    }

    public void showSuccessWithStatus(String string) {
        showBaseStatus(resSuccess,string);
    }

    public void showErrorWithStatus(String string) {
        showBaseStatus(resError, string);
    }

    private void showBaseStatus(int resId, String string) {
        ivSmallLoading.setImageResource(resId);
        tvMsg.setText(string);

        ivBigLoading.setVisibility(GONE);
        ivSmallLoading.setVisibility(VISIBLE);
        tvMsg.setVisibility(VISIBLE);
    }
}
