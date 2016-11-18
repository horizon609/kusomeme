package com.echoplex_x.kusomeme.utils;

import android.content.Context;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by echoplex_x on 2016/11/18.
 */

public class OKHttpHelper {

    private static OkHttpClient mOkHttpClient;

    private static final String API_BASE_URL = "http://123.56.233.178:30000/emoji?n=";

    static {
        initOkHttpClient();
    }

    private static void initOkHttpClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (mOkHttpClient == null) {
            synchronized (OKHttpHelper.class) {
                if (mOkHttpClient == null) {
                    //暂时不设缓存，因为每次请求的数据都不一样
                    mOkHttpClient = new OkHttpClient.Builder()
                            .addInterceptor(interceptor)
                            .retryOnConnectionFailure(true)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
    }

    public static String getStringFromUrl(Context context, int count) throws IOException {

        StringBuilder sb = new StringBuilder(API_BASE_URL);// http://123.56.233.178:30000/emoji?n="
        sb.append(count);
        Request request = new Request.Builder()
                .url(sb.toString())
                .build();

        Response response = mOkHttpClient.newCall(request).execute();
        return response.body().string();

    }
}
