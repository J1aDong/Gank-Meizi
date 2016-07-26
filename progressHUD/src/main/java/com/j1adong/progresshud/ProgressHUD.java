package com.j1adong.progresshud;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntDef;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by J1aDong on 16/7/13.
 */
public class ProgressHUD {

    public static final int None = 0;   // 允许遮罩下面控件点击
    public static final int Clear = 1;  // 不允许遮罩下面控件点击
    public static final int Black = 2;  // 不允许遮罩下面控件点击，背景黑色半透明
    public static final int Gradient = 3;   // 不允许遮罩下面控件点击，背景渐变半透明
    public static final int ClearCancel = 4;    // 不允许遮罩下面控件点击，点击遮罩消失
    public static final int BlackCancel = 5;    // 不允许遮罩下面控件点击，背景黑色半透明，点击遮罩消失
    public static final int GradientCancel = 6;    // 不允许遮罩下面控件点击，背景渐变半透明，点击遮罩消失

    /**
     * 遮罩
     */
    @IntDef({None, Clear, Black, Gradient, ClearCancel, BlackCancel, GradientCancel})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ProgressHUDMaskType {
    }

    @ProgressHUDMaskType
    private int mProgressHUDMaskType;

    private Context mContext;
    //消失延迟的时间
    private static final long DISMISSDELAYED = 1000;


    private final FrameLayout.LayoutParams mParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);

    private ViewGroup mContentView;//activity的根View
    private ViewGroup mRootView;//hud的根View
    private ProgressDefaultView mSharedView;

    private Animation outAnimation;
    private Animation inAnimation;
    private int mGravity = Gravity.CENTER;

    /**
     * 构造函数
     */
    public ProgressHUD(Context context) {
        mContext = context;
        initViews();
        initDefaultView();
        initAnimation();
    }

    private void initViews() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mContentView = (ViewGroup) ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
        mRootView = (ViewGroup) layoutInflater.inflate(R.layout.layout_progresshud,null);
        //使rootView能够填充视图
        mRootView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void initDefaultView() {
        mSharedView = new ProgressDefaultView(mContext);
        mParams.gravity = mGravity;
        mSharedView.setLayoutParams(mParams);
    }

    private void initAnimation() {
        if ( null == inAnimation){
            inAnimation = getInAnimation();
        }
        if ( null == outAnimation){
            outAnimation = getOutAnimation();
        }
    }

    /**
     * show的时候调用
     */
    private void onAttached(){
        mContentView.addView(mRootView);
        if (null != mSharedView.getParent()){
            ((ViewGroup) mSharedView.getParent()).removeView(mSharedView);
        }
        mRootView.addView(mSharedView);
    }

    /**
     * 添加这个View到activity的根视图
     */
    private void svShow(){
        mHandler.removeCallbacksAndMessages(null);
        if (!isShowing()){
            onAttached();
        }

        mSharedView.startAnimation(inAnimation);
    }

    public void show(){
        setMaskType(Black);
        mSharedView.show();
        svShow();
    }

    public void showWithMaskType(@ProgressHUDMaskType int type){
        setMaskType(type);
        mSharedView.show();
        svShow();
    }

    public void showWithStatus(String string){
        setMaskType(Black);
        mSharedView.showWithStatus(string);
        svShow();
    }

    public void showWithStatus(String string, @ProgressHUDMaskType int maskType) {
        setMaskType(maskType);
        mSharedView.showWithStatus(string);
        svShow();
    }

    public void showInfoWithStatus(String string) {
        setMaskType(Black);
        mSharedView.showInfoWithStatus(string);
        svShow();
        scheduleDismiss();
    }

    public void showInfoWithStatus(String string, @ProgressHUDMaskType int maskType) {
        setMaskType(maskType);
        mSharedView.showInfoWithStatus(string);
        svShow();
        scheduleDismiss();
    }

    public void showSuccessWithStatus(String string) {
        setMaskType(Black);
        mSharedView.showSuccessWithStatus(string);
        svShow();
        scheduleDismiss();
    }

    public void showSuccessWithStatus(String string, @ProgressHUDMaskType int maskType) {
        setMaskType(maskType);
        mSharedView.showSuccessWithStatus(string);
        svShow();
        scheduleDismiss();
    }

    public void showErrorWithStatus(String string) {
        setMaskType(Black);
        mSharedView.showErrorWithStatus(string);
        svShow();
        scheduleDismiss();
    }

    public void showErrorWithStatus(String string, @ProgressHUDMaskType int maskType) {
        setMaskType(maskType);
        mSharedView.showErrorWithStatus(string);
        svShow();
        scheduleDismiss();
    }

    private void setMaskType(@ProgressHUDMaskType int maskType) {
        mProgressHUDMaskType = maskType;
        switch (mProgressHUDMaskType){
            case None:
                configMaskType(android.R.color.transparent, false, false);
                break;
            case Clear:
                configMaskType(android.R.color.transparent, true, false);
                break;
            case ClearCancel:
                configMaskType(android.R.color.transparent, true, true);
                break;
            case Black:
                configMaskType(R.color.bgColor_overlay, true, false);
                break;
            case BlackCancel:
                configMaskType(R.color.bgColor_overlay, true, true);
                break;
            case Gradient:
                //TODO 设置半透明渐变背景
                configMaskType(R.drawable.bg_overlay_gradient, true, false);
                break;
            case GradientCancel:
                //TODO 设置半透明渐变背景
                configMaskType(R.drawable.bg_overlay_gradient, true, true);
                break;
            default:
                break;
        }
    }

    private void configMaskType(int bg, boolean clickable, boolean cancelable) {
        mRootView.setBackgroundResource(bg);
        mRootView.setClickable(clickable);
        setCancelable(cancelable);
    }

    /**
     * 检测该View是不是已经添加到根视图
     *
     * @return 如果视图已经存在该View返回true
     */
    public boolean isShowing() {
        return mRootView.getParent() != null;
    }

    private Animation getInAnimation() {
        int res = ProgrssHUDAnimateUtil.getAnimationResource(this.mGravity,true);
        return AnimationUtils.loadAnimation(mContext,res);
    }

    private Animation getOutAnimation() {
        int res = ProgrssHUDAnimateUtil.getAnimationResource(this.mGravity,false);
        return AnimationUtils.loadAnimation(mContext,res);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismiss();
        }
    };

    /**
     * 设置是否能触摸消失
     *
     * @param isCancelable
     */
    private void setCancelable(boolean isCancelable){
        View view = mRootView.findViewById(R.id.out_container);

        if (isCancelable){
            view.setOnTouchListener(onCancelableTouchListener);
        }else {
            view.setOnTouchListener(null);
        }
    }

    private void scheduleDismiss(){
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessageDelayed(0,DISMISSDELAYED);
    }

    private final View.OnTouchListener onCancelableTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if ( MotionEvent.ACTION_DOWN == motionEvent.getAction()){
                dismiss();
                setCancelable(false);
            }
            return false;
        }
    };

    /**
     * 消失
     */
    public void dismiss() {
        outAnimation.setAnimationListener(outAnimListener);
        mSharedView.startAnimation(outAnimation);
    }

    Animation.AnimationListener outAnimListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            dismissImmediately();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    private void dismissImmediately() {
        mRootView.removeView(mSharedView);
        mContentView.removeView(mRootView);
        mContext = null;
    }
}
