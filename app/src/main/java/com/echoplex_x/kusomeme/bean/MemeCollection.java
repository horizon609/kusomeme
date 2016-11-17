package com.echoplex_x.kusomeme.bean;

import java.util.List;

/**
 * Created by echoplex_x on 2016/11/17.
 */

public class MemeCollection {
    public List<MemeItem> memelists;

    public class MemeItem {
        private int id;
        private String url;

        public MemeItem(int id, String url) {
            this.id = id;
            this.url = url;
        }

        public int getId() {
            return id;
        }

        public String getUrl() {
            return url;
        }
    }
}
