package com.echoplex_x.kusomeme.showmeme;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import com.echoplex_x.kusomeme.R;



/**
 * Created by echoplex_x on 2016/11/10.
 */
public class ShowMemeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ShowMemeFragment mShowMemeFragment =
                (ShowMemeFragment) getSupportFragmentManager().findFragmentById(R.id.id_fragment_title);
        if(mShowMemeFragment == null){
            mShowMemeFragment = ShowMemeFragment.getInstance();
        }
        ShowMemePresenter mShowMemePresenter = new ShowMemePresenter(mShowMemeFragment,this);
        mShowMemeFragment.setPresenter(mShowMemePresenter);
    }




}
