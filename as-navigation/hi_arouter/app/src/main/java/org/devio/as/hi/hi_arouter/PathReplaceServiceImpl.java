package org.devio.as.hi.hi_arouter;

import android.content.Context;
import android.net.Uri;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.PathReplaceService;

// 重写跳转URL
// 实现PathReplaceService接口，并加以router注解
@Route(path = "/pathReplace/service") // 必须标明注解
public class PathReplaceServiceImpl implements PathReplaceService {
    @Override
    public String forString(String path) {
        if (path.contains("login")) {
            path += "&userId=123456789";
        }
        return path;
    }

    @Override
    public Uri forUri(Uri uri) {
        return null;
    }

    @Override
    public void init(Context context) {
        //被调用的时候 才会被触发
    }
}
