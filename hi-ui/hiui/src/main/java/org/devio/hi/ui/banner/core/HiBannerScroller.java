package org.devio.hi.ui.banner.core;

import android.content.Context;
import android.widget.Scroller;

/**
 * 用于设置滚动的时长
 */
public class HiBannerScroller extends Scroller {

    /**
     * 值越大，滑动越慢
     */
    private int mDuration = 1000;

    HiBannerScroller(Context context, int duration) {
        super(context);
        mDuration = duration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
}
