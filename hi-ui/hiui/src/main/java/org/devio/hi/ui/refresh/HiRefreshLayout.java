package org.devio.hi.ui.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

import org.devio.hi.library.log.HiLog;
import org.devio.hi.ui.refresh.HiOverView.HiRefreshState;

/**
 * 下拉刷新View
 */
public class HiRefreshLayout extends FrameLayout implements HiRefresh {
    private static final String TAG = HiRefreshLayout.class.getSimpleName();
    private HiRefreshState mState;
    private GestureDetector mGestureDetector;
    private AutoScroller mAutoScroller;
    private HiRefresh.HiRefreshListener mHiRefreshListener;
    protected HiOverView mHiOverView;
    private int mLastY;
    //刷新时是否禁止滚动
    private boolean disableRefreshScroll;

    public HiRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HiRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public HiRefreshLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        mGestureDetector = new GestureDetector(getContext(), hiGestureDetector);
        mAutoScroller = new AutoScroller();
    }

    @Override
    public void setDisableRefreshScroll(boolean disableRefreshScroll) {
        this.disableRefreshScroll = disableRefreshScroll;
    }

    @Override
    public void refreshFinished() {
        final View head = getChildAt(0);
        HiLog.i(this.getClass().getSimpleName(), "refreshFinished head-bottom:" + head.getBottom());
        mHiOverView.onFinish();
        mHiOverView.setState(HiRefreshState.STATE_INIT);
        final int bottom = head.getBottom();
        if (bottom > 0) {
            //下over pull 200，height 100
             //  bottom  =100 ,height 100
            recover(bottom);
        }
        mState = HiRefreshState.STATE_INIT;
    }

    @Override
    public void setRefreshListener(HiRefresh.HiRefreshListener hiRefreshListener) {
        mHiRefreshListener = hiRefreshListener;
    }

    /**
     * 设置下拉刷新的视图
     *
     * @param hiOverView 刷新视图
     */
    @Override
    public void setRefreshOverView(HiOverView hiOverView) {
        if (this.mHiOverView != null) {
            removeView(mHiOverView);
        }
        this.mHiOverView = hiOverView;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mHiOverView, 0, params);
    }

    HiGestureDetector hiGestureDetector = new HiGestureDetector() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float disX, float disY) {
            HiLog.i(TAG, "disY " + disY);
            if (Math.abs(disX) > Math.abs(disY) || mHiRefreshListener != null && !mHiRefreshListener.enableRefresh()) {
                //横向滑动，或刷新被禁止则不处理
                HiLog.i(TAG, "横向滑动，或刷新被禁止则不处理");
                return false;
            }
            if (disableRefreshScroll && mState == HiRefreshState.STATE_REFRESH) {
                // 刷新时是否禁止滑动
                HiLog.i(TAG, "刷新时 禁止滑动 ");
                return true;
            }

            View head = getChildAt(0);
            View child = HiScrollUtil.findScrollableChild(HiRefreshLayout.this);
            if (HiScrollUtil.childScrolled(child)) {
                //如果列表发生了滚动则不处理
                HiLog.i(TAG, "列表发生了滚动 不处理");
                return false;
            }
            //没有刷新或没有达到可以刷新的距离，且头部已经划出或下拉
            if ((mState != HiRefreshState.STATE_REFRESH || head.getBottom() <= mHiOverView.mPullRefreshHeight) && (head.getBottom() > 0 || disY <= 0.0F)) {
                //还在手动滑动中并没有释放
                if (mState != HiRefreshState.STATE_OVER_RELEASE) {
                    int dis;
                    //阻尼计算 根据下拉的距离
                    if (child.getTop() < mHiOverView.mPullRefreshHeight) {
                        dis = (int) (mLastY / mHiOverView.minDamp);
                    } else {
                        dis = (int) (mLastY / mHiOverView.maxDamp);
                    }
                    //如果是正在刷新状态，则不允许在滑动的时候改变状态
                    boolean bool = moveView(dis, true);
                    mLastY = (int) (-disY);
                    return bool;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //事件分发处理
        if (!mAutoScroller.isFinished()) {
            return false;
        }

        View head = getChildAt(0);
        View child = getChildAt(1);
        // 松开手后的处理
        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL
                || ev.getAction() == MotionEvent.ACTION_POINTER_INDEX_MASK) {//松开手
            if (head.getBottom() > 0) {
                if (mState != HiRefreshState.STATE_REFRESH) {//非正在刷新
                    recover(child.getTop());
                    return false;
                }
            }
            mLastY = 0;
        }
        // 处理手指滑动状态
        boolean consumed = mGestureDetector.onTouchEvent(ev);
        //HiLog.i(TAG, "gesture consumed：" + consumed);
        if ((consumed || (mState != HiRefreshState.STATE_INIT && mState != HiRefreshState.STATE_REFRESH)) && head.getBottom() != 0) {
            ev.setAction(MotionEvent.ACTION_CANCEL);//让父类接受不到真实的事件
            return super.dispatchTouchEvent(ev);
        }

        if (consumed) {
            return true;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //定义head和child的排列位置
        View head = getChildAt(0);  // HiOverView
        View child = getChildAt(1); // RecyclerView
        if (head != null && child != null) {
            HiLog.i(TAG, "onLayout head-height:" + head.getMeasuredHeight());
            int childTop = child.getTop();
            if (mState == HiRefreshState.STATE_REFRESH) {
                head.layout(left, mHiOverView.mPullRefreshHeight - head.getMeasuredHeight(), right, mHiOverView.mPullRefreshHeight);
                child.layout(left, mHiOverView.mPullRefreshHeight, right, mHiOverView.mPullRefreshHeight + child.getMeasuredHeight());
            } else {
                //left,top,right,bottom 将刷新布局隐藏起来
                head.layout(left, childTop - head.getMeasuredHeight(), right, childTop);
                HiLog.i(TAG, "onLayout head位置 left " + left + " top: " + (childTop - head.getMeasuredHeight()) +" right: " + + right +" bottom: " + childTop);
                HiLog.i(TAG, "onLayout child位置 left " + left + " top: " + (childTop) +" right: " + + right +" bottom: " + (childTop + child.getMeasuredHeight()));
                child.layout(left, childTop, right, childTop + child.getMeasuredHeight());
            }

            View other;
            //让HiRefreshLayout节点下两个以上的child能够不跟随手势移动以实现一些特殊效果，如悬浮的效果
            for (int i = 2; i < getChildCount(); ++i) {
                other = getChildAt(i);
                other.layout(0, top, right, bottom);
            }
            HiLog.i(TAG, "onLayout head-bottom:" + head.getBottom());
        }
    }

    /**
     * 恢复到原位置
     * @param dis
     */
    private void recover(int dis) {//dis =200  200-100
        if (mHiRefreshListener != null && dis > mHiOverView.mPullRefreshHeight) {
            // 滑动到刷新位置 HiOverView的底部到达最小刷新距离位置
            mAutoScroller.recover(dis - mHiOverView.mPullRefreshHeight);
            mState = HiRefreshState.STATE_OVER_RELEASE;
            HiLog.i(TAG, "超过最小刷新距离后，恢复到刷新位置");
        } else {
            mAutoScroller.recover(dis);
            HiLog.i(TAG, "没达到最小刷新距离，恢复到原始");
        }
    }

    /**
     * 根据偏移量移动header与child
     *
     * @param offsetY 偏移量 需要移动的Y轴距离
     * @param nonAuto true: 手动触发滚动  false：自动触发滚动
     * @return
     */
    private boolean moveView(int offsetY, boolean nonAuto) {
        HiLog.i("111", "changeState:" + nonAuto);
        View head = getChildAt(0);
        View child = getChildAt(1);
        int childTop = child.getTop() + offsetY;
        HiLog.e("111", "childTop:" + childTop);
        HiLog.i("-----", "moveDown head-bottom:" + head.getBottom() + ",child.getTop():" + child.getTop() + ",offsetY:" + offsetY);
        if (childTop <= 0) {//异常情况的补充 手指向上滑动
            HiLog.i(TAG, "childTop<=0,mState" + mState);
            offsetY = -child.getTop();
            //移动head与child的位置，到原始位置
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
            if (mState != HiRefreshState.STATE_REFRESH) {
                mState = HiRefreshState.STATE_INIT;
            }
        } else if (mState == HiRefreshState.STATE_REFRESH && childTop > mHiOverView.mPullRefreshHeight) {
            //如果正在下拉刷新中，禁止继续下拉
            return false;
        } else if (childTop <= mHiOverView.mPullRefreshHeight) {
            //下拉高度 还没超出设定的刷新距离
            if (mHiOverView.getState() != HiRefreshState.STATE_VISIBLE && nonAuto) {//头部开始显示
                mHiOverView.onVisible();
                mHiOverView.setState(HiRefreshState.STATE_VISIBLE);
                mState = HiRefreshState.STATE_VISIBLE;
            }
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
            if (childTop == mHiOverView.mPullRefreshHeight && mState == HiRefreshState.STATE_OVER_RELEASE) {
                HiLog.i(TAG, "refresh，childTop：" + childTop);
                refresh();
            }
        } else {
            //超出最小刷新位置
            if (mHiOverView.getState() != HiRefreshState.STATE_OVER && nonAuto) {
                //超出刷新位置
                mHiOverView.onOver();
                // 设置状态
                mHiOverView.setState(HiRefreshState.STATE_OVER);
            }
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
        }
        if (mHiOverView != null) {
            mHiOverView.onScroll(head.getBottom(), mHiOverView.mPullRefreshHeight);
        }
        return true;
    }


    /**
     * 刷新
     */
    private void refresh() {
        if (mHiRefreshListener != null) {
            mState = HiRefreshState.STATE_REFRESH;
            mHiOverView.onRefresh();
            mHiOverView.setState(HiRefreshState.STATE_REFRESH);
            mHiRefreshListener.onRefresh();
        }
    }


    /**
     * 借助Scroller实现视图的自动滚动
     * https://juejin.im/post/5c7f4f0351882562ed516ab6
     */
    private class AutoScroller implements Runnable {
        private Scroller mScroller;
        private int mLastY;
        private boolean mIsFinished;

        AutoScroller() {
            mScroller = new Scroller(getContext(), new LinearInterpolator());
            mIsFinished = true;
        }

        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) {//还未滚动完成
                HiLog.e(TAG, "Scrooler计算的偏移量" + (mScroller.getCurrY()));
                HiLog.e(TAG, "自动滚动偏移量" + (mLastY - mScroller.getCurrY()));
                // 向上滚动
                moveView(mLastY - mScroller.getCurrY(), false);
                // 当前偏移量Y
                // 最终mLastY的值就是要移动的距离 dis
                mLastY = mScroller.getCurrY();
                HiLog.e(TAG, "mLastY " + mLastY);
                post(this);
            } else {
                removeCallbacks(this);
                mIsFinished = true;
            }
        }

        void recover(int dis) {
            if (dis <= 0) {
                return;
            }
            HiLog.e(TAG, "要恢复到的位置 " + (dis));
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