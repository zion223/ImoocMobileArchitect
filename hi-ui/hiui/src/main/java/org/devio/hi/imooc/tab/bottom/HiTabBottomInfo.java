package org.devio.hi.imooc.tab.bottom;

import android.graphics.Bitmap;

import androidx.fragment.app.Fragment;

// Tab的数据模型
public class HiTabBottomInfo<Color>{
    public enum TabType {
        BITMAP, ICON
    }

    // 对应的Fragment类型
    public Class<? extends Fragment> fragment;
    public String name;
    // 默认bitmap
    public Bitmap defaultBitmap;
    public Bitmap selectedBitmap;
    public String iconFont;
    /**
     * Tips：在Java代码中直接设置iconfont字符串无效，需要定义在string.xml
     */
    public String defaultIconName;
    public String selectedIconName;
    // 默认的颜色
    public Color defaultColor;
    // 选中的颜色
    public Color tintColor;
    public TabType tabType;

    public HiTabBottomInfo(String name, Bitmap defaultBitmap, Bitmap selectedBitmap) {
        this.name = name;
        this.defaultBitmap = defaultBitmap;
        this.selectedBitmap = selectedBitmap;
        this.tabType = TabType.BITMAP;
    }

    public HiTabBottomInfo(String name, String iconFont, String defaultIconName, String selectedIconName, Color defaultColor, Color tintColor) {
        this.name = name;
        this.iconFont = iconFont;
        this.defaultIconName = defaultIconName;
        this.selectedIconName = selectedIconName;
        this.defaultColor = defaultColor;
        this.tintColor = tintColor;
        this.tabType = TabType.ICON;
    }
}
