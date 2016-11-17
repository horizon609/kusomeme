package com.echoplex_x.kusomeme.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.echoplex_x.kusomeme.R;
import com.echoplex_x.kusomeme.adapter.BaseRecyclerAdapter;
import com.echoplex_x.kusomeme.adapter.MemeAdapter;
import com.echoplex_x.kusomeme.bean.MemeCollection;
import com.echoplex_x.kusomeme.utils.LocalFileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URL;

/**
 * Created by echoplex_x on 2016/11/10.
 */
public class RecyclerViewActivity extends ActionBarActivity {
    private RecyclerView mRecyclerView;
    private MemeAdapter mMemeAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_recycler);
        MemeCollection memeCollection = initData();
        // 获取真实数据适配器并设置数据
        mMemeAdapter = new MemeAdapter(this);
        mMemeAdapter.addItems(memeCollection.memelists,0);
        // 包装适配器通知数据变更
        mMemeAdapter.notifyDataSetChanged();
        initViews();
        initEvents();
    }

    private MemeCollection initData() {
        String json;
        json = LocalFileUtils.getStringFormAsset(this, "picture.json");

        // 使用JsonTool工具将JSON数据封装到实例对象
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        MemeCollection memeCollection = gson.fromJson(json, MemeCollection.class);
        return memeCollection;
    }

    private void initEvents() {
        mMemeAdapter.setOnRecyclerViewListener(new BaseRecyclerAdapter.OnRecyclerViewListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(RecyclerViewActivity.this, "you clicked item " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }//后续收藏
        });
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) this.findViewById(R.id.adapter_recycler_view);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // 创建RecyclerView的数据适配器
        mMemeAdapter = new MemeAdapter(this);
        mRecyclerView.setAdapter(mMemeAdapter);
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
