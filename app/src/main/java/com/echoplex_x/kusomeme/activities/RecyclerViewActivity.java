package com.echoplex_x.kusomeme.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
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
import com.echoplex_x.kusomeme.utils.DownLoadHelper;
import com.echoplex_x.kusomeme.utils.OKHttpHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by echoplex_x on 2016/11/10.
 */
public class RecyclerViewActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private MemeAdapter mMemeAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mlinearLayoutManager;
    private boolean mIsRefreshing = false;
    List<MemeCollection.MemeItem> mMemelist;
    View rootView;
    BubbleTextView mBubbleTextView;
    BubblePopupWindow mBubblePopupWindow;
    private int mPosition;
    private ProgressDialog mSaveDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Callable<MemeCollection> callable = new Callable<MemeCollection>() {
            @Override
            public MemeCollection call() {
                String json = null;
                try {
                    json = OKHttpHelper.getStringFromUrl(RecyclerViewActivity.this, 20);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                MemeCollection memeCollection = gson.fromJson(json, MemeCollection.class);
                return memeCollection;
            }
        };

        FutureTask<MemeCollection> task = new FutureTask<MemeCollection>(callable);
        Thread t = new Thread(task);
        t.start();
        // 获取真实数据适配器并设置数据
        mMemelist = task.get().memelists;
        mMemeAdapter.addItems(mMemelist, 0);
        mSwipeRefreshLayout.setRefreshing(false);
        mIsRefreshing = false;
        // 包装适配器通知数据变更
        mMemeAdapter.notifyDataSetChanged();
    }

    private void initEvents() {
        mMemeAdapter.setOnRecyclerViewListener(new BaseRecyclerAdapter.OnRecyclerViewListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(RecyclerViewActivity.this, "you clicked item " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onItemLongClick(View view, final int position) {
                mBubblePopupWindow.showArrowTo(view, BubbleStyle.ArrowDirection.Up);
                Log.e("wt","mPosition:" + mPosition);
                mPosition = position;
                return false;
            }

        });
        rootView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //下载meme
                DownLoadHelper.onDownLoad(mMemelist.get(mPosition).getUrl(),RecyclerViewActivity.this);
                Log.e("wt","开始下载");
            }
        });
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
