package com.j1adong.recyclerviewhelper;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by J1aDong on 16/6/2.
 */
public class RvBaseAdapterHelper extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;

    public RvBaseAdapterHelper(View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
    }

    public TextView getTextView(int viewId) {
        return retrieveView(viewId);
    }

    public Button getButton(int viewId) {
        return retrieveView(viewId);
    }

    public ImageView getImageVeiw(int viewId) {
        return retrieveView(viewId);
    }

    public View getVeiw(int viewId) {
        return retrieveView(viewId);
    }

    protected <T extends View> T retrieveView(int viewId) {
        View view = mViews.get(viewId);
        if (null == view) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }
}
