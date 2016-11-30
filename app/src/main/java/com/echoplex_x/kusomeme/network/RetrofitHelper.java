package com.echoplex_x.kusomeme.network;

import com.echoplex_x.kusomeme.common.BaseApplication;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
/**
 * Created by Win10-PC on 2016/11/26.
 */

public class RetrofitHelper {
    private static final String API_BASE_URL = "http://123.56.233.178:30000/";
    private static OkHttpClient mOkHttpClient;
    private static MemeService mMemeService;

    /**
     * 获取表情包
     *
     * @return
     */

    public static MemeService geMemeApi() {
        if(mMemeService == null){
            synchronized (RetrofitHelper.class){
                if(mMemeService == null){
                    initOkHttpClient();
                    mMemeService = getRetrofit().create(MemeService.class);
                }
            }
        } return mMemeService;


    }

    /**
     * 初始化OKHttpClient
     * 设置缓存
     * 设置超时时间
     */
    private static void initOkHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //设置Http缓存
        Cache cache = new Cache(new File(BaseApplication.getInstance()
                .getCacheDir(), "HttpCache"), 1024 * 1024 * 100);

        mOkHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(interceptor)
                .addNetworkInterceptor(new StethoInterceptor())
                .retryOnConnectionFailure(true)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
    }

    private static Retrofit getRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return  retrofit;
    }
}
