package com.echoplex_x.kusomeme.network.api;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;

/**
 * Created by Win10-PC on 2016/11/19.
 */

public class DownLoadImageService implements Runnable {
    private String mUrl;
    private Context mContext;
    private ImageDownLoadCallBack mCallBack;

    public DownLoadImageService(String url, Context context, ImageDownLoadCallBack callBack) {
        this.mUrl = url;
        this.mContext = context;
        this.mCallBack = callBack;
    }

    @Override
    public void run() {
        File file = null;
        try {
        file = Glide.with(mContext)
                .load(mUrl)
                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                Log.e("wt","得到meme文件");
                Log.e("wt","mCallBack:" + mCallBack);
                mCallBack.onDownLoadSuccess(file);
            } else {
                mCallBack.onDownLoadFailed();
            }
        }
    }
}
