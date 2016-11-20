package com.echoplex_x.kusomeme.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.echoplex_x.kusomeme.activities.RecyclerViewActivity;
import com.echoplex_x.kusomeme.network.api.DownLoadImageService;
import com.echoplex_x.kusomeme.network.api.ImageDownLoadCallBack;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Win10-PC on 2016/11/20.
 */

public class DownLoadHelper {
    private static ExecutorService singleExecutor = null;
    private static final String MEME_STORE_PATH = Environment.getExternalStorageDirectory().getPath() + "/kusomeme/meme/";


    /**
     * 执行单线程列队执行
     */
    private static void runOnQueue(Runnable runnable) {
        if (singleExecutor == null) {
            singleExecutor = Executors.newSingleThreadExecutor();
        }
        singleExecutor.submit(runnable);
    }

    /**
     * 启动图片下载线程
     */
    public static void onDownLoad(String url, final Context context) {
        DownLoadImageService service = new DownLoadImageService(url,
                context,
                new ImageDownLoadCallBack() {

                    @Override
                    public void onDownLoadSuccess(File file) {
                        Log.e("wt","准备现实toast");
//                        Toast.makeText(context,"表情保存成功~",Toast.LENGTH_SHORT).show();
                        saveMeme(file);
                        Log.e("wt","保存成功");
                    }

                    @Override
                    public void onDownLoadFailed() {
                        // 图片保存失败
                        Log.e("wt","保存失败");
                        Toast.makeText(context,"表情保存失败，请稍后再试~",Toast.LENGTH_SHORT).show();
                    }
                });
        //启动图片下载线程
        runOnQueue(service);
    }

    private static void saveMeme(File file) {
        Log.e("wt","MEME_STORE_PATH:" + MEME_STORE_PATH);
        File dirFile = new File(MEME_STORE_PATH);
        if(!dirFile.exists()){
            dirFile.mkdir();
            Log.e("wt","创建存放meme目录完成");
        }
        File myCaptureFile = new File(MEME_STORE_PATH + file.getName());
        Log.e("wt","meme's name:" + myCaptureFile.getName());
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            bos.flush();
            bos.close();
            Log.e("wt","save complete.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
