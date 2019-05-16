package com.bozlun.healthday.android.commdbserver;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.bozlun.healthday.android.R;
import com.bozlun.healthday.android.siswatch.WatchBaseActivity;

/**
 * Created by Admin
 * Date 2019/3/13
 * 用于加载Fragment的activity
 */
public class CommentDataActivity extends WatchBaseActivity {

    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_data_layout);


        initViews();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.commDataContent,new CommDataFragment());
        fragmentTransaction.commit();


    }

    private void initViews() {

    }
}
