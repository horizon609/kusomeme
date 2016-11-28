package com.echoplex_x.kusomeme.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.echoplex_x.kusomeme.adapter.MemeAdapter;
import com.echoplex_x.kusomeme.bean.MemeCollection;
import com.echoplex_x.kusomeme.network.api.DownLoadImageService;
import com.echoplex_x.kusomeme.network.api.ImageDownLoadCallBack;
import com.echoplex_x.kusomeme.utils.DownLoadHelper;

import java.io.File;
import java.util.List;

/**
 * Created by Win10-PC on 2016/11/28.
 */

public class ShowMemePresenter implements ShowMemeContract.Presenter {
    private final ShowMemeContract.View mShowMemeView;
    private MainActivity mContext;
    private int mPosition;
    private MemeAdapter mMemeAdapter;
    private List<MemeCollection.MemeItem> mMemelist;

    public ShowMemePresenter(ShowMemeContract.View mShowMemeView,MainActivity context) {
        mContext = context;
        this.mShowMemeView = mShowMemeView;
    }

    @Override
    public void shareMeme(Uri uri) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        //这一句会把图片变成文件，可以调出返回第三方应用
//        shareIntent.putExtra(Intent.EXTRA_TEXT, "来自「kusomeme」的分享:" + "test");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/*");
        // Launch sharing dialog for image
        mContext.startActivity(Intent.createChooser(shareIntent, "Share meme"));
    }

    @Override
    public void clearMemeData() {
        mMemeAdapter.clearItems();
        mShowMemeView.setRefreshing(true);
    }

    @Override
    public void downLoadMeme() {
        DownLoadHelper.onDownLoad(mMemelist.get(mPosition).getUrl(),mContext,new ImageDownLoadCallBack() {

            @Override
            public void onDownLoadSuccess(File file, DownLoadImageService.IMAGE_TYPE type) {
                String fileName = null;
                switch (type) {
                    case GIF:
                        fileName = System.currentTimeMillis() + ".gif";
                        break;
                    case JPEG:
                        fileName = System.currentTimeMillis() + ".jpg";
                    default:
                        break;
                }
                Log.e("wt",Thread.currentThread().getName()+": 准备现实toast");
                //这里把图片保存在/storage/emulated/0/Pictures/下，可以立即刷新gallery中的图片，而在自己新建的的外部路径下无法立即更新，后续解决
                //使用google示例https://developer.android.com/reference/android/os/Environment.html#getExternalStoragePublicDirectory%28java.lang.String%29
                DownLoadHelper.saveImageToGallery(mContext
                        , file,fileName);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext,"表情保存成功~",Toast.LENGTH_SHORT).show();
                        mShowMemeView.hidePop();
                    }
                });
                Log.e("wt", Thread.currentThread().getName() + " :保存成功");
            }

            @Override
            public void onDownLoadFailed() {
                // 图片保存失败
                Log.e("wt","保存失败");
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext,"表情保存失败，请稍后再试~",Toast.LENGTH_SHORT).show();
                        mShowMemeView.hidePop();
                    }
                });

            }
        });
        Log.e("wt","开始下载");
    }

    @Override
    public void start() {

    }
}
