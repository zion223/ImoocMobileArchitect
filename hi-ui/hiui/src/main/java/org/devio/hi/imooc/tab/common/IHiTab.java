package org.devio.hi.imooc.tab.common;

public interface IHiTab<D> extends IHiTabLayout.OnTabSelectedListener<D> {

    // 设置tabInfo
    void setHiTabInfo(D data);
    // 设置高度
    void resetHeight(int height);
}
