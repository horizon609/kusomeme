package com.echoplex_x.kusomeme.activities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cpiz.android.bubbleview.BubblePopupWindow;
import com.cpiz.android.bubbleview.BubbleStyle;
import com.cpiz.android.bubbleview.BubbleTextView;
import com.echoplex_x.kusomeme.R;
import com.echoplex_x.kusomeme.adapter.BaseRecyclerAdapter;
import com.echoplex_x.kusomeme.adapter.MemeAdapter;
import com.echoplex_x.kusomeme.bean.MemeCollection;
import com.echoplex_x.kusomeme.network.api.DownLoadImageService;
import com.echoplex_x.kusomeme.network.api.ImageDownLoadCallBack;
import com.echoplex_x.kusomeme.utils.DownLoadHelper;
import com.echoplex_x.kusomeme.utils.OKHttpHelper;
import com.echoplex_x.kusomeme.utils.RetrofitHelper;
import com.echoplex_x.kusomeme.utils.SnackbarUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.android.schedulers.AndroidSchedulers;


/**
 * Created by echoplex_x on 2016/11/10.
 */
public class RecyclerViewActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private MemeAdapter mMemeAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mlinearLayoutManager;
    private boolean mIsRefreshing = false;
    private static final int THUMB_SIZE = 150;
    List<MemeCollection.MemeItem> mMemelist;
    View rootView;
    BubbleTextView mBubbleTextView;
    BubbleTextView mBubbleTextView2;
    BubblePopupWindow mBubblePopupWindow;
    Uri mImpUri;
    private int mPosition;
    private ExecutorService mShareExecutorService = Executors.newSingleThreadExecutor();
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_recycler);
        initViews();
        initEvents();
    }

    private void showProgressBar() {
        //加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                mIsRefreshing = true;
                try {
                    getMemeData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                clearMemeData();
                try {
                    getMemeData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void clearMemeData() {
        mMemeAdapter.clearItems();
        mIsRefreshing = true;
    }
    private void getMemeData() throws Exception {
        RetrofitHelper.geMemeApi().getMemeInfo(20)
        .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<MemeCollection>()
                {

                    @Override
                    public void call(MemeCollection memeCollection)
                    {
                        mMemelist = memeCollection.getMemelists();
                        finishTask();
                    }
                }, new Action1<Throwable>()
                {

                    @Override
                    public void call(Throwable throwable)
                    {
                        initEmptyView();
                    }
                });
    }

    private void initEmptyView() {
        mSwipeRefreshLayout.setRefreshing(false);
        SnackbarUtil.showMessage(mRecyclerView, "数据加载失败,请重新加载或者检查网络是否链接");
    }

    private void finishTask() {
        mSwipeRefreshLayout.setRefreshing(false);
        mIsRefreshing = false;
        // 获取真实数据适配器并设置数据
        mMemeAdapter.addItems(mMemelist, 0);
        // 包装适配器通知数据变更
        mMemeAdapter.notifyDataSetChanged();
    }

    private void share(Uri uri) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        //这一句会把图片变成文件，可以调出返回第三方应用
//        shareIntent.putExtra(Intent.EXTRA_TEXT, "来自「kusomeme」的分享:" + "test");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/*");
        // Launch sharing dialog for image
        startActivity(Intent.createChooser(shareIntent, "Share meme"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mShareExecutorService.shutdown();
    }


    private void initEvents() {
        mMemeAdapter.setOnRecyclerViewListener(new BaseRecyclerAdapter.OnRecyclerViewListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(RecyclerViewActivity.this, "you clicked item " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onItemLongClick(final View view, final int position) {
                mBubblePopupWindow.showArrowTo(view, BubbleStyle.ArrowDirection.Down);
                Log.e("wt","mPosition:" + mPosition);
                mPosition = position;

                mShareExecutorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = convertViewToBitmap(view);
                        mImpUri = getUriFromBitmap(bitmap);
                    }
                });

                return false;
            }

        });
        rootView.findViewById(R.id.popup_bubble).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //下载meme
                DownLoadHelper.onDownLoad(mMemelist.get(mPosition).getUrl(),RecyclerViewActivity.this,new ImageDownLoadCallBack() {

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
                        DownLoadHelper.saveImageToGallery(RecyclerViewActivity.this
                                , file,fileName);
                        RecyclerViewActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RecyclerViewActivity.this,"表情保存成功~",Toast.LENGTH_SHORT).show();
                                mBubblePopupWindow.dismiss();
                            }
                        });

                        Log.e("wt", Thread.currentThread().getName() + " :保存成功");
                    }

                    @Override
                    public void onDownLoadFailed() {
                        // 图片保存失败
                        Log.e("wt","保存失败");
                        RecyclerViewActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RecyclerViewActivity.this,"表情保存失败，请稍后再试~",Toast.LENGTH_SHORT).show();
                                mBubblePopupWindow.dismiss();
                            }
                        });

                    }
                });
                Log.e("wt","开始下载");
            }
        });
        rootView.findViewById(R.id.popup_bubble2).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                share(mImpUri);
            }
        });
    }

    public static Bitmap convertViewToBitmap(View view){
//        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//        view.buildDrawingCache();
//        Bitmap bitmap = view.getDrawingCache();
//        return bitmap;
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    public Uri getUriFromBitmap(Bitmap bitmap) {
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private void initViews() {
        initPop();
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        showProgressBar();
        mRecyclerView = (RecyclerView) this.findViewById(R.id.adapter_recycler_view);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));

        // 创建RecyclerView的数据适配器
        mMemeAdapter = new MemeAdapter(this);
        mRecyclerView.setAdapter(mMemeAdapter);

    }

    private void initPop() {
        rootView = LayoutInflater.from(this).inflate(R.layout.simple_text_bubble, null);
        mBubbleTextView = (BubbleTextView) rootView.findViewById(R.id.popup_bubble);
        mBubbleTextView2 = (BubbleTextView) rootView.findViewById(R.id.popup_bubble2);
        mBubblePopupWindow = new BubblePopupWindow(rootView, mBubbleTextView);
        mBubblePopupWindow.setCancelOnLater(3000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
