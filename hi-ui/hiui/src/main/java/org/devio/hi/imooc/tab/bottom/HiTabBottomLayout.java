package org.devio.hi.imooc.tab.bottom;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.devio.hi.imooc.util.HiDisplayUtil;
import org.devio.hi.imooc.util.HiViewUtil;
import org.devio.hi.imooc.tab.common.IHiTabLayout;
import org.devio.hi.imooc.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HiTabBottomLayout extends FrameLayout implements IHiTabLayout<HiTabBottom, HiTabBottomInfo<?>> {


    private List<IHiTabLayout.OnTabSelectedListener<HiTabBottomInfo<?>>> tabSelectedChangeListeners = new ArrayList<>();
    private HiTabBottomInfo<?> selectedInfo;
    private float bottomAlpha = 1f;
    //TabBottom高度
    private static float tabBottomHeight = 50;
    //TabBottom的头部线条高度
    private float bottomLineHeight = 0.5f;
    //TabBottom的头部线条颜色
    private String bottomLineColor = "#dfe0e1";
    private List<HiTabBottomInfo<?>> infoList;

    private static final String TAG_TAB_BOTTOM = "TAG_TAB_BOTTOM";

    public HiTabBottomLayout(@NonNull Context context) {
        this(context, null);
    }

    public HiTabBottomLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HiTabBottomLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public HiTabBottom findTab(HiTabBottomInfo<?> data) {
        // 查找FrameLayout里面的HiTabBottom
        FrameLayout fl = findViewWithTag(TAG_TAB_BOTTOM);
        for (int i = 0; i < fl.getChildCount(); i++) {
            View child = fl.getChildAt(i);
            if (child instanceof HiTabBottom) {
                HiTabBottom currentTab = (HiTabBottom) child;
                if (currentTab.getTabInfo() == data) {
                    return currentTab;
                }
            }
        }
        return null;
    }

    @Override
    public void addTabSelectedChangeListener(OnTabSelectedListener<HiTabBottomInfo<?>> listener) {
        tabSelectedChangeListeners.add(listener);
    }

    @Override
    public void defaultSelected(HiTabBottomInfo<?> defaultInfo) {
        onSelected(defaultInfo);
    }

    @Override
    public void inflateInfo(List<HiTabBottomInfo<?>> infoList) {
        if (infoList.isEmpty()) {
            return;
        }
        this.infoList = infoList;
        // 移除之前添加的View
        // 移除时 从末尾开始移除
        // 当前容器的第0个元素的中间的内容 因此不能移除
        for (int i = getChildCount() - 1; i > 0; i--) {
            removeViewAt(i);
        }
        selectedInfo = null;
        addBackground();
        // 使用迭代器
        Iterator<OnTabSelectedListener<HiTabBottomInfo<?>>> iterator = tabSelectedChangeListeners.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof HiTabBottom) {
                iterator.remove();
            }
        }
        FrameLayout fl = new FrameLayout(getContext());
        fl.setTag(TAG_TAB_BOTTOM);

        int height = HiDisplayUtil.dp2px(tabBottomHeight, getResources());
        // 屏幕宽度 / bottomInfo的个数 就是每个bottomLayout的宽度
        int width = HiDisplayUtil.getDisplayWidthInPx(getContext()) / infoList.size();
        for (int i = 0; i < infoList.size(); i++) {
            final HiTabBottomInfo<?> bottomInfo = infoList.get(i);
            LayoutParams params = new LayoutParams(width, height);
            params.gravity = Gravity.BOTTOM;
            params.leftMargin = width * i;

            HiTabBottom hiTabBottom = new HiTabBottom(getContext());
            // 每一个tabBottom都对应一个listener
            // 对外也可以添加Listener
            tabSelectedChangeListeners.add(hiTabBottom);
            hiTabBottom.setHiTabInfo(bottomInfo);
            // 添加BottomLayout
            fl.addView(hiTabBottom, params);
            // 设置点击事件
            hiTabBottom.setOnClickListener(v -> {
                onSelected(bottomInfo);
            });
        }

        LayoutParams flParams = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flParams.gravity = Gravity.BOTTOM;
        addView(fl, flParams);

        addBottomLine();
        fixContentView();
    }

    /**
     * 修复内容区域的底部Padding 如不修复则底部BottomLayout会遮挡
     */
    private void fixContentView() {
        if (!(getChildAt(0) instanceof ViewGroup)) {
            return;
        }
        // HiFragmentTabView
        ViewGroup rootView = (ViewGroup) getChildAt(0);
        ViewGroup targetView = HiViewUtil.findTypeView(rootView, RecyclerView.class);
        if (targetView == null) {
            targetView = HiViewUtil.findTypeView(rootView, ScrollView.class);
        }
        if (targetView == null) {
            targetView = HiViewUtil.findTypeView(rootView, AbsListView.class);
        }
        if (targetView != null) {
            // 设置bottom的padding
            targetView.setPadding(0, 0, 0, HiDisplayUtil.dp2px(tabBottomHeight, getResources()));
            // 凸起效果 View内部的padding也可以显示子View
            targetView.setClipToPadding(false);
        }

    }

    public void setTabAlpha(float tabAlpha) {
        this.bottomAlpha = tabAlpha;
    }

    public static void setTabHeight(float tabBottomHeight) {
        HiTabBottomLayout.tabBottomHeight = tabBottomHeight;
    }

    public void setBottomLineHeight(float bottomLineHeight) {
        this.bottomLineHeight = bottomLineHeight;
    }

    public void setBottomLineColor(String bottomLineColor) {
        this.bottomLineColor = bottomLineColor;
    }

    private void addBottomLine() {
        View bottomLine = new View(getContext());
        bottomLine.setBackgroundColor(Color.parseColor(bottomLineColor));

        LayoutParams bottomLineParams =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, HiDisplayUtil.dp2px(bottomLineHeight, getResources()));
        bottomLineParams.gravity = Gravity.BOTTOM;
        bottomLineParams.bottomMargin = HiDisplayUtil.dp2px(tabBottomHeight - bottomLineHeight, getResources());
        addView(bottomLine, bottomLineParams);
        bottomLine.setAlpha(bottomAlpha);
    }

    // 选中的是下一个info
    private void onSelected(HiTabBottomInfo<?> nextInfo) {
        for (OnTabSelectedListener<HiTabBottomInfo<?>> listener : tabSelectedChangeListeners) {
            listener.onTabSelectedChange(infoList.indexOf(nextInfo), selectedInfo, nextInfo);
        }
        this.selectedInfo = nextInfo;
    }

    // 添加背景色
    private void addBackground() {
        View backGroundView = LayoutInflater.from(getContext()).inflate(R.layout.hi_bottom_layout_bg, null);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, HiDisplayUtil.dp2px(tabBottomHeight, getResources()));
        params.gravity = Gravity.BOTTOM;
        addView(backGroundView, params);
        backGroundView.setAlpha(bottomAlpha);
    }
}
