package com.j1adong.recyclerviewhelper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import java.util.List;

/**
 * Created by J1aDong on 16/6/2.
 */
public abstract class RvBaseAdapter<T> extends RecyclerView.Adapter<RvBaseAdapterHelper> {

    public static final String TAG = RvBaseAdapter.class.getSimpleName();
    protected Context mContext;
    private int mLayoutResId;
    private List<T> mList;

    private boolean mNextLoadEnable;

    private View mHeaderView;
    private View mFooterView;
    protected static final int HEADER_VIEW = 0x00000111;
    protected static final int LOADING_VIEW = 0x00000222;
    protected static final int FOOTER_VIEW = 0x00000333;
    private boolean mLoadingMoreEnable = false;
    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean animationsLocked = false;
    private int mLastAnimatedPosition = -1;
    private boolean delayEnterAnimation = true;

    public RvBaseAdapter(Context context, int layoutResId, List<T> list) {
        this.mContext = context;
        this.mLayoutResId = layoutResId;
        this.mList = list;
    }

    @Override
    public RvBaseAdapterHelper onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 0) {
            return onCreateDefViewHolder(parent, viewType);
        } else if (viewType == LOADING_VIEW) {
            return new FooterViewHolder(getItemView(R.layout.def_loading, parent));
        } else if (viewType == HEADER_VIEW) {
            return new HeadViewHolder(mHeaderView);
        } else if (viewType == FOOTER_VIEW) {
            return new FooterViewHolder(mFooterView);
        } else {
            return onCreateDefViewHolder(parent, viewType);
        }
    }

    /**
     * 得到传入的List数组
     *
     * @return
     */
    public List<T> getList() {
        return mList;
    }

    public void isNextLoad(boolean isNextLoad) {
        mNextLoadEnable = isNextLoad;
        mLoadingMoreEnable = false;
        notifyDataSetChanged();
    }

    public static class HeadViewHolder extends RvBaseAdapterHelper {

        public HeadViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class FooterViewHolder extends RvBaseAdapterHelper {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    private RvBaseAdapterHelper onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return new ContentViewHolder(getItemView(mLayoutResId, parent));
    }

    public static class ContentViewHolder extends RvBaseAdapterHelper {

        public ContentViewHolder(View itemView) {
            super(itemView);
        }
    }

    protected View getItemView(int layoutResId, ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(layoutResId, parent, false);
    }

    @Override
    public void onBindViewHolder(RvBaseAdapterHelper helper, int position) {

        runEnterAnimation(helper.itemView, position);

        if (helper instanceof ContentViewHolder) {
            int index = position - getHeaderVeiwsCount();

            convert(helper, mList.get(index), index);
        } else if (helper instanceof FooterViewHolder) {
            if (mNextLoadEnable && !mLoadingMoreEnable && mOnLoadMoreListener != null) {
                mLoadingMoreEnable = true;
                mOnLoadMoreListener.onLoadMoreRequest();
                if (helper.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                    StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) helper.itemView.getLayoutParams();
                    params.setFullSpan(true);
                }
            }
        } else if (helper instanceof HeadViewHolder) {

        } else {
            int index = position - getHeaderVeiwsCount();
            onBindDefViewHolder(helper, mList.get(index), index);
        }
    }

    /**
     * 列表进入的动画
     *
     * @param view
     * @param position
     */
    private void runEnterAnimation(View view, int position) {
        if (animationsLocked) return;

        if (position > mLastAnimatedPosition) {
            mLastAnimatedPosition = position;
            view.setTranslationY(100);
            view.setAlpha(0.f);
            view.animate()
                    .translationY(0).alpha(1.f)
                    .setStartDelay(delayEnterAnimation ? 20 * (position) : 0)
                    .setInterpolator(new DecelerateInterpolator(2.f))
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animationsLocked = true;
                        }
                    })
                    .start();
        }
    }

    public void setDelayEnterAnimation(boolean delayEnterAnimation) {
        this.delayEnterAnimation = delayEnterAnimation;
    }

    public void setPreRefresh() {
        animationsLocked = false;
        mLastAnimatedPosition = -1;
    }

    /**
     * 需要覆盖
     *
     * @param helper
     * @param item
     * @param position
     */
    protected void onBindDefViewHolder(RvBaseAdapterHelper helper, T item, int position) {

    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        mNextLoadEnable = true;
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (null != mHeaderView && position == 0) {
            return HEADER_VIEW;
        } else if (position == mList.size() + getHeaderVeiwsCount()) {
            if (mNextLoadEnable)
                return LOADING_VIEW;
            else
                return FOOTER_VIEW;
        }
        return getDefItemViewType(position);
    }

    private int getDefItemViewType(int position) {
        return super.getItemViewType(position);
    }

    private int getHeaderVeiwsCount() {
        return mHeaderView == null ? 0 : 1;
    }

    private int getFooterViewsCount() {
        return mFooterView == null ? 0 : 1;
    }

    protected abstract void convert(RvBaseAdapterHelper help, T item, int position);

    /**
     * 添加HeaderView
     *
     * @param view
     */
    public void addHeaderView(View view) {
        if (null == view) {
            throw new RuntimeException("header is null");
        }
        this.mHeaderView = view;
        this.notifyDataSetChanged();
    }

    /**
     * 添加FooterView
     *
     * @param view
     */
    public void addFooterView(View view) {
        mNextLoadEnable = false;
        if (null == view) {
            throw new RuntimeException("footer is null");
        }
        this.mFooterView = view;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        int i = mNextLoadEnable ? 1 : 0;
        return mList.size() + i + getHeaderVeiwsCount() + getFooterViewsCount();
    }

    public interface OnLoadMoreListener {
        void onLoadMoreRequest();
    }
}