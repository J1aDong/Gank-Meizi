package com.j1adong.meizi.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;

import com.j1adong.blurview.BlurImageView;
import com.j1adong.meizi.listener.AppBarStateChangeListener;
import com.j1adong.meizi.R;
import com.j1adong.meizi.adapter.SimpleStringAdapter;
import com.j1adong.meizi.ui.RevealBackgroundView;
import com.j1adong.meizi.util.MyUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by J1aDong on 16/7/28.
 */
public class UserProfileActivity extends BaseActivity {
    //上一个页面需要画圆的起点坐标
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    @BindView(R.id.revealbackground)
    RevealBackgroundView mRevealbackground;
    @BindView(R.id.rl_content)
    CoordinatorLayout mRlContent;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppbarLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.iv_background)
    BlurImageView mIvBackground;

    public static void startUserProfileFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);

        mRevealbackground.setFillPaintColor(getResources().getColor(R.color.colorPrimary));

        mCollapsingToolbarLayout.setTitle(" ");


//        StatusBarUtil.setTranslucentForImageView(this,0,null);

        //使用CollapsingToolbarLayout必须把title设置到CollapsingToolbarLayout上，设置到Toolbar上则不会显示
//        mCollapsingToolbarLayout.setTitle("CollapsingToolbarLayout");
//        //通过CollapsingToolbarLayout修改字体颜色
//        mCollapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);//设置还没收缩时状态下字体颜色
//        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.GREEN);//设置收缩后Toolbar上字体的颜色
        setupToolbar();
        setupRevealbackGround(savedInstanceState);

    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setupRevealbackGround(Bundle savedInstanceState) {
        mRevealbackground.setOnStateChangeListener(new RevealBackgroundView.OnStateChangeListener() {
            @Override
            public void onStateChange(int state) {
                //动画结束,元素显示
                if (state == RevealBackgroundView.STATE_FINISHED) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mAppbarLayout.setVisibility(View.VISIBLE);

                    onPreActivityAnimation();
                } else {
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    mAppbarLayout.setVisibility(View.INVISIBLE);
                }
            }
        });
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            mRevealbackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mRevealbackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    mRevealbackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            mRevealbackground.setToFinishedFrame();
        }
    }

    @Override
    public void onPreActivityAnimation() {
        mIvBackground.blur(BitmapFactory.decodeResource(getResources(), R.mipmap.hzw));

        mAppbarLayout.setTranslationY(MyUtil.dp2px(this, -100));
        mAppbarLayout.setAlpha(0.f);
        mAppbarLayout.animate().setDuration(300).alpha(1.f).setInterpolator(new DecelerateInterpolator()).setStartDelay(20).translationY(0).start();

        mRecyclerView.setTranslationY(MyUtil.dp2px(this, 100));
        mRecyclerView.setAlpha(0.f);
        mRecyclerView.animate().setDuration(300).alpha(1.f).setInterpolator(new DecelerateInterpolator()).setStartDelay(20).translationY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setupRecyclerView();
            }
        }).start();


    }

    private void setupRecyclerView() {
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            strings.add("项目" + i);
        }
        final SimpleStringAdapter adapter = new SimpleStringAdapter(UserProfileActivity.this, R.layout.item_bottom_sheet_dialog, strings);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(UserProfileActivity.this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
