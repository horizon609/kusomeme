package com.echoplex_x.kusomeme.showmeme;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.echoplex_x.kusomeme.utils.SnackbarUtil;
import com.echoplex_x.kusomeme.utils.Util;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Win10-PC on 2016/11/28.
 */

public class ShowMemeFragment extends Fragment implements ShowMemeContract.View {
    private final String TAG = "ShowMemeFragment";
    private ShowMemeContract.Presenter mPresenter;
    private RecyclerView mRecyclerView;
    private MemeAdapter mMemeAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static ShowMemeFragment mShowMemeFragment;
    private boolean mIsRefreshing = false;

    View rootView;
    BubbleTextView mBubbleTextView;
    BubbleTextView mBubbleTextView2;
    BubblePopupWindow mBubblePopupWindow;
    private ExecutorService mShareExecutorService = Executors.newSingleThreadExecutor();

    private int mPosition;

    private Uri mImpUri;

    public static ShowMemeFragment getInstance() {
        if (mShowMemeFragment == null) {
            synchronized (ShowMemeFragment.class) {
                if (mShowMemeFragment == null) {
                    mShowMemeFragment = new ShowMemeFragment();
                }
            }
        }
        return mShowMemeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPop();
        initAdapter();
        initEvents();
    }

    private void initAdapter() {
        mMemeAdapter = new MemeAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //在fragment中，对SwipeRefreshLayout的初始化只能放在onCreate和onCreateView之后.否则就无法得到fragment的view
        //且不要放在onResume内，否则每次点亮屏幕都需要执行一次
        initSwipeRefreshLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    private void initViews(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.adapter_recycler_view);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        // 创建RecyclerView的数据适配器
        mRecyclerView.setAdapter(mMemeAdapter);

    }

    private void initSwipeRefreshLayout() {
        if (getView() == null) {
            return;
        }
        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh_layout);
        //加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                setRefreshing(true);
                showMemeData();
            }
        });
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
                Log.d(TAG, "mPosition:" + mPosition);
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
                mPresenter.downLoadMeme(mMemeAdapter.getDataList(), mPosition);
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
        if (view == null) {
            return;
        }
        mBubblePopupWindow.showArrowTo(view, BubbleStyle.ArrowDirection.Down);
    }

    @Override
    public void hidePop() {
        mBubblePopupWindow.dismiss();
    }

    @Override
    public void showProgressBar() {
        mSwipeRefreshLayout.setRefreshing(true);
    }


    @Override
    public void hideProgressBar() {
        setRefreshing(false);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showNetworkError() {
        hideProgressBar();
        SnackbarUtil.showMessage(mRecyclerView, "数据加载失败,请重新加载或者检查网络是否链接");
    }

    @Override
    public void showMemeData() {
        if (isRefreshing()) {
            showProgressBar();
            clearMemeData();
            try {
                mPresenter.populateMemeData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            if(mMemeAdapter.getDataList().size()==0){
                try {
                    showProgressBar();
                    mPresenter.populateMemeData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void clearMemeData() {
        mMemeAdapter.clearItems();
    }

    @Override
    public boolean isRefreshing() {
        return mIsRefreshing;
    }

    @Override
    public void setRefreshing(boolean isRefreshing) {
        mIsRefreshing = isRefreshing;
    }

    @Override
    public void addMemes(List<MemeCollection.MemeItem> memeItemList) {
        // 获取真实数据适配器并设置数据
        mMemeAdapter.addItems(memeItemList, 0);
        // 包装适配器通知数据变更
        mMemeAdapter.notifyDataSetChanged();
    }

    @Override
    public void setPresenter(ShowMemeContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
