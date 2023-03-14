package org.devio.hi.ui.tab.top;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.devio.hi.library.util.HiDisplayUtil;
import org.devio.hi.ui.tab.common.IHiTabLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Tips:
 * 自动滚动，实现点击的位置能够自动滚动以展示前后2个
 */
public class HiTabTopLayout extends HorizontalScrollView implements IHiTabLayout<HiTabTop, HiTabTopInfo<?>> {
    private List<OnTabSelectedListener<HiTabTopInfo<?>>> tabSelectedChangeListeners = new ArrayList<>();
    private HiTabTopInfo<?> selectedInfo;
    private List<HiTabTopInfo<?>> infoList;

    public HiTabTopLayout(Context context) {
        this(context, null);
    }

    public HiTabTopLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HiTabTopLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setVerticalScrollBarEnabled(false);
    }

    @Override
    public void inflateInfo(@NonNull List<HiTabTopInfo<?>> infoList) {
        if (infoList.isEmpty()) {
            return;
        }
        this.infoList = infoList;
        LinearLayout linearLayout = getRootLayout(true);
        selectedInfo = null;
        //清除之前添加的HiTabTop listener，Tips：Java foreach remove问题
        Iterator<OnTabSelectedListener<HiTabTopInfo<?>>> iterator = tabSelectedChangeListeners.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof HiTabTop) {
                iterator.remove();
            }
        }
        for (int i = 0; i < infoList.size(); i++) {
            final HiTabTopInfo<?> info = infoList.get(i);
            HiTabTop tab = new HiTabTop(getContext());
            tabSelectedChangeListeners.add(tab);
            tab.setHiTabInfo(info);
            linearLayout.addView(tab);
            tab.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSelected(info);
                }
            });
        }
    }



    private LinearLayout getRootLayout(boolean clear) {
        LinearLayout rootView = (LinearLayout) getChildAt(0);
        if (rootView == null) {
            rootView = new LinearLayout(getContext());
            rootView.setOrientation(LinearLayout.HORIZONTAL);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            addView(rootView, layoutParams);
        } else if (clear) {
            rootView.removeAllViews();
        }
        return rootView;
    }

    @Override
    public void addTabSelectedChangeListener(OnTabSelectedListener<HiTabTopInfo<?>> listener) {
        tabSelectedChangeListeners.add(listener);
    }

    @Nullable
    @Override
    public HiTabTop findTab(@NonNull HiTabTopInfo<?> info) {
        ViewGroup ll = getRootLayout(false);
        for (int i = 0; i < ll.getChildCount(); i++) {
            View child = ll.getChildAt(i);
            if (child instanceof HiTabTop) {
                HiTabTop tab = (HiTabTop) child;
                if (tab.getHiTabInfo() == info) {
                    return tab;
                }
            }
        }
        return null;
    }

    @Override
    public void defaultSelected(@NonNull HiTabTopInfo<?> defaultInfo) {
        onSelected(defaultInfo);
    }

    private void onSelected(@NonNull HiTabTopInfo<?> nextInfo) {
        for (OnTabSelectedListener<HiTabTopInfo<?>> listener : tabSelectedChangeListeners) {
            listener.onTabSelectedChange(infoList.indexOf(nextInfo), selectedInfo, nextInfo);
        }
        this.selectedInfo = nextInfo;
        autoScroll(nextInfo);
    }

    int tabWith;

    /**
     * 自动滚动，实现点击的位置能够自动滚动以展示前后2个
     *
     * @param nextInfo 点击tab的info
     */
    private void autoScroll(HiTabTopInfo<?> nextInfo) {
        HiTabTop tabTop = findTab(nextInfo);
        if (tabTop == null) return;
        int index = infoList.indexOf(nextInfo);
        int[] loc = new int[2];
        //获取点击的控件在屏幕的位置
        tabTop.getLocationInWindow(loc);
        int scrollWidth;
        if (tabWith == 0) {
            tabWith = tabTop.getWidth();
        }
        //判断点击了屏幕左侧还是右侧
        if ((loc[0] + tabWith / 2) > HiDisplayUtil.getDisplayWidthInPx(getContext()) / 2) {
            scrollWidth = rangeScrollWidth(index, 2);
        } else {
            scrollWidth = rangeScrollWidth(index, -2);
        }
        scrollTo(getScrollX() + scrollWidth, 0);
    }

    /**
     * 获取可滚动的范围
     *
     * @param index 从第几个开始
     * @param range 向前向后的范围
     * @return 可滚动的范围
     */
    private int rangeScrollWidth(int index, int range) {
        int scrollWidth = 0;
        for (int i = 0; i <= Math.abs(range); i++) {
            int next;
            if (range < 0) {
                next = range + i + index;
            } else {
                next = range - i + index;
            }
            if (next >= 0 && next < infoList.size()) {
                if (range < 0) {
                    scrollWidth -= scrollWidth(next, false);
                } else {
                    scrollWidth += scrollWidth(next, true);
                }
            }
        }
        return scrollWidth;

    }

    /**
     * 指定位置的控件可滚动的距离
     *
     * @param index   指定位置的控件
     * @param toRight 是否是点击了屏幕右侧
     * @return 可滚动的距离
     */
    private int scrollWidth(int index, boolean toRight) {
        HiTabTop target = findTab(infoList.get(index));
        if (target == null) return 0;
        Rect rect = new Rect();
        target.getLocalVisibleRect(rect);
        if (toRight) {//点击屏幕右侧
            if (rect.right > tabWith) {//right坐标大于控件的宽度时，说明完全没有显示
                return tabWith;
            } else {//显示部分，减去已显示的宽度
                return tabWith - rect.right;
            }
        } else {
            // TODO 这里有Bug
            if (rect.left <= -tabWith) {//left坐标小于等于-控件的宽度，说明完全没有显示
                return tabWith;
            } else if (rect.left > 0) {//显示部分
                return rect.left;
            }
            return 0;
        }
    }
}
