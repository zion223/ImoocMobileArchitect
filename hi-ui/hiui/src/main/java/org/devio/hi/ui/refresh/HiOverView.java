package org.devio.hi.ui.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import org.devio.hi.library.log.HiLog;
import org.devio.hi.library.util.HiDisplayUtil;

/**
 * 下拉刷新的Overlay视图,可以重载这个类来定义自己的Overlay
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
    /**
     * 触发下拉刷新 需要的最小高度
     */
    public int mPullRefreshHeight;
    /**
     * 最小阻尼
     */
    public float minDamp = 1.6f;
    /**
     * 最大阻尼 越大越难以滑动
     */
    public float maxDamp = 2.2f;

    public HiOverView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        preInit();
    }

    public HiOverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        preInit();
    }

    public HiOverView(Context context) {
        super(context);
        preInit();
    }

    protected void preInit() {
        mPullRefreshHeight = HiDisplayUtil.dp2px(66, getResources());
        HiLog.e("", "mPullRefreshHeight : " + mPullRefreshHeight);
        init();
    }

    /**
     * 初始化
     */
    public abstract void init();

    protected abstract void onScroll(int scrollY, int pullRefreshHeight);

    /**
     * 显示Overlay
     */
    protected abstract void onVisible();

    /**
     * 超过Overlay，释放就会加载
     */
    public abstract void onOver();

    /**
     * 开始加载
     */
    public abstract void onRefresh();

    /**
     * 加载完成
     */
    public abstract void onFinish();

    /**
     * 设置状态
     *
     * @param state 状态
     */
    public void setState(HiRefreshState state) {
        mState = state;
    }

    /**
     * 获取状态
     *
     * @return 状态
     */
    public HiRefreshState getState() {
        return mState;
    }

}