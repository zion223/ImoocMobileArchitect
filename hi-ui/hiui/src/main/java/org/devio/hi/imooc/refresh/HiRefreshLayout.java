package org.devio.hi.imooc.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.devio.hi.library.log.HiLog;
import org.devio.hi.ui.banner.indicator.HiIndicator;

/**
 * 下拉刷新外部包裹容器
 */
public class HiRefreshLayout extends FrameLayout implements HiRefresh {

    private HiRefreshListener listener;
    private HiOverView.HiRefreshState mState;
    private GestureDetector mGestureDetector;

    protected HiOverView hiOverView;
    //刷新时是否禁止滚动
    private boolean disableRefreshScroll;

    private int mLastY;

    private AutoScroller mAutoScroller;

    public HiRefreshLayout(@NonNull Context context) {
        this(context, null);
    }

    public HiRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HiRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        mGestureDetector = new GestureDetector(getContext(), hiGestureDetector);
        mAutoScroller = new AutoScroller();
    }

    HiGestureDetector hiGestureDetector = new HiGestureDetector() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            if (Math.abs(distanceX) > Math.asin(distanceY) || listener != null && !listener.enableRefresh()) {
                // 横向滑动 或者 禁止下拉刷新
                return false;
            }
            if (disableRefreshScroll && mState == HiOverView.HiRefreshState.STATE_REFRESH) {
                return true;
            }
            View head = getChildAt(0);
            View child = HiScrollUtil.findScrollableChild(HiRefreshLayout.this);
            if (HiScrollUtil.childScrolled(child)) {
                // 列表发生了滚动  不处理这种情况
                return false;
            }
            //当前不是刷新状态或没有达到可以刷新的最小距离，且头部已经划出或下拉
            if ((mState != HiOverView.HiRefreshState.STATE_REFRESH || head.getBottom() <= hiOverView.mPullRefreshHeight) && (head.getBottom() > 0 || distanceY <= 0.0F)) {
                if (mState == HiOverView.HiRefreshState.STATE_OVER_RELEASE) {
                    int speed;
                    //阻尼计算
                    if (child.getTop() < hiOverView.mPullRefreshHeight) {
                        speed = (int) (mLastY / hiOverView.minDamp);
                    } else {
                        speed = (int) (mLastY / hiOverView.maxDamp);
                    }
                    boolean down = moveDown(speed, true);
                    mLastY = (int) -distanceY;
                    return down;
                } else {
                    return false;
                }

            } else {
                return false;
            }
        }
    };

    /**
     * 根据偏移量移动header与child
     *
     * @param offsetY 偏移量
     * @param nonAuto 是否自动滚动
     * @return 是否消费
     */
    private boolean moveDown(int offsetY, boolean nonAuto) {

        View head = getChildAt(0);
        View child = getChildAt(1);
        int childTop = child.getTop() + offsetY;
        if (childTop <= 0) { // 异常情况
            offsetY = -child.getTop();
            //移动head与child的位置，到原始位置
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
            if (mState != HiOverView.HiRefreshState.STATE_REFRESH) {
                mState = HiOverView.HiRefreshState.STATE_INIT;
            }
        } else if (mState == HiOverView.HiRefreshState.STATE_REFRESH && childTop > hiOverView.mPullRefreshHeight) {
            return false;
        } else if (childTop <= hiOverView.mPullRefreshHeight) {
            if (hiOverView.getState() != HiOverView.HiRefreshState.STATE_VISIBLE && nonAuto) {
                hiOverView.onVisible();
                hiOverView.setState(HiOverView.HiRefreshState.STATE_VISIBLE);
                mState = HiOverView.HiRefreshState.STATE_VISIBLE;
            }
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
            if (childTop == hiOverView.mPullRefreshHeight && mState == HiOverView.HiRefreshState.STATE_OVER_RELEASE) {
                //HiLog.i(TAG, "refresh，childTop：" + childTop);
                refresh();
            }
        } else {
            if (hiOverView.getState() != HiOverView.HiRefreshState.STATE_OVER && nonAuto) {
                hiOverView.onOver();
                hiOverView.setState(HiOverView.HiRefreshState.STATE_OVER);
            }
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
        }
        if (hiOverView != null) {
            hiOverView.onScroll(head.getBottom(), hiOverView.mPullRefreshHeight);
        }
        return true;
    }

    private void refresh() {
        mState = HiOverView.HiRefreshState.STATE_REFRESH;
        hiOverView.onRefresh();
        hiOverView.setState(HiOverView.HiRefreshState.STATE_REFRESH);
        if (listener != null) {
            listener.onRefresh();
        }
    }


    @Override
    public void setDisableRefreshScroll(boolean disableRefreshScroll) {
        this.disableRefreshScroll = disableRefreshScroll;
    }

    @Override
    public void refreshFinished() {
        View head = getChildAt(0);
        hiOverView.onFinished();
        hiOverView.setState(HiOverView.HiRefreshState.STATE_INIT);
        int bottom = head.getBottom();
        if (bottom > 0) {
            recover(bottom);
        }
        mState = HiOverView.HiRefreshState.STATE_INIT;
    }

    @Override
    public void setRefreshListener(HiRefreshListener listener) {
        this.listener = listener;
    }

    @Override
    public void setRefreshOverView(HiOverView view) {
        if (this.hiOverView != null) {
            removeView(this.hiOverView);
        }
        this.hiOverView = view;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        // 添加到首位置
        addView(hiOverView, 0, params);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // 重新定义位置
        View head = getChildAt(0);
        View child = getChildAt(1);
        if (head != null && child != null) {
            int childTop = child.getTop();
            if (mState == HiOverView.HiRefreshState.STATE_REFRESH) {
                head.layout(0, hiOverView.mPullRefreshHeight - head.getMeasuredHeight(), right, hiOverView.mPullRefreshHeight);
                child.layout(0, hiOverView.mPullRefreshHeight, right, hiOverView.mPullRefreshHeight + child.getMeasuredHeight());
            } else {
                head.layout(0, childTop - head.getMeasuredHeight(), right, childTop);
                child.layout(0, childTop, right, childTop + child.getMeasuredHeight());
            }
            View other;
            for (int i = 2; i < getChildCount(); i++) {
                other = getChildAt(i);
                other.layout(0, top, right, bottom);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        View head = getChildAt(0);
        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_POINTER_INDEX_MASK) {
            // 用户松开手
            if (head.getBottom() > 0) {
                if (mState != HiOverView.HiRefreshState.STATE_REFRESH) {
                    // 当前不是刷新状态
                    recover(head.getBottom());
                    return false;
                }
            }
            mLastY = 0;
        }
        boolean consumed = mGestureDetector.onTouchEvent(ev);
        if ((consumed || (mState != HiOverView.HiRefreshState.STATE_INIT
                && mState != HiOverView.HiRefreshState.STATE_REFRESH)) && head.getBottom() != 0) {
            ev.setAction(MotionEvent.ACTION_CANCEL);
            return super.dispatchTouchEvent(ev);
        }
        if (consumed) {
            return true;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    /**
     * 恢复到原位置
     *
     * @param dis
     */
    private void recover(int dis) {//dis =200  200-100
        if (listener != null && dis > hiOverView.mPullRefreshHeight) {
            mAutoScroller.recover(dis - hiOverView.mPullRefreshHeight);
            mState = HiOverView.HiRefreshState.STATE_OVER_RELEASE;
        } else {
            mAutoScroller.recover(dis);
        }
    }

    private class AutoScroller implements Runnable {

        private Scroller mScroller;
        private int mLastY;
        private boolean mIsFinished;

        public AutoScroller() {
            mScroller = new Scroller(getContext(), new LinearInterpolator());
            mIsFinished = true;
        }

        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) {

                moveDown(mLastY - mScroller.getCurrY(), false);

                mLastY = mScroller.getCurrY();
                post(this);
            } else {
                removeCallbacks(this);
                mIsFinished = true;
            }
        }

        void recover(int dis) {
            if (dis < 0) return;
            removeCallbacks(this);
            mLastY = 0;
            mIsFinished = false;
            mScroller.startScroll(0, 0, 0, dis, 300);
            post(this);
        }

        boolean isFinished() {
            return mIsFinished;
        }
    }
}
