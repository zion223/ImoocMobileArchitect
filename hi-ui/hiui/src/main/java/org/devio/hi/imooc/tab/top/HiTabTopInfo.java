package org.devio.hi.imooc.tab.top;

import android.graphics.Bitmap;

import androidx.fragment.app.Fragment;

// Tab的数据模型
public class HiTabTopInfo<Color>{
    public enum TabType {
        BITMAP, TEXT
    }

    // 对应的Fragment类型
    public Class<? extends Fragment> fragment;
    public String name;
    // 默认bitmap
    public Bitmap defaultBitmap;
    public Bitmap selectedBitmap;

    // 默认的颜色
    public Color defaultColor;
    // 选中的颜色
    public Color tintColor;
    public TabType tabType;

    public HiTabTopInfo(String name, Bitmap defaultBitmap, Bitmap selectedBitmap) {
        this.name = name;
        this.defaultBitmap = defaultBitmap;
        this.selectedBitmap = selectedBitmap;
        this.tabType = TabType.BITMAP;
    }

    public HiTabTopInfo(String name,Color defaultColor, Color tintColor) {
        this.name = name;
        this.defaultColor = defaultColor;
        this.tintColor = tintColor;
        this.tabType = TabType.TEXT;
    }
}
