package org.devio.hi.imooc.tab.top;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.devio.hi.imooc.tab.common.IHiTabLayout;
import org.devio.hi.imooc.util.HiDisplayUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// 支持水平滚动
public class HiTabTopLayout extends HorizontalScrollView implements IHiTabLayout<HiTabTop, HiTabTopInfo<?>> {

    private List<OnTabSelectedListener<HiTabTopInfo<?>>> tabSelectedChangeListeners = new ArrayList<>();
    private HiTabTopInfo<?> selectedInfo;
    private List<HiTabTopInfo<?>> infoList;

    private int tabWidth;

    public HiTabTopLayout(Context context) {
        this(context, null);
    }

    public HiTabTopLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HiTabTopLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO 去除底部滚动条 scrollbar
        setHorizontalScrollBarEnabled(false);
    }

    @Override
    public HiTabTop findTab(HiTabTopInfo<?> data) {
        // 查找LinearLayout里面的HiTabTop
        LinearLayout fl = getRootLayout(false);
        for (int i = 0; i < fl.getChildCount(); i++) {
            View child = fl.getChildAt(i);
            if (child instanceof HiTabTop) {
                HiTabTop currentTab = (HiTabTop) child;
                if (currentTab.getTabInfo() == data) {
                    return currentTab;
                }
            }
        }
        return null;
    }

    @Override
    public void addTabSelectedChangeListener(OnTabSelectedListener<HiTabTopInfo<?>> listener) {
        tabSelectedChangeListeners.add(listener);
    }

    @Override
    public void defaultSelected(HiTabTopInfo<?> defaultInfo) {
        onSlected(defaultInfo);
    }

    @Override
    public void inflateInfo(List<HiTabTopInfo<?>> infoList) {

        if (infoList.isEmpty()) {
            return;
        }
        this.infoList = infoList;

        selectedInfo = null;
        // 使用迭代器 清除之前添加的Listener TODO Tips:Java foreach remove 问题 报错
        Iterator<OnTabSelectedListener<HiTabTopInfo<?>>> iterator = tabSelectedChangeListeners.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof HiTabTop) {
                iterator.remove();
            }
        }
        LinearLayout linearLayout = getRootLayout(true);
        for (int i = 0; i < infoList.size(); i++) {
            final HiTabTopInfo<?> hiTabTopInfo = infoList.get(i);
            HiTabTop hiTabTop = new HiTabTop(getContext());
            // 添加监听器
            tabSelectedChangeListeners.add(hiTabTop);
            hiTabTop.setHiTabInfo(hiTabTopInfo);
            // 从左到右依次添加
            linearLayout.addView(hiTabTop);
            hiTabTop.setOnClickListener(v -> {
                onSlected(hiTabTopInfo);
            });
        }
    }

    private LinearLayout getRootLayout(boolean clear) {
        LinearLayout rootView = (LinearLayout) getChildAt(0);
        if (rootView == null) {
            rootView = new LinearLayout(getContext());
            // 横向
            rootView.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            addView(rootView, layoutParams);
        } else if (clear) {
            rootView.removeAllViews();
        }
        return rootView;
    }

    private void onSlected(HiTabTopInfo<?> nextInfo) {
        for (OnTabSelectedListener<HiTabTopInfo<?>> listener : tabSelectedChangeListeners) {
            // 监听器回调
            listener.onTabSelectedChange(infoList.indexOf(nextInfo), selectedInfo, nextInfo);
        }
        if (this.selectedInfo != nextInfo) {
            // 自动滚动
            autoScroll(nextInfo);
        }
        this.selectedInfo = nextInfo;

    }

    private void autoScroll(HiTabTopInfo<?> nextInfo) {
        HiTabTop tabTop = findTab(nextInfo);
        if (tabTop == null) return;
        int index = infoList.indexOf(nextInfo);
        int[] loc = new int[2];
        // 在窗口中的坐标
        tabTop.getLocationInWindow(loc);
        int scrollWidth;
        if (tabWidth == 0) {
            tabWidth = tabTop.getWidth();
        }
        if ((loc[0] + tabWidth / 2) > HiDisplayUtil.getDisplayWidthInPx(getContext()) / 2) {
            // 当前点击的tab在屏幕右边 向左滑动
            scrollWidth = rangeScrollWidth(index, 2);
        } else {
            scrollWidth = rangeScrollWidth(index, -2);
        }
        // 水平滚动
        // x, y 表示偏移量
        scrollTo(getScrollX() + scrollWidth, 0);
    }

    // 需要滚动的距离
    private int rangeScrollWidth(int index, int range) {
        int scrollWidth = 0;
        for (int i = 0; i <= Math.abs(range); i++) {
            // 获取下一个topInfo
            int next;
            if (range < 0) {
                next = range + index + i;
            } else {
                next = range + index - i;
            }
            if (next >= 0 && next < infoList.size()) {
                if (range < 0) {
                    // 向右滑动 scrollWidth是负的
                    scrollWidth -= scrollWidth(next, false);
                } else {
                    // 向左滚动 scrollWidth是正的
                    scrollWidth += scrollWidth(next, true);
                }
            }
        }
        Log.e("xx", "需要滑动的距离 " + scrollWidth);
        return scrollWidth;
    }

    private int scrollWidth(int index, boolean toRight) {
        // 找到当前的Tab
        HiTabTop target = findTab(infoList.get(index));
        if (target == null) return 0;
        Rect rect = new Rect();
        /**
         * getLocalVisibleRect: 获取View在第一个可滚动的上级View（父View或祖父View或...）中的可见区域相对于此View的左顶点的距离（偏移量）
         *
         * 在第一个可滚动的上级View中的可见区域，即使被其他浮动View完全遮挡，也返回true
         * 不在第一个可滚动的上级View中的可见区域时，返回false，此时获取到的值为View距离第一个可滚动的上级View的左顶点的距离（偏移量）
         */
        boolean localVisibleRect = target.getLocalVisibleRect(rect);
        Log.e("xx", target.getTabNameView().getText() + "的可见right: " + rect.right + "可见left: " + rect.left + " 是否可见: " + localVisibleRect);
        if (toRight) {
            // 点击屏幕右侧 需要向左滑动
            if (rect.right > tabWidth) {
                // right坐标大于控件的宽度时，说明完全没有显示
                Log.e("xx", target.getTabNameView().getText() + "完全没有显示 需要显示的宽度" + tabWidth);
                return tabWidth;
            } else if (rect.right < tabWidth) {
                Log.e("xx", target.getTabNameView().getText() + "显示了一部分 需要显示的宽度: " + (tabWidth - rect.right));
                // 显示部分，减去已显示的宽度
                return tabWidth - rect.right;
            } else {
                Log.e("xx", target.getTabNameView().getText() + "完全显示了 需要显示的宽度: 0");
                return 0;
            }
        } else {
            // 点击屏幕左侧 需要向右滑动
            if (!localVisibleRect) {
                Log.e("xx", target.getTabNameView().getText() + "完全没有显示 需要显示的宽度" + tabWidth);
                return tabWidth;
            } else if (rect.left < tabWidth && rect.left != 0) {
                // 显示部分
                Log.e("xx", target.getTabNameView().getText() + "显示了一部分 需要显示的宽度: " + (rect.left));
                return rect.left;
            }else if(rect.left == 0){
                Log.e("xx", target.getTabNameView().getText() + "完全显示 需要显示的宽度 0");
                // 完全显示了
                return 0;
            }
           return 0;
        }
    }

}
