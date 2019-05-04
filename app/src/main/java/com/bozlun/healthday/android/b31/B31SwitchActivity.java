package com.bozlun.healthday.android.b31;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bozlun.healthday.android.Commont;
import com.bozlun.healthday.android.MyApp;
import com.bozlun.healthday.android.R;
import com.bozlun.healthday.android.bleutil.MyCommandManager;
import com.bozlun.healthday.android.bzlmaps.HellpEditActivity;
import com.bozlun.healthday.android.siswatch.WatchBaseActivity;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IAllSetDataListener;
import com.veepoo.protocol.listener.data.ICheckWearDataListener;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.model.datas.AllSetData;
import com.veepoo.protocol.model.datas.CheckWearData;
import com.veepoo.protocol.model.enums.EAllSetType;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.settings.AllSetSetting;
import com.veepoo.protocol.model.settings.CheckWearSetting;
import com.veepoo.protocol.model.settings.CustomSetting;
import com.veepoo.protocol.model.settings.CustomSettingData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * B31开关设置界面
 * Created by Admin
 * Date 2018/12/19
 */
public class B31SwitchActivity extends WatchBaseActivity implements CompoundButton.OnCheckedChangeListener {


    private static final String TAG = "B31SwitchActivity";

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    //佩戴检测
    @BindView(R.id.b31CheckWearToggleBtn)
    ToggleButton b31CheckWearToggleBtn;
    //心率自动检测
    @BindView(R.id.b31AutoHeartToggleBtn)
    ToggleButton b31AutoHeartToggleBtn;
    //血氧夜间检测
    @BindView(R.id.b31AutoBPOxyToggbleBtn)
    ToggleButton b31AutoBPOxyToggbleBtn;
    //秒表功能
    @BindView(R.id.b31SecondToggleBtn)
    ToggleButton b31SecondToggleBtn;
    //秒表的布局
    @BindView(R.id.b31sSecondRel)
    RelativeLayout b31sSecondRel;



    //断连提醒
    @BindView(R.id.b31SwitchDisAlertTogg)
    ToggleButton b31SwitchDisAlertTogg;


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setSwitchCheck();
        }
    };
    //B31s，500S自动血压测量功能
    @BindView(R.id.b30AutoBloadToggleBtn)
    ToggleButton b30AutoBloadToggleBtn;
    @BindView(R.id.b31sBPAutoDetchRel)
    RelativeLayout b31sBPAutoDetchRel;


    //时间是否是24小时制
    @BindView(R.id.b30SwitchTimeTypeTogg)
    ToggleButton b30SwitchTimeTypeTogg;
    @BindView(R.id.deviceTimeTypeRel)
    RelativeLayout deviceTimeTypeRel;

    //B31S、500S查找手机功能
    @BindView(R.id.b30SwitchFindPhoneToggleBtn)
    ToggleButton b30SwitchFindPhoneToggleBtn;
    @BindView(R.id.b31sFindPhoneRel)
    RelativeLayout b31sFindPhoneRel;

    //B31S、500S  SOS 功能
    @BindView(R.id.b30SwitchHlepSos)
    ToggleButton b30SwitchHlepSos;
    @BindView(R.id.b31sHelpSos)
    RelativeLayout b31sHelpSos;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b31_switch_layout);
        ButterKnife.bind(this);


        initViews();

        verticalDevice();

        readDeviceCusSetting();

    }

    private void verticalDevice() {
        if(MyCommandManager.DEVICENAME != null){
            boolean isB31HasBp = (boolean) SharedPreferencesUtils.getParam(B31SwitchActivity.this,Commont.IS_B31_HAS_BP_KEY,false);
            if(isB31HasBp){
                b31sBPAutoDetchRel.setVisibility(View.VISIBLE); //自动血压监测
                b31sFindPhoneRel.setVisibility(View.VISIBLE);   //查找手机
                b31sHelpSos.setVisibility(View.VISIBLE);    //sos
            }
        }

    }

    private void readDeviceCusSetting() {
        if (MyCommandManager.DEVICENAME == null)
            return;

        boolean isWearCheck = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISWearcheck, true);//佩戴
        b31CheckWearToggleBtn.setChecked(isWearCheck);
        MyApp.getInstance().getVpOperateManager().readCustomSetting(iBleWriteResponse, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {
                Log.e(TAG, "----------customSettingData=" + customSettingData.toString() + "\n" + customSettingData.getAutoHeartDetect());
                //自动心率测量
                if (customSettingData.getAutoHeartDetect() == EFunctionStatus.SUPPORT_OPEN) {
                    b31AutoHeartToggleBtn.setChecked(true);
                } else {
                    b31AutoHeartToggleBtn.setChecked(false);
                }
                //秒表
                if(customSettingData.getSecondsWatch() == EFunctionStatus.UNSUPPORT)
                    b31sSecondRel.setVisibility(View.GONE);

                //秒表
                if (customSettingData.getSecondsWatch() == EFunctionStatus.SUPPORT_OPEN) {
                    b31SecondToggleBtn.setChecked(true);
                } else {
                    b31SecondToggleBtn.setChecked(false);
                }

                //断链提醒
                if (customSettingData.getDisconnectRemind() == EFunctionStatus.SUPPORT_OPEN) {
                    b31SwitchDisAlertTogg.setChecked(true);
                } else {
                    b31SwitchDisAlertTogg.setChecked(false);
                }

                //是否是24小时制
                b30SwitchTimeTypeTogg.setChecked(customSettingData.is24Hour());

                //查找手机
                if(customSettingData.getFindPhoneUi() == EFunctionStatus.SUPPORT_OPEN){
                    b30SwitchFindPhoneToggleBtn.setChecked(true);
                }else{
                    b30SwitchFindPhoneToggleBtn.setChecked(false);
                }

                //sos
                if(customSettingData.getSOS() == EFunctionStatus.SUPPORT_OPEN){
                    b30SwitchHlepSos.setChecked(true);
                }else{
                    b30SwitchHlepSos.setChecked(false);
                }

                //自动测量血压
                if(customSettingData.getAutoBpDetect() == EFunctionStatus.SUPPORT_OPEN){
                    b30AutoBloadToggleBtn.setChecked(true);
                }else{
                    b30AutoBloadToggleBtn.setChecked(false);
                }


            }
        });

        //读取血氧自动检测的状态
        MyApp.getInstance().getVpOperateManager().readSpo2hAutoDetect(iBleWriteResponse, new IAllSetDataListener() {
            @Override
            public void onAllSetDataChangeListener(AllSetData allSetData) {
                Log.e(TAG, "---------allSetData=" + allSetData.toString());
                if (allSetData.getOprate() == 1 && allSetData.getIsOpen() == 1) {
                    b31AutoBPOxyToggbleBtn.setChecked(true);
                } else {
                    b31AutoBPOxyToggbleBtn.setChecked(false);
                }
            }
        });


    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.string_switch_setting));

        b31CheckWearToggleBtn.setOnCheckedChangeListener(this);
        b31AutoHeartToggleBtn.setOnCheckedChangeListener(this);
        b31AutoBPOxyToggbleBtn.setOnCheckedChangeListener(this);
        b31SecondToggleBtn.setOnCheckedChangeListener(this);
        b31SwitchDisAlertTogg.setOnCheckedChangeListener(this);

        b30SwitchTimeTypeTogg.setOnCheckedChangeListener(this);
        b30SwitchFindPhoneToggleBtn.setOnCheckedChangeListener(this);
        b30SwitchHlepSos.setOnCheckedChangeListener(this);
        b30AutoBloadToggleBtn.setOnCheckedChangeListener(this);

    }

    @OnClick({R.id.commentB30BackImg,R.id.b31sHelpSos})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.b31sHelpSos:  //sos点击编辑
                boolean isSos = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISHelpe, false);//sos
                if (isSos) {
                    startActivity(new Intent(B31SwitchActivity.this,HellpEditActivity.class)
                            .putExtra("type","b31"));
                }
                break;
        }



    }


    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!buttonView.isPressed())
            return;
        switch (buttonView.getId()) {
            case R.id.b31CheckWearToggleBtn:    //佩戴检测
                b31CheckWearToggleBtn.setChecked(isChecked);
                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISWearcheck, isChecked);//佩戴
                SharedPreferencesUtils.setParam(B31SwitchActivity.this, Commont.ISWearcheck, isChecked);
                CheckWearSetting checkWearSetting = new CheckWearSetting();
                checkWearSetting.setOpen(isChecked);
                MyApp.getInstance().getVpOperateManager().setttingCheckWear(iBleWriteResponse, new ICheckWearDataListener() {
                    @Override
                    public void onCheckWearDataChange(CheckWearData checkWearData) {
                        Log.e(TAG, "----佩戴检测=" + checkWearData.toString());
                    }
                }, checkWearSetting);
                break;
            case R.id.b31AutoHeartToggleBtn:    //心率自动检测
                b31AutoHeartToggleBtn.setChecked(isChecked);
                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISAutoHeart, isChecked);//自动测量心率
                handler.sendEmptyMessage(1001);
                break;
            case R.id.b31AutoBPOxyToggbleBtn:   //血氧夜间检测
                b31AutoBPOxyToggbleBtn.setChecked(isChecked);
                SharedPreferencesUtils.setParam(B31SwitchActivity.this, Commont.B31_Night_BPOxy, isChecked);
                AllSetSetting allSetSetting = new AllSetSetting(EAllSetType.SPO2H_NIGHT_AUTO_DETECT,
                        22, 0, 8, 0, 0, isChecked ? 1 : 0);
                MyApp.getInstance().getVpOperateManager().settingSpo2hAutoDetect(iBleWriteResponse, new IAllSetDataListener() {
                    @Override
                    public void onAllSetDataChangeListener(AllSetData allSetData) {

                    }
                }, allSetSetting);

                break;
            case R.id.b31SecondToggleBtn:       //秒表功能
                b31SecondToggleBtn.setChecked(isChecked);
                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSecondwatch, isChecked);//秒表
                handler.sendEmptyMessage(1001);
                break;
            case R.id.b31SwitchDisAlertTogg:    //断连提醒
                b31SwitchDisAlertTogg.setChecked(isChecked);
                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISDisAlert, isChecked);//断开连接提醒
                handler.sendEmptyMessage(1001);
                break;
            case R.id.b30SwitchTimeTypeTogg:    //是否是24小时制
                b30SwitchTimeTypeTogg.setChecked(isChecked);
                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.IS24Hour, isChecked);//是否为24小时制
                handler.sendEmptyMessage(1001);

                break;
            case R.id.b30SwitchFindPhoneToggleBtn:  //查找手机
                b30SwitchFindPhoneToggleBtn.setChecked(isChecked);
                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISFindPhone, isChecked);//查找手机
                handler.sendEmptyMessage(1001);
                break;
            case R.id.b30SwitchHlepSos:     //sos
                b30SwitchHlepSos.setChecked(isChecked);
                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISHelpe, isChecked);//sos
                handler.sendEmptyMessage(1001);
                break;
            case R.id.b30AutoBloadToggleBtn:    //自动血压测量
                b30AutoBloadToggleBtn.setChecked(isChecked);
                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISAutoBp, isChecked);//自动测量血压
                handler.sendEmptyMessage(1001);
                break;

        }
    }

    //开关设置
    private void setSwitchCheck() {
        showLoadingDialog("loading...");

        //运动过量提醒 B31不支持
        EFunctionStatus isOpenSportRemain = EFunctionStatus.UNSUPPORT;
        //血压/心率播报 B31不支持
        EFunctionStatus isOpenVoiceBpHeart = EFunctionStatus.UNSUPPORT;
        //查找手表  B31不支持
        EFunctionStatus isOpenFindPhoneUI ;
        //秒表功能  支持
        EFunctionStatus isOpenStopWatch;
        //低压报警 支持
        EFunctionStatus isOpenSpo2hLowRemind = EFunctionStatus.SUPPORT_OPEN;
        //肤色功能 支持
        EFunctionStatus isOpenWearDetectSkin = EFunctionStatus.SUPPORT_OPEN;

        //自动接听来电 不支持
        EFunctionStatus isOpenAutoInCall = EFunctionStatus.UNSUPPORT;
        //自动检测HRV 支持
        EFunctionStatus isOpenAutoHRV = EFunctionStatus.SUPPORT_OPEN;
        //断连提醒 支持
        EFunctionStatus isOpenDisconnectRemind;
        //SOS  B31不支持
        EFunctionStatus isOpenSOS ;


        //保存的状态
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


        //秒表功能
        if (isSecondwatch) {
            isOpenStopWatch = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isOpenStopWatch = EFunctionStatus.SUPPORT_CLOSE;
        }
        //断连提醒
        if (isDisconn) {
            isOpenDisconnectRemind = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isOpenDisconnectRemind = EFunctionStatus.SUPPORT_CLOSE;
        }

        //sos
        if(isSos){
            isOpenSOS = EFunctionStatus.SUPPORT_OPEN;
        }else{
            isOpenSOS = EFunctionStatus.SUPPORT_CLOSE;
        }

        //查找手机
        if(isFindPhone){
            isOpenFindPhoneUI = EFunctionStatus.SUPPORT_OPEN;
        }else{
            isOpenFindPhoneUI = EFunctionStatus.SUPPORT_CLOSE;
        }




        CustomSetting customSetting = new CustomSetting(true, isSystem, is24Hour, isAutomaticHeart,
                isAutomaticBoold, isOpenSportRemain, isOpenVoiceBpHeart, isOpenFindPhoneUI, isOpenStopWatch, isOpenSpo2hLowRemind,
                isOpenWearDetectSkin, isOpenAutoInCall, isOpenAutoHRV, isOpenDisconnectRemind, isOpenSOS);
        Log.e(TAG, "--------customSetting=" + customSetting.toString());

        MyApp.getInstance().getVpOperateManager().changeCustomSetting(iBleWriteResponse, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {
                closeLoadingDialog();
                Log.e(TAG, "----customSettingData=" + customSettingData.toString());
            }
        }, customSetting);


    }
}
