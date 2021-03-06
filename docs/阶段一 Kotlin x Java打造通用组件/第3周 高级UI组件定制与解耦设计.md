# 高级UI组件定制与解耦设计

## 通用刷新组件HiRefresh开发

1. 需求分析
    - 提供通用的API，支持自定义Head

2. 疑难点分析
    - 监听手指滑动，切换刷新状态
    - 实现平滑滚动

## HiBanner组件开发

1. 需求分析
    - 支持自动轮播
    - 支持轮播速度设置
    - 支持UI以及指示器高度定制
    - 不耦合图片加载库

2. 疑难点分析
    - 作为有限的item如何实现无限轮播    
    - 指示器的高度定制
    - 将网络图片库与Banner组件解耦
    - 如何设置ViewPager的滚动速度


ViewPager的滚动速度设置，通过反射修改ViewPager默认的mScroller对象

```java
    public void setScrollDuration(int duration) {
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            // 自定义的Scroller
            scrollerField.set(this, new HiBannerScroller(getContext(), duration));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

```