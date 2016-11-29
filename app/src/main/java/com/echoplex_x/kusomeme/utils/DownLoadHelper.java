package com.echoplex_x.kusomeme.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.echoplex_x.kusomeme.network.DownLoadImageService;
import com.echoplex_x.kusomeme.network.ImageDownLoadCallBack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.facebook.stetho.common.LogUtil.e;

/**
 * Created by Win10-PC on 2016/11/20.
 */

public class DownLoadHelper {
    private static ExecutorService singleExecutor = null;
    private static final String MEME_STORE_PATH = "kusomeme";


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
    public static void onDownLoad(String url, final Context context, ImageDownLoadCallBack callback) {
        DownLoadImageService service = new DownLoadImageService(url,
                context,callback);

        //启动图片下载线程
        runOnQueue(service);
    }

    public static void saveImageToGallery(Context context, File memeFile, String fileName) {
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        if (!path.exists()) {
            path.mkdir();
        }
        File file = new File(path, fileName);
        try {
            byte [] b = new byte[1024];
            FileInputStream fis = new FileInputStream(memeFile);
            FileOutputStream fos = new FileOutputStream(file);
            while (fis.read(b)!=-1){
                fos.write(b);
            }
            fos.flush();
            fos.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MediaScannerConnection.scanFile(context,
                new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }


}
