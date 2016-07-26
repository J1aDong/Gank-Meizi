package com.j1adong.meizi;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.facebook.rebound.SpringSystem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j1adong.blurview.BlurImageView;
import com.j1adong.meizi.adapter.GankAdapter;
import com.j1adong.meizi.bean.GankData;
import com.j1adong.meizi.bean.GankDatas;
import com.j1adong.meizi.bean.Meizi;
import com.j1adong.meizi.rx.rxandroid.SchedulersCompat;
import com.j1adong.meizi.ui.LeftDrawable;
import com.j1adong.meizi.ui.ReboundImageView;
import com.j1adong.meizi.ui.RefreshImageView;
import com.j1adong.meizi.ui.RightDrawable;
import com.j1adong.meizi.util.MyUtil;
import com.j1adong.meizi.util.SnackUtil;
import com.j1adong.meizi.util.StatusBarUtil;
import com.j1adong.recyclerviewhelper.stickyhead.StickyRecyclerHeadersDecoration;
import com.socks.library.KLog;
import com.tumblr.backboard.Actor;
import com.tumblr.backboard.MotionProperty;
import com.tumblr.backboard.imitator.Imitator;
import com.tumblr.backboard.imitator.InertialImitator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends BaseActivity {

    @BindView(R.id.iv_meizi)
    BlurImageView mIvMeizi;
    @BindView(R.id.iv_head)
    UIImageView mIvHead;
    @BindView(R.id.rv_ganks)
    RecyclerView mRvGanks;
    @BindView(R.id.rl_content)
    RelativeLayout mRlContent;
    @BindView(R.id.bs_root)
    LinearLayout mBsRoot;
    @BindView(R.id.iv_refresh)
    RefreshImageView mIvRefresh;
    @BindView(R.id.tv_date)
    TextView mTvDate;
    @BindView(R.id.tv_icon)
    UIImageView mTvIcon;
    @BindView(R.id.constraint)
    FrameLayout mConstraint;
    @BindView(R.id.iv_next)
    ReboundImageView mIvNext;
    @BindView(R.id.iv_pre)
    ReboundImageView mIvPre;
    @BindView(R.id.bs_topic)
    LinearLayout mBsTopic;

    /**
     * 进来后网络请求后显示图片
     */
    boolean mIsFirstShow = true;
    private List<GankData> mGankDatas = new ArrayList<>();
    private GankAdapter mGankAdapter;
    private List<GankDate> mHistoryDates;
    private BottomSheetBehavior<LinearLayout> mBehavior;
    /**
     * 当前可以显示的妹子图片
     */
    private List<GankData> mMeiziDatas = new ArrayList<>();
    /**
     * 当前的显示的日期在历史日期中的position
     */
    private int mHistoryDatePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        StatusBarUtil.setTranslucentForImageView(this, mRlContent);

        mBehavior = BottomSheetBehavior.from(mBsRoot);
        mBehavior.setPeekHeight(MyUtil.dp2px(this, 40));

        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                int width = MyUtil.dp2px(bottomSheet.getContext(), 80);
                int height = MyUtil.dp2px(bottomSheet.getContext(), 80);
                mTvIcon.getLayoutParams().width = (int) (width * slideOffset);
                mTvIcon.getLayoutParams().height = (int) (height * slideOffset);
                mTvIcon.requestLayout();

                mIvHead.setAlpha(1 - slideOffset);
            }
        });

        //点击底部栏的头部切换展开收拢效果
        mBsRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        mRvGanks.setLayoutManager(new LinearLayoutManager(this));
        mRvGanks.setItemAnimator(new DefaultItemAnimator());
        mGankAdapter = new GankAdapter(MainActivity.this, R.layout.item_gank, mGankDatas);
        mRvGanks.setAdapter(mGankAdapter);

        StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(mGankAdapter);
        mRvGanks.addItemDecoration(headersDecoration);

        getHistoryDate();
//        getMeizi();

        mIvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mHistoryDates) {
                    int size = mHistoryDates.size();
                    mHistoryDatePosition = (int) (Math.random() * size);
                    GankDate gankDate = mHistoryDates.get(mHistoryDatePosition);
                    mTvDate.setText(gankDate.getYYYYMMdd());
                    getGankByDate(gankDate.getYear(), gankDate.getMonth(), gankDate.getDay());

                    mIvRefresh.start();
                }
            }
        });

        RightDrawable rightDrawable = new RightDrawable(mIvNext);
        mIvNext.setDrawable(rightDrawable);
        mIvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHistoryDatePosition > 0) {
                    mHistoryDatePosition--;

                    GankDate gankDate = mHistoryDates.get(mHistoryDatePosition);
                    mTvDate.setText(gankDate.getYYYYMMdd());
                    getGankByDate(gankDate.getYear(), gankDate.getMonth(), gankDate.getDay());

                    mIvRefresh.start();
                } else {
                    SnackUtil.showShort(MainActivity.this, "到最新了");
                }
            }
        });


        LeftDrawable leftDrawable = new LeftDrawable(mIvPre);
        mIvPre.setDrawable(leftDrawable);
        mIvPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHistoryDatePosition < mHistoryDates.size() - 1) {
                    mHistoryDatePosition++;

                    GankDate gankDate = mHistoryDates.get(mHistoryDatePosition);
                    mTvDate.setText(gankDate.getYYYYMMdd());
                    getGankByDate(gankDate.getYear(), gankDate.getMonth(), gankDate.getDay());

                    mIvRefresh.start();
                } else {
                    SnackUtil.showShort(MainActivity.this, "到底了");
                }
            }
        });

        final InertialImitator motionImitatorX =
                new InertialImitator(MotionProperty.X, Imitator.TRACK_DELTA,
                        Imitator.FOLLOW_SPRING, 0, 0);

        final InertialImitator motionImitatorY =
                new InertialImitator(MotionProperty.Y, Imitator.TRACK_DELTA,
                        Imitator.FOLLOW_SPRING, 0, 0);

        new Actor.Builder(SpringSystem.create(), mIvHead)
                .addMotion(motionImitatorX, View.TRANSLATION_X)
                .addMotion(motionImitatorY, View.TRANSLATION_Y)
                .build();

        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                motionImitatorX.setMinValue(
                        -mConstraint.getMeasuredWidth() / 2 + mIvHead.getMeasuredWidth() / 2);
                motionImitatorX.setMaxValue(
                        mConstraint.getMeasuredWidth() / 2 - mIvHead.getMeasuredWidth() / 2);
                motionImitatorY.setMinValue(
                        -mConstraint.getMeasuredHeight() / 2 + mIvHead.getMeasuredWidth() / 2);
                motionImitatorY.setMaxValue(
                        mConstraint.getMeasuredHeight() / 2 - mIvHead.getMeasuredWidth() / 2);
                rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        final float[] alpha = {0};
        final boolean[] flag = {false};
        mIvMeizi.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                flag[0] = true;
                alpha[0] = mIvHead.getAlpha();
                clearMeizi();
                hideAll(true);
                mIvMeizi.animate().setDuration(100).setInterpolator(new DecelerateInterpolator())
                        .scaleX(1.05f).scaleY(1.05f).start();
                return false;
            }
        });

        mIvMeizi.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP && flag[0]) {
                    showMeizi(mMeiziDatas);
                    mIvHead.setAlpha(alpha[0]);
                    hideAll(false);
                    flag[0] = false;
                    mIvMeizi.animate().setDuration(100).setInterpolator(new DecelerateInterpolator())
                            .scaleX(1f).scaleY(1f).start();
                }
                return false;
            }
        });

    }

    /**
     * 隐藏除背景图以外的view
     *
     * @param flag
     */
    private void hideAll(boolean flag) {
        if (flag) {
            mBsRoot.animate().setInterpolator(new DecelerateInterpolator()).setDuration(100).alpha(0f).start();
            mIvRefresh.animate().setInterpolator(new DecelerateInterpolator()).setDuration(100).alpha(0f).start();
            mIvNext.animate().setInterpolator(new DecelerateInterpolator()).setDuration(100).alpha(0f).start();
            mIvPre.animate().setInterpolator(new DecelerateInterpolator()).setDuration(100).alpha(0f).start();
        } else {
            mBsRoot.animate().setInterpolator(new DecelerateInterpolator()).setDuration(100).alpha(1f).start();
            mIvRefresh.animate().setInterpolator(new DecelerateInterpolator()).setDuration(100).alpha(1f).start();
            mIvNext.animate().setInterpolator(new DecelerateInterpolator()).setDuration(100).alpha(1f).start();
            mIvPre.animate().setInterpolator(new DecelerateInterpolator()).setDuration(100).alpha(1f).start();
        }
    }

    private void getHistoryDate() {
        Api.getApiService().getHistoryDate().flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(String s) {
                HttpResult<List<String>> result = new Gson().fromJson(s, new TypeToken<HttpResult<List<String>>>() {
                }.getType());
                if (result.isError()) {
                    return Observable.error(new Throwable("请求失败"));
                } else {
                    List<String> gankDates = result.getResults();
                    return Observable.from(gankDates);
                }
            }
        }).map(new Func1<String, GankDate>() {
            @Override
            public GankDate call(String s) {
                String[] dates = s.split("-");
                GankDate gankDate = new GankDate(Integer.valueOf(dates[0]), Integer.valueOf(dates[1]), Integer.valueOf(dates[2]));
                return gankDate;
            }
        }).toList().compose(SchedulersCompat.<List<GankDate>>applyExecutorSchedulers())
                .subscribe(new Action1<List<GankDate>>() {
                    @Override
                    public void call(List<GankDate> gankDates) {
                        mHistoryDates = gankDates;
                        KLog.w("历史日期", mHistoryDates.size());
                        if (mHistoryDates.get(0).compareTo(MyUtil.getToadyGankDate())) {
                            SnackUtil.showShort(MainActivity.this, "今天的Gank更新拉");
                        } else {
                            SnackUtil.showShort(MainActivity.this, "今天木有更新Gank");
                        }
                        mHistoryDatePosition = 0;
                        GankDate gankDate = mHistoryDates.get(0);
                        mTvDate.setText(gankDate.getYYYYMMdd());
                        getGankByDate(gankDate.getYear(), gankDate.getMonth(), gankDate.getDay());

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        SnackUtil.showShort(MainActivity.this, "获取历史日期列表失败");
                    }
                });
    }

    private void getGankByDate(int year, int month, int day) {
        Api.getApiService().getGankByDate(year, month, day).flatMap(new Func1<String, Observable<GankDatas>>() {
            @Override
            public Observable<GankDatas> call(String s) {
                HttpResult<GankDatas> result = new Gson().fromJson(s, new TypeToken<HttpResult<GankDatas>>() {
                }.getType());
                if (result.isError()) {
                    return Observable.error(new Throwable("请求失败"));
                } else {
                    return Observable.just(result.getResults());
                }
            }
        }).map(new Func1<GankDatas, List<GankData>>() {
            @Override
            public List<GankData> call(GankDatas gankDatas) {
                mMeiziDatas.clear();
                if (null == gankDatas.getMeizis()) {
                    KLog.w("木有妹子");
                } else {
                    mMeiziDatas.addAll(gankDatas.getMeizis());
                }
                return gankDatas.getGankDatas();
            }
        }).compose(SchedulersCompat.<List<GankData>>applyExecutorSchedulers())
                .subscribe(new Action1<List<GankData>>() {
                    @Override
                    public void call(List<GankData> gankDatas) {

                        showMeizi(mMeiziDatas);

                        mRvGanks.scrollToPosition(0);
                        mGankDatas.clear();
                        mGankDatas.addAll(gankDatas);
                        mGankAdapter.setPreRefresh();
                        mGankAdapter.notifyDataSetChanged();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        SnackUtil.showShort(MainActivity.this, "获取Gank数据失败");
                    }
                });
    }

    private void showMeizi(List<GankData> meiziDatas) {
        if (null == meiziDatas) {
            SnackUtil.showShort(this, "这期木有妹子");
            return;
        }

        int i = 0;
        Glide.with(getApplicationContext()).load(meiziDatas.get(i).getUrl()).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                mIvMeizi.blur(resource);
                mTvIcon.setImageBitmap(resource);
            }
        });
        Glide.with(getApplicationContext()).load(meiziDatas.get(i).getUrl()).into(mIvHead);
    }

    private void clearMeizi() {
        if (mMeiziDatas.size() == 0) {
            return;
        }
        Glide.with(getApplicationContext()).load(mMeiziDatas.get(0).getUrl()).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                mIvMeizi.clear(resource);
                mIvHead.animate().setDuration(100).alpha(0).setInterpolator(new DecelerateInterpolator()).start();
            }
        });
    }
}
