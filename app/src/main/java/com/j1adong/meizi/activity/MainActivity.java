package com.j1adong.meizi.activity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j1adong.blurview.BlurImageView;
import com.j1adong.meizi.Api;
import com.j1adong.meizi.GankDate;
import com.j1adong.meizi.HttpResult;
import com.j1adong.meizi.R;
import com.j1adong.meizi.UIImageView;
import com.j1adong.meizi.adapter.GankAdapter;
import com.j1adong.meizi.bean.GankData;
import com.j1adong.meizi.bean.GankDatas;
import com.j1adong.meizi.rx.rxandroid.SchedulersCompat;
import com.j1adong.meizi.ui.BottomSheetUtil;
import com.j1adong.meizi.ui.CircleDrawable;
import com.j1adong.meizi.ui.HamburgerDrawable;
import com.j1adong.meizi.ui.LeftDrawable;
import com.j1adong.meizi.ui.ReboundImageView;
import com.j1adong.meizi.ui.RefreshImageView;
import com.j1adong.meizi.ui.RightDrawable;
import com.j1adong.meizi.util.MyUtil;
import com.j1adong.meizi.util.SnackUtil;
import com.j1adong.meizi.util.StatusBarUtil;
import com.j1adong.recyclerviewhelper.stickyhead.StickyRecyclerHeadersDecoration;
import com.socks.library.KLog;
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
    @BindView(R.id.iv_hamburger)
    ReboundImageView mIvHamburger;
    @BindView(R.id.iv_menu)
    UIImageView mIvMenu;
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
    private BottomSheetUtil mBottomSheetUtil;
    private List<View> mPreActivityViews;
    private List<View> mBSAnimationViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        StatusBarUtil.setTranslucentForImageView(this, mRlContent);

        onPreActivityAnimation();

        mBehavior = BottomSheetBehavior.from(mBsRoot);
        mBehavior.setPeekHeight(MyUtil.dp2px(this, 40));

        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        mBSAnimationViews = new ArrayList<>();
        mBSAnimationViews.add(mIvHamburger);
        mBSAnimationViews.add(mIvPre);
        mBSAnimationViews.add(mIvNext);
        mIvMenu.setTranslationY(-MyUtil.dp2px(this, 100));
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    for (int i = 0; i < mBSAnimationViews.size(); i++) {
                        View view = mBSAnimationViews.get(i);
                        view.animate().setDuration(500).translationY(-MyUtil.dp2px(view.getContext(), 100)).setInterpolator(new FastOutSlowInInterpolator()).setStartDelay(i * 60).start();
                    }
                    mIvMenu.setVisibility(View.VISIBLE);
                    mIvMenu.animate().setDuration(500).translationY(0.f).setInterpolator(new DecelerateInterpolator(2.f)).setStartDelay(100).start();
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    for (int i = 0; i < mBSAnimationViews.size(); i++) {
                        View view = mBSAnimationViews.get(i);
                        view.animate().setDuration(500).translationY(0.f).setInterpolator(new DecelerateInterpolator(2.f)).setStartDelay(i * 40).start();
                    }
                    mIvMenu.animate().setDuration(500).translationY(-MyUtil.dp2px(bottomSheet.getContext(), 100)).setInterpolator(new DecelerateInterpolator(2.f)).setStartDelay(100).start();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                int width = MyUtil.dp2px(bottomSheet.getContext(), 80);
                int height = MyUtil.dp2px(bottomSheet.getContext(), 80);
                mTvIcon.getLayoutParams().width = (int) (width * slideOffset);
                mTvIcon.getLayoutParams().height = (int) (height * slideOffset);
                mTvIcon.requestLayout();

//                mIvHead.setAlpha(1 - slideOffset);
            }
        });

        mIvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startToUserProfileActivity(view);
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

                    mIvRefresh.refresh();
                }
            }
        });

        setupMenuDrawable();
        setupHamburgerDrawable();

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

                    mIvRefresh.refresh();
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

                    mIvRefresh.refresh();
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


        final float[] alpha = {0};
        final boolean[] flag = {false};
        mIvMeizi.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                flag[0] = true;
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
                    hideAll(false);
                    flag[0] = false;
                    mIvMeizi.animate().setDuration(100).setInterpolator(new DecelerateInterpolator())
                            .scaleX(1f).scaleY(1f).start();
                }
                return false;
            }
        });

    }

    private void setupMenuDrawable() {
        mIvMenu.setImageDrawable(getResources().getDrawable(R.mipmap.hzw));
    }

    /**
     * 设置汉堡包图案
     */
    private void setupHamburgerDrawable() {
        final HamburgerDrawable hamburgerDrawable = new HamburgerDrawable(mIvHamburger);
        mIvHamburger.setDrawable(hamburgerDrawable);
        mIvHamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (hamburgerDrawable.getType() == HamburgerDrawable.MENU) {

                    if (null != mBottomSheetUtil && null != mHistoryDates) {
                        hamburgerDrawable.setType(HamburgerDrawable.ERROR);
                        mBottomSheetUtil.show();
                        mBottomSheetUtil.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                hamburgerDrawable.setType(HamburgerDrawable.MENU);
                            }
                        });
                        mBottomSheetUtil.setOnItemClickListener(new BottomSheetUtil.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                KLog.w("按了" + position);
                                mBottomSheetUtil.dismiss();

                                mHistoryDatePosition = position;
                                GankDate gankDate = mHistoryDates.get(mHistoryDatePosition);
                                mTvDate.setText(gankDate.getYYYYMMdd());
                                getGankByDate(gankDate.getYear(), gankDate.getMonth(), gankDate.getDay());
                                mIvRefresh.refresh();
                            }
                        });
                    }

                } else {
                    hamburgerDrawable.setType(HamburgerDrawable.MENU);
                }
            }
        });
    }

    /**
     * 跳转到UserProfile的页面
     *
     * @param view
     */
    private void startToUserProfileActivity(View view) {
        int[] startingLocation = new int[2];
        view.getLocationOnScreen(startingLocation);
        startingLocation[0] += view.getWidth() / 2;
        UserProfileActivity.startUserProfileFromLocation(startingLocation, MainActivity.this);
        overridePendingTransition(0, 0);
    }

    /**
     * 隐藏除背景图以外的view
     *
     * @param flag
     */
    private void hideAll(boolean flag) {
        int duration = 200;
        if (flag) {
            mIvHamburger.animate().setInterpolator(new DecelerateInterpolator()).setStartDelay(0).setDuration(duration).alpha(0.f).start();
            mBsRoot.animate().setInterpolator(new DecelerateInterpolator()).setStartDelay(0).setDuration(duration).alpha(0.f).start();
            mIvRefresh.animate().setInterpolator(new DecelerateInterpolator()).setStartDelay(0).setDuration(duration).alpha(0.f).start();
            mIvNext.animate().setInterpolator(new DecelerateInterpolator()).setStartDelay(0).setDuration(duration).alpha(0.f).start();
            mIvPre.animate().setInterpolator(new DecelerateInterpolator()).setStartDelay(0).setDuration(duration).alpha(0.f).start();
            mIvMenu.animate().setInterpolator(new DecelerateInterpolator()).setStartDelay(0).setDuration(duration).alpha(0.f).start();
        } else {
            mIvHamburger.animate().setInterpolator(new DecelerateInterpolator()).setStartDelay(0).setDuration(duration).alpha(1.f).start();
            mBsRoot.animate().setInterpolator(new DecelerateInterpolator()).setStartDelay(0).setDuration(duration).alpha(1.f).start();
            mIvRefresh.animate().setInterpolator(new DecelerateInterpolator()).setStartDelay(0).setDuration(duration).alpha(1.f).start();
            mIvNext.animate().setInterpolator(new DecelerateInterpolator()).setStartDelay(0).setDuration(duration).alpha(1.f).start();
            mIvPre.animate().setInterpolator(new DecelerateInterpolator()).setStartDelay(0).setDuration(duration).alpha(1.f).start();
            mIvMenu.animate().setInterpolator(new DecelerateInterpolator()).setStartDelay(0).setDuration(duration).alpha(1.f).start();
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

                        Observable.from(mHistoryDates).map(new Func1<GankDate, String>() {
                            @Override
                            public String call(GankDate gankDate) {
                                return gankDate.getYYYYMMdd();
                            }
                        }).toList().compose(SchedulersCompat.<List<String>>applyExecutorSchedulers())
                                .subscribe(new Action1<List<String>>() {
                                    @Override
                                    public void call(List<String> list) {
                                        mBottomSheetUtil = new BottomSheetUtil(MainActivity.this, "请选择日期", list);
                                    }
                                });

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        SnackUtil.showShortWithAction(MainActivity.this, "获取历史日期列表失败", "退出", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });
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
//        Glide.with(getApplicationContext()).load(meiziDatas.get(i).getUrl()).into(mIvHead);
    }

    private void clearMeizi() {
        if (mMeiziDatas.size() == 0) {
            return;
        }
        Glide.with(getApplicationContext()).load(mMeiziDatas.get(0).getUrl()).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                mIvMeizi.clear(resource);
//                mIvHead.animate().setDuration(100).alpha(0).setInterpolator(new DecelerateInterpolator()).start();
            }
        });
    }

    @Override
    public void onPreActivityAnimation() {
        mPreActivityViews = new ArrayList<>();
        mPreActivityViews.add(mIvPre);
        mPreActivityViews.add(mIvNext);
        mPreActivityViews.add(mIvRefresh);
        mPreActivityViews.add(mIvHamburger);

        for (View view : mPreActivityViews) {
            view.setAlpha(0.f);
        }

        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mPreActivityViews.size(); i++) {
                    View view = mPreActivityViews.get(i);
                    view.setTranslationY(-200);
                    view.setAlpha(0.f);
                    view.animate().translationY(0).alpha(1.f)
                            .setStartDelay(200 * i)
                            .setInterpolator(new DecelerateInterpolator(2.f))
                            .setDuration(500)
                            .start();
                }
            }
        }, 200);

    }
}
