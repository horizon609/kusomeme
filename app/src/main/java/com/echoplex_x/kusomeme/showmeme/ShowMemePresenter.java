package com.echoplex_x.kusomeme.showmeme;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.echoplex_x.kusomeme.bean.MemeCollection;
import com.echoplex_x.kusomeme.network.DownLoadImageService;
import com.echoplex_x.kusomeme.network.ImageDownLoadCallBack;
import com.echoplex_x.kusomeme.network.RetrofitHelper;
import com.echoplex_x.kusomeme.utils.DownLoadHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.R.attr.path;

/**
 * Created by Win10-PC on 2016/11/28.
 */

public class ShowMemePresenter implements ShowMemeContract.Presenter {
    private final String TAG = "ShowMemePresenter";
    private final ShowMemeContract.View mShowMemeView;
    private ShowMemeActivity mContext;
    public ShowMemePresenter(ShowMemeContract.View mShowMemeView,ShowMemeActivity context) {
        this.mShowMemeView = mShowMemeView;
        mContext = context;
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
        mContext.startActivity(Intent.createChooser(shareIntent, "Share meme"));
    }

    @Override
    public void downLoadMeme(List<MemeCollection.MemeItem> memelist,int position) {
        DownLoadHelper.onDownLoad(memelist.get(position).getUrl(),mContext,new ImageDownLoadCallBack() {

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
                Log.d(TAG,Thread.currentThread().getName()+": 准备现实toast");
                //TODO:这里把图片保存在/storage/emulated/0/Pictures/下，可以立即刷新gallery中的图片，而在自己新建的的外部路径下无法立即更新，后续解决
                //使用google示例https://developer.android.com/reference/android/os/Environment.html#getExternalStoragePublicDirectory%28java.lang.String%29
                try {
                    DownLoadHelper.saveImageToGallery(mContext
                            , file,fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext,"表情保存成功~",Toast.LENGTH_SHORT).show();
                        mShowMemeView.hidePop();
                    }
                });
                Log.d(TAG, Thread.currentThread().getName() + " :保存成功");
            }

            @Override
            public void onDownLoadFailed() {
                Log.d(TAG,"保存失败");
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext,"表情保存失败，请稍后再试~",Toast.LENGTH_SHORT).show();
                        mShowMemeView.hidePop();
                    }
                });

            }
        });
       Log.d(TAG,"开始下载");
    }

    @Override
    public void populateMemeData() {
        RetrofitHelper.geMemeApi().getMemeInfo(20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<MemeCollection>() {

                    @Override
                    public void call(MemeCollection memeCollection) {
                        mShowMemeView.setRefreshing(false);
                        mShowMemeView.hideProgressBar();
                        mShowMemeView.addMemes(memeCollection.getMemelists());
                    }
                }, new Action1<Throwable>() {

                    @Override
                    public void call(Throwable throwable) {
                        mShowMemeView.showNetworkError();
                    }
                });
    }

    @Override
    public void start() {
        mShowMemeView.showMemeData();
    }
}
