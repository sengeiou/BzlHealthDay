package com.bozlun.healthday.android.b15p.b15ppagefragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bozlun.healthday.android.Commont;
import com.bozlun.healthday.android.MyApp;
import com.bozlun.healthday.android.R;
import com.bozlun.healthday.android.b15p.activity.B15PHeartDetailActivity;
import com.bozlun.healthday.android.b15p.activity.B15PManualMeaureBloadActivity;
import com.bozlun.healthday.android.b15p.activity.B15PSleepDetailActivity;
import com.bozlun.healthday.android.b15p.activity.B15PStepDetailActivity;
import com.bozlun.healthday.android.b15p.common.B15PContentState;
import com.bozlun.healthday.android.b15p.interfaces.ConntentStuteListenter;
import com.bozlun.healthday.android.b15p.interfaces.FindDBListenter;
import com.bozlun.healthday.android.b15p.interfaces.SycnDataToDBListenter;
import com.bozlun.healthday.android.b30.ManualMeaureHeartActivity;
import com.bozlun.healthday.android.b30.b30view.B15PCusSleepView;
import com.bozlun.healthday.android.b30.b30view.B30CusHeartView;
import com.bozlun.healthday.android.b31.bpoxy.B31BpOxyAnysisActivity;
import com.bozlun.healthday.android.b31.bpoxy.ShowSpo2DetailActivity;
import com.bozlun.healthday.android.b31.bpoxy.util.ChartViewUtil;
import com.bozlun.healthday.android.b31.bpoxy.util.VpSpo2hUtil;
import com.bozlun.healthday.android.b31.hrv.B31HrvDetailActivity;
import com.bozlun.healthday.android.b31.model.B31HRVBean;
import com.bozlun.healthday.android.b31.model.B31Spo2hBean;
import com.bozlun.healthday.android.bleutil.MyCommandManager;
import com.bozlun.healthday.android.commdbserver.CommDBManager;
import com.bozlun.healthday.android.commdbserver.CommentDataActivity;
import com.bozlun.healthday.android.siswatch.LazyFragment;
import com.bozlun.healthday.android.siswatch.utils.WatchUtils;
import com.bozlun.healthday.android.util.Constant;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;
import com.littlejie.circleprogress.circleprogress.WaveProgress;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30S_SleepDataItem;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.tjdL4.tjdmain.Dev;
import com.tjdL4.tjdmain.L4M;
import com.tjdL4.tjdmain.contr.BracltBatLevel;
import com.tjdL4.tjdmain.contr.L4Command;
import com.veepoo.protocol.model.datas.HRVOriginData;
import com.veepoo.protocol.model.datas.Spo2hOriginData;
import com.veepoo.protocol.model.enums.ESpo2hDataType;
import com.veepoo.protocol.util.HRVOriginUtil;
import com.veepoo.protocol.util.HrvScoreUtil;
import com.veepoo.protocol.util.Spo2hOriginUtil;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.bozlun.healthday.android.b31.bpoxy.enums.Constants.CHART_MAX_HRV;
import static com.bozlun.healthday.android.b31.bpoxy.enums.Constants.CHART_MAX_SPO2H;
import static com.bozlun.healthday.android.b31.bpoxy.enums.Constants.CHART_MIN_HRV;
import static com.bozlun.healthday.android.b31.bpoxy.enums.Constants.CHART_MIN_SPO2H;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_BEATH_BREAK;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_HRV;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_SPO2H;

public class B15pHomeFragment extends LazyFragment
        implements ConntentStuteListenter {
    //, SycnChangeUIListenter {
    public final String TAG = "B15pHomeFragment";
    View rootView;
    Unbinder unbinder;
    //血压图标    血压测试、心率测试 按钮显示和隐藏
    @BindView(R.id.b30HomeB30Lin)
    LinearLayout b30HomeB30Lin;
    //电量
    @BindView(R.id.batteryTopImg)
    ImageView batteryTopImg;
    @BindView(R.id.batteryPowerTv)
    TextView batteryPowerTv;
    @BindView(R.id.b30connectStateTv)
    TextView b30ConnectStateTv;
    @BindView(R.id.b30HomeSwipeRefreshLayout)
    SmartRefreshLayout b30HomeSwipeRefreshLayout;


    //电量图片
    @BindView(R.id.battery_watchRecordShareImg)
    ImageView batteryWatchRecordShareImg;
    @BindView(R.id.watch_record_titleLin)
    LinearLayout watchRecordTitleLin;
    //波浪形进度条
    @BindView(R.id.b30ProgressBar)
    WaveProgress b30ProgressBar;
    //目标步数显示
    @BindView(R.id.b30GoalStepTv)
    TextView b30GoalStepTv;
    //日期
    @BindView(R.id.b30_top_dateTv)
    TextView b30TopDateTv;
    //设备图标
    @BindView(R.id.iv_top)
    ImageView ivTop;
    //链接状态返回
    private B15PContentState b15PContentState;
    //默认步数
    int defaultSteps = 0;
    /**
     * 目标步数
     */
    int goalStep;

    //运动图表最大步数
    @BindView(R.id.sycn_stute)
    TextView sycnStute;

    //血氧的图表
    @BindView(R.id.homeSpo2LinChartView)
    LineChart homeSpo2LinChartView;
    @BindView(R.id.b31Spo2AveTv)
    TextView b31Spo2AveTv;

    //心脏健康指数
    @BindView(R.id.hrvHeartSocreTv)
    TextView hrvHeartSocreTv;
    //HRV的图表
    @BindView(R.id.b31HomeHrvChart)
    LineChart b31HomeHrvChart;


    VpSpo2hUtil vpSpo2hUtil;
    @BindView(R.id.block_chartview_spo2h)
    LineChart mChartViewSpo2h;  //呼吸暂停图表
    @BindView(R.id.block_chartview_heart)
    LineChart mChartViewHeart;  //心脏负荷图表
    @BindView(R.id.block_chartview_sleep)
    LineChart mChartViewSleep;  //睡眠活动图表
    @BindView(R.id.block_chartview_breath)
    LineChart mChartViewBreath; //呼吸率图表
    @BindView(R.id.block_chartview_lowspo2h)
    LineChart mChartViewLowspo2h;   //低氧时间图表

    private List<Spo2hOriginData> data0To8 = new ArrayList<>();
    private List<HRVOriginData> datahrv = new ArrayList<>();
    private Gson gson = new Gson();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_b15p_home_layout, container, false);
        unbinder = ButterKnife.bind(this, rootView);


        init(rootView);
        /**
         * 设置监听----同步数据保存本地数据库
         */
        L4M.SetResultToDBListener(SycnDataToDBListenter.getSycnDataToDBListenter());
        initData();
        return rootView;
    }


    /**
     * 初始
     *
     * @param rootView
     */
    private void init(View rootView) {
        b15PContentState = B15PContentState.getInstance();
        b15PContentState.setB15PContentState(this);
        b15PContentState.bleIsContent();
        registerListenter();

//        //b15p 有血压   血压测试和心率测试，所以显示
//        b30HomeB30Lin.setVisibility(View.VISIBLE);
//        batteryWatchRecordShareImg.setVisibility(View.GONE);//原来的分享---》》现在是数据面板


        goalStep = (int) SharedPreferencesUtils.getParam(getContext(), "b30Goal", 0);
        b30TopDateTv.setText(WatchUtils.getCurrentDate());
        if (b30GoalStepTv != null)
            b30GoalStepTv.setText(getResources().getString(R.string.goal_step) + goalStep + getResources().getString(R.string.steps));
        //ivTop.setImageResource(R.mipmap.ic_home_top_b31);
        if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME)) {
//            if (WatchUtils.verBleNameForSearch( MyCommandManager.DEVICENAME)) {
//                if ( MyCommandManager.DEVICENAME.equals("F6")) {
//                    ivTop.setImageResource(R.mipmap.img_wirte_f6);
//                } else {
//                    ivTop.setImageResource(R.mipmap.ic_home_top_b31);
//                }
//            }
            if (MyCommandManager.DEVICENAME.length() > 1 && !MyCommandManager.DEVICENAME.equals("F6")) {
                if (MyCommandManager.DEVICENAME.substring(0, 1).equals("B")) {
                    ivTop.setImageResource(R.mipmap.ic_series_w_b);
                } else if (MyCommandManager.DEVICENAME.substring(0, 1).equals("L")) {
                    ivTop.setImageResource(R.mipmap.ic_series_w_l);
                } else if (MyCommandManager.DEVICENAME.substring(0, 1).equals("F")) {
                    ivTop.setImageResource(R.mipmap.ic_series_w_f);
                } else if (MyCommandManager.DEVICENAME.substring(0, 1).equals("M")) {
                    ivTop.setImageResource(R.mipmap.ic_series_w_m);
                } else if (MyCommandManager.DEVICENAME.substring(0, 1).equals("W")) {
                    ivTop.setImageResource(R.mipmap.ic_series_w_w);
                }
            } else {
                ivTop.setImageResource(R.mipmap.img_wirte_f6);
            }
        }


        //进度圆显示默认的步数
        if (getActivity() != null && !getActivity().isFinishing() && b30ProgressBar != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    b30ProgressBar.setMaxValue(goalStep);
                    b30ProgressBar.setValue(defaultSteps);
                }
            });
        }

        if (b30HomeSwipeRefreshLayout != null)
            b30HomeSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    Log.d(TAG, "手动刷新 ----- getBleDevicesDatas()   读取设备数据");
                    mHandler.sendEmptyMessageDelayed(0x01, 100);//第一次链接自动获取数据

                }
            });

    }

    private void initData() {
        sleepList = new ArrayList<>();
        heartList = new ArrayList<>();
        heartListNew = new ArrayList<>();

        b30ChartList = new ArrayList<>();
        tmpB30StepList = new ArrayList<>();
        tmpIntegerList = new ArrayList<>();

        initSpo2hUtil();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        b15PContentState.isConnect();
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
//        if (isVisible) {
//            if (MyB15PStepDetailActivityCommandManager.DEVICENAME != null) {
//                long currentTime = System.currentTimeMillis() / 1000;
//                //保存的时间
//                String tmpSaveTime = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "saveDate", currentTime + "");
//                long diffTime = (currentTime - Long.valueOf(tmpSaveTime)) / 60;
//                if (WatchConstants.isScanConn) {  //是搜索进来的
//                    WatchConstants.isScanConn = false;
//                    Log.e(TAG, "======搜索进来");
//                    getBleDevicesDatas();
//                    if (b30HomeSwipeRefreshLayout != null) b30HomeSwipeRefreshLayout.autoRefresh();
//                    Log.d("bobo", "onFragmentVisibleChange: autoRefresh()");
//                } else {  //不是搜索进来的
//                    Log.e(TAG, "====不是搜索进来的 " + diffTime);
//                    if (diffTime > 8) {// 大于五分钟没更新再取数据
//                        getBleDevicesDatas();
//                        if (b30HomeSwipeRefreshLayout != null)
//                            b30HomeSwipeRefreshLayout.autoRefresh();
//                    }
//                }
//            }
//        }
    }

    /**
     * 链接状态返回
     *
     * @param state 0 :未连接  1:正在连接  2 :已连接
     */
    @Override
    public void b15p_Connection_State(int state) {
        if (getActivity() != null && !getActivity().isFinishing()) {

            switch (state) {
                case 0:
                    Log.d(TAG, "--B15P--未连接");
                    if (b30ConnectStateTv != null)
                        b30ConnectStateTv.setText(getResources().getString(R.string.disconnted));
                    if (sycnStute != null)
                        sycnStute.setText(getResources().getString(R.string.disconnted));
                    //未连接----自动扫描去链接
                    b15PContentState.bleSeachDevices();
                    break;
                case 1:
                    Log.d(TAG, "--B15P--正在链接");
                    if (b30ConnectStateTv != null)
                        b30ConnectStateTv.setText(getResources().getString(R.string.disconnted));
                    if (sycnStute != null)
                        sycnStute.setText(getResources().getString(R.string.disconnted));
                    break;
                case 2:
                    Log.d(TAG, "--B15P--已连接");


                    if (b30ConnectStateTv != null)
                        b30ConnectStateTv.setText(getResources().getString(R.string.connted));
                    if (sycnStute != null)
                        sycnStute.setText(getResources().getString(R.string.connted));

                    int param = (int) SharedPreferencesUtils.getParam(getContext(), Commont.BATTERNUMBER, 0);
                    if (param > 0) {
                        //设置每次会来电电池电量
                        showBatterStute(param);
                    }

                    //设置每次回主界面，返回数据不清空的
                    clearDataStyle(0);

                    if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME)) {
                        if (MyCommandManager.DEVICENAME.length() > 1 && !MyCommandManager.DEVICENAME.equals("F6")) {
                            if (MyCommandManager.DEVICENAME.substring(0, 1).equals("B")) {
                                ivTop.setImageResource(R.mipmap.ic_series_w_b);
                            } else if (MyCommandManager.DEVICENAME.substring(0, 1).equals("L")) {
                                ivTop.setImageResource(R.mipmap.ic_series_w_l);
                            } else if (MyCommandManager.DEVICENAME.substring(0, 1).equals("F")) {
                                ivTop.setImageResource(R.mipmap.ic_series_w_f);
                            } else if (MyCommandManager.DEVICENAME.substring(0, 1).equals("M")) {
                                ivTop.setImageResource(R.mipmap.ic_series_w_m);
                            } else if (MyCommandManager.DEVICENAME.substring(0, 1).equals("W")) {
                                ivTop.setImageResource(R.mipmap.ic_series_w_w);
                            }
                        } else {
                            ivTop.setImageResource(R.mipmap.img_wirte_f6);
                        }
                    }

                    Log.d(TAG, "--是否是第一次链接   " + MyApp.b15pIsFirstConntent);
                    if (MyApp.b15pIsFirstConntent) {
                        MyApp.b15pIsFirstConntent = false;//第一次链接同步后改变第一次链接之后的状态

                        // 第一次链接自动获取数据
                        // 先获取电池在获取运动数据
                        mHandler.sendEmptyMessageDelayed(0x01, 100);
                    } else {

                        long currentTime = System.currentTimeMillis() / 1000;
                        //保存的时间
                        String tmpSaveTime = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "saveDate", currentTime + "");
                        long diffTime = (currentTime - Long.valueOf(tmpSaveTime)) / 60;
                        Log.e(TAG, "不是第一次链接--- 有 " + diffTime + "分钟没同步了");
                        if (diffTime > 5) {// 大于2分钟没更新再取数据
                            mHandler.sendEmptyMessageDelayed(0x01, 100);
                        } else {
                            /**
                             *
                             * 如果不是第一次同步每次回来主动从数据库中获取一次
                             * sycnDataListenterStep
                             * sycnDataListenterSleep
                             * sycnDataListenterHeart
                             *
                             * Dev.L4UI_PageDATA_PEDO
                             * Dev.L4UI_PageDATA_SLEEP
                             * Dev.L4UI_PageDATA_HEARTRATE
                             * @param type
                             */
                            Log.e(TAG, "不是第一次同步每次回来主动从数据库中获取一次");
                            mHandler.sendEmptyMessage(0x00);
                        }
                    }
                    break;
            }

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterListenter();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (b30ProgressBar != null) {
            b30ProgressBar.removeAnimator();
        }
        if (unbinder != null) unbinder.unbind();
    }


    /**
     * 显示同步状态的提
     *
     * @param isShow
     */
    void setSysTextStute(boolean isShow) {
        if (sycnStute != null) {
            if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME)) {
                if (isShow) {
                    sycnStute.setVisibility(View.VISIBLE);
                    sycnStute.setText(getResources().getString(R.string.syncy_data));
                } else {
                    sycnStute.setVisibility(View.GONE);
                }
            } else {
                sycnStute.setVisibility(View.VISIBLE);
                sycnStute.setText(getResources().getString(R.string.disconnted));
            }

        }
    }


    /**
     * 地一步
     * 获取设备电池电量
     */
    void getBatter() {

        setSysTextStute(true);

        L4Command.BatLevel(btResultListenr);
    }

    L4M.BTResultListenr btResultListenr = new L4M.BTResultListenr() {
        @Override
        public void On_Result(String TypeInfo, String StrData, Object DataObj) {
            final String tTypeInfo = TypeInfo;
            final String TempStr = StrData;
            final Object TempObj = DataObj;
            //Log.d(TAG, "电池电量    获取有返回了" + tTypeInfo + "  " + TempStr + "  " + TempObj);

            if (TypeInfo.equals(L4M.ERROR) && StrData.equals(L4M.TIMEOUT)) {
                Log.e(TAG, "--------------==  获取电量超时 ~~~~~~~~~~~~~~ 跳过去执行下一个指令");
                mHandler.sendEmptyMessageDelayed(0x02, 100);
                return;
            }

            if (tTypeInfo.equals(L4M.GetBatLevel) && TempStr.equals(L4M.Data)) {
                BracltBatLevel.BatLevel myBatLevel = (BracltBatLevel.BatLevel) TempObj;
                int batlevel = myBatLevel.batlevel;
                SharedPreferencesUtils.setParam(getContext(), Commont.BATTERNUMBER, batlevel);
                Log.e(TAG, "==  获取到的电量  " + batlevel);


                int param = (int) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.BATTERNUMBER, 0);
                if (param > 0) {
                    //设置每次会来电电池电量
                    showBatterStute(param);
                }

                mHandler.sendEmptyMessageDelayed(0x02, 100);
            }
        }
    };

    /**
     * 注册
     */
    void registerListenter() {
        //同步进度
        Dev.SetUpdateUiListener(Dev.L4UI_PageDATA_HEALTH, myUpDateUiCb);
        Dev.EnUpdateUiListener(myUpDateUiCb, 1);
    }


    /**
     * 反注册
     */
    void unRegisterListenter() {
        Dev.EnUpdateUiListener(myUpDateUiCb, 0);//同步进度
    }

    int type = 0;
    List<String> typSync = new ArrayList<>();
    Dev.UpdateUiListenerImpl myUpDateUiCb = new Dev.UpdateUiListenerImpl() {
        @Override
        public void UpdateUi(int ParaA, String StrData) {
            int TempPara = ParaA;
            String TempStrData = StrData;


            Log.e(TAG, "L4UI_DATA_SyncProgress =" + TempStrData + "====" + TempPara);

            if (TempPara == Dev.L4UI_DATA_SyncProgress && TempStrData.equals("1")) {
                typSync.clear();
            } else if (TempPara == Dev.L4UI_DATA_SyncProgress && TempStrData.equals("100")) {

                typSync.add("100");
                if (typSync.size() >= 2) {
                    type++;
                    switch (type) {
                        case 1:
                            mHandler.removeMessages(0x77);//取消 77 步数超时等待处理

                            Log.e("L4UI_DATA_SyncProgress", "步数获取完成------去获取睡眠");
                            mHandler.sendEmptyMessageDelayed(0x04, 500);
                            break;
                        case 2:
                            mHandler.removeMessages(0x88);//取消 88 睡眠超时处理

                            Log.e("L4UI_DATA_SyncProgress", "睡眠获取完成------去获取心率");
                            mHandler.sendEmptyMessageDelayed(0x05, 500);

//                            mHandler.sendEmptyMessageDelayed(0x55, 15 * 1000);
                            break;
                        case 3:
                            mHandler.removeMessages(0x99);//取消 99 心率超时处理

                            Log.e("L4UI_DATA_SyncProgress", "心率获取完成------去获取所有数据显示");
                            mHandler.sendEmptyMessageDelayed(0x00, 100);
                            break;
                    }
                }
            } else if (TempPara == Dev.L4UI_DATA_SyncProgress && TempStrData.equals("-100")) {

            }
        }
    };

    String mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
            switch (message.what) {
                case 0x55:
                    /**
                     * 同步完成上传数据
                     */
                    startUploadDBService();
                    break;
                /**
                 * 步数  睡眠  心率 的超时处理
                 */
                case 0x77://步数12秒等待超时
                    if (mHandler != null) {
                        mHandler.removeMessages(0x77);//删除超时等待
                        Log.e("L4UI_DATA_SyncProgress", "步数获取超时------去获取睡眠");

                        /**
                         * 从数据库中读步数
                         */
//                        mHandler.sendEmptyMessage(0x11);
                        mHandler.sendEmptyMessage(0x00);

                        mHandler.sendEmptyMessageDelayed(0x04, 500);//读取设备中的下一个睡眠数据
                    }
                    break;
                case 0x88://睡眠12秒等待超时
                    if (mHandler != null) {
                        mHandler.removeMessages(0x88);//删除超时等待
                        Log.e("L4UI_DATA_SyncProgress", "睡眠获取超时------去获取心率");

                        /**
                         * 从数据库中读睡眠
                         */
//                        mHandler.sendEmptyMessage(0x12);
                        mHandler.sendEmptyMessage(0x00);

                        mHandler.sendEmptyMessageDelayed(0x05, 500);//读取设备中的下一个心率数据
                    }
                    break;
                case 0x99://心率12秒等待超时
                    if (mHandler != null) {
                        mHandler.removeMessages(0x99);//删除超时等待
                        //mHandler.sendEmptyMessage(0x00);//获取数据库中的心率数据

                        Log.e("L4UI_DATA_SyncProgress", "心率获取超时------去获取数据库");
                        /**
                         * 从数据库中读心率
                         */
//                        mHandler.sendEmptyMessage(0x13);
                        mHandler.sendEmptyMessage(0x00);

                    }
                    break;
                case 0x00:
                    if (mHandler != null) {

                        //分别读取数据空中的数据
                        mHandler.sendEmptyMessageDelayed(0x11, 100);
                        mHandler.sendEmptyMessageDelayed(0x12, 200);
                        mHandler.sendEmptyMessageDelayed(0x13, 300);
                    }
                    break;
                /**
                 * 从设备获取数据
                 */
                case 0x01:// 获取电池电量
                    Log.d(TAG, "手动刷新 ----- 先获取电池电量");
                    getBatter();
                    break;
                case 0x02://同步数据
                    //链接成功了
                    if (MyCommandManager.DEVICENAME != null) {
                        if (getActivity() != null && !getActivity().isFinishing()) {
                            if (b30HomeSwipeRefreshLayout != null)
                                b30HomeSwipeRefreshLayout.setEnableRefresh(true);
                        }

                        int bt = (int) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.BATTERNUMBER, 0);
                        showBatterStute(bt);
                        getBleDevicesDatas();
                    }
                    break;
                case 0x03://获取步数
                    mHandler.sendEmptyMessageDelayed(0x77, 12 * 1000);//77 步数6秒超时处理
                    L4Command.GetPedo1();
                    //获取步数+睡眠
//                    L4Command.YsnALLData();
                    break;
                case 0x04://获取睡眠
                    mHandler.sendEmptyMessageDelayed(0x88, 12 * 1000);//88 睡眠超时处理
                    L4Command.GetSleep1();     //睡眠
//                    L4Command.CommSleepTime(currDay,5000);
                    break;
                case 0x05://获取心率
                    mHandler.sendEmptyMessageDelayed(0x99, 12 * 1000);//99 心率超时处理
                    L4Command.GetHeart1();
                    break;
                case 0x11:
                    if (!WatchUtils.isEmpty(mac)) {
//                        Log.e(TAG, "  mac  " + mac);

                        String[] Allsteps = {"all_step", mac, WatchUtils.obtainFormatDate(currDay)};
                        new FindDBListenter(new FindDBListenter.ChangeDBListenter<Integer>() {

                            @Override
                            public void updataAllStepDataToUIListenter(int integer) {
                                super.updataAllStepDataToUIListenter(integer);
                                defaultSteps = integer;
                                if (getActivity() != null && !getActivity().isFinishing()) {
                                    if (b30ProgressBar != null) {
                                        b30ProgressBar.setMaxValue(goalStep);
                                        b30ProgressBar.setValue(defaultSteps);
                                    }
                                }

                            }
                        }).execute(Allsteps);

                        String[] steps = {"step", mac, WatchUtils.obtainFormatDate(currDay)};
                        new FindDBListenter(new FindDBListenter.ChangeDBListenter<Integer>() {
//                            @Override
//                            public void updataDataToUIListenter(List<Object> stepAllDatas) {
//
//                                Log.e(TAG, stepAllDatas.toString());
//                                //setStep(stepAllDatas);
//
//                            }

                            @Override
                            public void updataStepDataToUIListenter(List<Integer> ts) {

//                                defaultSteps = 0;
//                                for (int i = 0; i < ts.size(); i++) {
//                                    defaultSteps += ts.get(i);
//                                }
//
//                                if (b30ProgressBar != null) {
//                                    b30ProgressBar.setMaxValue(goalStep);
//                                    b30ProgressBar.setValue(defaultSteps);
//                                }
                                showSportStepData(ts);
                            }
                        }).execute(steps);


                    }
                    break;
                case 0x12:
                    if (!WatchUtils.isEmpty(mac)) {

                        String[] steps = {"sleep", mac, WatchUtils.obtainFormatDate(currDay)};
                        new FindDBListenter(new FindDBListenter.ChangeDBListenter<W30S_SleepDataItem>() {
//                            @Override
//                            public void updataDataToUIListenter(List<Object> sleepAllDatas) {
//
////                                setSleep(sleepAllDatas);
//                            }





                            /**
                             *  HRV  的虚拟数据
                             * -----这个这个数据是从子线程直接返回的，所以要切换到主线程
                             */
                            @Override
                            public void updataHrvDataToUIListenter(final List<B31HRVBean> hrvBeanList) {

                                if (getActivity() != null && !getActivity().isFinishing()) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Log.e(TAG, "-----------HRV--HRV---SPO---SPO");
                                            if (hrvBeanList != null && !hrvBeanList.isEmpty()) {

                                                datahrv.clear();
                                                for (B31HRVBean hBean : hrvBeanList) {
                                                    // Log.e(TAG, "------xueyang---走到这里来了=" + hBean.toString());
                                                    datahrv.add(gson.fromJson(hBean.getHrvDataStr(), HRVOriginData.class));
                                                }

                                                if (datahrv != null) showHrvData(datahrv);
                                            }

                                        }
                                    });
                                }
                            }

                            /**
                             *  SPO  的虚拟数据
                             * -----这个这个数据是从子线程直接返回的，所以要切换到主线程
                             */
                            @Override
                            public void updataSpo2DataToUIListenter(final List<B31Spo2hBean> b31Spo2hBeanList) {
                                if (getActivity() != null && !getActivity().isFinishing()) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Log.e(TAG, "-----------HRV--HRV---SPO---SPO");
                                            if (b31Spo2hBeanList != null && !b31Spo2hBeanList.isEmpty()) {
                                                data0To8.clear();
                                                for (B31Spo2hBean hBean : b31Spo2hBeanList) {
                                                   // Log.e(TAG, "------xueyang---走到这里来了=" + hBean.toString());
                                                    data0To8.add(gson.fromJson(hBean.getSpo2hOriginData(), Spo2hOriginData.class));
                                                }

                                                if (data0To8 != null) updateSpo2View(data0To8);
                                            }

                                        }
                                    });
                                }
                            }

                            @Override
                            public void updataSleepDataToUIListenter(List<W30S_SleepDataItem> sleepDataList) {

                                if (getActivity() != null && !getActivity().isFinishing()) {


                                    String dateStr = WatchUtils.obtainFormatDate(currDay);
                                    if (!sleepDataList.isEmpty()) {
//                        ALLTIME = 0;//睡眠总时间
                                        AWAKE = 0;//清醒的次数
//                        DEEP = 0;//深睡
//                        SHALLOW = 0;//浅睡
                                        startSleepTime = "";
                                        endSleepTime = "";
                                        StringBuilder strSleep = new StringBuilder("");
                                        for (int i = 0; i < sleepDataList.size() - 1; i++) {
                                            String startTime = null;
                                            String startTimeLater = null;
                                            String sleep_type = null;
//                                Log.e(TAG, "===睡眠=C= " + sleepDataList.get(i).getStartTime() + "======" +
//                                        (sleepDataList.get(i).getSleep_type().equals("0") ? "===清醒" : sleepDataList.get(i).getSleep_type().equals("1") ? "---->>>浅睡" : "===>>深睡"));

                                            if (i == 0)
                                                startSleepTime = sleepDataList.get(i).getStartTime();
                                            endSleepTime = sleepDataList.get(i).getStartTime();
                                            if (i >= (sleepDataList.size() - 1)) {
                                                startTime = sleepDataList.get(i).getStartTime();
                                                startTimeLater = sleepDataList.get(i).getStartTime();
                                                sleep_type = sleepDataList.get(i).getSleep_type();
                                            } else {
                                                startTime = sleepDataList.get(i).getStartTime();
                                                startTimeLater = sleepDataList.get(i + 1).getStartTime();
                                                sleep_type = sleepDataList.get(i).getSleep_type();
                                            }
                                            String[] starSplit = startTime.split("[:]");
                                            String[] endSplit = startTimeLater.split("[:]");

                                            int startHour = Integer.valueOf(!TextUtils.isEmpty(starSplit[0].replace(",", "")) ? starSplit[0].replace(",", "") : "0");
                                            int endHour = Integer.valueOf(!TextUtils.isEmpty(endSplit[0].replace(",", "")) ? endSplit[0].replace(",", "") : "0");

                                            int startMin = Integer.valueOf(!TextUtils.isEmpty(starSplit[1].replace(",", "")) ? starSplit[1].replace(",", "") : "0");
                                            int endMin = (Integer.valueOf(!TextUtils.isEmpty(endSplit[1].replace(",", "")) ? endSplit[1].replace(",", "") : "0"));
                                            if (startHour > endHour) {
                                                endHour = endHour + 24;
                                            }
                                            int all_m = (endHour - startHour) * 60 + (endMin - startMin);
                                            //B15P元数据   清醒  0    浅睡 1   深睡 2
                                            //图标绘制时    浅睡  0    深睡 1   清醒 2
                                            if (sleep_type.equals("0")) {
                                                AWAKE++;
//                                Log.e(TAG, "====0===" + all_m);
                                                for (int j = 1; j <= all_m; j++) {
                                                    strSleep.append("2");
                                                }
                                            } else if (sleep_type.equals("1")) {
                                                //潜水
//                                SHALLOW = SHALLOW + all_m;
//                                ALLTIME = ALLTIME + all_m;
//                                Log.e(TAG, "====1===" + all_m);
                                                for (int j = 1; j <= all_m; j++) {
                                                    strSleep.append("0");
                                                }
                                            } else if (sleep_type.equals("2")) {
                                                //深水
//                                DEEP = DEEP + all_m;
//                                ALLTIME = ALLTIME + all_m;
//                                Log.e(TAG, "====2===" + all_m);
                                                for (int j = 1; j <= all_m; j++) {
                                                    strSleep.append("1");
                                                }
                                            }

                                        }

//                            Log.e(TAG, "===睡眠=D=" + strSleep.toString());


                                        if (!TextUtils.isEmpty(strSleep)) {

//                                Log.e(TAG, strSleep.toString().length() + " 睡眠 \n" + strSleep.toString());
                                            /**
                                             * 显示睡眠图标
                                             */
                                            showSleepData(strSleep.toString(), dateStr);
                                        }
                                    } else {
                                        if (b30CusSleepView != null) {
                                            b30CusSleepView.setSeekBarShow(false);
                                            b30CusSleepView.setSleepList(new ArrayList<Integer>());
                                        }
                                    }

                                }


                            }

                        }).execute(steps);

                    }

                    break;
                case 0x13:

                    if (!WatchUtils.isEmpty(mac)) {
                        String[] steps = {"heart", mac, WatchUtils.obtainFormatDate(currDay)};
                        new FindDBListenter(new FindDBListenter.ChangeDBListenter<Integer>() {
//                            @Override
//                            public void updataDataToUIListenter(List<Object> heartAllDatas) {
////                                 setHeart(heartAllDatas);
//                            }

                            @Override
                            public void updataHeartDataToUIListenter(List<Integer> ts, String latelyValues) {

                                if (getActivity() != null && !getActivity().isFinishing()) {
                                    /**
                                     * 去上传数据
                                     */
                                    mHandler.sendEmptyMessageDelayed(0x55, 1000);

                                    if (ts != null && !ts.isEmpty()) {

                                        //设置最近心率----这里拿的是最后一条心率的数据和时间
                                        if (!WatchUtils.isEmpty(latelyValues)) {
                                            String[] split = latelyValues.split("[#]");
                                            if (!WatchUtils.isEmpty(split[0])) {
                                                lastTimeTv.setText(split[0]);
                                            }
                                            if (!WatchUtils.isEmpty(split[1])) {
                                                b30HeartValueTv.setText(split[1]);
                                            }
                                        }
                                        if (b30CusHeartView != null)
                                            b30CusHeartView.setRateDataList(ts);
                                    } else {
                                        if (b30CusHeartView != null)
                                            b30CusHeartView.setRateDataList(heartList);
                                    }


                                    if (b30HomeSwipeRefreshLayout != null)
                                        b30HomeSwipeRefreshLayout.finishRefresh();
                                    setSysTextStute(false);
                                }

                            }
                        }).execute(steps);

                    }

                    break;
            }
            return false;
        }
    });


    /**
     * 当前显示哪天的数据(0_今天 1_昨天 2_前天)
     */
    private int currDay = 0;

    @OnClick({R.id.b30SportChartLin1, R.id.b30BarChart, R.id.b30CusHeartLin,
            R.id.b30CusBloadLin, R.id.b30MeaureHeartImg, R.id.b30MeaureBloadImg,
            R.id.b30SleepLin, R.id.homeTodayTv, R.id.homeYestTodayTv, R.id.homeBeYestdayTv,
            R.id.battery_watchRecordShareImg,
            R.id.b31BpOxyLin,R.id.b31HrvView,R.id.block_spo2h,R.id.block_heart,R.id.block_sleep,R.id.block_breath,R.id.block_lowspo2h})//, R.id.b36WomenStatusLin, R.id.b36WomenPrivacyImg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.b30SportChartLin1: // 运动数据详情
            case R.id.b30BarChart: // 运动数据详情
                B15PStepDetailActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate(currDay));
                break;
            case R.id.b30CusHeartLin:   //心率详情
                B15PHeartDetailActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate(currDay));
                break;
            case R.id.b30CusBloadLin:   //血压详情
                //B30BloadDetailActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate(currDay));
                break;
            case R.id.b30MeaureHeartImg:    //手动测量心率
                startActivity(new Intent(getActivity(), ManualMeaureHeartActivity.class)
                        .putExtra("what", "b15p"));
                break;
            case R.id.b30MeaureBloadImg:    //手动测量血压
//                startActivity(new Intent(getActivity(), ManualMeaureBloadActivity.class));
                startActivity(new Intent(getActivity(), B15PManualMeaureBloadActivity.class));
                break;
            case R.id.b30SleepLin:      //睡眠详情
                B15PSleepDetailActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate
                        (currDay));
                break;
            case R.id.homeTodayTv:  //今天
                clearDataStyle(0);
                mHandler.sendEmptyMessage(0x00);
                break;
            case R.id.homeYestTodayTv:  //昨天
                clearDataStyle(1);
                mHandler.sendEmptyMessage(0x00);
                break;
            case R.id.homeBeYestdayTv:  //前天
                clearDataStyle(2);
                mHandler.sendEmptyMessage(0x00);
                break;
            case R.id.battery_watchRecordShareImg:  //分享----现在是数据面板
                if (getActivity() == null || getActivity().isFinishing())
                    return;
//                Intent intent = new Intent(getActivity(), SharePosterActivity.class);
//                intent.putExtra("is18i", "B36");
//                intent.putExtra("stepNum", defaultSteps + "");
//                startActivity(intent);
                startActivity(new Intent(getActivity(), CommentDataActivity.class));
                break;


            case R.id.b31BpOxyLin:  //血氧分析
                B31BpOxyAnysisActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate(currDay));
                break;
            case R.id.b31HrvView:    //HRV
                B31HrvDetailActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate(currDay));
                break;
            case R.id.block_spo2h:  //血氧
                startToSpo2Detail("0", getResources().getString(R.string.vpspo2h_spo2h));
                break;
            case R.id.block_heart:  //心脏负荷
                startToSpo2Detail("1", getResources().getString(R.string.vpspo2h_toptitle_heart));
                break;
            case R.id.block_sleep:      //睡眠活动
                startToSpo2Detail("2", getResources().getString(R.string.vpspo2h_toptitle_sleep));
                break;
            case R.id.block_breath:     //呼吸率
                startToSpo2Detail("3", getResources().getString(R.string.vpspo2h_toptitle_breath));
                break;
            case R.id.block_lowspo2h:   //低氧时间
                startToSpo2Detail("4", getResources().getString(R.string.vpspo2h_toptitle_lowspo2h));
                break;
        }
    }

    //跳转至详情页面
    private void startToSpo2Detail(String tag, String titleTxt) {
        Intent intent = new Intent(getActivity(), ShowSpo2DetailActivity.class);
        intent.putExtra("spo2_tag", tag);
        intent.putExtra("title", titleTxt);
        intent.putExtra(Constant.DETAIL_DATE, WatchUtils.obtainFormatDate(currDay));
        getActivity().startActivity(intent);
    }


    /**
     * 显示电量
     *
     * @param batteryLevel
     */
    void showBatterStute(int batteryLevel) {
        try {
//            if (batteryTopImg == null) return;
//            if (batteryLevel >= 0 && batteryLevel == 1) {
//                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_two));
//            } else if (batteryLevel == 2) {
//                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_three));
//            } else if (batteryLevel == 3) {
//                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_four));
//            } else if (batteryLevel == 4) {
//                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_five));
//            }
//            if (batteryPowerTv != null) batteryPowerTv.setText("" + batteryLevel * 25 + "%");
            if (batteryTopImg == null) return;
            if (batteryLevel >= 0 && batteryLevel < 25) {
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_two));
            } else if (batteryLevel >= 25 && batteryLevel < 50) {
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_three));
            } else if (batteryLevel >= 50 && batteryLevel < 75) {
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_four));
            } else if (batteryLevel >= 75 && batteryLevel <= 100) {
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_five));
            }
            if (batteryPowerTv != null) batteryPowerTv.setText("" + batteryLevel + "%");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @BindView(R.id.homeTodayImg)
    ImageView homeTodayImg;
    @BindView(R.id.homeYestdayImg)
    ImageView homeYestdayImg;
    @BindView(R.id.homeBeYestdayImg)
    ImageView homeBeYestdayImg;

    /**
     * 当前显示哪天的数据(0_今天 1_昨天 2_前天)
     */
    private void clearDataStyle(final int code) {
        if (code == currDay) return;// 防重复点击
        if (homeTodayImg != null) homeTodayImg.setVisibility(View.INVISIBLE);
        if (homeYestdayImg != null) homeYestdayImg.setVisibility(View.INVISIBLE);
        if (homeBeYestdayImg != null) homeBeYestdayImg.setVisibility(View.INVISIBLE);
        switch (code) {
            case 0: //今天
                if (homeTodayImg != null) homeTodayImg.setVisibility(View.VISIBLE);
                break;
            case 1: //昨天
                if (homeYestdayImg != null) homeYestdayImg.setVisibility(View.VISIBLE);
                break;
            case 2: //前天
                if (homeBeYestdayImg != null) homeBeYestdayImg.setVisibility(View.VISIBLE);
                break;
        }
        currDay = code;
        /**
         * 在设置设置改变建厅里面获取数据是的方法的时间
         * sycnDataListenterStep
         * sycnDataListenterSleep
         * sycnDataListenterHeart
         */
        String date = WatchUtils.obtainFormatDate(currDay);
//        sycnDataListenterStep.setDateStr(date);
//        sycnDataListenterSleep.setDateStr(date);
//        sycnDataListenterHeart.setDateStr(date);
        //handler.sendEmptyMessageDelayed(0x11, 200);

        mHandler.sendEmptyMessage(0x00);
    }


    /**
     * 第二步
     * 链接成功了读取设备数据
     */
    void getBleDevicesDatas() {
        if (MyCommandManager.DEVICENAME == null) {
            if (b30HomeSwipeRefreshLayout != null) b30HomeSwipeRefreshLayout.finishRefresh();
            return;
        }
        try {
            //保存的时间
            SharedPreferencesUtils.setParam(MyApp.getContext(), "saveDate", (System.currentTimeMillis() / 1000) + "");
            mHandler.sendEmptyMessage(0x03);//获取步数
        } catch (Error e) {
        }
    }

//
//    //计算步数时用
//    String[] timeString = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
//            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
//

    //开始上传本地缓存的数据
    private void startUploadDBService() {
        CommDBManager.getCommDBManager().startUploadDbService(MyApp.getContext());
    }


    //运动图表最大步数
    @BindView(R.id.b30SportMaxNumTv)
    TextView b30SportMaxNumTv;
    //运动图表
    @BindView(R.id.b30BarChart)
    BarChart b30BarChart;
    @BindView(R.id.b30StartEndTimeTv)
    TextView b30StartEndTimeTv;

    //睡眠图表
    @BindView(R.id.b30CusSleepView)
    B15PCusSleepView b30CusSleepView;

    //心率图标
    @BindView(R.id.b30HomeHeartChart)
    B30CusHeartView b30CusHeartView;
    //最后一次时间
    @BindView(R.id.lastTimeTv)
    TextView lastTimeTv;
    //心率值
    @BindView(R.id.b30HeartValueTv)
    TextView b30HeartValueTv;

    //步数数据
    List<BarEntry> b30ChartList;
    //用于计算最大步数
    private List<Integer> tmpIntegerList;
    //设置步数图表的临时数据
    private List<BarEntry> tmpB30StepList;
    /**
     * 展示睡眠图表
     */
    //展示睡眠数据的集合
    private List<Integer> sleepList;

    //展示心率数据的集合
    List<Integer> heartList;
    List<Integer> heartListNew;
    //    int ALLTIME = 0;//睡眠总时间
    int AWAKE = 0;//清醒的次数
    //    int DEEP = 0;//深睡
//    int SHALLOW = 0;//浅睡
    String startSleepTime = "00:00";//入睡时间
    String endSleepTime = "00:00";//起床时间


    /**
     * 展示步数的图表，计算数据
     */
    private void showSportStepData(List<Integer> sportData) {


        if (getActivity() == null || getActivity().isFinishing()) return;
        if (b30ChartList != null) b30ChartList.clear();
        if (tmpIntegerList != null) tmpIntegerList.clear();
        if (tmpB30StepList != null) tmpB30StepList.clear();

        if (sportData != null && !sportData.isEmpty()) {
            List<Map<String, Integer>> listMap = new ArrayList<>();
            int k = 0;
            for (int i = 0; i < 24; i++) {
                Map<String, Integer> map = new HashMap<>();
                int time = i * 60;
                map.put("time", time);
                map.put("val", sportData.get(i));
                listMap.add(map);
            }

//            Log.e(TAG, "  " + listMap.toString());
            for (int i = 0; i < listMap.size(); i++) {
                Map<String, Integer> tmpMap = listMap.get(i);
                if (tmpB30StepList != null) tmpB30StepList.add(new BarEntry(i, tmpMap.get("val")));
                if (tmpIntegerList != null) tmpIntegerList.add(tmpMap.get("val"));
            }
            if (b30ChartList != null) b30ChartList.addAll(tmpB30StepList);
            if (b30SportMaxNumTv != null)
                b30SportMaxNumTv.setText(Collections.max(tmpIntegerList) + getResources().getString(R.string.steps));
            initBarChart(b30ChartList);
            if (b30BarChart != null) b30BarChart.invalidate();
        } else {
            initBarChart(b30ChartList);
            if (b30BarChart != null) {
                b30BarChart.setNoDataTextColor(Color.WHITE);
                b30BarChart.invalidate();
            }
        }
    }

    //步数图表展示
    @SuppressWarnings("deprecation")
    private void initBarChart(List<BarEntry> pointbar) {
        BarDataSet barDataSet = new BarDataSet(pointbar, "");
        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
        //barDataSet.setColor(Color.parseColor("#fa8072"));//设置第一组数据颜色
        barDataSet.setColor(Color.parseColor("#88d785"));//设置第一组数据颜色

        if (b30BarChart == null) return;
        Legend mLegend = b30BarChart.getLegend();
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(15.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
        mLegend.setEnabled(false);

        ArrayList<IBarDataSet> threeBarData = new ArrayList<>();//IBarDataSet 接口很关键，是添加多组数据的关键结构，LineChart也是可以采用对应的接口类，也可以添加多组数据
        threeBarData.add(barDataSet);

        BarData bardata = new BarData(threeBarData);
        bardata.setBarWidth(0.5f);  //设置柱子宽度

        b30BarChart.setData(bardata);
        b30BarChart.setDoubleTapToZoomEnabled(false);   //双击缩放
        b30BarChart.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);//设置注解的位置在左上方
        b30BarChart.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状

        b30BarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        b30BarChart.getXAxis().setDrawGridLines(false);//不显示网格
        b30BarChart.getXAxis().setEnabled(false);
        b30BarChart.setPinchZoom(false);
        b30BarChart.setScaleEnabled(false);
        b30BarChart.setTouchEnabled(false);

        b30BarChart.getDescription().setEnabled(false);

        b30BarChart.getAxisRight().setEnabled(false);//右侧不显示Y轴
        b30BarChart.getAxisLeft().setAxisMinValue(0.8f);//设置Y轴显示最小值，不然0下面会有空隙
        b30BarChart.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
        b30BarChart.getAxisLeft().setEnabled(false);

        b30BarChart.getXAxis().setSpaceMax(0.5f);
        b30BarChart.animateXY(1000, 2000);//设置动画
    }


    /**
     * 展示睡眠图表
     */
    private void showSleepData(String sleepLin, String dateStr) {
        if (getActivity() == null || getActivity().isFinishing()) return;
        //图标绘制时    浅睡  0    深睡 1   清醒 2
        int xing = WatchUtils.countStr(sleepLin, '2');
        int qian = WatchUtils.countStr(sleepLin, '0');
        int shen = WatchUtils.countStr(sleepLin, '1');
        int allSleep = shen + qian;
        if (b30StartEndTimeTv != null)
            b30StartEndTimeTv.setText(startSleepTime + "-" + endSleepTime);
//        Log.e(TAG, "===  " + xing + "  " + qian + "   " + shen + "  " + allSleep);
        DecimalFormat formater = new DecimalFormat("#0.0");

        formater.setRoundingMode(RoundingMode.HALF_UP);
        String S = formater.format((double) shen / 60.0);


        formater.setRoundingMode(RoundingMode.FLOOR);
        String Q = formater.format((double) qian / 60.0);
        String Z = formater.format((double) allSleep / 60.0);

        Log.e(TAG,
                "睡眠段总时长： " + sleepLin.length() + "分钟 == " + formater.format((double) sleepLin.length() / 60.0) +
                        "小时  实际睡眠时长： " + allSleep + "分钟 == " + Z +
                        "小时  深睡： " + shen + "分钟 == " + S +
                        "小时  浅睡： " + qian + "分钟 == " + Q +
                        "小时  清醒次数： " + AWAKE + " 次");

        //保存睡眠数据
        //SleepData sleepData = mp.getValue();
        //清醒时长=总的睡眠时长-深睡时长-清醒时长
        //int soberlen = sleepData.getAllSleepTime() - sleepData.getDeepSleepTime() - sleepData.getLowSleepTime();
        /**
         * 保存睡眠数据
         *
         * @param bleName   蓝牙名字
         * @param bleMac    蓝牙mac地址
         * @param dateStr   日期
         * @param deep      深睡时长
         * @param low       浅睡时长
         * @param sober     清醒时长
         * @param allSleep  所有睡眠时间
         * @param sleeptime 入睡时间
         * @param waketime  清醒时间
         * @param wakeCount 清醒次数
         */
        CommDBManager.getCommDBManager().saveCommSleepDbData((WatchUtils.isEmpty(L4M.GetConnecteddName()) ? "B15P" : L4M.GetConnecteddName()),
                WatchUtils.getSherpBleMac(MyApp.getContext()),
                dateStr,
                shen,
                qian,
                xing,
                allSleep + xing,
                startSleepTime,
                endSleepTime,
                AWAKE);
//      Log.e(TAG, "====" + AWAKE + "   " + SHALLOW + "  " + DEEP);


        sleepList.clear();
        if (!WatchUtils.isEmpty(sleepLin)) {
            if (WatchUtils.isEmpty(sleepLin) || sleepLin.length() < 2) {
                if (b30CusSleepView != null) b30CusSleepView.setSleepList(new ArrayList<Integer>());
                return;
            }
            for (int i = 0; i < sleepLin.length(); i++) {
                if (i <= sleepLin.length() - 1) {
                    int subStr = Integer.valueOf(sleepLin.substring(i, i + 1));
                    sleepList.add(subStr);
                }
            }
            sleepList.add(0, 2);
            sleepList.add(0);
            sleepList.add(2);
        } else {
            if (b30StartEndTimeTv != null) b30StartEndTimeTv.setText("");
        }
        if (sleepList != null && !sleepList.isEmpty()) {
            if (b30CusSleepView != null) {
                b30CusSleepView.setSeekBarShow(false);
                b30CusSleepView.setSleepList(sleepList);
            }
        } else {
            if (b30CusSleepView != null) {
                b30CusSleepView.setSeekBarShow(false);
                b30CusSleepView.setSleepList(new ArrayList<Integer>());
            }
        }
    }


    //显示HRV的数据
    private void showHrvData(List<HRVOriginData> dataList) {
        //Log.e(TAG,"----显示HRV="+dataList.size());
        if(dataList.size()>420)
            return;
        List<HRVOriginData> data0to8 = getMoringData(dataList);
        HRVOriginUtil mHrvOriginUtil = new HRVOriginUtil(data0to8);
        HrvScoreUtil hrvScoreUtil = new HrvScoreUtil();
        int heartSocre = hrvScoreUtil.getSocre(dataList);
        hrvHeartSocreTv.setText(getResources().getString(R.string.heart_health_sorce) + "\n" + heartSocre);
        final List<Map<String, Float>> tenMinuteData = mHrvOriginUtil.getTenMinuteData();
        //主界面
        showHomeView(tenMinuteData);
    }

    //显示HRV的数据
    private void showHomeView(List<Map<String, Float>> tenMinuteData) {

        //Log.e(TAG,"-----HRV-SPO  "+tenMinuteData.toString());
        ChartViewUtil chartViewUtilHome = new ChartViewUtil(b31HomeHrvChart, null, true,
                CHART_MAX_HRV, CHART_MIN_HRV, "No Data", TYPE_HRV);
        b31HomeHrvChart.getAxisLeft().removeAllLimitLines();
        b31HomeHrvChart.getAxisLeft().setDrawLabels(false);
        chartViewUtilHome.setxColor(R.color.head_text);
        chartViewUtilHome.setNoDataColor(R.color.head_text);
        chartViewUtilHome.drawYLable(false, 1);
        chartViewUtilHome.updateChartView(tenMinuteData);
        LineData data = b31HomeHrvChart.getData();
        if (data == null)
            return;
        LineDataSet dataSetByIndex = (LineDataSet) data.getDataSetByIndex(0);
        if (dataSetByIndex != null) {
            dataSetByIndex.setDrawFilled(false);
            dataSetByIndex.setColor(Color.parseColor("#EC1A3B"));
        }
    }







    //显示血氧的图
    private void updateSpo2View(List<Spo2hOriginData> dataList) {

        //Log.e(TAG, "----------血氧展示=" + dataList.size());

        List<Spo2hOriginData> data0To8 = getSpo2MoringData(dataList);
        Spo2hOriginUtil spo2hOriginUtil = new Spo2hOriginUtil(data0To8);
        //获取处理完的血氧数据
        final List<Map<String, Float>> tenMinuteDataBreathBreak = spo2hOriginUtil.getTenMinuteData(TYPE_BEATH_BREAK);
        final List<Map<String, Float>> tenMinuteDataSpo2h = spo2hOriginUtil.getTenMinuteData(TYPE_SPO2H);

        //Log.e(TAG, "-----HRV-SPO  A  " + tenMinuteDataBreathBreak.toString());
        //Log.e(TAG, "-----HRV-SPO  B  " + tenMinuteDataSpo2h.toString());
        //平均值
        int onedayDataArr[] = spo2hOriginUtil.getOnedayDataArr(ESpo2hDataType.TYPE_SPO2H);
        if (getActivity() == null)
            return;
        b31Spo2AveTv.setText(getResources().getString(R.string.ave_value) + "\n" + onedayDataArr[2]);
        initSpo2hUtil();
        if (vpSpo2hUtil != null) {
            vpSpo2hUtil.setData(dataList);
            vpSpo2hUtil.showAllChartView();
        }

        ChartViewUtil spo2ChartViewUtilHomes = new ChartViewUtil(homeSpo2LinChartView, null, true,
                CHART_MAX_SPO2H, CHART_MIN_SPO2H, "No Data", TYPE_SPO2H);
        spo2ChartViewUtilHomes.setxColor(R.color.head_text);
        spo2ChartViewUtilHomes.setNoDataColor(R.color.head_text);
        //更新血氧数据的图表
        spo2ChartViewUtilHomes.setBeathBreakData(tenMinuteDataBreathBreak);
        spo2ChartViewUtilHomes.updateChartView(tenMinuteDataSpo2h);
        spo2ChartViewUtilHomes.setBeathBreakData(tenMinuteDataBreathBreak);

        homeSpo2LinChartView.getAxisLeft().removeAllLimitLines();
        homeSpo2LinChartView.getAxisLeft().setDrawLabels(false);

        LineData data = homeSpo2LinChartView.getData();
        if (data == null)
            return;
        LineDataSet dataSetByIndex = (LineDataSet) data.getDataSetByIndex(0);
        if (dataSetByIndex != null) {
            dataSetByIndex.setDrawFilled(false);
            dataSetByIndex.setColor(Color.parseColor("#17AAE2"));
        }
    }


    /**
     * 获取0点-8点之间的数据
     *
     * @param originSpo2hList
     * @return
     */
    @NonNull
    private List<HRVOriginData> getMoringData(List<HRVOriginData> originSpo2hList) {
        List<HRVOriginData> moringData = new ArrayList<>();
        try{
            if (originSpo2hList == null || originSpo2hList.isEmpty())
                return moringData;
            for (HRVOriginData hRVOriginData : originSpo2hList) {
                if (hRVOriginData.getmTime().getHMValue() < 8 * 60) {
                    moringData.add(hRVOriginData);
                }
            }
            return moringData;
        }catch (Exception e){
            e.printStackTrace();
            moringData.clear();
            return moringData;
        }


    }

    /**
     * 获取0点-8点之间的数据
     *
     * @param originSpo2hList
     * @return
     */
    @NonNull
    private List<Spo2hOriginData> getSpo2MoringData(List<Spo2hOriginData> originSpo2hList) {
        List<Spo2hOriginData> spo2Data = new ArrayList<>();
        try {
            if (originSpo2hList == null || originSpo2hList.isEmpty())
                return spo2Data;
            for (Spo2hOriginData spo2hOriginData : originSpo2hList) {
                if (spo2hOriginData != null && spo2hOriginData.getmTime() != null) {
                    //Log.e(TAG, "----assa---  " + spo2hOriginData.getmTime().getHMValue());
                    if (spo2hOriginData.getmTime().getHMValue() < 8 * 60) {
                        spo2Data.add(spo2hOriginData);
                    }
                }
            }
            return spo2Data;
        } catch (Exception e) {
            e.printStackTrace();
            return spo2Data;
        }

    }


    /**
     * 设置相关属性
     */
    private void initSpo2hUtil() {
        vpSpo2hUtil = new VpSpo2hUtil();
        vpSpo2hUtil.setLinearChart(mChartViewSpo2h, mChartViewHeart,
                mChartViewSleep, mChartViewBreath, mChartViewLowspo2h);
        vpSpo2hUtil.setMarkView(MyApp.getContext(), R.layout.vpspo2h_markerview);
        vpSpo2hUtil.setModelIs24(false);
    }
}
