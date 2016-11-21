package com.echoplex_x.kusomeme.network.api;

import android.graphics.Bitmap;

import java.io.File;

import static android.R.attr.type;

/**
 * Created by Win10-PC on 2016/11/19.
 */

public interface ImageDownLoadCallBack {
    void onDownLoadSuccess(File file, DownLoadImageService.IMAGE_TYPE type);

    void onDownLoadFailed();
}
