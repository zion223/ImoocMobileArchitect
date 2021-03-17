package org.devio.hi.imooc.tab.common;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public interface IHiTabLayout<Tab extends ViewGroup, D> {
    Tab findTab(D data);

    void addTabSelectedChangeListener(OnTabSelectedListener<D> listener);

    void defaultSelected(D defaultInfo);

    void inflateInfo(List<D> infoList);

    interface OnTabSelectedListener<D> {
        void onTabSelectedChange(int index, @Nullable D prevInfo, @NonNull D nextInfo);
    }
}
