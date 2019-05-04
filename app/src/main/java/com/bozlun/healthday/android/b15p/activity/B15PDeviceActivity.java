package com.bozlun.healthday.android.b15p.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.bozlun.healthday.android.Commont;
import com.bozlun.healthday.android.MyApp;
import com.bozlun.healthday.android.R;
import com.bozlun.healthday.android.b30.B30DufActivity;
import com.bozlun.healthday.android.b30.B30ResetActivity;
import com.bozlun.healthday.android.b30.B30ScreenStyleActivity;
import com.bozlun.healthday.android.bleutil.MyCommandManager;
import com.bozlun.healthday.android.siswatch.NewSearchActivity;
import com.bozlun.healthday.android.siswatch.WatchBaseActivity;
import com.bozlun.healthday.android.siswatch.utils.WatchUtils;
import com.bozlun.healthday.android.util.LocalizeTool;
import com.bozlun.healthday.android.w30s.carema.W30sCameraActivity;
import com.bozlun.healthday.android.w30s.wxsport.WXSportActivity;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.tjdL4.tjdmain.Dev;
import com.tjdL4.tjdmain.L4M;
import com.tjdL4.tjdmain.contr.L4Command;
import com.tjdL4.tjdmain.contr.TimeUnitSet;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.Setting;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * B31的设备页面
 * Created by Admin
 * Date 2018/12/18
 */
public class B15PDeviceActivity extends WatchBaseActivity
        implements Rationale<List<String>> {

    private static final String TAG = "B15PDeviceActivity";
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.b31DeviceSportGoalTv)
    TextView b31DeviceSportGoalTv;
    @BindView(R.id.b31DeviceSleepGoalTv)
    TextView b31DeviceSleepGoalTv;
    @BindView(R.id.b31DeviceUnitTv)
    TextView b31DeviceUnitTv;
    @BindView(R.id.b31DeviceStyleRel)
    RelativeLayout b31DeviceStyleRel;

    @BindView(R.id.radioGroup_time)
    RadioGroup radioGroupTime;
    @BindView(R.id.radio_24)
    RadioButton radio24;
    @BindView(R.id.radio_12)
    RadioButton radio12;


    private AlertDialog.Builder builder;

    //运动目标
    ArrayList<String> sportGoalList;
    //睡眠目标
    ArrayList<String> sleepGoalList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b15p_device_layout);
        ButterKnife.bind(this);

        initViews();
        initData();
        readDeviceCusSetting();
    }

    private void initData() {

        sportGoalList = new ArrayList<>();
        sleepGoalList = new ArrayList<>();
        for (int i = 1000; i <= 64000; i += 1000) {
            sportGoalList.add(i + "");
        }

        for (int i = 1; i < 48; i++) {
            sleepGoalList.add(WatchUtils.mul(Double.valueOf(i), 0.5) + "");
        }


        //显示运动目标和睡眠目标
        int b30SportGoal = (int) SharedPreferencesUtils.getParam(B15PDeviceActivity.this, "b30Goal", 0);
        b31DeviceSportGoalTv.setText(b30SportGoal + "");
        //睡眠
        String b30SleepGoal = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "b30SleepGoal", "");
        b31DeviceSleepGoalTv.setText(b30SleepGoal + "");
        //公英制
        boolean isMeter = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), "isSystem", false);//是否为公制
        b31DeviceUnitTv.setText(isMeter ? getResources().getString(R.string.setkm) : getResources().getString(R.string.setmi));
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.device));

        radioGroupTime.setOnCheckedChangeListener(new RadioCheckeListenter());
    }


    private void readDeviceCusSetting() {
        if (MyCommandManager.DEVICENAME == null)
            return;
        if (!this.isFinishing()) showLoadingDialog(getResources().getString(R.string.dlog));
        //时间制
        String sTime = L4M.GetUser_TimeMode();
        Log.e(TAG, "时间制 " + sTime);
        if (sTime.equals("1")) {
            radio12.setChecked(true);
        } else {
            radio24.setChecked(true);
        }

        //单位制
        String sUnit = L4M.GetUser_Unit();
        Log.e(TAG, "单位制 " + sUnit);
        if (sUnit.equals(CMKG)) {
            b31DeviceUnitTv.setText(getResources().getString(R.string.setkm));
        } else if (sUnit.equals(INLB)) {
            b31DeviceUnitTv.setText(getResources().getString(R.string.setmi));
        }
        closeLoadingDialog();

    }


    /**
     * 设置单位或者时间制度
     *
     * @param ss
     * @param hh
     * @param mm
     * @return
     */
    boolean TimeUnitSet(int ss, int hh, int mm) {
        Log.e(TAG, "开始设置单位或者时间制度");
        TimeUnitSet.TimeUnitSetData myTimeUnitSetData = new TimeUnitSet.TimeUnitSetData();
        myTimeUnitSetData.Sett1 = ss;
        myTimeUnitSetData.Sett2 = hh;
        myTimeUnitSetData.Sett3 = mm;
        String ret = L4Command.Brlt_LANGSet(myTimeUnitSetData);
        if (ret.equals("OK")) {
            return true;
        } else {
            return false;
        }
    }


    @OnClick({R.id.commentB30BackImg, R.id.b31DeviceMsgRel,
            R.id.b31DeviceAlarmRel,
            R.id.img233, R.id.b31DeviceSportRel,
            R.id.b31DeviceSleepRel, R.id.b31DeviceUnitRel,
            R.id.b31DeviceSwitchRel, R.id.b31DevicePtoRel,
            R.id.b31DeviceResetRel, R.id.b31DeviceStyleRel,
            R.id.b31DeviceDfuRel, R.id.b31DeviceClearDataRel,
            R.id.wxSportRel, R.id.b31DisConnBtn
            , R.id.set_findeDevice})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.b31DeviceMsgRel:  //消息提醒
                startActivity(B15PMessAlertActivity.class);
                break;
            case R.id.b31DeviceAlarmRel:    //闹钟设置
                startActivity(B15PDeviceAlarmActivity.class);
                break;
            case R.id.b31DeviceSportRel:    //运动目标
                setSportGoal();
                break;
            case R.id.b31DeviceSleepRel:    //睡眠目标
                setSleepGoal();
                break;
            case R.id.b31DeviceUnitRel:     //单位设置
                showLoadingDialog(getResources().getString(R.string.dlog));
                handler.sendEmptyMessageDelayed(0x22, 800);
//                startActivity(TimesAndUntiSettingActivity.class);
                break;
            case R.id.b31DeviceSwitchRel:   //开关设置
                startActivity(B15PSwitchActivity.class);
                break;
            case R.id.set_findeDevice:
                //查找设备
                //查找
                L4Command.FindDev();
                break;
            case R.id.b31DevicePtoRel:      //拍照
                AndPermission.with(this)
                        .runtime()
                        .permission(Permission.Group.CAMERA, Permission.Group.STORAGE)
                        .rationale(this)//添加拒绝权限回调
                        .onGranted(new Action<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                startActivity(W30sCameraActivity.class);
                            }
                        })
                        .onDenied(new Action<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                /**
                                 * 当用户没有允许该权限时，回调该方法
                                 */
                                Toast.makeText(MyApp.getContext(), getString(R.string.string_no_permission), Toast.LENGTH_SHORT).show();
                                /**
                                 * 判断用户是否点击了禁止后不再询问，AndPermission.hasAlwaysDeniedPermission(MainActivity.this, data)
                                 */
                                if (AndPermission.hasAlwaysDeniedPermission(MyApp.getContext(), data)) {
                                    //true，弹窗再次向用户索取权限
                                    showSettingDialog(MyApp.getContext(), data);
                                }
                            }
                        }).start();

                break;
            case R.id.b31DeviceResetRel:    //重置设备密码
                startActivity(B30ResetActivity.class);
                break;
            case R.id.b31DeviceStyleRel:    //界面风格
                startActivity(B30ScreenStyleActivity.class);
                break;
            case R.id.b31DeviceDfuRel:      //固件更新
                startActivity(B30DufActivity.class, new String[]{"type"}, new String[]{"B15P"});
                break;
            case R.id.b31DeviceClearDataRel:    //清除数据

                new MaterialDialog.Builder(this)
                        .title(getResources().getString(R.string.prompt))
                        .content(getResources().getString(R.string.string_is_clear_data))
                        .positiveText(getResources().getString(R.string.confirm))
                        .negativeText(getResources().getString(R.string.cancle))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                            }
                        }).show();
                break;
            case R.id.wxSportRel:       //微信运动
                startActivity(WXSportActivity.class, new String[]{"bleName"}, new String[]{"B31"});
                break;
            case R.id.b31DisConnBtn:    //断开连接
                disB30Conn();

                break;
        }
    }


    //以下为单位设置之后，身高体重的保存格式

//    String cmHeight = "170CM";
//    String cmWeight = "50KG";
//    String inHeight = "5'61\"ft-in";
//    String inWeight = "130LB";

    String INLB = "IN LB";
    String CMKG = "CM KG";
    int userHeight = 170;
    int userWeight = 60;
    double numIN = 0.0328084;//1 cm = 0.0328084 ft
    double numLB = 2.2046226;//1 kg = 2.2046226 ft

    //展示公英制
    private void showUnitDialog() {
        userHeight = Integer.valueOf((String) SharedPreferencesUtils.getParam(B15PDeviceActivity.this, Commont.USER_HEIGHT, "170"));
        userWeight = Integer.valueOf((String) SharedPreferencesUtils.getParam(B15PDeviceActivity.this, Commont.USER_WEIGHT, "60"));

        Log.e(TAG, "   用户身高" + userHeight + "   用户体重" + userWeight);

        String runTypeString[] = new String[]{getResources().getString(R.string.setkm),
                getResources().getString(R.string.setmi)};
        builder = new AlertDialog.Builder(B15PDeviceActivity.this);
        builder.setTitle(getResources().getString(R.string.select_running_mode))
                .setItems(runTypeString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (i == 0) {
                            boolean b = TimeUnitSet(0, 255, 0);//CM KG

                            //监听
                            L4Command.Brlt_LANGGet(new L4M.BTResultListenr() {
                                @Override
                                public void On_Result(String TypeInfo, String StrData, Object DataObj) {
                                    final String tTypeInfo = TypeInfo;
                                    final String TempStr = StrData;
                                    final Object TempObj = DataObj;


                                    Log.e(TAG, "inTempStr:" + TempStr);
                                    if (TypeInfo.equals(L4M.ERROR) && StrData.equals(L4M.TIMEOUT)) {
                                        return;
                                    }

                                    if (tTypeInfo.equals(L4M.SetLANG) && TempStr.equals(L4M.OK)) {
                                        L4Command.Brlt_LANGGet(null);
                                    } else if (tTypeInfo.equals(L4M.GetLANG) && TempStr.equals(L4M.OK)) {
                                        TimeUnitSet.TimeUnitSetData timeUnitSetData = (TimeUnitSet.TimeUnitSetData) DataObj;
                                    }
                                }
                            });

                            Log.e(TAG, "公英制 CMKG 设置结果 " + b);
                            if (b) {
                                L4M.SaveUser_Unit(CMKG);


                                DecimalFormat df = new DecimalFormat("#");
                                String formatH = df.format(userHeight);
                                String formatW = df.format(userWeight);
                                Log.e(TAG, "=公制==身高  " + formatH + "CM   ===体重  " + formatW + "KG");


                                L4M.SaveUser_Height(formatH + "CM");
                                L4M.SaveUser_Weight(formatW + "KG");

                                b31DeviceUnitTv.setText(getResources().getString(R.string.setkm));
                                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
                            } else {
                                b31DeviceUnitTv.setText(getResources().getString(R.string.setmi));
                            }

                        } else {
                            //单位制
                            boolean b = TimeUnitSet(0, 255, 1);//IN LB
                            Log.e(TAG, "公英制 INLB 设置结果 " + b);
                            if (b) {
                                L4M.SaveUser_Unit(INLB); //单位制保存到本地（计步页面用到）


                                DecimalFormat dfH = new DecimalFormat("#.0");
                                DecimalFormat dfW = new DecimalFormat("#");
                                String formatH = dfH.format(userHeight * numIN);
                                String formatW = dfW.format(userWeight * numLB);
                                StringBuilder strSleep = new StringBuilder();
                                for (int a = 0; a < formatH.length(); a++) {
                                    if (formatH.charAt(a) == '.') {
                                        strSleep.append("'");
                                    } else {
                                        strSleep.append(formatH.charAt(a));
                                    }
                                }
                                Log.e(TAG, "=英制==身高  " + strSleep.toString() + "\"ft-in" + "   ===体重  " + formatW + "LB");

                                L4M.SaveUser_Height(strSleep.toString() + "\"ft-in");
                                L4M.SaveUser_Weight(formatW + "LB");

                                b31DeviceUnitTv.setText(getResources().getString(R.string.setmi));
                                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, false);//是否为公制
                            } else {
                                b31DeviceUnitTv.setText(getResources().getString(R.string.setkm));
                            }

                        }
                    }
                }).setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }


    //设置睡眠目标
    private void setSleepGoal() {
        ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(B15PDeviceActivity.this,
                new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        b31DeviceSleepGoalTv.setText(profession);
                        SharedPreferencesUtils.setParam(B15PDeviceActivity.this, "b30SleepGoal", profession.trim());
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(18) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(sleepGoalList) //min year in loop
                .dateChose("8.0") // date chose when init popwindow
                .build();
        dailyumberofstepsPopWin.showPopWin(B15PDeviceActivity.this);
    }


    //设置运动目标
    private void setSportGoal() {
        ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(B15PDeviceActivity.this,
                new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        b31DeviceSportGoalTv.setText(profession);
                        SharedPreferencesUtils.setParam(B15PDeviceActivity.this, "b30Goal", Integer.valueOf(profession.trim()));

                        setDeviceSportGoal();


                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(18) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(sportGoalList) //min year in loop
                .dateChose("8000") // date chose when init popwindow
                .build();
        dailyumberofstepsPopWin.showPopWin(B15PDeviceActivity.this);
    }

    //设置运动目标
    private void setDeviceSportGoal() {
        MyApp.getInstance().getB30ConnStateService().setUserInfoToDevice();
    }


    @Override
    public void showRationale(Context context, List<String> data, final RequestExecutor executor) {
        List<String> permissionNames = Permission.transformText(context, data);
        String message = getResources().getString(R.string.string_get_permission) + "\n" + permissionNames;

        new android.app.AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(getResources().getString(R.string.prompt))
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executor.execute();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executor.cancel();
                    }
                })
                .show();
    }

    /**
     * Display setting dialog.
     */
    public void showSettingDialog(Context context, final List<String> permissions) {
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = getResources().getString(R.string.string_get_permission) + "\n" + permissionNames;

        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(getResources().getString(R.string.prompt))
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPermission();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    /**
     * Set permissions.
     */
    private void setPermission() {
        AndPermission.with(this)
                .runtime()
                .setting()
                .onComeback(new Setting.Action() {
                    @Override
                    public void onAction() {
                        //Toast.makeText(MyApp.getContext(),"用户从设置页面返回。", Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
    }


    /**
     * 断开连接
     */
    private void disB30Conn() {
        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.prompt))
                .content(getResources().getString(R.string.string_devices_is_disconnected))
                .positiveText(getResources().getString(R.string.confirm))
                .negativeText(getResources().getString(R.string.cancle))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (MyCommandManager.DEVICENAME != null) {
                            MyCommandManager.DEVICENAME = null;
                            MyCommandManager.ADDRESS = null;

                            /**
                             *手动断开  b15p
                             */
                            Dev.RemoteDev_CloseManual();
                            count = 0;
                            if (handler != null) handler.sendEmptyMessageAtTime(0x11, 1000);
//                            MyApp.getInstance().getVpOperateManager().disconnectWatch(new IBleWriteResponse() {
//                                @Override
//                                public void onResponse(int state) {
//                                    Log.e("断开","---------state="+state);
//                                    if (state == -1) {
//                                        SharedPreferencesUtils.saveObject(B15PDeviceActivity.this, Commont.BLENAME, null);
//                                        SharedPreferencesUtils.saveObject(B15PDeviceActivity.this, Commont.BLEMAC, null);
//                                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
//                                        MyApp.getInstance().setMacAddress(null);// 清空全局
//                                        startActivity(NewSearchActivity.class);
//                                        finish();
//
//                                    }
//                                }
//                            });
                        } else {
                            MyCommandManager.DEVICENAME = null;
                            MyCommandManager.ADDRESS = null;
                            SharedPreferencesUtils.saveObject(B15PDeviceActivity.this, Commont.BLENAME, null);
                            SharedPreferencesUtils.saveObject(B15PDeviceActivity.this, Commont.BLEMAC, null);
                            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
                            MyApp.getInstance().setMacAddress(null);// 清空全局
                            startActivity(NewSearchActivity.class);
                            finish();
                        }


                    }
                }).show();
    }


    /**
     * 断开链接时实时监测是否断开成功
     */
    int count = 0;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0x11:
                    //正在连接
                    if (L4M.Get_Connect_flag() == 1 || L4M.Get_Connect_flag() == 2) {
                        if (count == 5) {
                            count = 0;
                            out();
                            handler.removeMessages(0x11);
                        } else {
                            count++;
                            if (handler != null) handler.sendEmptyMessageAtTime(0x11, 600);
                        }
                    } else {
                        Log.d("====", "--B15P--未连接");
                        out();
                    }
                    break;
                case 0x22:
                    showUnitDialog();
                    closeLoadingDialog();
                    break;
                case 0x33:
                    boolean b = TimeUnitSet(0, 0, 255);//24小时制
                    Log.e(TAG, "24小时制设置结果 " + b);

                    if (b) {
                        L4M.SaveUser_TimeMode("0");
                        SharedPreferencesUtils.setParam(B15PDeviceActivity.this, Commont.IS24Hour, true);
                    }else {
                        radio24.setChecked(false);
                        radio12.setChecked(true);
                    }
                    closeLoadingDialog();
                    break;
                case 0x44:
                    //时间制
                    boolean b1 = TimeUnitSet(0, 1, 255);//12小时制
                    Log.e(TAG, "12小时制设置结果 " + b1);
                    if (b1) {
                        L4M.SaveUser_TimeMode("1");
                        SharedPreferencesUtils.setParam(B15PDeviceActivity.this, Commont.IS24Hour, false);
                    }else {
                        radio24.setChecked(false);
                        radio12.setChecked(true);
                    }
                    closeLoadingDialog();
                    break;
            }
            return false;
        }
    });


    void out() {
        new LocalizeTool(MyApp.getContext()).putUpdateDate(WatchUtils
                .obtainFormatDate(1));// 同时把数据更新时间清楚更新最后更新数据的时间
        MyCommandManager.DEVICENAME = null;
        MyCommandManager.ADDRESS = null;
        SharedPreferencesUtils.saveObject(B15PDeviceActivity.this, Commont.BLENAME, null);
        SharedPreferencesUtils.saveObject(B15PDeviceActivity.this, Commont.BLEMAC, null);
        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
        MyApp.getInstance().setMacAddress(null);// 清空全局
        startActivity(NewSearchActivity.class);
        finish();
    }

    private class RadioCheckeListenter implements RadioGroup.OnCheckedChangeListener {

        /**
         * 时间制 选择
         *
         * @param radioGroup
         * @param checkedId
         */
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            if (radioGroup.isPressed()) return;
            switch (radioGroup.getId()) {
                case R.id.radioGroup_time:
                    if (!B15PDeviceActivity.this.isFinishing()) showLoadingDialog("setting...");

                    if (checkedId == R.id.radio_24) {

                        handler.sendEmptyMessageDelayed(0x33,800);
//                        boolean b = TimeUnitSet(0, 0, 255);//24小时制
//                        Log.e(TAG, "24小时制设置结果 " + b);
//
//                        if (b) {
//                            L4M.SaveUser_TimeMode("0");
//                            SharedPreferencesUtils.setParam(B15PDeviceActivity.this, Commont.IS24Hour, true);
//                        }else {
//                            radio24.setChecked(false);
//                            radio12.setChecked(true);
//                        }
//                        closeLoadingDialog();
                    } else if (checkedId == R.id.radio_12) {

                        handler.sendEmptyMessageDelayed(0x44,800);
//                        //时间制
//                        boolean b = TimeUnitSet(0, 1, 255);//12小时制
//                        Log.e(TAG, "12小时制设置结果 " + b);
//                        if (b) {
//                            L4M.SaveUser_TimeMode("1");
//                            SharedPreferencesUtils.setParam(B15PDeviceActivity.this, Commont.IS24Hour, false);
//                        }else {
//                            radio24.setChecked(false);
//                            radio12.setChecked(true);
//                        }
//                        closeLoadingDialog();
                    }
                    break;
            }
        }

    }
}
