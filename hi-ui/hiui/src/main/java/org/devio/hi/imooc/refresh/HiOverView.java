package org.devio.hi.imooc.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.devio.hi.imooc.util.HiDisplayUtil;

/**
 * 下拉刷新视图
 */
public abstract class HiOverView extends FrameLayout {

    public enum HiRefreshState {

        /**
         * 初始态
         */
        STATE_INIT,
        /**
         * Header展示的状态
         */
        STATE_VISIBLE,
        /**
         * 超出可刷新距离的状态
         */
        STATE_OVER,
        /**
         * 刷新中的状态
         */
        STATE_REFRESH,
        /**
         * 超出刷新位置松开手后的状态
         */
        STATE_OVER_RELEASE
    }

    protected HiRefreshState mState = HiRefreshState.STATE_INIT;
    // 触发下拉刷新的最小高度
    public int mPullRefreshHeight;
    //阻尼系数
    public float minDamp = 1.6f;
    public float maxDamp = 2.2f;

    public HiOverView(@NonNull Context context) {
        this(context, null);
    }

    public HiOverView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HiOverView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        preInit();
    }

    protected void preInit(){
        mPullRefreshHeight = HiDisplayUtil.dp2px(66, getResources());
        init();
    }

    // 初始化
    public abstract void init();

    protected abstract void onScroll(int scrollY, int pullRefreshHeight);

    // 显示Overlay
    protected abstract void onVisible();

    // 超过高度Overlay 释放手就会加载 刷新视图
    public abstract void onOver();

    // 正在刷新
    public abstract void onRefresh();

    // 刷新完成
    public abstract void onFinished();

    public void setState(HiRefreshState state){
        this.mState = state;
    }

    public HiRefreshState getState() {
        return mState;
    }
}
