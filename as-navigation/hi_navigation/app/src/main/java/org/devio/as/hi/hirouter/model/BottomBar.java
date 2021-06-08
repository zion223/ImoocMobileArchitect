package org.devio.as.hi.hirouter.model;

import java.util.List;

public class BottomBar {

    /**
     * selectTab : 0
     * tabs : [{"size":24,"enable":true,"index":0,"pageUrl":"main/tabs/home","title":"Home"},{"size":24,"enable":true,"index":1,"pageUrl":"main/tabs/dashborad","title":"Dashboard"},{"size":40,"enable":true,"index":2,"pageUrl":"main/tabs/notification","title":"Notification"}]
     */

    public int selectTab;
    public List<Tab> tabs;

    public static class Tab {
        /**
         * size : 24
         * enable : true
         * index : 0
         * pageUrl : main/tabs/home
         * title : Home
         */

        public int size;
        public boolean enable;
        public int index;
        public String pageUrl;
        public String title;
    }
}
