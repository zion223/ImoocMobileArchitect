package org.devio.hi.imooc.banner.core;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import org.devio.hi.imooc.R;
import org.devio.hi.imooc.banner.HiBanner;
import org.devio.hi.imooc.banner.indicator.HiIndicator;
import org.devio.hi.imooc.banner.indicator.HiNumIndicator;

import java.util.List;

// 代理类
public class HiBannerDelegate implements IHiBanner, ViewPager.OnPageChangeListener {

    private Context context;
    private HiBanner banner;

    private HiBannerAdapter mAdapter;
    private HiIndicator mHiIndicator;
    private boolean mAutoPlay;
    private boolean mLoop;
    private List<? extends HiBannerMo> mHiBannerMos;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private int mIntervalTime = 5000;
    private HiBanner.OnBannerClickListener mOnBannerClickListener;
    private HiViewPager mHiViewPager;
    private int mScrollDuration = -1;

    public HiBannerDelegate(Context context, HiBanner hiBanner) {
        this.context = context;
        this.banner = hiBanner;
    }


    @Override
    public void setBannerData(int layoutResId, @NonNull List<? extends HiBannerMo> models) {

        this.mHiBannerMos = models;
        init(layoutResId);
    }

    private void init(int layoutResId) {
        if (mAdapter == null) {
            mAdapter = new HiBannerAdapter(context);
        }
        if (mHiIndicator == null) {
            mHiIndicator = new HiNumIndicator(context);
        }
        mHiIndicator.onInflate(mHiBannerMos.size());

        mAdapter.setBannerData(mHiBannerMos);
        mAdapter.setLayoutResId(layoutResId);
        mAdapter.setAutoPlay(mAutoPlay);
        mAdapter.setLoop(mLoop);
        mAdapter.setOnBannerClickListener(mOnBannerClickListener);

        mHiViewPager = new HiViewPager(context);
        mHiViewPager.setIntervalTime(mIntervalTime);
        mHiViewPager.setAudoPlay(mAutoPlay);
        mHiViewPager.addOnPageChangeListener(this);
        // 设置ViewPager滚动速度

        mHiViewPager.setAdapter(mAdapter);

        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        banner.removeAllViews();
        banner.addView(mHiViewPager, layoutParams);
        banner.addView(mHiIndicator.get(), layoutParams);

    }

    @Override
    public void setBannerData(@NonNull List<? extends HiBannerMo> models) {
        setBannerData(R.layout.hi_banner_item_image, models);
    }

    @Override
    public void setHiIndicator(HiIndicator hiIndicator) {
        this.mHiIndicator = hiIndicator;
    }

    @Override
    public void setAutoPlay(boolean autoPlay) {
        this.mAutoPlay = autoPlay;
    }

    @Override
    public void setLoop(boolean loop) {
        this.mLoop = loop;
    }

    @Override
    public void setIntervalTime(int intervalTime) {
        if (intervalTime > 0) {
            this.mIntervalTime = intervalTime;
        }
    }

    @Override
    public void setBindAdapter(IBindAdapter bindAdapter) {
        mAdapter.setBindAdapter(bindAdapter);
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.mOnPageChangeListener = onPageChangeListener;
    }

    @Override
    public void setOnBannerClickListener(OnBannerClickListener onBannerClickListener) {
        this.mOnBannerClickListener = onBannerClickListener;
    }

    @Override
    public void setScrollDuration(int duration) {
        this.mScrollDuration = duration;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (mHiBannerMos != null) {
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(position);
            }
            mHiIndicator.onPointChange(position, mHiBannerMos.size());
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }
}
