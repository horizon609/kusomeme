package com.echoplex_x.kusomeme.utils;

import com.echoplex_x.kusomeme.bean.MemeCollection;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Win10-PC on 2016/11/26.
 */

public interface MemeService {
    @GET("emoji")
    Observable<MemeCollection> getMemeInfo(@Query("n") int num);
}
