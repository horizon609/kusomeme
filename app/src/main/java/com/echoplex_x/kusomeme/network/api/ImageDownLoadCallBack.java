package com.echoplex_x.kusomeme.network.api;

import java.io.File;

/**
 * Created by Win10-PC on 2016/11/19.
 */

public interface ImageDownLoadCallBack {
    void onDownLoadSuccess(File file);

    void onDownLoadFailed();
}
