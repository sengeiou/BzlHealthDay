package com.bozlun.healthday.android.b30;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arialyy.aria.core.Aria;
import com.bozlun.healthday.android.Commont;
import com.bozlun.healthday.android.MyApp;
import com.bozlun.healthday.android.R;
import com.bozlun.healthday.android.bleutil.MyCommandManager;
import com.bozlun.healthday.android.siswatch.WatchBaseActivity;
import com.bozlun.healthday.android.siswatch.utils.WatchUtils;
import com.bozlun.healthday.android.util.ToastUtil;
import com.bozlun.healthday.android.util.URLs;
import com.bozlun.healthday.android.w30s.bean.UpDataBean;
import com.bozlun.healthday.android.w30s.utils.httputils.RequestPressent;
import com.bozlun.healthday.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.suchengkeji.android.w30sblelibrary.utils.W30SBleUtils;
import com.tjdL4.tjdmain.L4M;
import com.tjdL4.tjdmain.ctrls.UpdateManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class B30DufActivity extends WatchBaseActivity implements UpdateManager.OnOTAUpdateListener, RequestView {

    private static final String TAG = "B30DufActivity";
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;

    @BindView(R.id.progress_number)
    TextView progressNumber;
    @BindView(R.id.b30DufBtn)
    Button btnStartUp;
    @BindView(R.id.up_prooss)
    LinearLayout up_prooss;
    @BindView(R.id.progressBar_upgrade)
    ProgressBar proBar;


    private String upDataStringUrl = "";//固件升级链接
    private String bleName = "null";
    private UpdateManager updateManager = null;
    private int upDataState = 0x01;
    private static final String FileStringPath = "/storage/emulated/0/Android/com.bozlun.healthday.android/cache/B25_DEV.zip";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_dfu);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        bleName = intent.getStringExtra("bleName");
        if (WatchUtils.isEmpty(bleName)) {
            bleName = "null";
        }
        initViews();

    }

    private void initViews() {
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
        if (updateManager != null) {
            updateManager = new UpdateManager(this);
            updateManager.setOnOTAUpdateListener(this);
        }


        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.firmware_upgrade));
    }


    @Override
    protected void onResume() {
        super.onResume();
        //获取版本失败，重新获取
        getNetWorke();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updateManager != null) updateManager = null;
    }


    @OnClick({R.id.commentB30BackImg, R.id.b30DufBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.b30DufBtn:
                if (btnStartUp.isEnabled()) {
                    if (upDataState == 0x01) {//可升级的状态
                        //   FileStringPath    bin文件(固件)路径
                        //	 tempAddr   设备mac地址
                        String tempAddr = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);

                        if (!WatchUtils.isEmpty(FileStringPath) && !WatchUtils.isEmpty(tempAddr)) {

                            File f = new File(FileStringPath);
                            if (f.exists()) {

                                Log.e(TAG, " bin文件(固件)路径  " + FileStringPath + " \n  设备mac地址  " + tempAddr);
                                //判断文件是否存在
                                boolean b = existsFile(FileStringPath);
                                if (b) {
                                    Log.e(TAG, "----2222----");
                                    updateManager.updateOTA(FileStringPath, tempAddr);
                                } else {
                                    Toast.makeText(B30DufActivity.this,
                                            getResources().getString(R.string.string_w30s_ota_file), Toast.LENGTH_SHORT).show();//"文件路径出错"
                                    if (!WatchUtils.isEmpty(upDataStringUrl)) {
                                        Log.e(TAG, "----3333----");
                                        Aria.download(this)
                                                .load(String.valueOf(upDataStringUrl))     //读取下载地址
                                                .setDownloadPath(FileStringPath) //设置文件保存的完整路径
                                                .start();   //启动下载
                                    } else {
                                        Log.e(TAG, "----4444----");
                                        //获取网络的版本
                                        getNetWorke();
                                    }

                                }

                            } else {
                                if (!WatchUtils.isEmpty(upDataStringUrl)) {
                                    Toast.makeText(B30DufActivity.this,
                                            getResources().getString(R.string.string_w30s_ota_file), Toast.LENGTH_SHORT).show();//OTA包不存在，正在下载
                                    Aria.download(this)
                                            .load(String.valueOf(upDataStringUrl))     //读取下载地址
                                            .setDownloadPath(FileStringPath) //设置文件保存的完整路径
                                            .start();   //启动下载
                                } else {
                                    getNetWorke();
                                }
                            }


                        }
                    } else if (upDataState == 0x02) {//版本获取失败的状态
                        //获取版本失败，重新获取
                        getNetWorke();
                    }
                }

//                if (!WatchUtils.isEmpty(bleName)
//                        && bleName.equals("B25")
//                        && (L4M.Get_Connect_flag() == 2)) {
//
//
//
//                } else {
//                    ToastUtil.showShort(B30DufActivity.this, getResources().getString(R.string.latest_version));
//                }
                break;
        }
    }


    /**
     * 判断文件是否存在
     */
    public boolean existsFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }
        return false;
    }


    /**
     * OTA 升级相关
     *
     * @param len
     * @param current
     */
    @Override
    public void progressChanged(int len, int current) {
        int progre_num = current * 100 / len; //升级进度
        proBar.setIndeterminate(false);
        proBar.setProgress(progre_num);

        btnStartUp.setEnabled(false);//默认禁止点击
        btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//默认不能点击的背景
        btnStartUp.setText(getResources().getString(R.string.upgrade));//"升级中禁止操作"
    }

    @Override
    public void finishOTA(boolean success) {
        if (!success) {
            //升级失败
            up_prooss.setVisibility(View.INVISIBLE);//默认隐藏
            btnStartUp.setEnabled(false);//默认禁止点击
            btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//默认不能点击的背景
            btnStartUp.setText(getResources().getString(R.string.latest_version));//已经是最新版本
        } else {
            up_prooss.setVisibility(View.INVISIBLE);//默认隐藏
            btnStartUp.setEnabled(false);//默认禁止点击
            btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//默认不能点击的背景
            btnStartUp.setText(getResources().getString(R.string.latest_version));
        }
    }

    @Override
    public void startOTA(int len) {
        //开始升级
        proBar.setIndeterminate(true);

        btnStartUp.setEnabled(false);//默认禁止点击
        btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//默认不能点击的背景
        btnStartUp.setText(getResources().getString(R.string.upgrade));//"升级中禁止操作"
    }


    private RequestPressent requestPressent;

    /**
     * getNetWorke()获取后台版本
     */
    public void getNetWorke() {
        try {
            String b25_V = (String) SharedPreferencesUtils.getParam(B30DufActivity.this, "B25_V", "1");
            if (WatchUtils.isEmpty(b25_V))
                return;
            String baseurl = URLs.HTTPs;
            JSONObject jsonObect = new JSONObject();
            jsonObect.put("clientType", "Android_B25");
            jsonObect.put("version", b25_V);
            jsonObect.put("status", "0");
            jsonObect.put("devType", "B25");

            if (requestPressent != null) {
                //获取版本
                requestPressent.getRequestJSONObject(1, baseurl + URLs.getVersion, B30DufActivity.this, jsonObect.toString(), 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /***
     * 关于网络请求
     * @param what
     */
    @Override
    public void showLoadDialog(int what) {
        showLoadingDialog("获取版本中...");
    }

    @Override
    public void successData(int what, Object object, int daystag) {
        closeLoadingDialog();
        if (what != 1) return;
        if (object == null) return;
        Log.e(TAG, "---------obj=" + object.toString());
        UpDataBean upDataBean = new Gson().fromJson(object.toString(), UpDataBean.class);
//        if (isOneUp==0){
        if (upDataBean.getResultCode().equals("010")) {
            //String w30S_v = (String) SharedPreferenceUtil.get(NewW30sFirmwareUpgrade.this, "W30S_V", "20");
            up_prooss.setVisibility(View.INVISIBLE);//默认隐藏
            btnStartUp.setEnabled(false);//默认禁止点击
            btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//默认不能点击的背景
            btnStartUp.setText(getResources().getString(R.string.latest_version));//已经是最新版本
        } else {
            String version = upDataBean.getVersion();
            String b25_V = (String) SharedPreferencesUtils.getParam(B30DufActivity.this, "B25_V", "1");
            if (Integer.valueOf(version.trim()) > Integer.valueOf(b25_V.trim())) {
//                    MyCommandManager.isOta = true;  //升级状态下
//                    W30SBleUtils.isOtaConn = true;
                upDataState = 0x01;
                up_prooss.setVisibility(View.VISIBLE);//默认显示
                btnStartUp.setEnabled(true);
                btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_selector));//能点击的背景
                btnStartUp.setText(getResources().getString(R.string.string_w30s_upgrade));
                progressNumber.setText(getResources().getString(R.string.string_w30s_upgradeable) + " ->> V" + version);//"可升级 ->> " + version
                //获取下载链接
                upDataStringUrl = upDataBean.getUrl().trim();
                Log.e(TAG, "---upDataStringUrl--=" + upDataStringUrl);
                Aria.download(this)
                        .load(String.valueOf(upDataStringUrl))     //读取下载地址
                        .setDownloadPath(FileStringPath) //设置文件保存的完整路径
                        .start();   //启动下载


                progressNumber.setText("准备完成  可更新");//"准备更新"
                proBar.setIndeterminate(true);

            } else {
//                    W30SBleUtils.isOtaConn = false;
                up_prooss.setVisibility(View.INVISIBLE);//默认隐藏
                btnStartUp.setEnabled(false);//默认禁止点击
                btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//默认不能点击的背景
                btnStartUp.setText(getResources().getString(R.string.latest_version));//已经是最新版本
            }

        }
//        }
    }

    @Override
    public void failedData(int what, Throwable e) {
        closeLoadingDialog();
//        W30SBleUtils.isOtaConn = false;
        e.getMessage();
        upDataState = 0x02;
        Toast.makeText(this, getResources().getString(R.string.get_fail) + e.getMessage(), Toast.LENGTH_SHORT).show();
        btnStartUp.setEnabled(true);
        btnStartUp.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_off));//能点击的背景
        up_prooss.setVisibility(View.INVISIBLE);//默认隐藏
        btnStartUp.setText(getResources().getString(R.string.string_w30s_reacquire_version));//"重新获取版本"
    }

    @Override
    public void closeLoadDialog(int what) {

    }
}
