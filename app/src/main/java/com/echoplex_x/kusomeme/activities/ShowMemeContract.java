package com.echoplex_x.kusomeme.activities;

import android.net.Uri;

import com.echoplex_x.kusomeme.BasePresenter;
import com.echoplex_x.kusomeme.BaseView;
/**
 * Created by Win10-PC on 2016/11/28.
 */

public interface ShowMemeContract {
    interface View extends BaseView<Presenter> {
        void showPop(android.view.View view);
        void hidePop();
        void showProgressBar();
        void hideProgressBar();
        void showNetworkError();
        void setRefreshing(boolean refreshing);
    }

    interface Presenter extends BasePresenter {
        void shareMeme(Uri uri);
        void clearMemeData();
        void downLoadMeme();
    }
}
