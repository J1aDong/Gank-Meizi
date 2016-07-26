package com.j1adong.meizi.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.j1adong.meizi.Config;
import com.j1adong.meizi.R;
import com.j1adong.meizi.WebViewActivity;
import com.j1adong.meizi.bean.GankData;
import com.j1adong.meizi.util.MyUtil;
import com.j1adong.recyclerviewhelper.RvBaseAdapter;
import com.j1adong.recyclerviewhelper.RvBaseAdapterHelper;

import java.util.List;

/**
 * Created by J1aDong on 16/7/22.
 */
public class GankAdapter extends RvBaseAdapter<GankData> {
    public GankAdapter(Context context, int layoutResId, List<GankData> list) {
        super(context, layoutResId, list);
    }

    @Override
    protected void convert(RvBaseAdapterHelper help, final GankData item, int position) {
        help.getTextView(R.id.tv_gank).setText(item.getDesc());
        MyUtil.setCusFont(help.getTextView(R.id.tv_gank), "FZQK.ttf");
        help.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(Config.URL, item.getUrl());
                mContext.startActivity(intent);
            }
        });
    }
}
