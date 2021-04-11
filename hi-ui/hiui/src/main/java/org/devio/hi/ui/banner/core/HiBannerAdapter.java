package org.devio.hi.ui.banner.core;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import org.devio.hi.ui.banner.HiBanner;

import java.util.List;

/**
 * HiViewPager的适配器，为页面填充数据
 */
public class HiBannerAdapter extends PagerAdapter {
    private Context mContext;
    private SparseArray<HiBannerViewHolder> mCachedViews = new SparseArray<>();
    private HiBanner.OnBannerClickListener mBannerClickListener;
    private IBindAdapter mBindAdapter;

    private List<? extends HiBannerMo> models;
    /**
     * 是否开启自动轮
     */
    private boolean mAutoPlay = true;
    /**
     * 非自动轮播状态下是否可以循环切换 控制getCount()返回的数量
     */
    private boolean mLoop = false;
    private int mLayoutResId = -1;
    private int loopCount = 500;

    public HiBannerAdapter(@NonNull Context mContext) {
        this.mContext = mContext;
    }

    public void setBannerData(@NonNull List<? extends HiBannerMo> models) {
        this.models = models;
        initCachedView();
        notifyDataSetChanged();

    }

    private void initCachedView() {
        mCachedViews = new SparseArray<>();
        for (int i = 0; i < models.size(); i++) {
            HiBannerViewHolder viewHolder = new HiBannerViewHolder(createView());
            mCachedViews.put(i, viewHolder);
        }
    }

    public void setBindAdapter(IBindAdapter bindAdapter) {
        this.mBindAdapter = bindAdapter;
    }

    public void setOnBannerClickListener(HiBanner.OnBannerClickListener OnBannerClickListener) {
        this.mBannerClickListener = OnBannerClickListener;
    }

    public void setLayoutResId(@LayoutRes int layoutResId) {
        this.mLayoutResId = layoutResId;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.mAutoPlay = autoPlay;
    }

    public void setLoop(boolean loop) {
        this.mLoop = loop;
    }

    /**
     * 获取Banner页面数量
     *
     * @return
     */
    public int getRealCount() {
        return models == null ? 0 : models.size();
    }

    @Override
    public int getCount() {
        //无限轮播关键点 数量为 Integer.MAX_VALUE 就可以一直滑动
        return mAutoPlay ? loopCount * models.size()  : (mLoop ? loopCount * models.size() : getRealCount());
    }

    /**
     * 获取初次展示的item位置
     */
    public int getFirstItem() {
        // TODO Tips: 这里是为了配合instantiateItem方法中 realPosition = position % getRealCount();
        // Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % getRealCount()的主要目的是用于获取realPosition=0的位置
        Log.e("xx", "firstItemPostion " + (Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % getRealCount()) + "");
        //return Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % getRealCount();
        //return 40 - 40 % getRealCount();
        int currentItem = loopCount / 2;
        if (currentItem % getRealCount() == 0) {
            return currentItem;
        }
        // 直到找到从0开始的位置
        while (currentItem % getRealCount() != 0) {
            currentItem++;
        }
        return currentItem;

    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        int realPosition = position;
        if (getRealCount() > 0) {
            realPosition = position % getRealCount();
        }
        Log.e("xx", "adapter " + position + "realPosition " + realPosition);
        HiBannerViewHolder viewHolder = mCachedViews.get(realPosition);
        if (container.equals(viewHolder.rootView.getParent())) {
            container.removeView(viewHolder.rootView);
        }

        onBind(viewHolder, models.get(realPosition), realPosition);
        if (viewHolder.rootView.getParent() != null) {
            ((ViewGroup) viewHolder.rootView.getParent()).removeView(viewHolder.rootView);
        }
        container.addView(viewHolder.rootView);
        return viewHolder.rootView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    }


    private View createView() {
        if (mLayoutResId == -1) {
            throw new IllegalArgumentException("you must be set LayoutResId first");
        }

        return LayoutInflater.from(mContext).inflate(mLayoutResId, null, false);
    }

    protected void onBind(@NonNull final HiBannerViewHolder viewHolder, @NonNull final HiBannerMo bannerMo, final int position) {
        viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBannerClickListener != null) {
                    mBannerClickListener.onBannerClick(viewHolder, bannerMo, position);
                }
            }
        });
        if (mBindAdapter != null) {
            // 交给用户去实现的接口
            mBindAdapter.onBind(viewHolder, bannerMo, position);
        }
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        //让item每次都会刷新
        return POSITION_NONE;
    }

    public static class HiBannerViewHolder {
        private SparseArray<View> viewHolderSparseArr;
        View rootView;

        HiBannerViewHolder(View rootView) {
            this.rootView = rootView;
        }

        public View getRootView() {
            return rootView;
        }

        public <V extends View> V findViewById(int id) {
            if (!(rootView instanceof ViewGroup)) {
                return (V) rootView;
            }
            if (this.viewHolderSparseArr == null) {
                this.viewHolderSparseArr = new SparseArray<>(1);
            }

            V childView = (V) viewHolderSparseArr.get(id);
            if (childView == null) {
                childView = rootView.findViewById(id);
                this.viewHolderSparseArr.put(id, childView);
            }

            return childView;
        }

    }
}