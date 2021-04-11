package org.devio.hi.imooc.banner.core;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

public class HiViewPager extends ViewPager {

    private int mIntervalTime;
    private boolean mAutoPlay = false;
    private boolean isLayout;

    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            nextPage();
            mHandler.postDelayed(this, mIntervalTime);
        }
    };


    public HiViewPager(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(isLayout && getAdapter() != null || getAdapter().getCount() >0){
            try {
                //fix 使用RecyclerView + ViewPager bug https://blog.csdn.net/u011002668/article/details/72884893
                Field mScroller = ViewPager.class.getDeclaredField("mFirstLayout");
                mScroller.setAccessible(true);
                mScroller.set(this, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        startPlay();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        isLayout = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        //fix 使用RecyclerView + ViewPager bug
        if (((Activity) getContext()).isFinishing()) {
            super.onDetachedFromWindow();
        }
        stopPlay();
    }

    // 切换到下一张页面
    private int nextPage() {
        int nextPosition = -1;
        if(getAdapter() == null || getAdapter().getCount() <=1){
            stopPlay();
            return nextPosition;
        }
        nextPosition = getCurrentItem() + 1;
        if(nextPosition >= getAdapter().getCount()){
            // TODO Tips: 获取第一个item的索引 不一定是0
            nextPosition = ((HiBannerAdapter) getAdapter()).getFirstItemPosition();
        }
        setCurrentItem(nextPosition, true);
        return nextPosition;
    }

    public void setAutoPlay(boolean audoPlay) {
        this.mAutoPlay = audoPlay;
        if(!audoPlay){
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    // 用户手动滑动进行页面切换时 不开启自动播放功能
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                startPlay();
                break;
            default:
                stopPlay();
        }
        return super.onTouchEvent(ev);
    }

    public void startPlay() {
        mHandler.removeCallbacksAndMessages(null);
        if(mAutoPlay){
            mHandler.postDelayed(mRunnable, mIntervalTime);
        }
    }

    public void stopPlay() {
        mHandler.removeCallbacksAndMessages(null);
    }

    public void setIntervalTime(int intervalTime) {
        this.mIntervalTime = intervalTime;
    }
}
