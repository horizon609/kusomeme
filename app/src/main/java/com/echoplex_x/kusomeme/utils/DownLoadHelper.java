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

import static android.R.attr.path;
import static android.R.attr.x;
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

    public static void saveImageToGallery(Context context, File memeFile, String fileName) throws IOException {
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        if (!path.exists()) {
            path.mkdir();
        }
        File file = new File(path, fileName);
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            byte[] b = new byte[2048];
            fis = new FileInputStream(memeFile);
            fos = new FileOutputStream(file);
            while (fis.read(b) != -1) {
                fos.write(b);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null)
                fos.flush();
            if (fis != null)
                fis.close();
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
