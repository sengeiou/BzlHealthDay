package com.bozlun.healthday.android.b30;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bozlun.healthday.android.MyApp;
import com.bozlun.healthday.android.R;
import com.bozlun.healthday.android.b30.b30view.MyCicleView;
import com.bozlun.healthday.android.bleutil.MyCommandManager;
import com.bozlun.healthday.android.siswatch.WatchBaseActivity;
import com.bozlun.healthday.android.siswatch.utils.WatchUtils;
import com.tjdL4.tjdmain.contr.Health_HeartBldPrs;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IHeartDataListener;
import com.veepoo.protocol.model.datas.HeartData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/8/6.
 * <p>
 * 手动测量心率
 * <p>
 * Created by Administrator on 2018/8/6.
 */

/**
 * 手动测量心率
 */

/**
 * Created by Administrator on 2018/8/6.
 */

/**
 * 手动测量心率
 */
public class ManualMeaureHeartActivity extends WatchBaseActivity {

    private static final String TAG = "ManualMeaureHeartActivi";

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;
    @BindView(R.id.b30cirImg)
    ImageView b30cirImg;
    @BindView(R.id.b30ScaleLin)
    LinearLayout b30ScaleLin;
    @BindView(R.id.b30MeaureHeartValueTv)
    TextView b30MeaureHeartValueTv;
    //    @BindView(R.id.b30finishTv)
//    TextView b30finishTv;
    @BindView(R.id.b30MeaureHeartStartBtn)
    ImageView b30MeaureHeartStartBtn;


    /**
     * 圆环进度
     */
    @BindView(R.id.my_cicleview)
    MyCicleView my_cicleview;
    @BindView(R.id.rec_img_back)
    RelativeLayout rec_img_back;


    //是否正常测量
    private boolean isMeaure = false;
    //缩放动画
    Animation animationRoate;
    ScaleAnimation animation_suofang;
    String devicesType;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    HeartData heartData = (HeartData) msg.obj;
                    Log.e(TAG, "----heartData-=" + heartData.toString());
                    b30MeaureHeartValueTv.setText(heartData.getData() + "");
                    break;
                case 0x11:
                    //b30MeaureHeartStartBtn.setImageResource(R.drawable.detect_heart_start);

                    b30MeaureHeartStartBtn.setImageResource(R.mipmap.ic_heart_stop);
                    rec_img_back.setBackgroundResource(R.drawable.circle_sharpe_heart);
                    isMeaure = false;
                    stopAllAnimat(b30ScaleLin, b30cirImg);
                    //b30finishTv.setText("测量完毕");
                    String heartValue = (String) msg.obj;
                    Log.e(TAG, "----heartData-=" + heartValue);
                    b30MeaureHeartValueTv.setText(heartValue + "");
                    my_cicleview.stopTestAction(heartValue + "");
                    break;
                case 0x12:
                    Health_HeartBldPrs.ForceClose_HeartrateMeasure();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_meaure_heart_b15p);
        ButterKnife.bind(this);

        try {
            devicesType = getIntent().getStringExtra("what");
        } catch (Error error) {
        }


        initViews();
    }

    private void initViews() {
        commentB30TitleTv.setText(getResources().getString(R.string.string_manual_blood_pressure));
        commentB30ShareImg.setVisibility(View.GONE);
        commentB30BackImg.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.commentB30BackImg, R.id.commentB30ShareImg, R.id.b30MeaureHeartStartBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.commentB30ShareImg:
                WatchUtils.shareCommData(ManualMeaureHeartActivity.this);
                break;
            case R.id.b30MeaureHeartStartBtn:   //开始和结束
                if (MyCommandManager.DEVICENAME != null) {


                    if (!isMeaure) {

                        if (!WatchUtils.isEmpty(devicesType) && devicesType.equals("b15p")) {
                            testB15PHeart();
                        } else {

                            isMeaure = true;
                            b30MeaureHeartStartBtn.setImageResource(R.drawable.detect_heart_pause);
                            startAllAnimat(b30ScaleLin, b30cirImg);
                            MyApp.getInstance().getVpOperateManager().startDetectHeart(iBleWriteResponse, new IHeartDataListener() {
                                @Override
                                public void onDataChange(HeartData heartData) {
                                    if (heartData != null) {
                                        Message message = handler.obtainMessage();
                                        message.obj = heartData;
                                        message.what = 1001;
                                        handler.sendMessage(message);
                                    }
                                }
                            });
                        }


                    } else {
                        if (!WatchUtils.isEmpty(devicesType) && devicesType.equals("b15p")) {
                            //如果推出界面或者界面停止时心率还在测量，则强行关闭心率测量
                            Health_HeartBldPrs.ForceClose_HeartrateMeasure();
//                            b30MeaureHeartStartBtn.setImageResource(R.drawable.detect_heart_start);
                            if (b30MeaureHeartStartBtn != null)
                                b30MeaureHeartStartBtn.setImageResource(R.mipmap.ic_heart_stop);
                            if (rec_img_back!=null)rec_img_back.setBackgroundResource(R.drawable.circle_sharpe_heart);
                            isMeaure = false;

                            my_cicleview.stopTestAction("-- ");

                        } else {
//                            b30MeaureHeartStartBtn.setImageResource(R.drawable.detect_heart_start);
                            if (b30MeaureHeartStartBtn != null)
                                b30MeaureHeartStartBtn.setImageResource(R.mipmap.ic_heart_stop);
                            if (rec_img_back!=null)rec_img_back.setBackgroundResource(R.drawable.circle_sharpe_heart);
                            isMeaure = false;
                            stopAllAnimat(b30ScaleLin, b30cirImg);
                            //b30finishTv.setText("测量完毕");
                            MyApp.getInstance().getVpOperateManager().stopDetectHeart(iBleWriteResponse);
                        }
                    }
                } else {
                    // b30finishTv.setText(getResources().getString(R.string.device)+getResources().getString(R.string.string_not_coon));
                }
                break;
        }
    }

    /**
     * b15P心率测量
     */
    private void testB15PHeart() {
        Health_HeartBldPrs.Get_HeartrateMeasureResult(new Health_HeartBldPrs.HeartResultListener() {
            @Override
            public void OnErr(String EventStr, String ErrInfo) {
                if (EventStr.equals(Health_HeartBldPrs.START)) {
                    if (ErrInfo.equals(Health_HeartBldPrs.NOTSUPPORT)) {
                        Log.e(TAG, "-----手环不支持心率测量");
                    }
                } else if (EventStr.equals(Health_HeartBldPrs.CONNECT)) {
                    switch (ErrInfo) {
                        case Health_HeartBldPrs.WRONGCONNECTION:
                            Log.e(TAG, "-----蓝牙连接不正常");
                            break;
                        case Health_HeartBldPrs.ARESYNCHRONIZED:
                            Log.e(TAG, "-----正在同步心率数据,稍后再试!");
                            break;
                        case Health_HeartBldPrs.CONNECTLATER:
                            Log.e(TAG, "-----请稍后测量心率再试");
                            break;
                    }
                }
            }

            @Override
            public void OnOpen(String EventStr) {
                if (EventStr.equals(Health_HeartBldPrs.OPENSTART)) {
                    //测量开始
                    Log.e(TAG, "-----开始测量心率");
                    isMeaure = true;
                    //b30MeaureHeartStartBtn.setImageResource(R.drawable.detect_heart_pause);
                    if (b30MeaureHeartStartBtn != null)
                        b30MeaureHeartStartBtn.setImageResource(R.mipmap.ic_heart_start);
                    if (rec_img_back!=null)rec_img_back.setBackgroundResource(R.drawable.circle_sharpe_heart);
                    //startAllAnimat(b30ScaleLin, b30cirImg);

                    //b30finishTv.setText("测量中...");
                    my_cicleview.startTestAction();
                } else if (EventStr.equals(Health_HeartBldPrs.OPENOK)) {
                    //打开测量成功，等待结果
                    //可以在界面做等待超时处理，超时后可以使用Health_HeartBldPrs.ForceClose_HeartrateMeasure()强制关闭
                    Log.e(TAG, "-----打开开始测量心率成功， 可以强制关闭");
                    handler.sendEmptyMessageAtTime(0x12, 60 * 1000);//一分钟没检测到强制关闭
                }
            }

            @Override
            public void OnClose(String EventStr) {
                if (EventStr.equals(Health_HeartBldPrs.CLOSE)) {
                    //测量关闭
                    Log.e(TAG, "-----心率测量关闭");
                    b30MeaureHeartValueTv.setText("--");
                    //b30finishTv.setText(" ");
                } else if (EventStr.equals(Health_HeartBldPrs.END)) {
                    //测量结束
                    Log.e(TAG, "-----心率测量结束");
                }
            }

            @Override
            public void OnData(String EventStr, String DataInfo) {
                if (EventStr.equals(Health_HeartBldPrs.RESULTDATA)) {
                    //测量成果，返回数据
                    Log.e(TAG, "-----心率测量成功，返回结果 " + DataInfo);
                    Message message = handler.obtainMessage();
                    message.obj = DataInfo;
                    message.what = 0x11;
                    handler.sendMessage(message);
                } else if (EventStr.equals(Health_HeartBldPrs.FAIL)) {
                    //测量失败
                    Log.e(TAG, "-----心率测量失败 " + DataInfo);
                    b30MeaureHeartValueTv.setText("--");
//                    b30MeaureHeartStartBtn.setImageResource(R.drawable.detect_heart_start);
                    if (b30MeaureHeartStartBtn != null)
                        b30MeaureHeartStartBtn.setImageResource(R.mipmap.ic_heart_stop);
                    if (rec_img_back!=null)rec_img_back.setBackgroundResource(R.drawable.circle_sharpe_heart);
                    isMeaure = false;
                    stopAllAnimat(b30ScaleLin, b30cirImg);
                    //b30finishTv.setText("测量失败");
                }
            }
        });
    }


    private void startAllAnimat(View view1, View view2) {
        startFlick(view1);  //开启缩放动画
        startAnimat(view2); //开启旋转动画

    }

    //停止所有动画
    private void stopAllAnimat(View view1, View view2) {
        stopScanlAni(view1);
        stopRoateAnimt(view2);
    }

    private void stopScanlAni(View view) {
        if (view != null) {
            view.clearAnimation();
        }
    }


    private void stopRoateAnimt(View view) {
        if (view != null) {
            view.clearAnimation();
        }

    }


    //缩放动画
    public static void startFlick(View view) {
        if (null == view) {
            return;
        }
        ScaleAnimation animation_suofang = new ScaleAnimation(1.4f, 1.0f,
                1.4f, 1.0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation_suofang.setDuration(3000);                     //执行时间
        animation_suofang.setRepeatCount(-1);                   //重复执行动画
        animation_suofang.setRepeatMode(Animation.REVERSE);     //重复 缩小和放大效果
        view.startAnimation(animation_suofang);

    }

    //旋转动画
    private void startAnimat(View view) {
        if (view == null) {
            return;
        }
        animationRoate = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        animationRoate.setInterpolator(lin);

        animationRoate.setDuration(3 * 1000);
        animationRoate.setRepeatCount(-1);//动画的反复次数
        animationRoate.setFillAfter(true);//设置为true，动画转化结束后被应用
        view.startAnimation(animationRoate);//開始动画

    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };

    @Override
    protected void onStop() {
        super.onStop();
//        b30MeaureHeartStartBtn.setImageResource(R.drawable.detect_heart_start);
        if (b30MeaureHeartStartBtn != null)
            b30MeaureHeartStartBtn.setImageResource(R.mipmap.ic_heart_stop);
        if (rec_img_back!=null)rec_img_back.setBackgroundResource(R.drawable.circle_sharpe_heart);
        isMeaure = false;
        stopAllAnimat(b30ScaleLin, b30cirImg);
        //b30finishTv.setText("测量完毕");
        if (!WatchUtils.isEmpty(devicesType) && devicesType.equals("b15p")) {
            //如果推出界面或者界面停止时心率还在测量，则强行关闭心率测量
            Health_HeartBldPrs.ForceClose_HeartrateMeasure();
        } else {
            MyApp.getInstance().getVpOperateManager().stopDetectHeart(iBleWriteResponse);
        }

    }
}
