package com.bozlun.healthday.android.siswatch;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bozlun.healthday.android.Commont;
import com.bozlun.healthday.android.bleutil.MyCommandManager;
import com.bumptech.glide.Glide;
import com.bozlun.healthday.android.R;
import com.bozlun.healthday.android.activity.wylactivity.wyl_util.ScreenShot;
import com.bozlun.healthday.android.net.OkHttpObservable;
import com.bozlun.healthday.android.rxandroid.CommonSubscriber;
import com.bozlun.healthday.android.rxandroid.SubscriberOnNextListener;
import com.bozlun.healthday.android.siswatch.bean.WatchDataDatyBean;
import com.bozlun.healthday.android.siswatch.utils.WatchUtils;
import com.bozlun.healthday.android.siswatch.view.RiseNumberTextView;
import com.bozlun.healthday.android.util.Common;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.bozlun.healthday.android.util.URLs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/16.
 */

public class H8ShareActivity extends WatchBaseActivity {
    private static final String TAG = "H8ShareActivity";

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //总数
    @BindView(R.id.h8ShareTotalTv)
    RiseNumberTextView h8ShareTotalTv;
    //累计
    @BindView(R.id.h8ShareGrandTv)
    TextView h8ShareGrandTv;

    List<WatchDataDatyBean> watchDataList;
    Map<String, Integer> sumMap;
    @BindView(R.id.h8ShareBtn)
    Button h8ShareBtn;
    @BindView(R.id.h8_dataShareHeadImg)
    ImageView h8DataShareHeadImg;
    @BindView(R.id.h8SharedUserNameTv)
    TextView h8SharedUserNameTv;
    @BindView(R.id.rela_logo)
    RelativeLayout rela_logo;

    private List<Integer> totalStesList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h8share);
        ButterKnife.bind(this);

        if (MyCommandManager.DEVICENAME != null && MyCommandManager.DEVICENAME.equals("W30")) {
            rela_logo.setVisibility(View.GONE);
        }
        initViews();

        getUserInfo();
        getTotalDatas();    //获取总数

    }

    private void getUserInfo() {
        final String url = URLs.HTTPs + URLs.getUserInfo; //查询用户信息
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("userId", SharedPreferencesUtils.readObject(H8ShareActivity.this, "userId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SubscriberOnNextListener subs = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("resultCode").equals("001")) {
                        JSONObject myInfoJsonObject = jsonObject.getJSONObject("userInfo");
                        if (myInfoJsonObject != null) {

                            String imgHead = myInfoJsonObject.getString("image");
                            if (!WatchUtils.isEmpty(imgHead)) {
                                RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true);
                                //头像
                                Glide.with(H8ShareActivity.this).load(imgHead)
                                        .apply(mRequestOptions)
                                        .into(h8DataShareHeadImg);    //头像
                            }
                            String userName = myInfoJsonObject.getString("nickName");
                            if (!WatchUtils.isEmpty(userName)) {
                                h8SharedUserNameTv.setText(userName);
                            }

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        };
        CommonSubscriber coms = new CommonSubscriber(subs, H8ShareActivity.this);
        OkHttpObservable.getInstance().getData(coms, url, jsonObj.toString());
    }

    private void getTotalDatas() {
        watchDataList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String url = URLs.HTTPs + URLs.GET_WATCH_DATA_DATA;
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userId", SharedPreferencesUtils.readObject(H8ShareActivity.this, "userId"));
            jsonParams.put("deviceCode", SharedPreferencesUtils.readObject(H8ShareActivity.this, Commont.BLEMAC));
            //开始时间
            jsonParams.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 365)));
            //结束时间
            jsonParams.put("endDate", WatchUtils.getCurrentDate());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SubscriberOnNextListener sb = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                if (result != null) {
                    try {
                        JSONObject jsonO = new JSONObject(result);
                        String dayData = jsonO.getString("day");
                        if (!dayData.equals("[]")) {
                            watchDataList = new Gson().fromJson(dayData, new TypeToken<List<WatchDataDatyBean>>() {
                            }.getType());
                            sumMap = new HashMap<>();
                            int sum = 0;
                            for (int i = 0; i < watchDataList.size(); i++) {
                                String strDate = watchDataList.get(i).getRtc().substring(0, 7);
                                if (sumMap.get(strDate) != null) {
                                    sum += watchDataList.get(i).getStepNumber();
                                } else {
                                    sum = watchDataList.get(i).getStepNumber();
                                }
                                sumMap.put(strDate, sum);
                            }
                            totalStesList = new ArrayList<>();
                            int sumStep = 0;
                            for (Map.Entry<String, Integer> mps : sumMap.entrySet()) {
                                sumStep += mps.getValue();
                            }

                            Log.d(TAG, "====sumStep======" + sumStep);
                            if (!h8ShareTotalTv.isRunning()) {
                                h8ShareTotalTv.setInteger(1, sumStep);
                                h8ShareTotalTv.setDuration(1500);
                                h8ShareTotalTv.start();
                            } else {
                                h8ShareTotalTv.setInteger(1, sumStep);
                            }
                            if (Locale.getDefault().getLanguage().equals("zh")) {
                                h8ShareGrandTv.setText("累计步数:" + sumStep + "步");
                            } else {
                                h8ShareGrandTv.setText(getResources().getString(R.string.grandtotalstep) + " " + sumStep + " " + getResources().getString(R.string.steps));
                            }


//                            if(sumStep <10000){
//                                if(!h8ShareTotalTv.isRunning()){
//                                    h8ShareTotalTv.setInteger(1, 10000);
//                                    h8ShareTotalTv.setDuration(1500);
//                                    h8ShareTotalTv.start();
//                                }
//                                if(Locale.getDefault().getLanguage().equals("zh")){
//                                    h8ShareGrandTv.setText("累计步数: 1 万步");
//                                }else{
//                                    h8ShareGrandTv.setText(getResources().getString(R.string.grandtotalstep)+ " "+10+" "+getResources().getString(R.string.thousand));
//                                }
//                            }else{
//                                int totalSteps = Integer.valueOf(StringUtils.substringBefore((WatchUtils.div(sumStep, 10000, 0) * 10000) + "", "."));
//                                if (totalSteps == 0) {
//                                    h8ShareTotalTv.setText("0");
//                                }else {
//                                    if(!h8ShareTotalTv.isRunning()){
//                                        h8ShareTotalTv.setInteger(1, totalSteps);
//                                        h8ShareTotalTv.setDuration(1500);
//                                        h8ShareTotalTv.start();
//                                    }
//                                }
//                                // h8ShareTotalTv.setText("" + StringUtils.substringBefore((WatchUtils.div(sumStep, 10000, 0)*10000)+"",".") + "");
//                                if(Locale.getDefault().getLanguage().equals("zh")){
//                                    h8ShareGrandTv.setText("累计步数:" + StringUtils.substringBefore((WatchUtils.div(sumStep, 10000, 0)) + "", ".") + "万步");
//                                }else{
//                                    h8ShareGrandTv.setText(getResources().getString(R.string.grandtotalstep)+ " "+StringUtils.substringBefore((WatchUtils.div(sumStep, 1000, 0)) + "", ".")+getResources().getString(R.string.thousand));
//                                }
//
//                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        };
        CommonSubscriber coms = new CommonSubscriber(sb, H8ShareActivity.this);
        OkHttpObservable.getInstance().getData(coms, url, jsonParams.toString());
    }

    private void initViews() {
        tvTitle.setText(getResources().getString(R.string.weibo_upload_content));
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeLoadingDialog();
            doShare();
            handler.removeMessages(1001);
            h8ShareBtn.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
        }
    };

    private void doShare() {
        Date timedf = new Date();
        SimpleDateFormat formatdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String xXXXdf = formatdf.format(timedf);
        String filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + System.currentTimeMillis() + ".png";
        Log.e(TAG,"-----imgPath="+filePath);
        ScreenShot.shoot(H8ShareActivity.this, new File(filePath));
        Common.showShare(H8ShareActivity.this, null, false, filePath);
    }

    @OnClick(R.id.h8ShareBtn)
    public void onViewClicked() {
        h8ShareBtn.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        showLoadingDialog("Loading...");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1001);
            }
        }, 3000);

    }
}
