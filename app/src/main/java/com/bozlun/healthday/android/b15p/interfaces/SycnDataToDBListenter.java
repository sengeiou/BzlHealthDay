package com.bozlun.healthday.android.b15p.interfaces;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bozlun.healthday.android.Commont;
import com.bozlun.healthday.android.MyApp;
import com.bozlun.healthday.android.b15p.b15pdb.B15PDBCommont;
import com.bozlun.healthday.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.tjdL4.tjdmain.L4M;

import java.util.Arrays;

public class SycnDataToDBListenter extends L4M.BTResultToDBListenr {
    private static final String TAG = "SycnDataToDBListenter";
    private static SycnDataToDBListenter sycnDataToDBListenter;
    private B15PDBCommont b15PDBCommont = null;//b15P数据库工具
    /**
     * 用于过滤数据，只拿去三天的数据
     * 0_今天 1_昨天 2_前天
     */
    private String t = WatchUtils.obtainFormatDate(0);
    private String y = WatchUtils.obtainFormatDate(1);
    private String q = WatchUtils.obtainFormatDate(2);
    static String mac = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);

    private SycnDataToDBListenter() {
        this.b15PDBCommont = B15PDBCommont.getInstance();
        mac = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);
    }

    public static SycnDataToDBListenter getSycnDataToDBListenter() {
        sycnDataToDBListenter = null;
//        if (sycnDataToDBListenter == null) {
        synchronized (SycnDataToDBListenter.class) {
            if (sycnDataToDBListenter == null) {
                sycnDataToDBListenter = new SycnDataToDBListenter();
            }

//            }
        }
        return sycnDataToDBListenter;
    }


    @Override
    public void On_Result(String typeInfo, String strData, Object dataObj) {
//        Log.e(TAG, "原始数据  ==TypeInfo:" + typeInfo + "  StrData:" + strData);
//
//        try {
//            //接收健康数据后 处理....
//            //TypeInfo 类型
//            //HEART_NOW         --当前测量的心率值
//            //BLDPRESS_NOW      --当前测量的血压值
//            //HEART_HISTORY     --历史测量的心率值
//            //BLDPRESS_HISTORY  --历史测量的血压值
//
//            //PEDO_DAY          --天记步数据
//            //PEDO_TIME_HISTORY --时间段记步数据
//
//            //SLEEP_DAY         --天睡眠数据
//            //SLEEP_TIME_HISTORY--天睡眠时间段数
//            if (!WatchUtils.isEmpty(mac)) {
//                switch (typeInfo) {
//                    case "HEART_NOW"://当前测量的心率值
//                        Log.e(TAG, "单词检测心率保存");
//                        if (!WatchUtils.isEmpty(mac) && !WatchUtils.isEmpty(strData.substring(2, strData.length() - 1)))
//                            b15PDBCommont.saveHeartToDB(mac, WatchUtils.getCurrentDate1(), Integer.valueOf(strData.substring(2, strData.length() - 1)));
//                        else b15PDBCommont.saveHeartToDB(mac, WatchUtils.getCurrentDate1(), 0);
//                        break;
//                    case "BLDPRESS_NOW"://当前测量的血压值
//                        //StrData:[,115,83]
//                        Log.e(TAG, "单词检测血压保存");
//                        String[] splitBloopNew = strData.substring(2, strData.length() - 1).split(",");
//                        if (!WatchUtils.isEmpty(mac) && !WatchUtils.isEmpty(strData.substring(2, strData.length() - 1))) {
//                            //保存到历史表库
//                            b15PDBCommont.saveBloopToDB(mac,
//                                    WatchUtils.getCurrentDate1(),
//                                    Integer.valueOf(splitBloopNew[0]), Integer.valueOf(splitBloopNew[1]));
//                            //保存单条
//                            b15PDBCommont.saveTestBloopToDB(mac, Integer.valueOf(splitBloopNew[0]), Integer.valueOf(splitBloopNew[1]));
//                        } else {
//                            b15PDBCommont.saveBloopToDB(mac, WatchUtils.getCurrentDate1(), 0, 0);
//                            b15PDBCommont.saveTestBloopToDB(mac, 0, 0);
//                        }
//                        break;
//                    case "HEART_HISTORY"://历史测量的心率值
//                        String[] hearts = {"heart", strData};
//                        new SaveDatas().execute(hearts);
//                        break;
//                    case "BLDPRESS_HISTORY"://历史测量的血压值
//                        String[] bloops = {"bloop", strData};
//                        new SaveDatas().execute(bloops);
//                        break;
//                    case "PEDO_DAY"://天记步数据 汇总
//                        //TypeInfo:PEDO_DAY  StrData:[,2019-05-13,164]
//                        String[] all_step = {"all_step", strData};
//                        new SaveDatas().execute(all_step);
//                        break;
//                    case "PEDO_TIME_HISTORY"://时间段记步数据
//                        String[] steps = {"step", strData};
//                        new SaveDatas().execute(steps);
//                        break;
//                    case "SLEEP_DAY"://天睡眠数据
//
//                        break;
//                    case "SLEEP_TIME_HISTORY"://天睡眠时间段数
//                        String[] sleeps = {"sleep", strData};
//                        new SaveDatas().execute(sleeps);
//
//                        break;
//                }
//            }
//        } catch (Error e) {
//        }


    }

    @Override
    public void On_ProgressResult(String typeInfo, int datTotal, int datIdx, String strData, Object DataObj) {
        Log.e(TAG, "原始数据A  ==TypeInfo:" + typeInfo + "  StrData:" + strData + "  datTotal：" + datTotal + "  datIdx:" + datIdx + "   DataObj:" + DataObj);

        try {
            //接收健康数据后 处理....
            //TypeInfo 类型
            //HEART_NOW         --当前测量的心率值
            //BLDPRESS_NOW      --当前测量的血压值
            //HEART_HISTORY     --历史测量的心率值
            //BLDPRESS_HISTORY  --历史测量的血压值

            //PEDO_DAY          --天记步数据
            //PEDO_TIME_HISTORY --时间段记步数据

            //SLEEP_DAY         --天睡眠数据
            //SLEEP_TIME_HISTORY--天睡眠时间段数
            if (!WatchUtils.isEmpty(mac)) {
                switch (typeInfo) {
                    case "HEART_NOW"://当前测量的心率值
                        Log.e(TAG, "单词检测心率保存");
                        if (!WatchUtils.isEmpty(mac) && !WatchUtils.isEmpty(strData.substring(2, strData.length() - 1)))
                            b15PDBCommont.saveHeartToDB(mac, WatchUtils.getCurrentDate1(), Integer.valueOf(strData.substring(2, strData.length() - 1)));
                        else b15PDBCommont.saveHeartToDB(mac, WatchUtils.getCurrentDate1(), 0);
                        break;
                    case "BLDPRESS_NOW"://当前测量的血压值
                        //StrData:[,115,83]
                        Log.e(TAG, "单词检测血压保存");
                        String[] splitBloopNew = strData.substring(2, strData.length() - 1).split(",");
                        if (!WatchUtils.isEmpty(mac) && !WatchUtils.isEmpty(strData.substring(2, strData.length() - 1))) {
                            //保存到历史表库
                            b15PDBCommont.saveBloopToDB(mac,
                                    WatchUtils.getCurrentDate1(),
                                    Integer.valueOf(splitBloopNew[0]), Integer.valueOf(splitBloopNew[1]));
                            //保存单条
                            b15PDBCommont.saveTestBloopToDB(mac, Integer.valueOf(splitBloopNew[0]), Integer.valueOf(splitBloopNew[1]));
                        } else {
                            b15PDBCommont.saveBloopToDB(mac, WatchUtils.getCurrentDate1(), 0, 0);
                            b15PDBCommont.saveTestBloopToDB(mac, 0, 0);
                        }
                        break;
                    case "HEART_HISTORY_111"://case "HEART_HISTORY_111"://历史测量的心率值
                        String[] hearts = {"heart", strData};
                        new SaveDatas().execute(hearts);
                        break;
                    case "BLDPRESS_HISTORY"://历史测量的血压值
                        String[] bloops = {"bloop", strData};
                        new SaveDatas().execute(bloops);
                        break;
                    case "PEDO_DAY"://天记步数据 汇总
                        //TypeInfo:PEDO_DAY  StrData:[,2019-05-13,164]
                        String[] all_step = {"all_step", strData};
                        new SaveDatas().execute(all_step);
                        break;
                    case "PEDO_TIME_HISTORY"://时间段记步数据
                        String[] steps = {"step", strData};
                        new SaveDatas().execute(steps);
                        break;
                    case "SLEEP_DAY"://天睡眠数据

                        break;
                    case "SLEEP_TIME_HISTORY"://天睡眠时间段数
                        String[] sleeps = {"sleep", strData};
                        new SaveDatas().execute(sleeps);

                        break;
                }
            }
        } catch (Error e) {
        }
    }


    @SuppressLint("StaticFieldLeak")
    class SaveDatas extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String strDataType = strings[0];
            switch (strDataType) {
                case "all_step"://汇总步数
                    String strDataAllStep = strings[1];
                    //TypeInfo:PEDO_DAY  StrData:[,2019-05-13,164]
                    String[] splitAllStep = strDataAllStep.substring(2, strDataAllStep.length() - 1).split(",");

//                    if (splitAllStep[0].substring(0, 10).equals(t)
//                            || splitAllStep[0].substring(0, 10).equals(y)
//                            || splitAllStep[0].substring(0, 10).equals(q)
//                            ) {

                       // Log.e(TAG, "线程中保存汇总步数" + splitAllStep[0] + "   " + Integer.valueOf(splitAllStep[1]));
                        //保存详细步数数据------(L4M.Get_Connect_flag() == 2说明设备已经连接
                        if (!WatchUtils.isEmpty(mac)
//                                && !splitStep[0].substring(11, 19).equals("00:00:00")
                                && (L4M.Get_Connect_flag() == 2))
                            b15PDBCommont.saveAllStepToDB(mac, splitAllStep[0], Integer.valueOf(splitAllStep[1]));
//                    }
                    break;
                case "step":
                    String strDataStep = strings[1];
                    //[,2019-04-24 13:30:00,337]
                    String[] splitStep = strDataStep.substring(2, strDataStep.length() - 1).split(",");
                    //Log.e(TAG, "=保存前看时间===" + Arrays.toString(splitStep) + "今天 " + t + " 昨天 " + y + " 前天 " + q);
                    if (splitStep[0].substring(0, 10).equals(t)
                            || splitStep[0].substring(0, 10).equals(y)
                            || splitStep[0].substring(0, 10).equals(q)) {

                        //Log.e(TAG, "线程中保存步数" + splitStep[0] + "   " + Integer.valueOf(splitStep[1]));
                        //保存详细步数数据------(L4M.Get_Connect_flag() == 2说明设备已经连接
                        if (!WatchUtils.isEmpty(mac)
//                                && !splitStep[0].substring(11, 19).equals("00:00:00")
                                && (L4M.Get_Connect_flag() == 2))
                            b15PDBCommont.saveStepToDB(mac, splitStep[0], Integer.valueOf(splitStep[1]));
                    }
                    break;
                case "sleep":
                    String strDataSleep = strings[1];
                    String[] splitSleep = strDataSleep.substring(2, strDataSleep.length() - 1).split(",");
                    //Log.e(TAG, " 睡眠数据保存之前  " + strDataSleep + "   " + splitSleep[0] + "  " + t + " " + y + " " + q + "  " + WatchUtils.obtainAroundDate(q, true));
//                    if (splitSleep[0].substring(0, 10).equals(t)
//                            || splitSleep[0].substring(0, 10).equals(y)
//                            || splitSleep[0].substring(0, 10).equals(q)
//                            || splitSleep[0].substring(0, 10).equals(WatchUtils.obtainAroundDate(q, true))) {

                        //Log.e(TAG, "线程中保存睡眠" + splitSleep[0] + "   " + splitSleep[1]);
                        //保存详细睡眠数据------(L4M.Get_Connect_flag() == 2说明设备已经连接
                        if (!WatchUtils.isEmpty(mac)
                                && (L4M.Get_Connect_flag() == 2))
                            b15PDBCommont.saveSleepToDB(mac, splitSleep[0], splitSleep[1]);
//                    }
                    break;
                case "heart":
                    String strDataHeart = strings[1];
                    String[] splitHeart = strDataHeart.substring(2, strDataHeart.length() - 1).split(",");
//                    if (splitHeart[0].substring(0, 10).equals(t)
//                            || splitHeart[0].substring(0, 10).equals(y)
//                            || splitHeart[0].substring(0, 10).equals(q)) {

                        //Log.e(TAG, "线程中保存心率" + splitHeart[0] + "   " + Integer.valueOf(splitHeart[1]));
                        //保存详细步数数据------(L4M.Get_Connect_flag() == 2说明设备已经连接
                        if (!WatchUtils.isEmpty(mac)
                                && (L4M.Get_Connect_flag() == 2))
                            b15PDBCommont.saveHeartToDB(mac, splitHeart[0], Integer.valueOf(splitHeart[1]));
//                    }
                    break;
                case "bloop":
                    String strDataBloop = strings[1];
                    //[,2019-04-17 09:36:30,129,89]------(L4M.Get_Connect_flag() == 2说明设备已经连接
                    String[] splitBloop = strDataBloop.substring(2, strDataBloop.length() - 1).split(",");
//                    if (splitBloop[0].substring(0, 10).equals(t)
//                            || splitBloop[0].substring(0, 10).equals(y)
//                            || splitBloop[0].substring(0, 10).equals(q)) {

                        //Log.e(TAG, "线程中保存血压" + splitBloop[0] + "   " + Integer.valueOf(splitBloop[1]) + "  " + Integer.valueOf(splitBloop[2]));
                        if (!WatchUtils.isEmpty(mac)
                                && (L4M.Get_Connect_flag() == 2))
                            b15PDBCommont.saveBloopToDB(mac, splitBloop[0],
                                    Integer.valueOf(splitBloop[1]),
                                    Integer.valueOf(splitBloop[2]));
//                    }
                    break;
            }

            return null;
        }
    }
}
