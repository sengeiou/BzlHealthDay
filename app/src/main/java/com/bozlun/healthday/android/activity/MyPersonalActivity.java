package com.bozlun.healthday.android.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aigestudio.wheelpicker.widgets.DatePick;
import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.bozlun.healthday.android.view.MineQrcodeView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bozlun.healthday.android.Commont;
import com.bozlun.healthday.android.MyApp;
import com.bozlun.healthday.android.R;
import com.bozlun.healthday.android.b30.view.B30SkinColorView;
import com.bozlun.healthday.android.base.BaseActivity;
import com.bozlun.healthday.android.bean.MessageEvent;
import com.bozlun.healthday.android.bleutil.MyCommandManager;
import com.bozlun.healthday.android.imagepicker.PickerBuilder;
import com.bozlun.healthday.android.net.OkHttpObservable;
import com.bozlun.healthday.android.rxandroid.CommonSubscriber;
import com.bozlun.healthday.android.rxandroid.DialogSubscriber;
import com.bozlun.healthday.android.rxandroid.SubscriberOnNextListener;
import com.bozlun.healthday.android.siswatch.utils.Base64BitmapUtil;
import com.bozlun.healthday.android.siswatch.utils.WatchUtils;
import com.bozlun.healthday.android.util.ImageTool;
import com.bozlun.healthday.android.util.LocalizeTool;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.bozlun.healthday.android.util.ToastUtil;
import com.bozlun.healthday.android.util.URLs;
import com.bozlun.healthday.android.w30s.utils.httputils.RequestPressent;
import com.bozlun.healthday.android.w30s.utils.httputils.RequestView;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.google.gson.Gson;
import com.tjdL4.tjdmain.L4M;
import com.tjdL4.tjdmain.contr.BrltUserParaSet;
import com.tjdL4.tjdmain.contr.L4Command;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.listener.data.IPersonInfoDataListener;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.enums.EOprateStauts;
import com.veepoo.protocol.model.settings.CustomSetting;
import com.veepoo.protocol.model.settings.CustomSettingData;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import org.apache.commons.lang.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.bozlun.healthday.android.util.Common.userInfo;

/**
 * Created by thinkpad on 2017/3/8.
 * 个人信息
 */

public class MyPersonalActivity extends BaseActivity implements RequestView {

    private static final String TAG = "MyPersonalActivity";

    private static final int GET_CAMERA_REQUEST_CODE = 1001;

    private final static String SKIN_COLOR_KEY = "skin_position";


    //    @BindView(R.id.tv_title)
//    TextView tvTitle;
    @BindView(R.id.mine_logo_iv_personal)
    CircleImageView mineLogoIv;
    @BindView(R.id.nickname_tv)
    TextView nicknameTv;
    @BindView(R.id.sex_tv)
    TextView sexTv;
    @BindView(R.id.height_tv)
    TextView heightTv;
    @BindView(R.id.weight_tv)
    TextView weightTv;
    @BindView(R.id.birthday_tv)
    TextView birthdayTv;
    @BindView(R.id.bottomsheet)
    BottomSheetLayout bottomSheetLayout;
    @BindView(R.id.personal_avatar_relayout)
    RelativeLayout personalAvatarRelayout;
    @BindView(R.id.nickname_relayout_personal)
    RelativeLayout nicknameRelayoutPersonal;
    @BindView(R.id.sex_relayout)
    RelativeLayout sexRelayout;
    @BindView(R.id.height_relayout)
    RelativeLayout heightRelayout;
    @BindView(R.id.weight_relayout)
    RelativeLayout weightRelayout;
    @BindView(R.id.birthday_relayout)
    RelativeLayout birthdayRelayout;

    //单位设置的布局
    @BindView(R.id.personal_UnitLin)
    RelativeLayout personalUnitLin;

    //显示的肤色
    @BindView(R.id.defaultSkinColorImg)
    ImageView defaultSkinColorImg;
    @BindView(R.id.skinColorRel)
    RelativeLayout skinColorRel;
    @BindView(R.id.personalH8UnitTv)
    TextView personalH8UnitTv;
    private String nickName, sex, flag;
    private DialogSubscriber dialogSubscriber;
    private boolean isSubmit;

    private CommonSubscriber commonSubscriber;
    private SubscriberOnNextListener subscriberOnNextListener;
    private ArrayList<String> heightList;
    private ArrayList<String> weightList;

    private int userSex = 1;
    private int userHeight = 170;
    private int userWeitht = 60;


    boolean w30sunit = true;
    private String bleName;

    /**
     * 本地化帮助类
     */
    private LocalizeTool mLocalTool;
    /**
     * 请求回来的参数,或者要提交的
     */
    private UserInfo mUserInfo = null;
    /**
     * Json帮助类
     */
    private Gson gson = new Gson();
    private RequestQueue requestQueue;
    private RequestPressent requestPressent;


    private Uri mCutUri;


    //肤色选择的view
    private B30SkinColorView b30SkinColorView;
    //H8公英制选择dialog
    private AlertDialog.Builder alertDialog;
    //我的二维码view
    private MineQrcodeView mineQrcodeView;



    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        requestPressent.detach();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
    }


    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences share = getSharedPreferences("nickName", 0);
        String name = share.getString("name", "");
        if (!WatchUtils.isEmpty(name)) {
            nicknameTv.setText(name);
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_personal_info;
    }

    @Override
    protected void initViews() {
        w30sunit = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);
//        tvTitle.setText(R.string.personal_info);
        findViewById(R.id.personal_info_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.personal_info_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePersonData();
            }
        });
        heightList = new ArrayList<>();
        weightList = new ArrayList<>();
        bleName = (String) SharedPreferencesUtils.readObject(MyPersonalActivity.this,  Commont.BLENAME);
        if (!WatchUtils.isEmpty(bleName) && WatchUtils.isVPBleDevice(bleName)) {
            skinColorRel.setVisibility(View.VISIBLE);
        } else {
            skinColorRel.setVisibility(View.GONE);
        }


        if (!WatchUtils.isEmpty(bleName) && bleName.equals("bozlun")) {    //H8手表
            personalUnitLin.setVisibility(View.VISIBLE);
        } else {
            personalUnitLin.setVisibility(View.GONE);
        }


        // mLocalTool = new LocalizeTool(this);
        // w30sunit = mLocalTool.getMetricSystem();
        if (w30sunit) {
            personalH8UnitTv.setText(getResources().getString(R.string.setkm));

        } else {
            personalH8UnitTv.setText(getResources().getString(R.string.setmi));

        }

        EventBus.getDefault().register(this);


        //设置选择列表
        setListData();

        //肤色显示
        showSkinColorData();


        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("resultCode");
                    System.out.print("resultCode" + resultCode);
                    Log.e("MyPerson", "----resultCode--" + resultCode + "-isSubmit----" + isSubmit);
                    if ("001".equals(resultCode)) {
                        ToastUtil.showShort(MyPersonalActivity.this, getString(R.string.submit_success));
                        getUserInfoData();
                    } else {
                        ToastUtil.showShort(MyPersonalActivity.this, getString(R.string.submit_fail));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

    }

    //显示肤色
    private void showSkinColorData() {
        int colorPosition = (int) SharedPreferencesUtils.getParam(MyPersonalActivity.this, SKIN_COLOR_KEY, 2);
        defaultSkinColorImg.setImageResource(B30SkinColorView.imgStr[colorPosition]);


    }

    private void setListData() {
        heightList.clear();
        weightList.clear();
        if (w30sunit) {     //公制
            for (int i = 120; i < 231; i++) {
                heightList.add(i + " cm");
            }
            for (int i = 20; i < 200; i++) {
                weightList.add(i + " kg");
            }
        } else {  // 英制
            for (int i = 44; i < 100; i++) {
                heightList.add(i + " in");
            }
            for (int i = 20; i < 220; i++) {
                weightList.add(i + " lb");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);


        requestQueue = NoHttp.newRequestQueue(1);
        requestPressent = new RequestPressent();
        requestPressent.attach(this);


        AndPermission.with(MyPersonalActivity.this)
                .runtime()
                .permission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                    }
                }).start();


        //获取用户数据
        getUserInfoData();
    }

    // 获取用户数据
    private void getUserInfoData() {
        String url = URLs.HTTPs + URLs.getUserInfo;
        if (requestPressent != null) {
            HashMap<String, String> map = new HashMap<>();
            map.put("userId", (String) SharedPreferencesUtils.readObject(MyPersonalActivity.this, "userId"));
            String mapJson = gson.toJson(map);
            requestPressent.getRequestJSONObject(1, url, this, mapJson, 11);
        }
    }

    /**
     * 修改用户数据
     */
    private void savePersonData() {
        if (mUserInfo == null) return;
        String mapjson = gson.toJson(mUserInfo);
        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, this);
        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.yonghuziliao, mapjson);
    }

    /**
     * 刷新所以得数据（名字和头像）
     */
    public void shuaxin() {
        isSubmit = false;
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", (String) SharedPreferencesUtils.readObject(MyPersonalActivity.this, "userId"));
        String mapjson = gson.toJson(map);
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, this);
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.getUserInfo, mapjson);
    }

    @OnClick({R.id.personal_avatar_relayout,
            R.id.nickname_relayout_personal,
            R.id.sex_relayout, R.id.height_relayout,
            R.id.weight_relayout, R.id.birthday_relayout,
            R.id.skinColorRel, R.id.personal_UnitLin,
            R.id.persionQrcodeRel})
    public void onClick(View view) {
        String userId = (String) SharedPreferencesUtils.readObject(MyPersonalActivity.this, "userId");
        SharedPreferences share = getSharedPreferences("Login_id", 0);
        int isoff = share.getInt("id", 0);
        if (!WatchUtils.isEmpty(userId)) {
            if (userId.equals("9278cc399ab147d0ad3ef164ca156bf0")) {  //判断是否是游客身份，如果是游客身份无权限修改信息
                ToastUtil.showToast(MyPersonalActivity.this, MyPersonalActivity.this.getResources().getString(R.string.noright));
            } else {
                switch (view.getId()) {
                    case R.id.personal_avatar_relayout://  修改头像
                        if(mUserInfo == null)
                            return;
                        if (AndPermission.hasAlwaysDeniedPermission(MyPersonalActivity.this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            chooseImgForUserHead(); //选择图片来源
                        } else {
                            AndPermission.with(MyPersonalActivity.this)
                                    .runtime()
                                    .permission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    .onGranted(new Action<List<String>>() {
                                        @Override
                                        public void onAction(List<String> data) {

                                        }
                                    })
                                    .onDenied(new Action<List<String>>() {
                                        @Override
                                        public void onAction(List<String> data) {

                                        }
                                    }).start();

                        }
                        break;
                    case R.id.nickname_relayout_personal:
                        if(mUserInfo == null)
                            return;
                        startActivityForResult(new Intent(MyPersonalActivity.this, ModifyNickNameActivity.class), 1000);
                        break;
                    case R.id.sex_relayout:
                        if(mUserInfo == null)
                            return;
                        showSexDialog();
                        break;
                    case R.id.height_relayout:      // 身高
                        if(mUserInfo == null)
                            return;
                        if (w30sunit) { //公制
                            ProfessionPick professionPopWin = new ProfessionPick.Builder(MyPersonalActivity.this, new ProfessionPick.OnProCityPickedListener() {
                                @Override
                                public void onProCityPickCompleted(String profession) {
                                    heightTv.setText(profession);
                                    mUserInfo.height = profession.trim().substring(0, 3);// 记录一下提交要用
                                    flag = "height";
//                                    heightTv.setText(profession);
//                                    height = profession.substring(0, 3);
                                    String uHeight = profession.substring(0, 3).trim();
                                    modifyPersonData(uHeight);
                                }
                            }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                                    .textCancel(getResources().getString(R.string.cancle))
                                    .btnTextSize(16) // button text size
                                    .viewTextSize(25) // pick view text size
                                    .colorCancel(Color.parseColor("#999999")) //color of cancel button
                                    .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                                    .setProvinceList(heightList) //min year in loop
                                    .dateChose("120cm") // date chose when init popwindow
                                    .build();
                            professionPopWin.showPopWin(MyPersonalActivity.this);
                        } else {      //英制
                            ProfessionPick professionPopWin = new ProfessionPick.Builder(MyPersonalActivity.this, new ProfessionPick.OnProCityPickedListener() {
                                @Override
                                public void onProCityPickCompleted(String profession) {
                                    heightTv.setText(profession);
                                    String tmpHeight = StringUtils.substringBefore(profession, "in").trim();
                                    flag = "height";
                                    //height = profession.substring(0, 3);
                                    //1,英寸转cm
                                    double tmpCal = WatchUtils.mul(Double.valueOf(tmpHeight), 2.5);
                                    //截取小数点前的数据
                                    int beforeTmpCal = Integer.valueOf(StringUtils.substringBefore(String.valueOf(tmpCal), ".").trim());
                                    //截取小数点后的数据
                                    String afterTmpCal = StringUtils.substringAfter(String.valueOf(tmpCal), ".").trim();
                                    //判断小数点后一位是否》=5
                                    int lastAterTmpCal = Integer.valueOf(afterTmpCal.length() >= 1 ? afterTmpCal.substring(0, 1) : "0");
                                    if (lastAterTmpCal >= 5) {
                                        mUserInfo.height = (beforeTmpCal + 1) + "";
                                    } else {
                                        mUserInfo.height = beforeTmpCal + "";
                                    }
                                    modifyPersonData(mUserInfo.height);
                                }
                            }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                                    .textCancel(getResources().getString(R.string.cancle))
                                    .btnTextSize(16) // button text size
                                    .viewTextSize(25) // pick view text size
                                    .colorCancel(Color.parseColor("#999999")) //color of cancel button
                                    .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                                    .setProvinceList(heightList) //min year in loop
                                    .dateChose("67 in") // date chose when init popwindow
                                    .build();
                            professionPopWin.showPopWin(MyPersonalActivity.this);
                        }

                        break;
                    case R.id.weight_relayout:  //体重
                        if(mUserInfo == null)
                            return;
                        if (w30sunit) { //公制
                            ProfessionPick weightPopWin = new ProfessionPick.Builder(MyPersonalActivity.this, new ProfessionPick.OnProCityPickedListener() {
                                @Override
                                public void onProCityPickCompleted(String profession) {
                                    weightTv.setText(profession);
                                    mUserInfo.weight = profession.substring(0, 3);// 记录一下提交要用
                                    flag = "weight";
                                    modifyPersonData(profession.substring(0, 3).trim());
                                }
                            }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                                    .textCancel(getResources().getString(R.string.cancle))
                                    .btnTextSize(16) // button text size
                                    .viewTextSize(25) // pick view text size
                                    .colorCancel(Color.parseColor("#999999")) //color of cancel button
                                    .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                                    .setProvinceList(weightList) //min year in loop
                                    .dateChose("60 kg") // date chose when init popwindow
                                    .build();
                            weightPopWin.showPopWin(MyPersonalActivity.this);
                        } else {
                            //英制体重
                            ProfessionPick weightPopWin = new ProfessionPick.Builder(MyPersonalActivity.this, new ProfessionPick.OnProCityPickedListener() {
                                @Override
                                public void onProCityPickCompleted(String profession) {
                                    weightTv.setText(profession);
                                    flag = "weight";
                                    String tmpWeid = StringUtils.substringBefore(profession, "lb").trim();
                                    double calWeid = WatchUtils.mul(Double.valueOf(tmpWeid), 0.454);
                                    //截取小数点前的数据
                                    String beforeCalWeid = StringUtils.substringBefore(String.valueOf(calWeid), ".");
                                    //截取后小数点后的数据
                                    String afterCalWeid = StringUtils.substringAfter(String.valueOf(calWeid), ".");
                                    int lastNum = Integer.valueOf(afterCalWeid.length() >= 1 ? afterCalWeid.substring(0, 1) : "0");
                                    //判断小数点后一位是否大于5
                                    if (lastNum >= 5) {
                                        mUserInfo.weight = String.valueOf(Integer.valueOf(beforeCalWeid.trim()) + 1);
                                    } else {
                                        mUserInfo.weight = beforeCalWeid.trim();
                                    }
                                    // weight = profession.substring(0, 3);
                                    modifyPersonData(mUserInfo.weight);
                                }
                            }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                                    .textCancel(getResources().getString(R.string.cancle))
                                    .btnTextSize(16) // button text size
                                    .viewTextSize(25) // pick view text size
                                    .colorCancel(Color.parseColor("#999999")) //color of cancel button
                                    .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                                    .setProvinceList(weightList) //min year in loop
                                    .dateChose("132 lb") // date chose when init popwindow
                                    .build();
                            weightPopWin.showPopWin(MyPersonalActivity.this);
                        }

                        break;
                    case R.id.birthday_relayout:    //生日
                        if(mUserInfo == null)
                            return;
                        DatePick pickerPopWin = new DatePick.Builder(MyPersonalActivity.this, new DatePick.OnDatePickedListener() {
                            @Override
                            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                                birthdayTv.setText(dateDesc);
                                mUserInfo.birthday = dateDesc;
                                flag = "birthday";
                                modifyPersonData(dateDesc);//
                            }
                        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                                .btnTextSize(16) // button text size
                                .viewTextSize(25) // pick view text size
                                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                                .minYear(1800) //min year in loop
                                .maxYear(2050) // max year in loop
                                .dateChose("2000-01-01") // date chose when init popwindow
                                .build();
                        pickerPopWin.showPopWin(MyPersonalActivity.this);
                        break;
                    case R.id.skinColorRel: //肤色选择
                        b30SkinColorView = new B30SkinColorView(this);
                        b30SkinColorView.show();
                        b30SkinColorView.setB30SkinColorListener(new B30SkinColorView.B30SkinColorListener() {
                            @Override
                            public void doSureSkinClick(int selectImgId, int position) {
                                b30SkinColorView.dismiss();
                                defaultSkinColorImg.setImageResource(selectImgId);

                                //保存选择的肤色下标
                                SharedPreferencesUtils.setParam(MyPersonalActivity.this, SKIN_COLOR_KEY, position);
                                 if(MyCommandManager.DEVICENAME == null)
                                     return;
                                if (position == 4 || position == 5) {
                                    setSwtchStutas(false);
                                } else {
                                    setSwtchStutas(true);
                                }

                            }

                            @Override
                            public void doCancleSkinClick() {
                                b30SkinColorView.dismiss();
                            }
                        });
                        break;
                    case R.id.personal_UnitLin:     //公英制单位
                        if(mUserInfo == null)
                            return;
                        showChooseH8Unit();
                        break;
                    case R.id.persionQrcodeRel:     //我的二维码显示
                        if(mineQrcodeView == null)
                            mineQrcodeView = new MineQrcodeView(MyPersonalActivity.this);
                        mineQrcodeView.show();
                        break;
                }

            }
        }

    }

    /**
     * 黑色皮肤关闭检测
     */
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
    private void setSwtchStutas(boolean isOpen) {
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
        if (isOpen) {
            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISWearcheck, true);//佩戴
            isWear = EFunctionStatus.SUPPORT_OPEN;
        } else {
            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISWearcheck, false);//佩戴
            isWear = EFunctionStatus.SUPPORT_CLOSE;
        }
        //来电
        if (CallPhone) {
            isCallPhone = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isCallPhone = EFunctionStatus.SUPPORT_CLOSE;
        }
        //断开
        if (isDisconn) {
            isDisAlert = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isDisAlert = EFunctionStatus.SUPPORT_CLOSE;
        }
        //sos
        if (isSos) {
            isHelper = EFunctionStatus.SUPPORT_CLOSE;
        } else {
            isHelper = EFunctionStatus.SUPPORT_CLOSE;
        }





        showLoadingDialog(getResources().getString(R.string.dlog));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MyApp.getInstance().getVpOperateManager().changeCustomSetting(new IBleWriteResponse() {
                    @Override
                    public void onResponse(int i) {

                    }
                }, new ICustomSettingDataListener() {
                    @Override
                    public void OnSettingDataChange(CustomSettingData customSettingData) {
                        closeLoadingDialog();
//                        Log.d("TAG", "----修改状态" + customSettingData.toString());
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
                        if (isSystem) {
                            customSettingData.setMetricSystem(EFunctionStatus.SUPPORT_OPEN);
                            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
                        } else {
                            customSettingData.setMetricSystem(EFunctionStatus.SUPPORT_CLOSE);
                            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, false);//是否为公制
                        }
                        if (isSos) {
                            customSettingData.setSOS(EFunctionStatus.SUPPORT_CLOSE);
                        } else {
                            customSettingData.setSOS(EFunctionStatus.SUPPORT_CLOSE);
                        }
                        if (isDisconn) {
                            customSettingData.setDisconnectRemind(EFunctionStatus.SUPPORT_OPEN);
                        } else {
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

    //H8公英制设置
    private void showChooseH8Unit() {
        final String[] unitStr = new String[]{getResources().getString(R.string.setkm), getResources().getString(R.string.setmi)};
        alertDialog = new AlertDialog.Builder(MyPersonalActivity.this)
                .setTitle(getResources().getString(R.string.prompt))
                .setItems(unitStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:     //公制
                                personalH8UnitTv.setText(unitStr[0]);
                                w30sunit = true;
                                break;
                            case 1:     //英制
                                personalH8UnitTv.setText(unitStr[1]);
                                w30sunit = false;
                                break;
                        }
                        setListData();
                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, w30sunit);//是否为公制

                    }
                }).setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.create().show();
    }

    //选择图片
    private void chooseImgForUserHead() {
        MenuSheetView menuSheetView =
                new MenuSheetView(MyPersonalActivity.this, MenuSheetView.MenuType.LIST, R.string.select_photo, new MenuSheetView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (bottomSheetLayout.isSheetShowing()) {
                            bottomSheetLayout.dismissSheet();
                        }
                        switch (item.getItemId()) {
                            case R.id.take_camera:
                                cameraPic();
                                break;
                            case R.id.take_Album:   //相册
                                getImage(PickerBuilder.SELECT_FROM_GALLERY);
//                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                                intent.setType("image/*");
//                                startActivityForResult(intent,120);
                                break;
                            case R.id.cancle:
                                break;
                        }
                        return true;
                    }
                });
        menuSheetView.inflateMenu(R.menu.menu_takepictures);
        bottomSheetLayout.showWithSheetView(menuSheetView);
    }

    //选择性别
    private void showSexDialog() {
        new MaterialDialog.Builder(MyPersonalActivity.this)
                .title(R.string.select_sex)
                .items(R.array.select_sex)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        //0表示男,1表示女
                        if (which == 0) {
                            mUserInfo.sex = "M";// 记录一下,提交的时候用
                            sexTv.setText(getResources().getString(R.string.sex_nan));
                            //保存性别
                            SharedPreferencesUtils.setParam(MyPersonalActivity.this,Commont.USER_SEX,"M");
                        } else {
                            mUserInfo.sex = "F";
                            sexTv.setText(getResources().getString(R.string.sex_nv));
                            //保存性别
                            SharedPreferencesUtils.setParam(MyPersonalActivity.this,Commont.USER_SEX,"F");
                        }
                        flag = "sex";
                        modifyPersonData(mUserInfo.sex);
                        return true;
                    }
                })
                .positiveText(R.string.select)
                .show();
    }

    //相册选择
    private void getImage(int type) {
        new PickerBuilder(MyPersonalActivity.this, type)
                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                    @Override
                    public void onImageReceived(Uri imageUri) {
                        //设置头像
                        RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true);
                        Glide.with(MyPersonalActivity.this).
                                load(imageUri).apply(mRequestOptions).into(mineLogoIv);

                        uploadPic(ImageTool.getRealFilePath(MyPersonalActivity.this, imageUri), 1);
//                        if (mUserInfo!=null)mUserInfo.image = ImageTool.getRealFilePath(MyPersonalActivity.this, imageUri)；
                    }
                })
                .setImageName("headImg")
                .setImageFolderName("NewBluetoothStrap")
                .setCropScreenColor(Color.CYAN)
                .setOnPermissionRefusedListener(new PickerBuilder.onPermissionRefusedListener() {
                    @Override
                    public void onPermissionRefused() {
                    }
                })
                .start();
    }

    /**
     * 上传头像图片
     *
     * @param flag 0_Base64 1_路径
     */
    private void uploadPic(String filePath, int flag) {
        Log.e(TAG, "----上传图片=" + filePath);
        isSubmit = false;
        HashMap<String, Object> map = new HashMap<>();
        if (mUserInfo != null && !TextUtils.isEmpty(mUserInfo.userId))
            map.put("userId", mUserInfo.userId);
        if (flag == 0) {
            map.put("image", filePath);
        } else {
            map.put("image", ImageTool.GetImageStr(filePath));
        }
        String mapjson = gson.toJson(map);
        Log.e(TAG, "----上传图片mapjson=" + mapjson);
        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, MyPersonalActivity.this);
        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.ziliaotouxiang, mapjson);
    }

    //完善用户资料
    private void modifyPersonData(String val) {
        isSubmit = true;
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", SharedPreferencesUtils.readObject(MyApp.getContext(), "userId"));
        map.put(flag, val);
        String mapjson = gson.toJson(map);
        Log.e(TAG, "-----mapJson=" + mapjson);
        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, MyPersonalActivity.this);
        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.yonghuziliao, mapjson);

        //B25或者B15P设置用户信息
        if(bleName == null)
            return;
        if(L4M.Get_Connect_flag() == 1){
            Set<String> set = new HashSet<>(Arrays.asList(WatchUtils.TJ_FilterNamas));
            //腾进达方案，用户信息要同步
            if (set.contains(bleName)) {
                //身高
                int resultHeight = 0;
                //体重
                int resultWeight = 0;
                String b15pUHeight = heightTv.getText().toString();
                String b15pUWeight = weightTv.getText().toString();
                if(w30sunit){   //公制
                    resultHeight = Integer.valueOf(StringUtils.substringBefore(b15pUHeight,"cm").trim());
                    resultWeight = Integer.valueOf(StringUtils.substringBefore(b15pUWeight,"kg").trim());
                }else{  //英制
                    resultHeight = Integer.valueOf(StringUtils.substringBefore(b15pUHeight,"in").trim());
                    resultWeight = Integer.valueOf(StringUtils.substringBefore(b15pUWeight,"lb").trim());
                }

                //生日
                String b15pUAge = birthdayTv.getText().toString().trim();
                int resultAge = WatchUtils.getAgeFromBirthTime(b15pUAge);
                String b15pSex = mUserInfo.sex;
                //设置固件的个人信息
                setUserData((b15pSex.equals("M") ? 0 : 1), resultHeight, resultWeight, resultAge);
            }
        }

    }




    void setUserData(int mGender, int mHeight, int mWeight, int mAge) {
        //设置数据对象
        BrltUserParaSet.UserParaSetData myUserParaSetData = new BrltUserParaSet.UserParaSetData();
        myUserParaSetData.mGender = mGender;//int mGender 性别  0男 1女
        myUserParaSetData.mHeight = mHeight;//int mHeight 身高
        myUserParaSetData.mWeight = mWeight;//int mWeight 体重
        myUserParaSetData.mAge = mAge;  //int mAge    年龄

        //设置
        String ret = L4Command.Brlt_UserParaSet(myUserParaSetData);
        Log.e(TAG, "==========  用户信息设置= " + ret);
    }


    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
        if (what == 1) {
            initUserInfo(object.toString());
        }
    }

    private void initUserInfo(String result) {
        UserInfoResult resultVo = gson.fromJson(result, UserInfoResult.class);
        if (resultVo == null || !resultVo.resultCode.equals("001")) return;
        mUserInfo = resultVo.userInfo;
        if (mUserInfo == null) return;
        SharedPreferencesUtils.saveObject(MyPersonalActivity.this,Commont.USER_INFO_DATA,gson.toJson(mUserInfo));
        Glide.with(this).load(mUserInfo.image).into(mineLogoIv);//头像
        nicknameTv.setText(mUserInfo.nickName);// 昵称
        int sexRes = "M".equals(mUserInfo.sex) ? R.string.sex_nan : R.string.sex_nv;
        sexTv.setText(sexRes);// 性别

        String heightStr = mUserInfo.height;// 身高
        if (heightStr.contains("cm")) {
            heightStr = heightStr.trim().substring(0, heightStr.length() - 2);
        }
        heightStr = heightStr.trim();
        mUserInfo.height = heightStr;// 去掉cm后存起来

        String weightStr = mUserInfo.weight;// 体重
        if (weightStr.contains("kg")) {
            weightStr = weightStr.trim().substring(0, weightStr.length() - 2);
        }
        weightStr = weightStr.trim();
        mUserInfo.weight = weightStr;// 去掉kg后存起来

        if (!w30sunit) {// 英制要处理一下
            heightTv.setText(obtainHeight(heightStr));
            weightTv.setText(obtainWeight(weightStr));
        } else {
            heightTv.setText(heightStr + "cm");
            weightTv.setText(weightStr + "kg");
        }
        birthdayTv.setText(mUserInfo.birthday);


        syncUserInfoToB30();
    }

    //同步手环数据 B30,B36
    private void syncUserInfoToB30() {
        if(MyCommandManager.DEVICENAME == null)
            return;
        if(WatchUtils.isVPBleDevice(MyCommandManager.DEVICENAME)){
            //目标步数
            int sportGoal = (int) SharedPreferencesUtils.getParam(MyApp.getContext(), "b30Goal", 10000);
            MyApp.getInstance().getVpOperateManager().syncPersonInfo(new IBleWriteResponse() {
                @Override
                public void onResponse(int i) {

                }
            }, new IPersonInfoDataListener() {
                @Override
                public void OnPersoninfoDataChange(EOprateStauts eOprateStauts) {
                }
            }, WatchUtils.getUserPerson(sportGoal));
        }
    }

    /**
     * 计算英制身高
     */
    private String obtainHeight(String mHeight) {
        int tmpuserHeight = Integer.valueOf(mHeight);
        double showTmpHe = WatchUtils.mul(Double.valueOf(tmpuserHeight), 0.4);
        //截取的小数点前部分
        int tmpBeforeHe = Integer.valueOf(StringUtils.substringBefore(String.valueOf(showTmpHe), "."));
        String afterTmpH = StringUtils.substringAfter(String.valueOf(showTmpHe), ".").trim();
        //截取的小数点后部分
        int tmpAftereHe = Integer.valueOf(afterTmpH.length() >= 1 ? afterTmpH.substring(0, 1) : "0");
        //判断截取小数点后一位是否大于5
        if (tmpAftereHe >= 5) {
            return StringUtils.substringBefore(String.valueOf(tmpBeforeHe + 1), ".") + "in";
        } else {
            return StringUtils.substringBefore(String.valueOf(showTmpHe), ".") + "in";
        }
    }

    /**
     * 计算英制体重
     */
    private String obtainWeight(String mWeight) {
        int tmpWid = Integer.valueOf(mWeight);
        double showWid = WatchUtils.mul(Double.valueOf(tmpWid), 2.2);
        //截取小数点前的数据
        String beforeShowWid = StringUtils.substringBefore(String.valueOf(showWid), ".");

        //截取小数点后的数据
        String afterShowWid = StringUtils.substringAfter(String.valueOf(showWid), ".");
        //小数点后一位
        int lastWidNum = Integer.valueOf(afterShowWid.length() >= 1 ? afterShowWid.substring(0, 1) : "0");
        //判断小数点后一位是否》=5
        if (lastWidNum >= 5) {
            return (Integer.valueOf(beforeShowWid) + 1) + "lb";
        } else {
            return beforeShowWid + "lb";
        }
    }


    @Override
    public void failedData(int what, Throwable e) {
        Log.e(TAG, "----fail=" + e.getMessage());
    }

    @Override
    public void closeLoadDialog(int what) {

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG, "-----result-=" + requestCode + "--resu=" + resultCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1000:// 改昵称回来
                    String nickName = data.getStringExtra("name");
                    if(!WatchUtils.isEmpty(nickName)){
                        nicknameTv.setText(nickName);
                        mUserInfo.nickName = nickName;// 记录一下,到时提交用
                        flag = "nickName";
                        modifyPersonData(nickName);
                    }

                    break;
                case 120: //从相册图片后返回的uri
                    //启动裁剪
                    if (data != null) {
                        handlerImageOnKitKat(data);
                    }

                    //startActivityForResult(CutForPhoto(data.getData()),111);
                    break;
                case 1001: //相机返回的 uri
                    //启动裁剪
                    String path = getExternalCacheDir().getPath();
                    Log.e(TAG, "----裁剪path=" + path);
                    String name = "output.png";
                    startActivityForResult(CutForCamera(path, name), 111);
                    break;
                case 111:
                    try {
                        //获取裁剪后的图片，并显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(
                                this.getContentResolver().openInputStream(mCutUri));
                        //showImg.setImageBitmap(bitmap);
                        mineLogoIv.setImageBitmap(bitmap);

//                        RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
//                                .skipMemoryCache(true);
//                        Glide.with(MyPersonalActivity.this).
//                                load(mCutUri).apply(mRequestOptions).into(mineLogoIv);
                        //uploadPic(ImageTool.getRealFilePath(MyPersonalActivity.this, mCutUri));
                        uploadPic(Base64BitmapUtil.bitmapToBase64(bitmap), 0);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    /**
     * 打开相机
     */
    private void cameraPic() {
        //创建一个file，用来存储拍照后的照片
        File outputfile = new File(getExternalCacheDir().getPath(), "output.png");
        try {
            if (outputfile.exists()) {
                outputfile.delete();//删除
            }
            outputfile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Uri imageuri;
        if (Build.VERSION.SDK_INT >= 24) {
            imageuri = FileProvider.getUriForFile(MyPersonalActivity.this,
                    "com.bozlun.healthday.android.fileprovider_racefitpro", //可以是任意字符串
                    outputfile);
//            imageuri = FileProvider.getUriForFile(MyPersonalActivity.this,
//                    "com.guider.ringmiihx.fileprovider", //可以是任意字符串
//                    outputfile);
//            imageuri = FileProvider.getUriForFile(MyPersonalActivity.this,
//                    "com.example.bozhilun.android.fileprovider", //可以是任意字符串
//                    outputfile);
        } else {
            imageuri = Uri.fromFile(outputfile);
        }
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
        startActivityForResult(intent, 1001);
    }


    /**
     * 拍照之后，启动裁剪
     *
     * @param camerapath 路径
     * @param imgname    img 的名字
     * @return
     */

    private Intent CutForCamera(String camerapath, String imgname) {
        try {
            //设置裁剪之后的图片路径文件
            File cutfile = new File(getExternalCacheDir().getPath(),
                    "cutcamera.png"); //随便命名一个
            if (cutfile.exists()) { //如果已经存在，则先删除,这里应该是上传到服务器，然后再删除本地的，没服务器，只能这样了
                cutfile.delete();
            }
            cutfile.createNewFile();
            //初始化 uri
            Uri imageUri = null; //返回来的 uri
            Uri outputUri = null; //真实的 uri
            Intent intent = new Intent("com.android.camera.action.CROP");
            //拍照留下的图片
            File camerafile = new File(camerapath, imgname);
            if (Build.VERSION.SDK_INT >= 24) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                imageUri = FileProvider.getUriForFile(MyPersonalActivity.this,
                        "com.bozlun.healthday.android.fileprovider_racefitpro",
                        camerafile);
//                imageUri = FileProvider.getUriForFile(MyPersonalActivity.this,
//                        "com.guider.ringmiihx.fileprovider",
//                        camerafile);
//                imageUri = FileProvider.getUriForFile(MyPersonalActivity.this,
//                        "com.example.bozhilun.android.fileprovider",
//                        camerafile);
            } else {
                imageUri = Uri.fromFile(camerafile);
            }
            outputUri = Uri.fromFile(cutfile);
            //把这个 uri 提供出去，就可以解析成 bitmap了
            mCutUri = outputUri;
            // crop为true是设置在开启的intent中设置显示的view可以剪裁
            intent.putExtra("crop", true);
            // aspectX,aspectY 是宽高的比例，这里设置正方形
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            //设置要裁剪的宽高
            intent.putExtra("outputX", 150);
            intent.putExtra("outputY", 150);
            intent.putExtra("scale", true);
            //如果图片过大，会导致oom，这里设置为false
            intent.putExtra("return-data", false);
            if (imageUri != null) {
                intent.setDataAndType(imageUri, "image/*");
            }
            if (outputUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            }
            intent.putExtra("noFaceDetection", true);
            //压缩图片
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            return intent;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 图片裁剪
     *
     * @param uri
     * @return
     */
    private Intent CutForPhoto(Uri uri) {
        Log.e(TAG, "-----相册选择uri=" + uri);
        try {
            //直接裁剪
            Intent intent = new Intent("com.android.camera.action.CROP");
            //设置裁剪之后的图片路径文件
            File cutfile = new File(getExternalCacheDir().getPath(),
                    "cutcamera.png"); //随便命名一个
            if (cutfile.exists()) { //如果已经存在，则先删除,这里应该是上传到服务器，然后再删除本地的，没服务器，只能这样了
                cutfile.delete();
            }
            cutfile.createNewFile();
            //初始化 uri
            Uri imageUri = uri; //返回来的 uri
            Uri outputUri = null; //真实的 uri
            Log.d(TAG, "CutForPhoto: " + cutfile);
            outputUri = Uri.fromFile(cutfile);
            mCutUri = outputUri;
            Log.d(TAG, "mCameraUri: " + mCutUri);
            // crop为true是设置在开启的intent中设置显示的view可以剪裁
            intent.putExtra("crop", true);
            // aspectX,aspectY 是宽高的比例，这里设置正方形
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            //设置要裁剪的宽高
            intent.putExtra("outputX", 150); //200dp
            intent.putExtra("outputY", 150);
            intent.putExtra("scale", true);
            //如果图片过大，会导致oom，这里设置为false
            intent.putExtra("return-data", false);
            if (imageUri != null) {
                intent.setDataAndType(imageUri, "image/*");
            }
            if (outputUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            }
            intent.putExtra("noFaceDetection", true);
            //压缩图片
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            return intent;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @SuppressLint("NewApi")
    private void handlerImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri,则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的URI，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的Uri,直接获取图片路径即可
            imagePath = uri.getPath();
        }
        Log.e(TAG, "---imagePath=" + imagePath);

        if (imagePath != null) {
            //CutForPhoto(Uri.fromFile(new File(imagePath)));

            startActivityForResult(CutForPhoto(Uri.fromFile(new File(imagePath))), 111);
        }

    }


    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private class UserInfoResult {
        String resultCode;
        UserInfo userInfo;
    }

    private class UserInfo {
        String userId;
        String image;
        String nickName;
        String sex;//"M","F"
        String height;//"170 cm"
        String weight;//"60 kg"
        String birthday;//"2000-06-15"

        @Override
        public String toString() {
            return "UserInfo{" +
                    "userId='" + userId + '\'' +
                    ", image='" + image + '\'' +
                    ", nickName='" + nickName + '\'' +
                    ", sex='" + sex + '\'' +
                    ", height='" + height + '\'' +
                    ", weight='" + weight + '\'' +
                    ", birthday='" + birthday + '\'' +
                    '}';
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        syncUserInfoData();
    }






    //同步用户信息
    private void syncUserInfoData() {
        String userData = (String) SharedPreferencesUtils.readObject(MyPersonalActivity.this, "saveuserinfodata");
        if (!WatchUtils.isEmpty(userData)) {
            try {
                int weight;
                JSONObject jsonO = new JSONObject(userData);
                String userSex = jsonO.getString("sex");    //性别 男 M ; 女 F
                String userAge = jsonO.getString("birthday");   //生日
                String userWeight = jsonO.getString("weight");  //体重
                String tempWeight = StringUtils.substringBefore(userWeight, "kg").trim();
                if (tempWeight.contains(".")) {
                    weight = Integer.valueOf(StringUtils.substringBefore(tempWeight, ".").trim() + "0");
                } else {
                    weight = Integer.valueOf(tempWeight + "0");
                }
                String userHeight = ((String) SharedPreferencesUtils.getParam(MyPersonalActivity.this, "userheight", "")).trim();
                int sex;
                if (userSex.equals("M")) {    //男
                    sex = 0;
                } else {
                    sex = 1;
                }
                int age = WatchUtils.getAgeFromBirthTime(userAge);  //年龄
                int height = Integer.valueOf(userHeight);

                Log.e(TAG, "---------H9--①设置用户信息 " + "性别：" + sex + "年龄：" + age + "身高：" + height + "体重：" + weight);
                //同步用户信息
                AppsBluetoothManager.getInstance(MyApp.getInstance()).sendCommand(new com.sdk.bluetooth.protocol.command.user.UserInfo(commandResultCallbackAll,
                        5, sex, age, height, weight));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


    BaseCommand.CommandResultCallback commandResultCallbackAll = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {
            Log.d(TAG,"--------同步用户信息给设备成功");
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            Log.d(TAG,"--------同步用户信息给设备失败");
        }
    };

}
