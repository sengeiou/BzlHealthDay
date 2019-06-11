package com.bozlun.healthday.android.b15p.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bozlun.healthday.android.R;
import com.bozlun.healthday.android.siswatch.WatchBaseActivity;
import com.bozlun.healthday.android.siswatch.utils.WatchUtils;
import com.tjdL4.tjdmain.L4M;
import com.tjdL4.tjdmain.contr.L4Command;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 内部测试用，
 * Created by Admin
 * Date 2019/6/10
 */
public class B15PIntervalActivity extends WatchBaseActivity  {

    private static final String TAG = "B15PIntervalActivity";


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.showB15PStatusTv)
    TextView showB15PStatusTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b15p_internal);
        ButterKnife.bind(this);

        initViews();

        L4M.SysnALLData();


        /**
         * 设置监听----同步数据保存本地数据库
         */
        L4M.SetResultToDBListener(listenr);

    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(WatchUtils.getCurrentDate());
    }

    @OnClick({R.id.commentB30BackImg, R.id.readDeviceStepBtn, R.id.readDbStepBtn, R.id.readDeviceHeartBtn, R.id.readDbHeartBtn, R.id.readDeviceSleepBtn, R.id.readDBSleepBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.readDeviceStepBtn:    //读取手环的步数
                L4Command.GetPedo1();
                break;
            case R.id.readDbStepBtn:    //读取数据库中的数据

                break;
            case R.id.readDeviceHeartBtn:   //读取手环中的心率

                break;
            case R.id.readDbHeartBtn:   //读取数据库中的心率

                break;
            case R.id.readDeviceSleepBtn:   //读取手环中的睡眠

                break;
            case R.id.readDBSleepBtn:   //读取数据库中的睡眠1

                break;

        }
    }


    private L4M.BTResultToDBListenr listenr = new L4M.BTResultToDBListenr() {
        @Override
        public void DoData(int i, String s, String s1) {
            super.DoData(i, s, s1);

            Log.e(TAG,"-----DoData="+i+"--s="+s+"---s1="+s1);
        }

        @Override
        public void ProgressData(int i, int i1, int i2, String s, String s1) {
            super.ProgressData(i, i1, i2, s, s1);
            Log.e(TAG,"----ProgressData="+i+"--i1="+i1+"--i2="+i2+"---s="+s+"--s1="+s1);

        }

        @Override
        public void On_Result(String s, String s1, Object o) {
            Log.e(TAG,"----------on_Resul="+s+"---s1="+s1+"---o="+(o==null?"为null":o.toString()));
        }

        @Override
        public void On_ProgressResult(String s, int i, int i1, String s1, Object o) {
            Log.e(TAG,"-------On_ProgressResult="+s+"---i="+i+"--i1="+i1+"--s1="+s1+"--o="+(o==null?"为null":o.toString()));
            if(s.equals("PEDO_DAY")){   //当天的汇总步数
                String[] all_step = {"all_step", s1};

            }
            //历史步数，详细
            if(s.equals("PEDO_TIME_HISTORY")){

            }




        }
    };



}
