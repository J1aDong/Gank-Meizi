package com.j1adong.meizi.adapter;

import android.content.Context;

import com.j1adong.meizi.R;
import com.j1adong.recyclerviewhelper.RvBaseAdapter;
import com.j1adong.recyclerviewhelper.RvBaseAdapterHelper;

import java.util.List;

/**
 * Created by J1aDong on 16/7/30.
 */
public class SimpleStringAdapter extends RvBaseAdapter<String> {

    public SimpleStringAdapter(Context context, int layoutResId, List<String> list) {
        super(context, layoutResId, list);
    }

    @Override
    protected void convert(RvBaseAdapterHelper help, String item, int position) {
        help.getTextView(R.id.tv_bottom_sheet_dialog).setText(item);
    }
}
