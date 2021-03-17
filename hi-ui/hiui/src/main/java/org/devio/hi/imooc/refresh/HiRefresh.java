package org.devio.hi.imooc.refresh;

public interface HiRefresh {
    // 刷新时禁止滚动
    void setDisableRefreshScroll(boolean disableRefreshScroll);

    void refreshFinished();

    void setRefreshListener(HiRefreshListener listener);

    void setRefreshOverView(HiOverView view);
    interface HiRefreshListener{
        void onRefresh();
        boolean enableRefresh();
    }

}
