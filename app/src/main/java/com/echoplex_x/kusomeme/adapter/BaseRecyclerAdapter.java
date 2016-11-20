package com.echoplex_x.kusomeme.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echoplex_x on 2016/11/10.
 */
public abstract class BaseRecyclerAdapter<T,VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected final String TAG = getClass().getSimpleName();
    protected final Context mContext;

    protected List<T> mDataList = new ArrayList<>();

    /** 条目操作的回调监听 */
    protected OnRecyclerViewListener onRecyclerViewListener;

    public BaseRecyclerAdapter(Context context) {
        mContext = context;
    }

    public BaseRecyclerAdapter(Context context, ArrayList<T> arrayList) {
        mContext = context;
        mDataList = arrayList;
    }

    public List<T> getDataList() {
        return mDataList;
    }

    public T getItemData(int position) {
        return position < mDataList.size() ? mDataList.get(position) : null;
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    /**
     * 移除某一条记录
     *
     * @param position 移除数据的position
     */
    public void removeItem(int position) {
        if (position < mDataList.size()) {
            mDataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * 添加一条记录
     *
     * @param data     需要加入的数据结构
     * @param position 插入位置
     */
    public void addItem(T data, int position) {
        if (position <= mDataList.size()) {
            mDataList.add(position, data);
            notifyItemInserted(position);
        }
    }

    /**
     * 添加一条记录
     *
     * @param data 需要加入的数据结构
     */
    public void addItem(T data) {
        addItem(data, mDataList.size());
    }

    /**
     * 移除所有记录
     */
    public void clearItems() {
        int size = mDataList.size();
        if (size > 0) {
            mDataList.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

    /**
     * 批量添加记录
     *
     * @param data     需要加入的数据结构
     * @param position 插入位置
     */
    public void addItems(List<T> data, int position) {
        if (position <= mDataList.size() && data != null && data.size() > 0) {
            mDataList.addAll(position, data);
            notifyItemRangeChanged(position, data.size());
        }
    }

    /**
     * 批量添加记录
     *
     * @param data 需要加入的数据结构
     */
    public void addItems(List<T> data) {
        addItems(data, mDataList.size());
    }

    /**
     * 设置条目操作监听
     *
     * @param l
     */
    public void setOnRecyclerViewListener(OnRecyclerViewListener l) {
        this.onRecyclerViewListener = l;
    }

    /**
     * 条目操作回调监听接口
     */
    public interface OnRecyclerViewListener {
        /**
         * 条目点击的监听回调
         *
         * @param position
         */
        void onItemClick(View view, int position);

        /**
         * 长按点击的监听回调
         *
         * @param position
         */
        boolean onItemLongClick(View view,int position);
    }
}
