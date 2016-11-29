package com.echoplex_x.kusomeme.showmeme;

import android.net.Uri;

import com.echoplex_x.kusomeme.common.BasePresenter;
import com.echoplex_x.kusomeme.common.BaseView;
import com.echoplex_x.kusomeme.bean.MemeCollection;

import java.util.List;

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
        void showMemeData();
        void clearMemeData();
        boolean isRefreshing();
        void setRefreshing(boolean isRefreshing);
        void addMemes(List<MemeCollection.MemeItem> memeItemList);
    }

    interface Presenter extends BasePresenter {
        void shareMeme(Uri uri);
        void downLoadMeme(List<MemeCollection.MemeItem> memelist, int position);
        void populateMemeData();
    }
}
