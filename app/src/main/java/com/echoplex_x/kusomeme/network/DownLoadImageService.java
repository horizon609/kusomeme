package com.echoplex_x.kusomeme.network;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;

/**
 * Created by Win10-PC on 2016/11/19.
 */

public class DownLoadImageService implements Runnable {
    private final String TAG = "DownLoadImageService";
    private String mUrl;
    private Context mContext;
    private ImageDownLoadCallBack mCallBack;
    public enum IMAGE_TYPE{
        GIF,JPEG
    };

    public DownLoadImageService(String url, Context context, ImageDownLoadCallBack callBack) {
        this.mUrl = url;
        this.mContext = context;
        this.mCallBack = callBack;
    }

    @Override
    public void run() {
        File file = null;
        IMAGE_TYPE type = IMAGE_TYPE.JPEG;
        try {
            if (mUrl.endsWith(".gif")) {
                type = IMAGE_TYPE.GIF;
            }
            //因为kusomeme要保存gif的，不能在这里用asBitmap.因为Glide不区分gif和jpg的，所以会把gif变成静态的存进Bitmap里面
            file = Glide.with(mContext)
                    .load(mUrl)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                Log.d(TAG, "得到meme文件");
                Log.d(TAG, "mCallBack:" + mCallBack);
                mCallBack.onDownLoadSuccess(file, type);
            } else {
                mCallBack.onDownLoadFailed();
            }
        }
    }
}
