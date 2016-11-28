package com.echoplex_x.kusomeme.activities;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import com.echoplex_x.kusomeme.R;



/**
 * Created by echoplex_x on 2016/11/10.
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ShowMemeFragment mShowMemeFragment;
        ShowMemePresenter mShowMemePresenter;
        mShowMemeFragment.setPresenter(mShowMemePresenter);
    }




}
