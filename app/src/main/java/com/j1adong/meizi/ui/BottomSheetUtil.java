package com.j1adong.meizi.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.j1adong.meizi.R;
import com.j1adong.recyclerviewhelper.RvBaseAdapter;
import com.j1adong.recyclerviewhelper.RvBaseAdapterHelper;

import java.util.List;

/**
 * Created by J1aDong on 16/7/28.
 */
public class BottomSheetUtil {

    private final BottomSheetBehavior<View> behavior;
    private TextView mTvTopic;
    BottomSheetDialog mBottomSheetDialog;
    private RecyclerView mRecyclerView;
    private View mContentView;
    private Context mContext;
    private OnItemClickListener mListener = null;

    public BottomSheetUtil(Context context, String title, List<String> list) {
        mContext = context;
        mBottomSheetDialog = new BottomSheetDialog(context);
        mContentView = LayoutInflater.from(context).inflate(R.layout.view_bottom_sheet_dialog, null, false);
        mBottomSheetDialog.setContentView(mContentView);

        Window window = mBottomSheetDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.7f;
        window.setAttributes(lp);

        mTvTopic = (TextView) mContentView.findViewById(R.id.tv_topic);
        mTvTopic.setText(title);

        mRecyclerView = (RecyclerView) mContentView.findViewById(R.id.rv_bottom_sheet_dialog);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        SimpleStringAdapter adapter = new SimpleStringAdapter(context, R.layout.item_bottom_sheet_dialog, list);
        mRecyclerView.setAdapter(adapter);

        View parent = (View) mContentView.getParent();
        behavior = BottomSheetBehavior.from(parent);
        behavior.setPeekHeight(mContentView.getHeight());
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetDialog.dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    public void show() {
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        mBottomSheetDialog.show();
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        mBottomSheetDialog.setOnDismissListener(listener);
    }

    public void dismiss() {
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public class SimpleStringAdapter extends RvBaseAdapter<String> {

        public SimpleStringAdapter(Context context, int layoutResId, List<String> list) {
            super(context, layoutResId, list);
        }

        @Override
        protected void convert(RvBaseAdapterHelper help, String item, final int position) {
            help.getTextView(R.id.tv_bottom_sheet_dialog).setText(item);
            help.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != mListener) {
                        mListener.onItemClick(position);
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
