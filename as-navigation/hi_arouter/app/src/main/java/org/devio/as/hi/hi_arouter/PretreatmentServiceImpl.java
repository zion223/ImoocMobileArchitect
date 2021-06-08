package org.devio.as.hi.hi_arouter;

import android.content.Context;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.PretreatmentService;

// 实现 PretreatmentService 接口,并加以router注解
//@Route(path = "/pretreatment/service")
public class PretreatmentServiceImpl implements PretreatmentService {
    @Override
    public boolean onPretreatment(Context context, Postcard postcard) {
        // 跳转前预处理，如果需要自行处理跳转，该方法返回 false 即可
        return false;
    }

    @Override
    public void init(Context context) {
        //被调用的时候 才会被触发
    }
}
