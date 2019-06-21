package com.bozlun.healthday.android.b15p.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aigestudio.wheelpicker.widgets.DatePick;
import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.bozlun.healthday.android.Commont;
import com.bozlun.healthday.android.MyApp;
import com.bozlun.healthday.android.R;
import com.bozlun.healthday.android.activity.ModifyNickNameActivity;
import com.bozlun.healthday.android.activity.MyPersonalActivity;
import com.bozlun.healthday.android.base.BaseActivity;
import com.bozlun.healthday.android.bleutil.MyCommandManager;
import com.bozlun.healthday.android.imagepicker.PickerBuilder;
import com.bozlun.healthday.android.net.OkHttpObservable;
import com.bozlun.healthday.android.rxandroid.CommonSubscriber;
import com.bozlun.healthday.android.rxandroid.DialogSubscriber;
import com.bozlun.healthday.android.rxandroid.SubscriberOnNextListener;
import com.bozlun.healthday.android.siswatch.utils.Base64BitmapUtil;
import com.bozlun.healthday.android.siswatch.utils.WatchUtils;
import com.bozlun.healthday.android.util.ImageTool;
import com.bozlun.healthday.android.util.ToastUtil;
import com.bozlun.healthday.android.util.URLs;
import com.bozlun.healthday.android.w30s.utils.httputils.RequestPressent;
import com.bozlun.healthday.android.w30s.utils.httputils.RequestView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.tjdL4.tjdmain.contr.BrltUserParaSet;
import com.tjdL4.tjdmain.contr.L4Command;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IPersonInfoDataListener;
import com.veepoo.protocol.model.enums.EOprateStauts;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class B15PMineActivity extends BaseActivity implements RequestView {
    private static final String TAG = "B15PMineActivity";
    @BindView(R.id.mine_logo_iv_personal)
    CircleImageView mineLogoIvPersonal;
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

    boolean w30sunit = true;
    private ArrayList<String> heightList;
    private ArrayList<String> weightList;


    private DialogSubscriber dialogSubscriber;
    private SubscriberOnNextListener subscriberOnNextListener;

    /**
     * Json帮助类
     */
    private Gson gson = new Gson();
    private RequestQueue requestQueue;
    private RequestPressent requestPressent;

    /**
     * 请求回来的参数,或者要提交的
     */
    private UserInfo mUserInfo = null;
    private String flag;
    private Uri mCutUri;

    @Override
    protected void initViews() {
        w30sunit = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);
        heightList = new ArrayList<>();
        weightList = new ArrayList<>();

        //设置选择列表
        setListData();


        requestQueue = NoHttp.newRequestQueue(1);
        requestPressent = new RequestPressent();
        requestPressent.attach(this);

        AndPermission.with(B15PMineActivity.this)
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

        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("resultCode");
                    System.out.print("resultCode" + resultCode);
                    Log.e("MyPerson", "----resultCode--" + resultCode);
                    if ("001".equals(resultCode)) {
                        ToastUtil.showShort(B15PMineActivity.this, getString(R.string.submit_success));
                        getUserInfoData();
                    } else {
                        ToastUtil.showShort(B15PMineActivity.this, getString(R.string.submit_fail));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    // 获取用户数据
    private void getUserInfoData() {
        String url = URLs.HTTPs + URLs.getUserInfo;
        if (requestPressent != null) {
            HashMap<String, String> map = new HashMap<>();
            map.put("userId", (String) SharedPreferencesUtils.readObject(B15PMineActivity.this, "userId"));
            String mapJson = gson.toJson(map);
            requestPressent.getRequestJSONObject(1, url, this, mapJson, 11);
        }
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
    protected int getContentViewId() {
        return R.layout.layout_mine_b25;
    }

    @OnClick({R.id.mine_logo_iv_personal, R.id.personal_info_back, R.id.nickname_linear, R.id.sex_linear, R.id.rec_height, R.id.rec_weight, R.id.rec_birthday})
    public void onViewClicked(View view) {

        String userId = (String) SharedPreferencesUtils.readObject(B15PMineActivity.this, "userId");
        SharedPreferences share = getSharedPreferences("Login_id", 0);
        int isoff = share.getInt("id", 0);
        if (!WatchUtils.isEmpty(userId)) {
            if (userId.equals("9278cc399ab147d0ad3ef164ca156bf0")) {  //判断是否是游客身份，如果是游客身份无权限修改信息
                ToastUtil.showToast(B15PMineActivity.this, B15PMineActivity.this.getResources().getString(R.string.noright));
            } else {
                switch (view.getId()) {
                    case R.id.mine_logo_iv_personal:
                        if (mUserInfo == null)
                            return;
                        if (AndPermission.hasAlwaysDeniedPermission(B15PMineActivity.this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            chooseImgForUserHead(); //选择图片来源
                        } else {
                            AndPermission.with(B15PMineActivity.this)
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
                    case R.id.personal_info_back:
                        finish();
                        break;
                    case R.id.nickname_linear:
                        if (mUserInfo == null)
                            return;
                        startActivityForResult(new Intent(B15PMineActivity.this, ModifyNickNameActivity.class).putExtra("name", nicknameTv.getText().toString()), 1000);
                        break;
                    case R.id.sex_linear:
                        if (mUserInfo == null)
                            return;
                        showSexDialog();
                        break;
                    case R.id.rec_height:
                        if (mUserInfo == null)
                            return;
                        if (w30sunit) { //公制
                            ProfessionPick professionPopWin = new ProfessionPick.Builder(B15PMineActivity.this, new ProfessionPick.OnProCityPickedListener() {
                                @Override
                                public void onProCityPickCompleted(String profession) {
                                    heightTv.setText(profession);
                                    mUserInfo.height = profession.trim().substring(0, 3);// 记录一下提交要用
                                    flag = "height";
//                                    heightTv.setText(profession);
//                                    height = profession.substring(0, 3);

                                    String uHeight = profession.substring(0, 3).trim();
                                    height = uHeight.trim();//身高赋值
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
                            professionPopWin.showPopWin(B15PMineActivity.this);
                        } else {      //英制
                            ProfessionPick professionPopWin = new ProfessionPick.Builder(B15PMineActivity.this, new ProfessionPick.OnProCityPickedListener() {
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
                                    height = mUserInfo.height;//身高赋值
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
                            professionPopWin.showPopWin(B15PMineActivity.this);
                        }

                        break;
                    case R.id.rec_weight:
                        if (mUserInfo == null)
                            return;
                        if (w30sunit) { //公制
                            ProfessionPick weightPopWin = new ProfessionPick.Builder(B15PMineActivity.this, new ProfessionPick.OnProCityPickedListener() {
                                @Override
                                public void onProCityPickCompleted(String profession) {
                                    weightTv.setText(profession);
                                    mUserInfo.weight = profession.substring(0, 3);// 记录一下提交要用
                                    flag = "weight";
                                    weight = profession.substring(0, 3);//体重赋值
                                    modifyPersonData(weight.trim());
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
                            weightPopWin.showPopWin(B15PMineActivity.this);
                        } else {
                            //英制体重
                            ProfessionPick weightPopWin = new ProfessionPick.Builder(B15PMineActivity.this, new ProfessionPick.OnProCityPickedListener() {
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
                                    weight = mUserInfo.weight;//体重赋值
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
                            weightPopWin.showPopWin(B15PMineActivity.this);
                        }
                        break;
                    case R.id.rec_birthday:
                        if (mUserInfo == null)
                            return;
                        DatePick pickerPopWin = new DatePick.Builder(B15PMineActivity.this, new DatePick.OnDatePickedListener() {
                            @Override
                            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                                birthdayTv.setText(dateDesc);
                                mUserInfo.birthday = dateDesc;
                                flag = "birthday";
                                age = getAge(dateDesc);//年龄赋值
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
                        pickerPopWin.showPopWin(B15PMineActivity.this);
                        break;
                }
            }
        }
    }


    //选择图片
    private void chooseImgForUserHead() {
        MenuSheetView menuSheetView =
                new MenuSheetView(B15PMineActivity.this, MenuSheetView.MenuType.LIST, R.string.select_photo, new MenuSheetView.OnMenuItemClickListener() {
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
            imageuri = FileProvider.getUriForFile(B15PMineActivity.this,
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
                imageUri = FileProvider.getUriForFile(B15PMineActivity.this,
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

    //相册选择
    private void getImage(int type) {
        new PickerBuilder(B15PMineActivity.this, type)
                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                    @Override
                    public void onImageReceived(Uri imageUri) {
                        //设置头像
                        RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true);
                        Glide.with(B15PMineActivity.this).
                                load(imageUri).apply(mRequestOptions).into(mineLogoIvPersonal);

                        uploadPic(ImageTool.getRealFilePath(B15PMineActivity.this, imageUri), 1);
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
//        isSubmit = false;
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
        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, B15PMineActivity.this);
        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.ziliaotouxiang, mapjson);
    }

    //选择性别
    private void showSexDialog() {
        new MaterialDialog.Builder(B15PMineActivity.this)
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
                            SharedPreferencesUtils.setParam(B15PMineActivity.this, Commont.USER_SEX, "M");
                        } else {
                            mUserInfo.sex = "F";
                            sexTv.setText(getResources().getString(R.string.sex_nv));
                            //保存性别
                            SharedPreferencesUtils.setParam(B15PMineActivity.this, Commont.USER_SEX, "F");
                        }
                        flag = "sex";
                        sex = mUserInfo.sex;//性别赋值
                        modifyPersonData(mUserInfo.sex);
                        return true;
                    }
                })
                .positiveText(R.string.select)
                .show();
    }

    //完善用户资料
    private void modifyPersonData(String val) {

        //设置固件的个人信息
        setUserData((sex.equals("M") ? 0 : 1), Integer.valueOf(height.trim()), Integer.valueOf(weight.trim()), Integer.valueOf(age.trim()));

//        isSubmit = true;
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", SharedPreferencesUtils.readObject(MyApp.getContext(), "userId"));
        map.put(flag, val);
        String mapjson = gson.toJson(map);
        Log.e(TAG, "-----mapJson=" + mapjson);
        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, B15PMineActivity.this);
        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.yonghuziliao, mapjson);
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

    @Override
    public void failedData(int what, Throwable e) {

    }

    @Override
    public void closeLoadDialog(int what) {

    }


    private void initUserInfo(String result) {
        UserInfoResult resultVo = gson.fromJson(result, UserInfoResult.class);
        if (resultVo == null || !resultVo.resultCode.equals("001")) return;
        mUserInfo = resultVo.userInfo;
        if (mUserInfo == null) return;
        SharedPreferencesUtils.saveObject(B15PMineActivity.this, Commont.USER_INFO_DATA, gson.toJson(mUserInfo));
        Glide.with(this).load(mUserInfo.image).into(mineLogoIvPersonal);//头像
        nicknameTv.setText(mUserInfo.nickName);// 昵称
        int sexRes = "M".equals(mUserInfo.sex) ? R.string.sex_nan : R.string.sex_nv;
        sex = mUserInfo.sex;//性别赋值
        sexTv.setText(sexRes);// 性别

        String heightStr = mUserInfo.height;// 身高
        if (heightStr.contains("cm")) {
            heightStr = heightStr.trim().substring(0, heightStr.length() - 2);
        }
        heightStr = heightStr.trim();
        height = heightStr;//身高赋值
        mUserInfo.height = heightStr;// 去掉cm后存起来

        String weightStr = mUserInfo.weight;// 体重
        if (weightStr.contains("kg")) {
            weightStr = weightStr.trim().substring(0, weightStr.length() - 2);
        }
        weightStr = weightStr.trim();
        weight = weightStr;//体重赋值
        mUserInfo.weight = weightStr;// 去掉kg后存起来

        if (!w30sunit) {// 英制要处理一下
            heightTv.setText(obtainHeight(heightStr));
            weightTv.setText(obtainWeight(weightStr));
        } else {
            heightTv.setText(heightStr + "cm");
            weightTv.setText(weightStr + "kg");
        }
        age = getAge(mUserInfo.birthday);//年龄赋值
        birthdayTv.setText(mUserInfo.birthday);

        syncUserInfoToB30();
    }


    String getAge(String birthday) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        String a = "0";
        try {
            date = formatter.parse(birthday);
            a = getAgeFromBirthTime(date) + "";//年龄赋值
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return a;
    }

    // 根据年月日计算年龄,birthTimeString:"1994-11-14"
    public int getAgeFromBirthTime(Date date) {
        // 得到当前时间的年、月、日
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            int yearNow = cal.get(Calendar.YEAR);
            int monthNow = cal.get(Calendar.MONTH) + 1;
            int dayNow = cal.get(Calendar.DATE);
            //得到输入时间的年，月，日
            cal.setTime(date);
            int selectYear = cal.get(Calendar.YEAR);
            int selectMonth = cal.get(Calendar.MONTH) + 1;
            int selectDay = cal.get(Calendar.DATE);
            // 用当前年月日减去生日年月日
            int yearMinus = yearNow - selectYear;
            int monthMinus = monthNow - selectMonth;
            int dayMinus = dayNow - selectDay;
            int age = yearMinus;// 先大致赋值
            if (yearMinus <= 0) {
                age = 0;
            }
            if (monthMinus < 0) {
                age = age - 1;
            } else if (monthMinus == 0) {
                if (dayMinus < 0) {
                    age = age - 1;
                }
            }
            return age;
        }
        return 0;
    }


    //同步手环数据 B30,B36
    private void syncUserInfoToB30() {
        if (MyCommandManager.DEVICENAME == null)
            return;
        if (WatchUtils.isVPBleDevice(MyCommandManager.DEVICENAME)) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG, "-----result-=" + requestCode + "--resu=" + resultCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1000:// 改昵称回来
                    String nickName = data.getStringExtra("name");
                    if (!WatchUtils.isEmpty(nickName)) {
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
                        mineLogoIvPersonal.setImageBitmap(bitmap);

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


    String sex, height, weight, age;

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

}
