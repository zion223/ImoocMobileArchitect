package org.devio.as.hi.hirouter.model;

public class Destination {
    /**
     * 页面url
     */
    public String pageUrl;
    /**
     * 路由节点（页面）的id
     */
    public int id;
    /**
     * 是否作为路由的第一个启动页
     */
    public boolean asStarter;
    /**
     * 路由节点(页面)的类型,activity,dialog,fragment
     */
    public String destType;
    /**
     * 全类名
     */
    public String clazName;
}
