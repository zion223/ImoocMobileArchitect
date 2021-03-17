package com.imooc.mobile.common.tab;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.devio.hi.imooc.tab.bottom.HiTabBottomInfo;

import java.util.List;

/**
 * 通过FragmentManager来完成
 */
public class HiTabViewAdapter {
    private List<HiTabBottomInfo<?>> mInfoList;
    private Fragment mCurFragment;
    private FragmentManager mFragmentManager;

    public HiTabViewAdapter(FragmentManager fragmentManager, List<HiTabBottomInfo<?>> infoList) {
        this.mInfoList = infoList;
        this.mFragmentManager = fragmentManager;
    }

    /**
     * 实例化以及显示指定位置的fragment
     *
     * @param container HiFragmentTabView
     * @param position 切换位置
     */
    public void instantiateItem(View container, int position) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (mCurFragment != null) {
            // 隐藏当前Fragment
            fragmentTransaction.hide(mCurFragment);
        }
        String name = container.getId() + ":" + position;
        // 通过Tag查找Fragment
        Fragment fragment = mFragmentManager.findFragmentByTag(name);
        if (fragment != null) {
            // 已经存在了
            fragmentTransaction.show(fragment);
        } else {
            fragment = getItem(position);
            if (!fragment.isAdded()) {
                // 添加fragment时 添加fragment的tag
                fragmentTransaction.add(container.getId(), fragment, name);
            }
        }
        mCurFragment = fragment;
        fragmentTransaction.commit();
    }

    public Fragment getItem(int position) {
        try {
            // 创建Fragment
            return mInfoList.get(position).fragment.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public Fragment getCurFragment() {
        return mCurFragment;
    }

    public int getCount() {
        return mInfoList == null ? 0 : mInfoList.size();
    }
}
