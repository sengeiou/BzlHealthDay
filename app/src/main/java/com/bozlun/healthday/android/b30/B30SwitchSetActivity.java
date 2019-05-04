package com.bozlun.healthday.android.b30;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bozlun.healthday.android.Commont;
import com.bozlun.healthday.android.MyApp;
import com.bozlun.healthday.android.R;
import com.bozlun.healthday.android.bleutil.MyCommandManager;
import com.bozlun.healthday.android.siswatch.WatchBaseActivity;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.settings.CustomSetting;
import com.veepoo.protocol.model.settings.CustomSettingData;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/8/13.
 */

/**
 * B30的开关设置
 */
public class B30SwitchSetActivity extends WatchBaseActivity {

    private static final String TAG = "B30SwitchSetActivity";

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.b30CheckWearToggleBtn)
    ToggleButton b30CheckWearToggleBtn;
    @BindView(R.id.b30AutoHeartToggleBtn)
    ToggleButton b30AutoHeartToggleBtn;
    @BindView(R.id.b30AutoBloadToggleBtn)
    ToggleButton b30AutoBloadToggleBtn;
    @BindView(R.id.b30SwitchFindPhoneToggleBtn)
    ToggleButton b30SwitchFindPhoneToggleBtn;
    @BindView(R.id.b30SecondToggleBtn)
    ToggleButton b30SecondToggleBtn;
    @BindView(R.id.b30SwitchDisAlertTogg)
    ToggleButton b30SwitchDisAlertTogg;
    @BindView(R.id.b30SwitchTimeTypeTogg)
    ToggleButton b30SwitchTimeTypeTogg;
    @BindView(R.id.disconn_alarm)
    RelativeLayout disconn_alarm;
    @BindView(R.id.secondwatch)
    RelativeLayout secondwatch;

    boolean isHaveMetricSystem = true;
    boolean isMetric = true;
    boolean isOpenAutoHeartDetect = false;
    boolean isOpenAutoBpDetect = false;  //血压
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;

    private Vibrator vibrator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        setContentView(R.layout.activity_b30_switch_set);
        ButterKnife.bind(this);

        initViews();
        readSwitchData();

    }


    EFunctionStatus isFindePhone = EFunctionStatus.SUPPORT_CLOSE;//控制查找手机UI
    EFunctionStatus isStopwatch = EFunctionStatus.SUPPORT_CLOSE;////秒表
    EFunctionStatus isWear = EFunctionStatus.SUPPORT_CLOSE;//佩戴检测
    EFunctionStatus isCallPhone = EFunctionStatus.SUPPORT_CLOSE;//来电
    EFunctionStatus isHelper = EFunctionStatus.SUPPORT_CLOSE;//SOS 求救
    EFunctionStatus isDisAlert = EFunctionStatus.SUPPORT_CLOSE;//断开
    boolean isSystem = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
    boolean is24Hour = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.IS24Hour, true);//是否为24小时制
    boolean isAutomaticHeart = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISAutoHeart, true);//自动测量心率
    boolean isAutomaticBoold = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISAutoBp, true);//自动测量血压
    boolean isSecondwatch = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSecondwatch, true);//秒表
    boolean isWearCheck = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISWearcheck, true);//佩戴
    boolean isFindPhone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISFindPhone, true);//查找手机
    boolean CallPhone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISCallPhone, true);//来电
    boolean isDisconn = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISDisAlert, true);//断开连接提醒
    boolean isSos = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISHelpe, false);//sos

    void stuteCheck() {

        isSystem = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
        is24Hour = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.IS24Hour, true);//是否为24小时制
        isAutomaticHeart = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(),  Commont.ISAutoHeart, true);//自动测量心率
        isAutomaticBoold = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISAutoBp, true);//自动测量血压
        isSecondwatch = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSecondwatch, true);
        isWearCheck = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISWearcheck, true);
        isFindPhone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISFindPhone, true);
        CallPhone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISCallPhone, true);
        isDisconn = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISDisAlert, true);
        isSos = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISHelpe, false);


        //查找手机
        if (isFindPhone) {
            isFindePhone = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isFindePhone = EFunctionStatus.SUPPORT_CLOSE;
        }

        //秒表
        if (isSecondwatch) {
            isStopwatch = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isStopwatch = EFunctionStatus.SUPPORT_CLOSE;
        }

        //佩戴检测
        if (isWearCheck) {
            isWear = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isWear = EFunctionStatus.SUPPORT_CLOSE;
        }
        //来电
        if (CallPhone) {
            isCallPhone = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isCallPhone = EFunctionStatus.SUPPORT_CLOSE;
        }
        //断开
        if (isDisconn){
            isDisAlert= EFunctionStatus.SUPPORT_OPEN;
        }else {
            isDisAlert = EFunctionStatus.SUPPORT_CLOSE;
        }
        //sos
        if (isSos) {
            isHelper = EFunctionStatus.SUPPORT_CLOSE;
        } else {
            isHelper = EFunctionStatus.SUPPORT_CLOSE;
        }
    }

    private void readSwitchData() {

        isWearCheck = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISWearcheck, true);//佩戴
        b30CheckWearToggleBtn.setChecked(isWearCheck);

        if (MyCommandManager.DEVICENAME != null) {
            MyApp.getInstance().getVpOperateManager().readCustomSetting(new IBleWriteResponse() {
                @Override
                public void onResponse(int i) {

                }
            }, new ICustomSettingDataListener() {
                @Override
                public void OnSettingDataChange(CustomSettingData customSettingData) {
                    Log.e(TAG, "---自定义设置--=" + customSettingData.toString());

                    if (customSettingData.getFindPhoneUi() == EFunctionStatus.SUPPORT_OPEN) {//查找手机
                        b30SwitchFindPhoneToggleBtn.setChecked(true);
                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISFindPhone, true);
                    } else {
                        b30SwitchFindPhoneToggleBtn.setChecked(false);
                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISFindPhone, false);
                    }
                    if (customSettingData.getSecondsWatch() == EFunctionStatus.SUPPORT_OPEN) {//秒表
                        b30SecondToggleBtn.setChecked(true);
                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSecondwatch, true);
                    } else {
                        b30SecondToggleBtn.setChecked(false);
                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSecondwatch, false);
                    }
                    if (customSettingData.getAutoHeartDetect() == EFunctionStatus.SUPPORT_OPEN) {//读取心率自动检测功能是否开启
                        b30AutoHeartToggleBtn.setChecked(true);
                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISAutoHeart, true);
                    } else {
                        b30AutoHeartToggleBtn.setChecked(false);
                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISAutoHeart, false);
                    }

                    if (customSettingData.getAutoBpDetect() == EFunctionStatus.SUPPORT_OPEN) {//读取血压自动检测功能是否开启
                        b30AutoBloadToggleBtn.setChecked(true);
                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISAutoBp, true);
                    } else {
                        b30AutoBloadToggleBtn.setChecked(false);
                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISAutoBp, false);
                    }

                    if (customSettingData.isIs24Hour()){
                        b30SwitchTimeTypeTogg.setChecked(true);
                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.IS24Hour, true);
                    }else {
                        b30SwitchTimeTypeTogg.setChecked(false);
                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.IS24Hour, false);
                    }

                    if (customSettingData.getDisconnectRemind() == EFunctionStatus.SUPPORT_OPEN){
                        b30SwitchDisAlertTogg.setChecked(true);
                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISDisAlert, true);
                    }else {
                        b30SwitchDisAlertTogg.setChecked(false);
                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISDisAlert, false);
                    }

                    if (customSettingData.getDisconnectRemind() == EFunctionStatus.UNSUPPORT) {//判断是否支持断连提醒
                        disconn_alarm.setVisibility(View.GONE);
                    }else {
                        disconn_alarm.setVisibility(View.VISIBLE);
                    }

//                    if(customSettingData.getFindPhoneUi() == EFunctionStatus.SUPPORT_OPEN){//判断是否开启查找手机UI
//                        b30SwitchFindPhoneToggleBtn.setChecked(true);
//                    }else {
//                        b30SwitchFindPhoneToggleBtn.setChecked(false);
//                    }

                }
            });

        }
    }


    private void initViews() {

        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.string_switch_setting));//"开关设置"
        b30CheckWearToggleBtn.setOnCheckedChangeListener(new SwitchClickListener());
        b30AutoHeartToggleBtn.setOnCheckedChangeListener(new SwitchClickListener());
        b30AutoBloadToggleBtn.setOnCheckedChangeListener(new SwitchClickListener());
        b30SwitchFindPhoneToggleBtn.setOnCheckedChangeListener(new SwitchClickListener());
        b30SecondToggleBtn.setOnCheckedChangeListener(new SwitchClickListener());
        b30SwitchDisAlertTogg.setOnCheckedChangeListener(new SwitchClickListener());
        b30SwitchTimeTypeTogg.setOnCheckedChangeListener(new SwitchClickListener());

    }

    @OnClick(R.id.commentB30BackImg)
    public void onViewClicked() {
        finish();

    }

    private class SwitchClickListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
            if (buttonView.isPressed()) {
                if (MyCommandManager.DEVICENAME != null) {
                    Log.d("TAG", "------点击啦");
                    switch (buttonView.getId()) {
                        case R.id.b30CheckWearToggleBtn:    //佩戴检测
                            SharedPreferencesUtils.setParam(B30SwitchSetActivity.this,  Commont.ISWearcheck, isChecked);
//                        CheckWearSetting checkWearSetting = new CheckWearSetting();
//                        checkWearSetting.isOpen();
//                        checkWearSetting.setOpen(isChecked);
//                        MyApp.getVpOperateManager().setttingCheckWear(iBleWriteResponse, new ICheckWearDataListener() {
//                            @Override
//                            public void onCheckWearDataChange(CheckWearData checkWearData) {
//                                Log.e(TAG, "------佩戴检测=" + checkWearData.getCheckWearState());
//                                if (checkWearData.getCheckWearState() == ECheckWear.OPEN_SUCCESS) {
//                                    SharedPreferencesUtils.setParam(B30SwitchSetActivity.this, "b30wearcheck", true);
//                                } else {
//                                    SharedPreferencesUtils.setParam(B30SwitchSetActivity.this, "b30wearcheck", false);
//                                    checkWearData.setCheckWearState(ECheckWear.CLOSE_SUCCESS);
//                                }
//                            }
//                        }, checkWearSetting);
                            break;
                        case R.id.b30AutoHeartToggleBtn:    //心率自动检测

                            b30AutoHeartToggleBtn.setChecked(isChecked);
                            SharedPreferencesUtils.setParam(B30SwitchSetActivity.this,  Commont.ISAutoHeart, isChecked);

//                        MyApp.getVpOperateManager().changeCustomSetting(iBleWriteResponse, new ICustomSettingDataListener() {
//                            @Override
//                            public void OnSettingDataChange(CustomSettingData customSettingData) {
//                                Log.e(TAG, "----OnSettingDataChange=" + customSettingData.toString());
//
//                                if (isChecked) {
//                                    customSettingData.setAutoHeartDetect(EFunctionStatus.SUPPORT_OPEN);
//                                    isOpenAutoBpDetect = true;
//                                    Log.e("心率--", customSettingData.getAutoHeartDetect() + "--开启");
//                                    SharedPreferencesUtils.setParam(B30SwitchSetActivity.this, "b30AutoHeart", true);
//                                } else {
//                                    customSettingData.setAutoHeartDetect(EFunctionStatus.SUPPORT_CLOSE);
//                                    isOpenAutoBpDetect = false;
//                                    Log.e("心率--", customSettingData.getAutoHeartDetect() + "--关闭");
//                                    SharedPreferencesUtils.setParam(B30SwitchSetActivity.this, "b30AutoHeart", false);
//                                }
//                            }
//                        }, new CustomSetting(isHaveMetricSystem, isMetric, is24Hour, isChecked, isOpenAutoBpDetect));


                            break;
                        case R.id.b30AutoBloadToggleBtn:    //血压自动检测
                            b30AutoBloadToggleBtn.setChecked(isChecked);
                            SharedPreferencesUtils.setParam(B30SwitchSetActivity.this, Commont.ISAutoBp, isChecked);


//                        MyApp.getVpOperateManager().changeCustomSetting(iBleWriteResponse, new ICustomSettingDataListener() {
//                            @Override
//                            public void OnSettingDataChange(CustomSettingData customSettingData) {
//                                Log.e(TAG, "----OnSettingDataChange=" + customSettingData.toString());
//                                if (isChecked) {
//                                    customSettingData.setAutoBpDetect(EFunctionStatus.SUPPORT_OPEN);
//                                    isOpenAutoHeartDetect = true;
//                                    Log.e("血压--", customSettingData.getAutoBpDetect() + "--血压开启");
//                                    SharedPreferencesUtils.setParam(B30SwitchSetActivity.this, "b30AutoBp", true);
//                                } else {
//                                    customSettingData.setAutoBpDetect(EFunctionStatus.SUPPORT_CLOSE);
//                                    isOpenAutoHeartDetect = false;
//                                    Log.e("血压--", customSettingData.getAutoBpDetect() + "--血压关闭");
//                                    SharedPreferencesUtils.setParam(B30SwitchSetActivity.this, "b30AutoBp", false);
//                                }
//                            }
//                        }, new CustomSetting(isHaveMetricSystem, isMetric, is24Hour, isOpenAutoHeartDetect, isChecked));

                            break;
                        case R.id.b30SwitchFindPhoneToggleBtn:  //查找手机
                            checkViberPerminess();
                            b30SwitchFindPhoneToggleBtn.setChecked(isChecked);
                            //打开查找手机UI   CustomSetting(isHaveMetricSystem, isMetric, is24Hour, isOpenAutoHeartDetect, isOpenAutoBpDetect, EFunctionStatus.UNKONW, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_OPEN, EFunctionStatus.UNKONW, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.UNKONW, EFunctionStatus.UNKONW, EFunctionStatus.UNKONW)

                            //关闭查手机UI     CustomSetting(isHaveMetricSystem, isMetric, is24Hour, isOpenAutoHeartDetect, isOpenAutoBpDetect, EFunctionStatus.UNKONW, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.UNKONW, EFunctionStatus.UNKONW, EFunctionStatus.UNKONW, EFunctionStatus.UNKONW, EFunctionStatus.UNKONW, EFunctionStatus.UNKONW)

                            //                 CustomSetting(isHaveMetricSystem, isMetric, is24Hour, isOpenAutoHeartDetect, isOpenAutoBpDetect,  isOpenSportRemain, EFunctionStatus isOpenVoiceBpHeart, EFunctionStatus isOpenFindPhoneUI, EFunctionStatus isOpenStopWatch, EFunctionStatus isOpenSpo2hLowRemind, EFunctionStatus isOpenWearDetectSkin, EFunctionStatus isOpenAutoInCall, EFunctionStatus isOpenAutoHRV, EFunctionStatus isOpenDisconnectRemind)
                            SharedPreferencesUtils.setParam(B30SwitchSetActivity.this, Commont.ISFindPhone, isChecked);
//                        if (isChecked) {
//
//                            MyApp.getVpOperateManager().changeCustomSetting(iBleWriteResponse, new ICustomSettingDataListener() {
//                                @Override
//                                public void OnSettingDataChange(CustomSettingData customSettingData) {
//
//                                }
//                            }, new CustomSetting(isHaveMetricSystem, isMetric, is24Hour,
//                                    isOpenAutoHeartDetect, isOpenAutoBpDetect, EFunctionStatus.UNKONW,
//                                    EFunctionStatus.UNKONW, EFunctionStatus.SUPPORT_OPEN, EFunctionStatus.UNKONW,
//                                    EFunctionStatus.UNKONW, EFunctionStatus.UNKONW, EFunctionStatus.UNKONW, EFunctionStatus.UNKONW, EFunctionStatus.UNKONW));
//
//                            MyApp.getVpOperateManager().settingFindPhoneListener(new IFindPhonelistener() {
//                                @Override
//                                public void findPhone() {
//                                    Log.d("findPhone", "findPhone: " + "查找手机-");
//                                    vibrate();//震动三秒
//                                    MediaPlayer mp = new MediaPlayer();
//                                    AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.music);
//                                    try {
//                                        mp.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
//                                                file.getLength());
//                                        mp.prepare();
//                                        file.close();
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                    mp.setVolume(0.5f, 0.5f);
//                                    mp.setLooping(false);
//                                    mp.start();
//
//                                }
//                            });
//
//                        } else {
//                            MyApp.getVpOperateManager().changeCustomSetting(iBleWriteResponse, new ICustomSettingDataListener() {
//                                        @Override
//                                        public void OnSettingDataChange(CustomSettingData customSettingData) {
//
//                                        }
//                                    }, new CustomSetting(isHaveMetricSystem, isMetric, is24Hour, isOpenAutoHeartDetect, isOpenAutoBpDetect, EFunctionStatus.UNKONW, EFunctionStatus.UNKONW, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.UNKONW, EFunctionStatus.UNKONW, EFunctionStatus.UNKONW, EFunctionStatus.UNKONW, EFunctionStatus.UNKONW, EFunctionStatus.UNKONW)
//                            );
//
//                        }
                            break;
                        case R.id.b30SecondToggleBtn:   //秒表功能 isOpenStopWatch
                            /**
                             * 秒表和查找手机还没修改完成
                             */
                            b30SecondToggleBtn.setChecked(isChecked);
                            Log.d("TAG", "------秒表状态" + isChecked);
                            SharedPreferencesUtils.setParam(B30SwitchSetActivity.this, Commont.ISSecondwatch, isChecked);


//                        if (isChecked) {
//                            MyApp.getVpOperateManager().changeCustomSetting(iBleWriteResponse, new ICustomSettingDataListener() {
//                                @Override
//                                public void OnSettingDataChange(CustomSettingData customSettingData) {
//                                    Log.d("dede", "OnSettingDataChange: " + customSettingData.getSecondsWatch());
//
//                                }
//                            }, new CustomSetting(isHaveMetricSystem, isMetric, is24Hour, isOpenAutoHeartDetect, isOpenAutoBpDetect, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.UNKONW, EFunctionStatus.SUPPORT_OPEN, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE));
//
//                        } else {
//                            MyApp.getVpOperateManager().changeCustomSetting(iBleWriteResponse, new ICustomSettingDataListener() {
//                                @Override
//                                public void OnSettingDataChange(CustomSettingData customSettingData) {
//                                    Log.d("dede", "OnSettingDataChange: " + customSettingData.getSecondsWatch());
//
//                                }
//                            }, new CustomSetting(isHaveMetricSystem, isMetric, is24Hour, isOpenAutoHeartDetect, isOpenAutoBpDetect, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.UNKONW, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE));
//
//                        }

                            break;

                        case R.id.b30SwitchDisAlertTogg:    //断连提醒

                            b30SwitchDisAlertTogg.setChecked(isChecked);
                            SharedPreferencesUtils.setParam(B30SwitchSetActivity.this, Commont.ISDisAlert, isChecked);

//                        if (isChecked) {
//                            MyApp.getVpOperateManager().changeCustomSetting(iBleWriteResponse, new ICustomSettingDataListener() {
//                                @Override
//                                public void OnSettingDataChange(CustomSettingData customSettingData) {
//
//                                }
//                            }, new CustomSetting(isHaveMetricSystem, isMetric, is24Hour, isOpenAutoHeartDetect, isOpenAutoBpDetect, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_OPEN, EFunctionStatus.SUPPORT_CLOSE));
//
//                        } else {
//                            MyApp.getVpOperateManager().changeCustomSetting(iBleWriteResponse, new ICustomSettingDataListener() {
//                                @Override
//                                public void OnSettingDataChange(CustomSettingData customSettingData) {
//
//                                }
//                            }, new CustomSetting(isHaveMetricSystem, isMetric, is24Hour, isOpenAutoHeartDetect, isOpenAutoBpDetect, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE, EFunctionStatus.SUPPORT_CLOSE));
//
//                        }


                            break;
                        case R.id.b30SwitchTimeTypeTogg:    //是否为24小时制
                            b30SwitchTimeTypeTogg.setChecked(isChecked);
                            SharedPreferencesUtils.setParam(B30SwitchSetActivity.this, Commont.IS24Hour, isChecked);
                            break;
                    }
                    stuteCheck();

                }

                showLoadingDialog(getResources().getString(R.string.dlog));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MyApp.getInstance().getVpOperateManager().changeCustomSetting(new IBleWriteResponse() {
                            @Override
                            public void onResponse(int i) {
                                closeLoadingDialog();
                            }
                        }, new ICustomSettingDataListener() {
                            @Override
                            public void OnSettingDataChange(CustomSettingData customSettingData) {

                                Log.d("TAG", "----修改状态" + customSettingData.toString());

                                if (is24Hour) {
                                    customSettingData.setIs24Hour(true);
                                } else {
                                    customSettingData.setIs24Hour(false);
                                }
                                if (isSystem) {
                                    customSettingData.setMetricSystem(EFunctionStatus.SUPPORT_OPEN);
                                } else {
                                    customSettingData.setMetricSystem(EFunctionStatus.SUPPORT_CLOSE);
                                }
                                if (isAutomaticHeart) {
                                    customSettingData.setAutoHeartDetect(EFunctionStatus.SUPPORT_OPEN);
                                } else {
                                    customSettingData.setAutoHeartDetect(EFunctionStatus.SUPPORT_CLOSE);
                                }
                                if (isAutomaticBoold) {
                                    customSettingData.setAutoBpDetect(EFunctionStatus.SUPPORT_OPEN);
                                } else {
                                    customSettingData.setAutoBpDetect(EFunctionStatus.SUPPORT_CLOSE);
                                }
                                if (isFindPhone) {
                                    customSettingData.setFindPhoneUi(EFunctionStatus.SUPPORT_OPEN);
                                } else {
                                    customSettingData.setFindPhoneUi(EFunctionStatus.SUPPORT_CLOSE);
                                }
                                if (isSos){
                                    customSettingData.setSOS(EFunctionStatus.SUPPORT_CLOSE);
                                }else {
                                    customSettingData.setSOS(EFunctionStatus.SUPPORT_CLOSE);
                                }
                                if (isDisconn){
                                    customSettingData.setDisconnectRemind(EFunctionStatus.SUPPORT_OPEN);
                                }else {
                                    customSettingData.setDisconnectRemind(EFunctionStatus.SUPPORT_CLOSE);
                                }
                            }
                        }, new CustomSetting(true,//isHaveMetricSystem
                                isSystem, //isMetric
                                is24Hour,//is24Hour
                                isAutomaticHeart, //isOpenAutoHeartDetect
                                isAutomaticBoold,//isOpenAutoBpDetect
                                EFunctionStatus.UNSUPPORT,//isOpenSportRemain
                                EFunctionStatus.UNSUPPORT,//isOpenVoiceBpHeart
                                isFindePhone,//isOpenFindPhoneUI
                                isStopwatch,//isOpenStopWatch
                                EFunctionStatus.UNSUPPORT,//isOpenSpo2hLowRemind
                                isWear,//isOpenWearDetectSkin
                                isCallPhone,//isOpenAutoInCall
                                EFunctionStatus.UNKONW,//isOpenAutoHRV
                                isDisAlert,//isOpenDisconnectRemind
                                isHelper));//isOpenSOS


                    }
                }, 1000);

            }

        }

    }

    //检查是否有震动的权限
    private void checkViberPerminess() {
        if (!AndPermission.hasPermissions(B30SwitchSetActivity.this, Manifest.permission.VIBRATE)) {
            AndPermission.with(B30SwitchSetActivity.this)
                    .runtime()
                    .permission(Manifest.permission.VIBRATE)
                    .rationale(new Rationale<List<String>>() {
                        @Override
                        public void showRationale(Context context, List<String> data, RequestExecutor executor) {
                            Toast.makeText(context, getResources().getString(R.string.string_necessary_authority), Toast.LENGTH_SHORT).show();
                        }
                    }).start();
        }
    }

    public void vibrate() {
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(3000); //参数标识  震动持续多少毫秒
        }
    }

}
