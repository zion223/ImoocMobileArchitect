package org.devio.hi.imooc.banner.core;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import org.devio.hi.ui.banner.HiBanner;

import java.util.ArrayList;
import java.util.List;

public class HiBannerAdapter extends PagerAdapter {


    private Context mContext;
    private SparseArray<HiBannerViewHolder> mCachedViews = new SparseArray<>();
    private IHiBanner.OnBannerClickListener mBannerClickListener;
    private IBindAdapter mBindAdapter;
    private List<? extends HiBannerMo> models;
    /**
     * 是否开启自动轮播
     */
    private boolean mAutoPlay = true;
    /**
     * 非自动轮播状态下是否可以循环切换
     */
    private boolean mLoop = false;
    private int mLayoutResId = -1;

    private int loopCount = 500;
    public HiBannerAdapter(Context context) {
        this.mContext = context;
    }

    public void setBindAdapter(IBindAdapter bindAdapter) {
        this.mBindAdapter = bindAdapter;
    }

    public void setOnBannerClickListener(IHiBanner.OnBannerClickListener OnBannerClickListener) {
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

    public int getRealCount() {
        return models == null ? 0 : models.size();
    }

    @Override
    public int getCount() {
        return mAutoPlay ? models.size() * loopCount : (mLoop ? models.size() * loopCount : getRealCount());
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
        HiBannerViewHolder viewHolder = mCachedViews.get(realPosition);
        if (container.equals(viewHolder.rootView.getParent())) {
            container.removeView(viewHolder.rootView);
        }
        if (viewHolder.rootView.getParent() != null) {
            ((ViewGroup) viewHolder.rootView.getParent()).removeView(viewHolder.rootView);
        }
        onBind(viewHolder, models.get(realPosition), position);
        container.addView(viewHolder.rootView);
        return viewHolder.rootView;
    }


    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
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
            // 交由用户去实现 接口
            mBindAdapter.onBind(viewHolder, bannerMo, position);
        }
    }


    public void setBannerData(List<? extends HiBannerMo> data) {
        this.models = data;
        notifyDataSetChanged();
        initCachedView();
    }

    public int getFirstItemPosition() {

        int currentItem = loopCount / 2;
        if(currentItem % getRealCount()  ==0 ){
            return currentItem;
        }
        // 直到找到从0开始的位置
        while (currentItem % getRealCount() != 0){
            currentItem++;
        }
        return currentItem;

    }

    private void initCachedView() {
        mCachedViews = new SparseArray<>();
        for (int i = 0; i < models.size(); i++) {
            HiBannerViewHolder viewHolder = new HiBannerViewHolder(createView(LayoutInflater.from(mContext), null));
            mCachedViews.put(i, viewHolder);
        }
    }

    private View createView(LayoutInflater inflater, ViewGroup parent) {
        if (mLayoutResId == -1) {
            throw new IllegalArgumentException("");
        }
        return inflater.inflate(mLayoutResId, parent, false);
    }

    public static class HiBannerViewHolder {
        private SparseArray<View> viewSparseArray;
        View rootView;

        public HiBannerViewHolder(View view) {
            this.rootView = view;
        }

        public View getRootView() {
            return rootView;
        }

        public <V extends View> V findViewById(int id) {
            if (!(rootView instanceof ViewGroup)) {
                return (V) rootView;
            }
            if (this.viewSparseArray == null) {
                this.viewSparseArray = new SparseArray<>(1);
            }

            V childView = (V) viewSparseArray.get(id);
            if (childView == null) {
                childView = rootView.findViewById(id);
                this.viewSparseArray.put(id, childView);
            }

            return childView;
        }
    }
}
