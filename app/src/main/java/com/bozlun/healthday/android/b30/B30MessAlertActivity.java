package com.bozlun.healthday.android.b30;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.internal.telephony.ITelephony;
import com.bozlun.healthday.android.Commont;
import com.bozlun.healthday.android.MyApp;
import com.bozlun.healthday.android.R;
import com.bozlun.healthday.android.bleutil.MyCommandManager;
import com.bozlun.healthday.android.siswatch.WatchBaseActivity;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IDeviceControlPhone;
import com.veepoo.protocol.listener.data.ISocialMsgDataListener;
import com.veepoo.protocol.model.datas.FunctionSocailMsgData;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/8/13.
 */

/**
 * B30消息提醒页面
 */
public class B30MessAlertActivity extends WatchBaseActivity {

    private static final String TAG = "B30MessAlertActivity";

    @BindView(R.id.b30SkypeTogg)
    ToggleButton b30SkypeTogg;
    @BindView(R.id.b30WhatsAppTogg)
    ToggleButton b30WhatsAppTogg;
    @BindView(R.id.b30FacebookTogg)
    ToggleButton b30FacebookTogg;
    @BindView(R.id.b30LinkedTogg)
    ToggleButton b30LinkedTogg;
    @BindView(R.id.b30TwitterTogg)
    ToggleButton b30TwitterTogg;
    @BindView(R.id.b30LineTogg)
    ToggleButton b30LineTogg;
    @BindView(R.id.b30WechatTogg)
    ToggleButton b30WechatTogg;
    @BindView(R.id.b30QQTogg)
    ToggleButton b30QQTogg;
    @BindView(R.id.b30MessageTogg)
    ToggleButton b30MessageTogg;
    @BindView(R.id.b30PhoneTogg)
    ToggleButton b30PhoneTogg;
    @BindView(R.id.newSearchTitleTv)
    TextView newSearchTitleTv;

    boolean isOpenPhone = false;
    boolean isMsg = false;
    //微信
    boolean isWeChat = false;
    //QQ
    boolean isQQ = false;
    //facebook
    boolean isFaceBook = false;
    //twitter
    boolean isTwitter = false;

    //Linkin
    boolean isLinkin = false;
    //skype
    boolean isSkype = false;
    //Whats
    boolean isWhats = false;
    //Line
    boolean isLine = false;
    //other
    boolean isOther = false;


    @BindView(R.id.b30OhterTogg)
    ToggleButton b30OhterTogg;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_msgalert);
        ButterKnife.bind(this);

        initViews();

        //申请权限
        requestPermiss();

        getPhoneStatus();
        //读取社交消息设置
        readSocialMsg();

        //getDoNotDisturb();

    }


    private void getPhoneStatus() {
        AudioManager audioManager = (AudioManager) MyApp.getInstance().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int ringMode = audioManager.getRingerMode();
            //audioManager.getStreamVolume()
            Log.e(TAG, "-------手环模式=" + ringMode);
            switch (ringMode) {
                case AudioManager.RINGER_MODE_NORMAL:
                    //普通模式
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    //振动模式
                    break;
                case AudioManager.RINGER_MODE_SILENT:
                    //静音模式
                    break;
            }

        }
    }


    //获取Do not disturb权限,才可进行音量操作
    private void getDoNotDisturb() {
        NotificationManager notificationManager =
                (NotificationManager) MyApp.getInstance().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            MyApp.getInstance().getApplicationContext().startActivity(intent);
        }

    }


    //申请电话权限
    private void requestPermiss() {
//        if (!AndPermission.hasPermissions(B30MessAlertActivity.this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG})) {
//            AndPermission.with(B30MessAlertActivity.this)
//                    .runtime()
//                    .permission(Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG)
//                    .rationale(new Rationale<List<String>>() {
//                        @Override
//                        public void showRationale(Context context, List<String> data, RequestExecutor executor) {
//                            executor.execute();
//                        }
//                    })
//                    .start();
//        }
//
//        if (!AndPermission.hasPermissions(B30MessAlertActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS})) {
//            AndPermission.with(B30MessAlertActivity.this)
//                    .runtime()
//                    .permission(Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG)
//                    .start();
//        }
        if (!AndPermission.hasPermissions(B30MessAlertActivity.this, new String[]{
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG})) {
            AndPermission.with(B30MessAlertActivity.this)
                    .runtime()
                    .permission(Manifest.permission.CALL_PHONE,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.READ_CALL_LOG)
//                            ,Manifest.permission.WRITE_CALL_LOG)
                    .rationale(new Rationale<List<String>>() {
                        @Override
                        public void showRationale(Context context, List<String> data, RequestExecutor executor) {
                            executor.execute();
                        }
                    })
                    .start();
        }

        if (!AndPermission.hasPermissions(B30MessAlertActivity.this, new String[]{
//                Manifest.permission.READ_SMS,
                Manifest.permission.READ_CONTACTS})) {
            AndPermission.with(B30MessAlertActivity.this)
                    .runtime()
                    .permission(
//                            Manifest.permission.READ_SMS,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.READ_CALL_LOG)//,Manifest.permission.WRITE_CALL_LOG)
                    .start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * 跳转到通知使用权
     *
     * @param context
     * @return
     */
    private boolean gotoNotificationAccessSetting(Context context) {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                context.startActivity(intent);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }


    private void registerPhoneStateListener() {
        CustomPhoneStateListener customPhoneStateListener = new CustomPhoneStateListener(this);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.listen(customPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }


    private void readSocialMsg() {
        if (MyCommandManager.DEVICENAME != null) {
            MyApp.getInstance().getVpOperateManager().readSocialMsg(iBleWriteResponse, new ISocialMsgDataListener() {
                @Override
                public void onSocialMsgSupportDataChange(FunctionSocailMsgData functionSocailMsgData) {
                    Log.e(TAG, "----读取=" + functionSocailMsgData.toString() + functionSocailMsgData.getPhone() + EFunctionStatus.SUPPORT_OPEN);
                    if (functionSocailMsgData.getSkype() == EFunctionStatus.SUPPORT_OPEN) {
                        b30SkypeTogg.setChecked(true);
                        isSkype = true;
                    } else {
                        b30SkypeTogg.setChecked(false);
                        isSkype = false;
                    }

                    if (functionSocailMsgData.getWhats() == EFunctionStatus.SUPPORT_OPEN) {
                        b30WhatsAppTogg.setChecked(true);
                        isWhats = true;
                    } else {
                        b30WhatsAppTogg.setChecked(false);
                        isWhats = false;
                    }

                    if (functionSocailMsgData.getFacebook() == EFunctionStatus.SUPPORT_OPEN) {
                        b30FacebookTogg.setChecked(true);
                        isFaceBook = true;
                    } else {
                        b30FacebookTogg.setChecked(false);
                        isFaceBook = false;
                    }
                    if (functionSocailMsgData.getLinkin() == EFunctionStatus.SUPPORT_OPEN) {
                        b30LinkedTogg.setChecked(true);
                        isLinkin = true;
                    } else {
                        b30LinkedTogg.setChecked(false);
                        isLinkin = false;
                    }
                    if (functionSocailMsgData.getTwitter() == EFunctionStatus.SUPPORT_OPEN) {
                        b30TwitterTogg.setChecked(true);
                        isTwitter = true;
                    } else {
                        b30TwitterTogg.setChecked(false);
                        isTwitter = false;
                    }
                    //viber


                    if (functionSocailMsgData.getLine() == EFunctionStatus.SUPPORT_OPEN) {
                        b30LineTogg.setChecked(true);
                        isLine = true;
                    } else {
                        b30LineTogg.setChecked(false);
                        isLine = false;
                    }
                    if (functionSocailMsgData.getWechat() == EFunctionStatus.SUPPORT_OPEN) {
                        b30WechatTogg.setChecked(true);
                        isWeChat = true;
                    } else {
                        b30WechatTogg.setChecked(false);
                        isWeChat = false;
                    }
                    if (functionSocailMsgData.getQq() == EFunctionStatus.SUPPORT_OPEN) {
                        b30QQTogg.setChecked(true);
                        isQQ = true;
                    } else {
                        b30QQTogg.setChecked(false);
                        isQQ = false;
                    }
                    if (functionSocailMsgData.getMsg() == EFunctionStatus.SUPPORT_OPEN) {
                        b30MessageTogg.setChecked(true);
                        isMsg = true;
                    } else {
                        b30MessageTogg.setChecked(false);
                        isMsg = false;
                    }
                    if (functionSocailMsgData.getPhone() == EFunctionStatus.SUPPORT_OPEN) {
                        b30PhoneTogg.setChecked(true);
                        isOpenPhone = true;
                    } else {
                        b30PhoneTogg.setChecked(false);
                        isOpenPhone = false;
                    }

                    //other
                    isOther = functionSocailMsgData.getOther() == EFunctionStatus.SUPPORT_OPEN;
                    b30OhterTogg.setChecked(isOther);
                }
            });
        }
    }


    private void initViews() {
        newSearchTitleTv.setText(getResources().getString(R.string.string_ocial_message));//社交小心哦
        b30SkypeTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30WhatsAppTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30FacebookTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30LinkedTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30TwitterTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30LineTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30WechatTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30QQTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30MessageTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30PhoneTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30OhterTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());


    }

    @OnClick({R.id.newSearchTitleLeft, R.id.newSearchRightImg1, R.id.msgOpenNitBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.newSearchTitleLeft:
                finish();
                break;
            case R.id.newSearchRightImg1:

                break;
            case R.id.msgOpenNitBtn:    //打开通知
                Intent intentr = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivityForResult(intentr, 1001);
                break;
        }
    }

    //监听
    private class ToggCheckChanageListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!buttonView.isPressed())
                return;
            switch (buttonView.getId()) {
                case R.id.b30SkypeTogg: //skype
                    isSkype = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISSkype, isChecked);
                    break;
                case R.id.b30WhatsAppTogg:  //whatspp
                    isWhats = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISWhatsApp, isChecked);
                    break;
                case R.id.b30FacebookTogg:  //facebook
                    isFaceBook = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISFacebook, isChecked);
                    break;
                case R.id.b30LinkedTogg:    //linked
                    isLinkin = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISLinkendln, isChecked);
                    break;
                case R.id.b30TwitterTogg:   //twitter
                    isTwitter = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISTwitter, isChecked);
                    break;
                case R.id.b30LineTogg:  //line
                    isLine = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISLINE, isChecked);
                    break;
                case R.id.b30WechatTogg:    //wechat
                    isWeChat = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISWechart, isChecked);
                    break;
                case R.id.b30QQTogg:    //qq
                    isQQ = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISQQ, isChecked);
                    break;
                case R.id.b30MessageTogg:   //msg
                    requestPermiss();
                    isMsg = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISMsm, isChecked);
                    break;
                case R.id.b30OhterTogg:     //其它
                    isOther = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this,Commont.ISOther,isChecked);
                    break;
                case R.id.b30PhoneTogg: //phone
                    requestPermiss();
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISPhone, isChecked);
                    isOpenPhone = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, "isCallPhone", isChecked);
                    MyApp.getInstance().getVpOperateManager().settingDeviceControlPhone(new IDeviceControlPhone() {
                        @Override
                        public void rejectPhone() {//TODO 挂电话还没弄好

                            try {
                                Method method = Class.forName("android.os.ServiceManager")
                                        .getMethod("getService", String.class);//getSystemService内部就是调用了ServiceManager的getService方法。
                                IBinder binder = (IBinder) method.invoke(null,
                                        new Object[]{TELEPHONY_SERVICE});
                                ITelephony iTelephony = ITelephony.Stub.asInterface(binder);
                                iTelephony.endCall();
                                Log.d("call---", "rejectPhone: " + "电话被挂断了");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void cliencePhone() {
                            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                            if (audioManager != null) {
                                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                audioManager.getStreamVolume(AudioManager.STREAM_RING);
                                Log.d("call---", "RINGING 已被静音");
                            }
                        }

                        @Override
                        public void knocknotify(int i) {

                        }

                        @Override
                        public void sos() {

                        }

//                        @Override
//                        public void knocknotify(int i) {
//
//                        }
//
//                        @Override
//                        public void sos() {
//
//                        }
                    });

                    break;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setSocailMsg();
                }
            });

        }
    }


    private void setSocailMsg() {
        FunctionSocailMsgData socailMsgData = new FunctionSocailMsgData();
        //电话提醒
        if (isOpenPhone) {
            socailMsgData.setPhone(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setPhone(EFunctionStatus.SUPPORT_CLOSE);
        }
        //短信
        if (isMsg) {
            socailMsgData.setMsg(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setMsg(EFunctionStatus.SUPPORT_CLOSE);
        }
        //微信
        if (isWeChat) {
            socailMsgData.setWechat(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setWechat(EFunctionStatus.SUPPORT_CLOSE);
        }
        //QQ
        if (isQQ) {
            socailMsgData.setQq(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setQq(EFunctionStatus.SUPPORT_CLOSE);
        }
        //新浪 不支持
        socailMsgData.setSina(EFunctionStatus.UNSUPPORT);
        //facebook
        if (isFaceBook) {
            socailMsgData.setFacebook(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setFacebook(EFunctionStatus.SUPPORT_CLOSE);
        }

        if (isTwitter) {
            socailMsgData.setTwitter(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setTwitter(EFunctionStatus.SUPPORT_CLOSE);
        }
        //flicker  不支持
        socailMsgData.setFlickr(EFunctionStatus.UNSUPPORT);
        if (isLinkin) {
            socailMsgData.setLinkin(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setLinkin(EFunctionStatus.SUPPORT_CLOSE);
        }

        if (isWhats) {
            socailMsgData.setWhats(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setWhats(EFunctionStatus.SUPPORT_CLOSE);
        }
        if (isLine) {
            socailMsgData.setLine(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setLine(EFunctionStatus.SUPPORT_CLOSE);
        }

        //instagram
        socailMsgData.setInstagram(EFunctionStatus.SUPPORT_OPEN);
        //snapchat
        socailMsgData.setSnapchat(EFunctionStatus.SUPPORT_OPEN);
        if (isSkype) {
            socailMsgData.setSkype(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setSkype(EFunctionStatus.SUPPORT_CLOSE);
        }
        //gmail
        socailMsgData.setGmail(EFunctionStatus.SUPPORT_OPEN);

        //other
        if(isOther){
            socailMsgData.setOther(EFunctionStatus.SUPPORT_OPEN);
        }else{
            socailMsgData.setOther(EFunctionStatus.SUPPORT_CLOSE);
        }

        Log.e(TAG, "-------------socailMsgData=" + socailMsgData.toString());

        MyApp.getInstance().getVpOperateManager().settingSocialMsg(iBleWriteResponse, new ISocialMsgDataListener() {
            @Override
            public void onSocialMsgSupportDataChange(FunctionSocailMsgData functionSocailMsgData) {
                Log.d(TAG, "-----设置-=" + functionSocailMsgData.toString());
            }
        }, socailMsgData);

    }


    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };

    public boolean isSupportOpen(EFunctionStatus data, FunctionSocailMsgData functionSocailMsgData) {
        List<EFunctionStatus> list = new ArrayList<>();
        list.add(functionSocailMsgData.getPhone());
        if (list.contains(data) && data == EFunctionStatus.SUPPORT_OPEN) {
            return true;
        } else {
            return false;
        }

    }

}
