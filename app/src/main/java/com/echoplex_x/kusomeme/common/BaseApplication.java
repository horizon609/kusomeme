package com.echoplex_x.kusomeme.common;


import android.app.Application;

/**
 * Created by echoplex_x on 2016/11/18.
 */

public class BaseApplication extends Application {
    private static BaseApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static BaseApplication getInstance(){
       return mInstance;
    }
}
