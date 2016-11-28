package com.echoplex_x.kusomeme.activities;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cpiz.android.bubbleview.BubblePopupWindow;
import com.cpiz.android.bubbleview.BubbleStyle;
import com.cpiz.android.bubbleview.BubbleTextView;
import com.echoplex_x.kusomeme.R;
import com.echoplex_x.kusomeme.adapter.BaseRecyclerAdapter;
import com.echoplex_x.kusomeme.adapter.MemeAdapter;
import com.echoplex_x.kusomeme.bean.MemeCollection;
import com.echoplex_x.kusomeme.utils.RetrofitHelper;
import com.echoplex_x.kusomeme.utils.SnackbarUtil;
import com.echoplex_x.kusomeme.utils.Util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Win10-PC on 2016/11/28.
 */

public class ShowMemeFragment extends Fragment implements ShowMemeContract.View{
    private ShowMemeContract.Presenter mPresenter;
    private RecyclerView mRecyclerView;
    private MemeAdapter mMemeAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mIsRefreshing = false;

    View rootView;
    BubbleTextView mBubbleTextView;
    BubbleTextView mBubbleTextView2;
    BubblePopupWindow mBubblePopupWindow;
    private ExecutorService mShareExecutorService = Executors.newSingleThreadExecutor();

    private int mPosition;
    List<MemeCollection.MemeItem> mMemelist;
    private Uri mImpUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);
        initViews(view);
        initEvents();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initViews(View view) {
        initPop();
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        showProgressBar();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.adapter_recycler_view);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));

        // 创建RecyclerView的数据适配器
        mMemeAdapter = new MemeAdapter(getActivity());
        mRecyclerView.setAdapter(mMemeAdapter);

    }
    private void initPop() {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.simple_text_bubble, null);
        mBubbleTextView = (BubbleTextView) rootView.findViewById(R.id.popup_bubble);
        mBubbleTextView2 = (BubbleTextView) rootView.findViewById(R.id.popup_bubble2);
        mBubblePopupWindow = new BubblePopupWindow(rootView, mBubbleTextView);
        mBubblePopupWindow.setCancelOnLater(3000);
    }

    private void initEvents() {
        mMemeAdapter.setOnRecyclerViewListener(new BaseRecyclerAdapter.OnRecyclerViewListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity(), "you clicked item " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onItemLongClick(final View view, final int position) {
                showPop(view);
                Log.e("wt","mPosition:" + mPosition);
                mPosition = position;

                mShareExecutorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = Util.convertViewToBitmap(view);
                        mImpUri = Util.getUriFromBitmap(bitmap);
                    }
                });

                return false;
            }

        });
        rootView.findViewById(R.id.popup_bubble).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //下载meme
                mPresenter.downLoadMeme();
            }
        });
        rootView.findViewById(R.id.popup_bubble2).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mPresenter.shareMeme(mImpUri);
            }
        });
    }


    @Override
    public void showPop(View view) {
        mBubblePopupWindow.showArrowTo(view, BubbleStyle.ArrowDirection.Down);
    }

    @Override
    public void hidePop(){
        mBubblePopupWindow.dismiss();
    }

    @Override
    public void showProgressBar() {
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

    @Override
    public void hideProgressBar() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showNetworkError() {
        hideProgressBar();
        SnackbarUtil.showMessage(mRecyclerView, "数据加载失败,请重新加载或者检查网络是否链接");
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        mIsRefreshing = refreshing;
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
                        showNetworkError();
                    }
                });
    }



    private void finishTask() {
        mSwipeRefreshLayout.setRefreshing(false);
        mIsRefreshing = false;
        // 获取真实数据适配器并设置数据
        mMemeAdapter.addItems(mMemelist, 0);
        // 包装适配器通知数据变更
        mMemeAdapter.notifyDataSetChanged();
    }



    @Override
    public void setPresenter(ShowMemeContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
