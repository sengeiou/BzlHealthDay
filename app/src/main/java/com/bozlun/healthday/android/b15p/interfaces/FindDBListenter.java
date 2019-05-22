package com.bozlun.healthday.android.b15p.interfaces;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.bozlun.healthday.android.Commont;
import com.bozlun.healthday.android.LogTestUtil;
import com.bozlun.healthday.android.MyApp;
import com.bozlun.healthday.android.activity.MyPersonalActivity;
import com.bozlun.healthday.android.b15p.b15pdb.B15PAllStepDB;
import com.bozlun.healthday.android.b15p.b15pdb.B15PDBCommont;
import com.bozlun.healthday.android.b15p.b15pdb.B15PHeartDB;
import com.bozlun.healthday.android.b15p.b15pdb.B15PSleepDB;
import com.bozlun.healthday.android.b15p.b15pdb.B15PStepDB;
import com.bozlun.healthday.android.b31.model.B31HRVBean;
import com.bozlun.healthday.android.b31.model.B31Spo2hBean;
import com.bozlun.healthday.android.commdbserver.CommDBManager;
import com.bozlun.healthday.android.siswatch.utils.WatchUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30S_SleepDataItem;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.tjdL4.tjdmain.L4M;
import com.veepoo.protocol.model.datas.HRVOriginData;
import com.veepoo.protocol.model.datas.Spo2hOriginData;
import com.veepoo.protocol.model.datas.TimeData;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FindDBListenter extends AsyncTask<String, Void, String> {
    private static final String TAG = "FindDBListenter";
    private ChangeDBListenter changeDBListenter;

    public FindDBListenter(ChangeDBListenter changeDBListenter) {
        this.changeDBListenter = changeDBListenter;
    }

    String[] timeString = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};

    String[] timeString2 = {"00:00", "00:30", "01:00", "01:30", "02:00", "02:30", "03:00", "03:30",
            "04:00", "04:30", "05:00", "05:30", "06:00", "06:30", "07:00", "07:30", "08:00", "08:30",
            "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",
            "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30",
            "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00", "23:30"};
    int defaultSteps = 0;

    @Override
    protected String doInBackground(String... strings) {
//        List<Object> allDataList = null;
        String jsonString = "";
        String type = strings[0];
        String mac = strings[1];
        String date = strings[2];
        if (!WatchUtils.isEmpty(type)
                && !WatchUtils.isEmpty(mac)
                && !WatchUtils.isEmpty(date)) {

            switch (type) {
                case "all_step":
                    List<B15PAllStepDB> allStepAllDatas = B15PDBCommont.getInstance().findAllStepAllDatas(mac, date);
                    List<Integer> allStepList = new ArrayList<>();
                    allStepList.add(0, 0);
                    if (allStepAllDatas != null && !allStepAllDatas.isEmpty()) {
                        allStepList.set(0, allStepAllDatas.get(0).getStepItemNumber());
                    }
                    jsonString = "FFF" + JSON.toJSON(allStepList).toString();
                    LogTestUtil.e(TAG, "==ALL_STEP  " + jsonString);
                    break;
                case "step":
                    List<B15PStepDB> allStepDatasList = (List<B15PStepDB>) B15PDBCommont.getInstance().findStepAllDatas(mac, date);
//                    Log.e(TAG, " --- 步数查询 --- " + (allStepDatasList == null ? "查询步数为空" : allStepDatasList.toString()));
                    List<Integer> allDataList = new ArrayList<>();
                    if (allStepDatasList != null && !allStepDatasList.isEmpty()) {
                        int hourStep = 0;
                        for (int i = 0; i < timeString.length; i++) {
                            hourStep = 0;
                            defaultSteps = 0;
                            for (int j = 0; j < allStepDatasList.size(); j++) {
                                B15PStepDB b15PStepDB = (B15PStepDB) allStepDatasList.get(j);
                                if (b15PStepDB != null) {
                                    String stepData = b15PStepDB.getStepData();
                                    String stepTime = b15PStepDB.getStepTime();
                                    //根据日期判断去当天的值
                                    if (stepData.substring(0, 10).equals(date)) {//例如：现在是   2019-04-25
                                        defaultSteps = defaultSteps + b15PStepDB.getStepItemNumber();
                                        if (timeString[i].equals(stepTime.substring(0, 2))) {
                                            hourStep += b15PStepDB.getStepItemNumber();
                                        }
                                        //保存总步数  数据面板的显示
                                        CommDBManager.getCommDBManager().saveCommCountStepDate((WatchUtils.isEmpty(L4M.GetConnecteddName()) ? "B15P" : L4M.GetConnecteddName()),
                                                mac,
                                                date,
                                                defaultSteps);
                                    }
                                }
                            }
                            allDataList.add(hourStep);
                        }
                    }
                    jsonString = "AAA" + JSON.toJSON(allDataList).toString();
                    //LogTestUtil.e(TAG, "==STEP  " + jsonString);

                    break;
                case "sleep":
                    List<B15PSleepDB> allSleepDatasList = (List<B15PSleepDB>) B15PDBCommont.getInstance().findSleepAllDatas(mac, date);
                    //LogTestUtil.e(TAG, " --- 睡眠查询 --- " + (allSleepDatasList == null ? "查询睡眠为空" : allSleepDatasList.toString()));
                    boolean sleepIsNull = B15PDBCommont.getInstance().findSleepIsNull(mac, date);

                    /**
                     * 判断当存在正常睡眠数据的时候就回执显示HRV和血氧
                     */
                    if (sleepIsNull){

                        /**
                         * 有睡眠书据-------设置血氧假数据
                         */
                        setSpo2Datas(mac, date);
                        /**
                         * 有睡眠书据-------设置HRV假数据
                         */
                        setHRvDatas(mac, date);
                    }
                    List<W30S_SleepDataItem> allDataListSleep = new ArrayList<>();
                    if (allSleepDatasList != null && !allSleepDatasList.isEmpty()) {

                        allDataListSleep.clear();
                        List<String> timesList = new ArrayList<>();
                        for (int i = 0; i < allSleepDatasList.size(); i++) {
                            B15PSleepDB b15PSleepDB = allSleepDatasList.get(i);
                            if (b15PSleepDB != null) {
                                String sleepData = b15PSleepDB.getSleepData();
                                String sleepTime = b15PSleepDB.getSleepTime();


                                if (sleepData.substring(0, 10).equals(date)
                                        || sleepData.substring(0, 10).equals(WatchUtils.obtainAroundDate(date, true))) {

                                    //Log.e(TAG, " 查询到的单个睡眠数据  " + sleepData + "   " + sleepTime + "  " + date);
                                    if ((sleepData.substring(0, 10).equals(date) && Integer.valueOf(sleepTime.substring(0, 2)) <= 16)
                                            || (sleepData.substring(0, 10).equals(WatchUtils.obtainAroundDate(date, true)) && Integer.valueOf(sleepTime.substring(0, 2)) >= 21)) {

                                        if (!timesList.contains(sleepTime)) {
                                            timesList.add(sleepTime);
                                            W30S_SleepDataItem w30S_sleepDataItem = new W30S_SleepDataItem();
                                            w30S_sleepDataItem.setStartTime(sleepTime);
                                            w30S_sleepDataItem.setSleep_type(b15PSleepDB.getSleepType());
                                            allDataListSleep.add(w30S_sleepDataItem);
                                        }
                                    }
                                }
//                                if ((sleepData.substring(0, 10).equals(date)
//                                        && Integer.valueOf(sleepTime.substring(0, 2)) <= 16)
//                                        || (sleepData.substring(0, 10).equals(WatchUtils.obtainAroundDate(date, true))
//                                        && Integer.valueOf(sleepTime.substring(0, 2)) >= 22)) {
//
//
//                                    W30S_SleepDataItem w30S_sleepDataItem = new W30S_SleepDataItem();
//                                    w30S_sleepDataItem.setStartTime(sleepTime);
//                                    w30S_sleepDataItem.setSleep_type(b15PSleepDB.getSleepType());
//                                    allDataListSleep.add(w30S_sleepDataItem);
//                                }
                            }
                        }
                    }

                    jsonString = "BBB" + JSON.toJSON(allDataListSleep).toString();
                    //LogTestUtil.e(TAG, "==SLEEP  " + jsonString);
                    break;
                case "heart":
                    List<B15PHeartDB> heartAllDatas = (List<B15PHeartDB>) B15PDBCommont.getInstance().findHeartAllDatas(mac, date);
                    //Log.e(TAG, " --- 心率查询 --- " + (heartAllDatas == null ? "查询心率为空" : heartAllDatas.toString()));
                    List<Integer> allDataListHeart = new ArrayList<>();
                    List<Integer> heartListNew = new ArrayList<>();
                    if (heartAllDatas != null && !heartAllDatas.isEmpty()) {
                        Map<String, Integer> heartMapFa = new HashMap<>();//用于保存每个小时
//                        Map<String, Integer> heartMap = new LinkedHashMap<>();
//                        /**
//                         * 设置长度为48的空的数据组
//                         */
//                        for (int i = 0; i < timeString.length; i++) {
//                            heartMap.put(timeString[i], 0);
//                        }
                        int allValues1 = 0;
                        int valueCount1 = 0;

                        int allValues2 = 0;
                        int valueCount2 = 0;


                        Map<String, Integer> heartMapHM = new LinkedHashMap<>();
                        Map<String, Integer> heartMapHMTime = new HashMap<>();//用于保存每个小时


                        /**
                         * 设置长度为48的空的数据组
                         */
                        for (int i = 0; i < timeString2.length; i++) {
                            heartMapHM.put(timeString2[i], 0);
                        }

                        for (int i = 0; i < heartAllDatas.size(); i++) {
                            B15PHeartDB b15PHeartDB = heartAllDatas.get(i);
                            if (b15PHeartDB != null) {
                                String heartData = b15PHeartDB.getHeartData();//2019-10-10
                                String heartTime = b15PHeartDB.getHeartTime();//12:10:10


                                if (date.equals(heartData.substring(0, 10))) {
                                    int heartNumber = b15PHeartDB.getHeartNumber();//心率值

                                    String[] split = heartTime.split("[:]");
                                    //Log.e(TAG, "===== heartTime " + heartTime + "   " +heartNumber);
                                    if (heartMapFa.containsKey(split[0])) {

                                        //判断---心率之取当天数据，获取数据（mac 时间）返回本身就是当天的，不用判断也可以
                                        if (heartMapHMTime.containsKey(split[0] + ":00")
                                                && (double) (Double.valueOf(split[1]) / 60.0) <= 0.5) {

                                            valueCount1++;//1小时测量的次数---用于计算前半小时内的平均数
                                            allValues1 = allValues1 + heartNumber;//前半小时内的所有数据累加
                                            heartMapHMTime.put(split[0] + ":00", 0);

                                            if (allValues1 > 0) {
                                                heartMapHM.put(split[0] + ":00", (int) allValues1 / valueCount1);
                                            } else {
                                                heartMapHM.put(split[0] + ":00", 0);
                                            }
                                            // Log.e(TAG, "===== 正点 " + split[0] + ":00   " + (int) allValues1 / valueCount1);

                                        } else if (heartMapHMTime.containsKey(split[0] + ":30")
                                                && (double) (Double.valueOf(split[1]) / 60.0) > 0.5) {

                                            valueCount2++;//1小时测量的次数---用于计算后半小时内的平均数
                                            allValues2 = allValues2 + heartNumber;//后半小时内的所有数据累加
                                            heartMapHMTime.put(split[0] + ":30", 0);

                                            if (allValues2 > 0) {
                                                heartMapHM.put(split[0] + ":30", (int) allValues2 / valueCount2);
                                            } else {
                                                heartMapHM.put(split[0] + ":30", 0);
                                            }
                                            //Log.e(TAG, "===== 半点 " + split[0] + ":30   " + (int) allValues2 / valueCount2);
                                        } else {
                                            //Log.e(TAG, "===== 不包含 2 " + split[0] + "  " + (double) (Double.valueOf(split[1]) / 60.0));
                                            if ((double) (Double.valueOf(split[1]) / 60.0) <= 0.5) {//分除60 小于等于0.5 那就是 前半小时
                                                heartMapHMTime.put(split[0] + ":00", 0);

                                                allValues1 = heartNumber;
                                                valueCount1 = 1;
                                                heartMapHM.put(split[0] + ":00", (int) allValues1 / valueCount1);

//                                                Log.e(TAG, "===== 添加正点 2 " + split[0] + ":00   " + (int) allValues1 / valueCount1);
                                            } else {//后半小时
                                                heartMapHMTime.put(split[0] + ":30", 0);

                                                allValues2 = heartNumber;
                                                valueCount2 = 1;
                                                heartMapHM.put(split[0] + ":30", (int) allValues2 / valueCount2);

//                                                Log.e(TAG, "===== 添加半点 2  " + split[0] + ":30   " + (int) allValues2 / valueCount2);
                                            }

                                        }


                                    } else {
                                        heartMapFa.put(split[0], 0);

                                        //Log.e(TAG, "===== 不包含 " + split[0] + "  " + (double) (Double.valueOf(split[1]) / 60.0));
                                        if ((double) (Double.valueOf(split[1]) / 60.0) <= 0.5) {//分除60 小于等于0.5 那就是 前半小时
                                            heartMapHMTime.put(split[0] + ":00", 0);

                                            allValues1 = heartNumber;
                                            valueCount1 = 1;
                                            heartMapHM.put(split[0] + ":00", (int) allValues1 / valueCount1);

//                                            Log.e(TAG, "===== 添加正点 " + split[0] + ":00   " + (int) allValues1 / valueCount1);
                                        } else {//后半小时
                                            heartMapHMTime.put(split[0] + ":30", 0);

                                            allValues2 = heartNumber;
                                            valueCount2 = 1;
                                            heartMapHM.put(split[0] + ":30", (int) allValues2 / valueCount2);

//                                            Log.e(TAG, "===== 添加半点 " + split[0] + ":30   " + (int) allValues2 / valueCount2);
                                        }
                                    }
                                }
//                                if (date.equals(heartData.substring(0, 10))) {
//                                    int heartNumber = b15PHeartDB.getHeartNumber();//心率值
//
//                                    String[] split = heartTime.split("[:]");
//
//
//                                    if (heartMapFa.containsKey(split[0])){
//
//
//                                        //判断---心率之取当天数据，获取数据（mac 时间）返回本身就是当天的，不用判断也可以
//                                        if (heartMapHMTime.containsKey(split[0] + ":00")) {
//
//                                            valueCount1++;//1小时测量的次数---用于计算前半小时内的平均数
//                                            allValues1 = allValues1 + heartNumber;//前半小时内的所有数据累加
//                                            heartMapHMTime.put(split[0] + ":00", 0);
//
//                                            if (allValues1 > 0) {
//                                                heartMapHM.put(split[0] + ":00", (int) allValues1 / valueCount1);
//                                            } else {
//                                                heartMapHM.put(split[0] + ":00", 0);
//                                            }
//                                            Log.e(TAG, "===== 正点 " + split[0] + ":00   " + (int) allValues1 / valueCount1);
//
//                                        } else if (heartMapHMTime.containsKey(split[0] + ":30")) {
//
//                                            valueCount2++;//1小时测量的次数---用于计算后半小时内的平均数
//                                            allValues2 = allValues2 + heartNumber;//后半小时内的所有数据累加
//                                            heartMapHMTime.put(split[0] + ":30", 0);
//
//                                            if (allValues2 > 0) {
//                                                heartMapHM.put(split[0] + ":30", (int) allValues2 / valueCount2);
//                                            } else {
//                                                heartMapHM.put(split[0] + ":30", 0);
//                                            }
//                                            Log.e(TAG, "===== 半点 " + split[0] + ":30   " + (int) allValues2 / valueCount2);
//                                        } else {
//                                            Log.e(TAG, "===== 不包含 " + split[0] + "  " + (double) (Double.valueOf(split[1]) / 60.0));
//                                            if ((double) (Double.valueOf(split[1]) / 60.0) <= 0.5) {//分除60 小于等于0.5 那就是 前半小时
//                                                heartMapHMTime.put(split[0] + ":00", 0);
//
//                                                allValues1 = heartNumber;
//                                                valueCount1 = 1;
//                                                heartMapHM.put(split[0] + ":00", (int) allValues1 / valueCount1);
//
//                                                Log.e(TAG, "===== 添加正点 " + split[0] + ":00   " + (int) allValues1 / valueCount1);
//                                            } else {//后半小时
//                                                heartMapHMTime.put(split[0] + ":30", 0);
//
//                                                allValues2 = heartNumber;
//                                                valueCount2 = 1;
//                                                heartMapHM.put(split[0] + ":30", (int) allValues2 / valueCount2);
//
//                                                Log.e(TAG, "===== 添加半点 " + split[0] + ":30   " + (int) allValues2 / valueCount2);
//                                            }
//
//                                        }
//
//
//
//                                    }else {
//                                        heartMapFa.put(split[0],0);
//
//                                        Log.e(TAG, "===== 不包含 " + split[0] + "  " + (double) (Double.valueOf(split[1]) / 60.0));
//                                        if ((double) (Double.valueOf(split[1]) / 60.0) <= 0.5) {//分除60 小于等于0.5 那就是 前半小时
//                                            heartMapHMTime.put(split[0] + ":00", 0);
//
//                                            allValues1 = heartNumber;
//                                            valueCount1 = 1;
//                                            heartMapHM.put(split[0] + ":00", (int) allValues1 / valueCount1);
//
//                                            Log.e(TAG, "===== 添加正点 " + split[0] + ":00   " + (int) allValues1 / valueCount1);
//                                        } else {//后半小时
//                                            heartMapHMTime.put(split[0] + ":30", 0);
//
//                                            allValues2 = heartNumber;
//                                            valueCount2 = 1;
//                                            heartMapHM.put(split[0] + ":30", (int) allValues2 / valueCount2);
//
//                                            Log.e(TAG, "===== 添加半点 " + split[0] + ":30   " + (int) allValues2 / valueCount2);
//                                        }
//                                    }
//
//
//
//                                }

                            }
                        }

                        //Log.e(TAG, "=AAS==SB=心率真实值设置完成 " + heartMapHM.size() + "   " + heartMapHM.toString());


//                        for (int j = 0; j < heartAllDatas.size(); j++) {
//                            B15PHeartDB b15PHeartDB = heartAllDatas.get(j);
//                            if (b15PHeartDB != null) {
//                                String heartData = b15PHeartDB.getHeartData();
//                                String heartTime = b15PHeartDB.getHeartTime();
//                                // 判断只是取今天的心率数据
//                                if (date.equals(heartData.substring(0, 10))) {
//                                    int heartNumber = b15PHeartDB.getHeartNumber();
//                                    //Log.e(TAG, "====进入循环内，有数据 = " + heartData.substring(11, 13) + "   " + dayTimes + "  " + heartNumber);
//                                    //2019-04-22 13:59:38
//
////                                        ////2019-04-22 13:59:38 ------13:00
////                                        if (heartMapFa.containsKey(heartData.substring(11, 13) + ":00")
////                                                || heartMapFa.containsKey(heartData.substring(11, 13) + ":30")) {
////
////                                            //2019-04-22 13:59:38 ----- 59
////                                            String substring = heartData.substring(14, 16).trim();
////                                            if (Integer.valueOf(substring) / 60 >= 0.5) {//该小时 30 分钟以前
////                                                valueCount1++;//半小时测量的次数---用于计算半小时内的平均数
////                                                allValues1 = allValues1 + heartNumber;//半小时内的所有数据累加
////                                                if (allValues1 > 0) {
////                                                    heartMap.put(heartData.substring(11, 13) + ":00", (int) allValues1 / valueCount1);
////                                                } else {
////                                                    heartMap.put(heartData.substring(11, 13) + ":00", (int) 0);
////                                                }
////                                                heartMapFa.put(heartData.substring(11, 13) + ":00", 0);
////                                            } else {//该小时 30 分钟以后
////                                                valueCount2++;//半小时测量的次数---用于计算半小时内的平均数
////                                                allValues2 = allValues2 + heartNumber;//半小时内的所有数据累加
////                                                if (allValues2 > 0) {
////                                                    heartMap.put(heartData.substring(11, 13) + ":30", (int) allValues2 / valueCount2);
////                                                } else {
////                                                    heartMap.put(heartData.substring(11, 13) + ":30", (int) 0);
////                                                }
////                                                heartMapFa.put(heartData.substring(11, 13) + ":30", 0);
////                                            }
////                                        } else {
////                                            String substring = heartData.substring(14, 16).trim();
////                                            if (Integer.valueOf(substring) / 60 >= 0.5) {//该小时 30 分钟以前
////                                                valueCount1 = 1;
////                                                allValues1 = heartNumber;
////                                                heartMap.put(heartData.substring(11, 13) + ":00", allValues1);
////                                                heartMapFa.put(heartData.substring(11, 13) + ":00", 0);
////                                            } else {//该小时 30 分钟以后
////                                                valueCount2 = 1;
////                                                allValues2 = heartNumber;
////                                                heartMap.put(heartData.substring(11, 13) + ":30", allValues2);
////                                                heartMapFa.put(heartData.substring(11, 13) + ":30", 0);
////                                            }
////                                        }
//
//                                    ////2019-04-22 13:59:38 ------13:00
//
//                                    if (heartMapFa.containsKey(heartTime.substring(0, 2))) {
//                                        valueCount1++;//1小时测量的次数---用于计算1小时内的平均数
//                                        allValues1 = allValues1 + heartNumber;//1小时内的所有数据累加
//                                        heartMapFa.put(heartTime.substring(0, 2), 0);
//
//                                        if (allValues1 > 0) {
//                                            heartMap.put(heartTime.substring(0, 2), (int) allValues1 / valueCount1);
//                                        } else {
//                                            heartMap.put(heartTime.substring(0, 2), (int) 0);
//                                        }
//                                    } else {
//                                        valueCount1 = 1;
//                                        allValues1 = heartNumber;
//                                        heartMap.put(heartTime.substring(0, 2), allValues1);
//                                        heartMapFa.put(heartTime.substring(0, 2), 0);
//                                    }
//                                }
//                            }
//                        }
//
//                        Log.e(TAG, "=AAS===心率真实值设置完成 " + heartMap.size() + "   " + heartMap.toString());
                        allDataListHeart.clear();
                        heartListNew.clear();
                        int noZeroCount = 0;
                        int noZeroAvgCount = 0;
                        if (!heartMapHM.isEmpty()) {
                            List<Integer> heartValue = new ArrayList<>();
                            for (String key : heartMapHM.keySet()) {
                                heartValue.add(heartMapHM.get(key));
                            }
                            List<Map<String, Integer>> listMap = new ArrayList<>();

                            for (int i = 0; i < 48; i++) {
                                Map<String, Integer> map = new HashMap<>();
                                int time = 30;
                                if (i % 2 == 0) {//0  2  4  6      30   120   240  360
                                    time = i * 60;
                                } else { // 1 3 5 7    60  180  300  420
                                    time = i * 30;
                                }
                                map.put("val", heartValue.get(i));
                                map.put("time", time);
                                listMap.add(map);
                            }
//                            for (int i = 0; i < 48; i++) {
//                                int time = 30;
//                                Map<String, Integer> map = new HashMap<>();
//                                if (i % 2 == 0) {//0  2  4  6      30   120   240  360
//                                    time = i * 60;
//                                    map.put("val", heartValue.get(i / 2));
//                                } else { // 1 3 5 7    60  180  300  420
//                                    time = i * 30;
//                                    map.put("val", 0);
//                                }
//                                map.put("time", time);
//                                listMap.add(map);
//                            }
                            for (int i = 0; i < listMap.size(); i++) {
                                Map<String, Integer> map = listMap.get(i);
                                allDataListHeart.add(map.get("val"));
                                if (map.get("val") > 0) {
                                    noZeroCount++;
                                    noZeroAvgCount += map.get("val");
                                    heartListNew.add(map.get("val"));
                                }
                            }

                            /**
                             * 保存心率数据
                             *
                             * @param bleName  蓝牙名字
                             * @param bleMac   蓝牙mac地址
                             * @param dataStr  日期
                             * @param avgHeart 平均心率
                             */
                            if (!heartListNew.isEmpty()) {
                                CommDBManager.getCommDBManager().saveCommHeartData((WatchUtils.isEmpty(L4M.GetConnecteddName()) ? "B15P" : L4M.GetConnecteddName()),
                                        WatchUtils.getSherpBleMac(MyApp.getContext()), date,
                                        Collections.max(heartListNew), Collections.min(heartListNew), (int) noZeroAvgCount / noZeroCount);
                            }
                        }

                    }

                    jsonString = "CCC" + JSON.toJSON(allDataListHeart).toString();
                    //LogTestUtil.e(TAG, "==HEART  " + jsonString);

                    break;
            }
        }
        return jsonString;
    }

    /**
     * 设置模拟数据-----在这里可以模拟数据  HRV
     *
     * @param mac
     * @param date
     */
    private void setHRvDatas(String mac, String date) {
        // B31HRVBean{dateStr='2019-05-18', bleMac='E9:37:C4:E7:28:D8',
        // hrvDataStr='
        // {"allCurrentPackNumber":420,
        // "currentPackNumber":420,
        // "date":"2019-05-18",
        // "hrvType":0,
        // "hrvValue":86,
        // "mTime":{"day":18,"hour":6,"minute":59,"month":5,"second":0,"weekDay":0,"year":2019},
        // "rate":"100,98,99,107,106,100,103,103,99,103",
        // "tempOne":10}'}

        String where = "bleMac = ? and dateStr = ?";
        /**
         * 测试时加上这句
         */
        //LitePal.deleteAll(B31HRVBean.class);
        List<B31HRVBean> hrvBeanList = LitePal.where(where, mac, date).find(B31HRVBean.class);
//        Log.e(TAG, "=====HRV  数据是否为空  " + ((hrvBeanList == null || hrvBeanList.isEmpty()) ? "是" : "否"));
        if (hrvBeanList == null || hrvBeanList.isEmpty()) {
            if (hrvBeanList == null) hrvBeanList = new ArrayList<>();


            //模拟数据可以在这里，之后再回调出去回去
            //模拟数据的工具
            Random random = new Random();
            Calendar calendar = Calendar.getInstance();  //获取当前时间，作为图标的名字
            int hour = 0;
            Map<String, List<B31HRVBean>> hrvMap = new HashMap<>();
            /**
             * 随机选择HRV图的绘制范围
             */
            int ra = random.nextInt(3);

            /**
             * 随机选择心率取值的范围
             */
            int raH = random.nextInt(10);

            for (int i = 0; i < 420; i++) {
                HRVOriginData mHRVOriginData = new HRVOriginData();
                mHRVOriginData.setAllCurrentPackNumber(420);//所有数据包的总和
                mHRVOriginData.setCurrentPackNumber(420);//当前数据包的位置
                mHRVOriginData.setDate(WatchUtils.getCurrentDate());
                mHRVOriginData.setHrvType(0);


                /**
                 * 模拟心率数据
                 */
                int rDHrvValue = getNum(random, 60, 95);
                String m = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.USER_SEX, "M");
                switch (raH) {
                    case 0://正常情况下的心率
                        if (!WatchUtils.isEmpty(m)) {
                        if (m.equals("M") || m.equals("男")) {
                            rDHrvValue = getNum(random, 60, 80);
                        } else {
                            rDHrvValue = getNum(random, 70, 80);
                        }
                    }
                        break;
                    case 1://偏高
                        if (!WatchUtils.isEmpty(m)) {
                            if (m.equals("M") || m.equals("男")) {
                                rDHrvValue = getNum(random, 70, 95);
                            } else {
                                rDHrvValue = getNum(random, 75, 95);
                            }
                        }
                        break;
                    case 2://偏低
                        if (!WatchUtils.isEmpty(m)) {
                            if (m.equals("M") || m.equals("男")) {
                                rDHrvValue = getNum(random, 50, 70);
                            } else {
                                rDHrvValue = getNum(random, 55, 70);
                            }
                        }
                        break;
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9://正常情况下的心率-----这些增加正常的高几率
                        if (!WatchUtils.isEmpty(m)) {
                            if (m.equals("M") || m.equals("男")) {
                                rDHrvValue = getNum(random, 60, 80);
                            } else {
                                rDHrvValue = getNum(random, 70, 80);
                            }
                        }
                        break;
                }

                mHRVOriginData.setHrvValue(rDHrvValue);
                TimeData timeData = new TimeData();
                timeData.setYear(calendar.get(Calendar.YEAR));
                timeData.setMonth(calendar.get(Calendar.MONTH) + 1);
                timeData.setDay(calendar.get(Calendar.DAY_OF_MONTH));
                if (i % 60 == 0) {
                    hour = i / 60;
                }
                int minute = i % 60;
                timeData.setHour(hour);
                timeData.setMinute(minute);
                timeData.setSecond(0);
                mHRVOriginData.setmTime(timeData);


                String ratr = "";
                switch (ra) {
                    case 0:
                        int rDRate11 = getNum(random, 60, 70);
                        int rDRate21 = getNum(random, 65, 78);
                        int rDRate31 = getNum(random, 70, 86);
                        int rDRate41 = getNum(random, 75, 94);
                        int rDRate51 = getNum(random, 80, 100);
                        int rDRate61 = getNum(random, 80, 100);
                        int rDRate71 = getNum(random, 75, 94);
                        int rDRate81 = getNum(random, 70, 86);
                        int rDRate91 = getNum(random, 65, 78);
                        int rDRate101 = getNum(random, 60, 70);
                        ratr = rDRate11 + "," + rDRate21 + "," + rDRate31 + "," + rDRate41 + "," + rDRate51 + ","
                                + rDRate61 + "," + rDRate71 + "," + rDRate81 + "," + rDRate91 + "," + rDRate101;
                        break;
                    case 1:
                        int rDRate12 = getNum(random, 60, 70);
                        int rDRate22 = getNum(random, 60, 78);
                        int rDRate32 = getNum(random, 60, 86);
                        int rDRate42 = getNum(random, 60, 94);
                        int rDRate52 = getNum(random, 60, 100);
                        int rDRate62 = getNum(random, 60, 100);
                        int rDRate72 = getNum(random, 60, 94);
                        int rDRate82 = getNum(random, 60, 86);
                        int rDRate92 = getNum(random, 60, 78);
                        int rDRate102 = getNum(random, 60, 70);
                        ratr = rDRate12 + "," + rDRate22 + "," + rDRate32 + "," + rDRate42 + "," + rDRate52 + ","
                                + rDRate62 + "," + rDRate72 + "," + rDRate82 + "," + rDRate92 + "," + rDRate102;
                        break;
                    case 2:
                        int rDRate13 = getNum(random, 50, 90);
                        int rDRate23 = getNum(random, 50, 90);
                        int rDRate33 = getNum(random, 60, 90);
                        int rDRate43 = getNum(random, 60, 90);
                        int rDRate53 = getNum(random, 70, 90);
                        int rDRate63 = getNum(random, 70, 90);
                        int rDRate73 = getNum(random, 70, 90);
                        int rDRate83 = getNum(random, 70, 90);
                        int rDRate93 = getNum(random, 50, 70);
                        int rDRate103 = getNum(random, 50, 70);

                        ratr = rDRate13 + "," + rDRate23 + "," + rDRate33 + "," + rDRate43 + "," + rDRate53 + ","
                                + rDRate63 + "," + rDRate73 + "," + rDRate83 + "," + rDRate93 + "," + rDRate103;
                        break;
                }




//                Log.e(TAG, "====EEE " + ratr);
                mHRVOriginData.setRate(ratr);
//                String ratr = "";
//                for (int j = 0; j < 10; j++) {
//                    int rDRate = getNum(random, 60, 95);
//                    ratr += rDRate + ",";
//                }
//                Log.e(TAG, "====EEE " + ratr.substring(0, ratr.length() - 1));
//                mHRVOriginData.setRate(ratr.substring(0, ratr.length() - 1));

                /**
                 * 模拟温度，原始数据里面只出现  9   和 10
                 */
                int rDTempOne = getNum(random, 9, 10);
                mHRVOriginData.setTempOne(rDTempOne);

                B31HRVBean b31Spo2hBean = new B31HRVBean();
                b31Spo2hBean.setBleMac(MyApp.getInstance().getMacAddress());
                b31Spo2hBean.setDateStr(mHRVOriginData.getDate());
                b31Spo2hBean.setHrvDataStr(gson.toJson(mHRVOriginData));
                hrvBeanList.add(b31Spo2hBean);
            }
            hrvMap.put("today", hrvBeanList);
            saveHRVToDBServer(hrvMap);
        }

        changeDBListenter.updataHrvDataToUIListenter(hrvBeanList);
    }


    Gson gson = new Gson();

    /**
     * 设置模拟数据-----在这里可以模拟数据  SPO2
     *
     * @param mac
     * @param date
     */
    private void setSpo2Datas(String mac, String date) {

        String where = "bleMac = ? and dateStr = ?";
        /**
         * 测试时加上这句
         */
        //LitePal.deleteAll(B31Spo2hBean.class);
        List<B31Spo2hBean> b31Spo2hBeanList = LitePal.where(where, mac, date).find(B31Spo2hBean.class);
//        Log.e(TAG, "=====SPO2  数据是否为空  " + ((b31Spo2hBeanList == null || b31Spo2hBeanList.isEmpty()) ? "是" : "否"));
        if (b31Spo2hBeanList == null || b31Spo2hBeanList.isEmpty()) {
            if (b31Spo2hBeanList == null) b31Spo2hBeanList = new ArrayList<>();


            //模拟数据可以在这里，之后再回调出去回去
            //模拟数据的工具
            Random random = new Random();
            Calendar calendar = Calendar.getInstance();  //获取当前时间，作为图标的名字
            int hour = 0;

            Map<String, List<B31Spo2hBean>> spo2Map = new HashMap<>();
            for (int i = 0; i < 420; i++) {
                Spo2hOriginData mSpo2hOriginData = new Spo2hOriginData();
                mSpo2hOriginData.setAllPackNumner(420);//所有数据包的总和
                mSpo2hOriginData.setApneaResult(0);//呼吸衰竭
                /**
                 * 模拟心脏负荷数据
                 */
                int rDCardiacLoad = setRandomCardiacLoad(random);
//                Log.e(TAG, "随机生成的心脏负荷是  " + rDCardiacLoad);
                mSpo2hOriginData.setCardiacLoad(rDCardiacLoad);//心脏负荷  （轻度：0-20  正常：21-40  异常：>=41）


                mSpo2hOriginData.setCurrentPackNumber(420);//当前数据包的位置
                mSpo2hOriginData.setDate(WatchUtils.getCurrentDate());
                mSpo2hOriginData.sethRVariation(86);//心率变异性
                mSpo2hOriginData.setHeartValue(129);//心值
                mSpo2hOriginData.setHypopnea(0);//呼吸不足


                /**
                 * 模拟缺氧时间
                 * 呼吸暂停点
                 */
                int[] rOHypoxiaTime = setRandomHypoxiaTimeAndHypoxia(random);
                mSpo2hOriginData.setHypoxiaTime(rOHypoxiaTime[0]);//设定缺氧时间 （正常：0-20    异常：21-300）
                mSpo2hOriginData.setIsHypoxia(rOHypoxiaTime[1]);//低氧--- 呼吸暂停点


                TimeData timeData = new TimeData();
                timeData.setYear(calendar.get(Calendar.YEAR));
                timeData.setMonth(calendar.get(Calendar.MONTH) + 1);
                timeData.setDay(calendar.get(Calendar.DAY_OF_MONTH));

                if (i % 60 == 0) {
                    hour = i / 60;
                }
                int minute = i % 60;

                timeData.setHour(hour);
                timeData.setMinute(minute);
                timeData.setSecond(0);

                mSpo2hOriginData.setmTime(timeData);

                int spo2 = getNum(random, 95, 99);//模拟生成氧值
                mSpo2hOriginData.setOxygenValue(spo2);//氧值 （正常：95-99）

                /**
                 * 模拟生成呼呼吸率
                 */
                int rDSRespirationRate = setRandomRespirationRate(random);
                mSpo2hOriginData.setRespirationRate(rDSRespirationRate);//呼吸速率（正常：0-26    异常：27-50）
                mSpo2hOriginData.setSportValue(0);//运动值
                mSpo2hOriginData.setStepValue(0);//步数值
                mSpo2hOriginData.setTemp1(4);


                B31Spo2hBean b31Spo2hBean = new B31Spo2hBean();
                b31Spo2hBean.setBleMac(MyApp.getInstance().getMacAddress());
                b31Spo2hBean.setDateStr(mSpo2hOriginData.getDate());
                b31Spo2hBean.setSpo2hOriginData(gson.toJson(mSpo2hOriginData));
                b31Spo2hBeanList.add(b31Spo2hBean);
            }
            spo2Map.put("today", b31Spo2hBeanList);
            saveSpo2Data(spo2Map);
        }
        /**
         * 更新虚拟数据
         */
        changeDBListenter.updataSpo2DataToUIListenter(b31Spo2hBeanList);
    }


    //保存HRV
    private void saveHRVToDBServer(Map<String, List<B31HRVBean>> resultHrvMap) {
        if (resultHrvMap != null && !resultHrvMap.isEmpty()) {
            //今天的 直接保存
            List<B31HRVBean> todayHrvList = resultHrvMap.get("today");

            if (todayHrvList != null && !todayHrvList.isEmpty())
                LitePal.saveAll(todayHrvList);
        }
    }

    //保存spo2数据
    private void saveSpo2Data(Map<String, List<B31Spo2hBean>> resultMap) {
        if (resultMap != null && !resultMap.isEmpty()) {
            //今天
            List<B31Spo2hBean> todayLt = resultMap.get("today");
            if (todayLt != null && !todayLt.isEmpty()) {
                LitePal.saveAll(todayLt);
            }
        }
    }


    @Override
    protected void onPostExecute(String ts) {
        super.onPostExecute(ts);
//        if (ts == null) ts = new ArrayList<>();
        if (!WatchUtils.isEmpty(ts) && ts.length() > 3) {
//            Log.i(TAG, "数据返回的   " + ts);

            String substring = ts.substring(0, 3);
            String reas = ts.substring(3, ts.length());
            switch (substring) {
                case "FFF"://汇总步数
                    List<Integer> allstepList = new Gson().fromJson(reas, new TypeToken<List<Integer>>() {
                    }.getType());
                    if (allstepList != null && !allstepList.isEmpty()) {
                        Integer integer = allstepList.get(0);
                        changeDBListenter.updataAllStepDataToUIListenter(integer);
                    }
                    break;
                case "AAA"://步数
                    List<Integer> stepList = new Gson().fromJson(reas, new TypeToken<List<Integer>>() {
                    }.getType());
                    changeDBListenter.updataStepDataToUIListenter(stepList);
                    break;
                case "BBB"://睡眠
                    List<W30S_SleepDataItem> sleepList = new Gson().fromJson(reas, new TypeToken<List<W30S_SleepDataItem>>() {
                    }.getType());
                    changeDBListenter.updataSleepDataToUIListenter(sleepList);
                    break;
                case "CCC"://心率
                    List<Integer> heartList = new Gson().fromJson(reas, new TypeToken<List<Integer>>() {
                    }.getType());
                    String LatelyValues = "";
                    if (heartList != null && !heartList.isEmpty()) {
                        for (int i = 0; i < heartList.size(); i++) {
                            if (heartList.get(i) > 0) {
                                LatelyValues = buidleHour(String.valueOf((float) i / 2f), heartList.get(i));
//                                Log.i(TAG, "最近心律 " + LatelyValues);
                            }
                        }
                    }
                    changeDBListenter.updataHeartDataToUIListenter(heartList, LatelyValues);
                    break;
            }
        }

    }


    /**
     * 返回 每一个点的心率的时间和心率值
     *
     * @param hour
     * @param heartValue
     * @return
     */
    private String buidleHour(String hour, int heartValue) {
        String times = "00:00";
        if (hour.contains(".")) {
            String[] split = hour.split("[.]");
            if (split.length > 0) {
                if (!WatchUtils.isEmpty(split[0]) && !WatchUtils.isEmpty(split[1])) {
                    if (Integer.valueOf(split[1]) > 0) {
                        times = (split[0].length() == 2 ? split[0] : "0" + split[0]) + ":30";
                    } else {
                        times = (split[0].length() == 2 ? split[0] : "0" + split[0]) + ":00";
                    }

                }
            }
        } else {
            times = (times.length() == 2 ? times : "0" + times) + ":00";
        }
        times = times + "#" + heartValue;
        return times;
    }

    public abstract static class ChangeDBListenter<T> {
//        public abstract void updataDataToUIListenter(List<Object> ts);

        public void updataAllStepDataToUIListenter(int integer) {
        }

        public void updataStepDataToUIListenter(List<Integer> ts) {
        }


        public void updataSleepDataToUIListenter(List<W30S_SleepDataItem> ts) {
        }


        public void updataHeartDataToUIListenter(List<Integer> ts, String latelyValues) {
        }


        /**
         * SPO的 假数据
         *
         * @param
         */
        public void updataSpo2DataToUIListenter(List<B31Spo2hBean> b31Spo2hBeanList) {
        }

        /**
         * HRV
         *
         * @param hrvBeanList
         */
        public void updataHrvDataToUIListenter(List<B31HRVBean> hrvBeanList) {
        }
    }


    //-------------------------------------------以下是关于模拟数据的----------------------------------------


    /**
     * 模拟生成呼呼吸率
     * （正常：0-26    异常：27-50）
     *
     * @param random
     * @return
     */
    private int setRandomRespirationRate(Random random) {
        //判断模拟生成缺氧时间的那一部分数据（1，2，3）
        int rDS = random.nextInt(9);//0-3的随机数，包含0，不包含3
        int rDSRespirationRate = getNum(random, 0, 26);//模拟生成呼吸速率
        //1.正常
        //2.异常
        //3.正常+异常
        switch (rDS) {
            case 0:
                rDSRespirationRate = getNum(random, 0, 26);//模拟生成呼吸速率
                break;
            case 1:
                rDSRespirationRate = getNum(random, 27, 50);//模拟生成呼吸速率
                break;
            case 2:
                rDSRespirationRate = getNum(random, 0, 50);//模拟生成呼吸速率
                break;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                rDSRespirationRate = getNum(random, 0, 26);//模拟生成呼吸速率
                break;
        }
        return rDSRespirationRate;
    }


    /**
     * 心脏负荷 模拟数据
     * （轻度：0-20  正常：21-40  异常：>=41）
     *
     * @param random
     * @return
     */
    private int setRandomCardiacLoad(Random random) {
        //判断模拟生成心脏负荷的那一部分数据（1，2，3，4）
        int rDH = random.nextInt(14);//0-16的随机数，包含0，不包含16

        int rDCardiacLoad = getNum(random, 21, 40);//心脏负荷  （轻度：0-20  正常：21-40  异常：>=41）
        //1. 正常
        //2. 轻度
        //3. 异常
        //4. 轻度+正常
        //5. 异常+正常
        //6. 轻度+正常+异常
        switch (rDH) {
            case 0:
                //正常范围----   21-40
                rDCardiacLoad = getNum(random, 21, 40);
                break;
            case 1:
                //轻度范围----   0-20
                rDCardiacLoad = getNum(random, 0, 20);
                break;
            case 2:
                //异常范围----   >=41
                rDCardiacLoad = 41;
                break;
            case 3:
                //轻度+正常范围----  0-20   21-40  ===== 0-40
                rDCardiacLoad = getNum(random, 0, 40);
                break;
            case 4:
                //异常+正常范围----  21-40   >=41 ===== 21-41
                rDCardiacLoad = getNum(random, 21, 41);
                break;
            case 5:
                //轻度+正常+异常范围---   0-41
                rDCardiacLoad = getNum(random, 0, 41);
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
                //正常范围----   21-40
                rDCardiacLoad = getNum(random, 21, 40);
                break;
        }

        return rDCardiacLoad;
    }

    /**
     * 模拟缺氧时间
     * 呼吸暂停点
     * （正常：0-20    异常：21-300）
     *
     * @param random
     * @return
     */
    public int[] setRandomHypoxiaTimeAndHypoxia(Random random) {

        //判断模拟生成缺氧时间的那一部分数据（1，2，3）
        /**
         * 将出现异常的几率降低到40分之1
         */
        int rDO = random.nextInt(41);//0-10的随机数，包含0，不包含10
        int rOHypoxiaTime = getNum(random, 0, 20);//设定缺氧时间 （正常：0-20    异常：21-300）
        int rOHypoxia = 0;
        int[] ran = {rOHypoxiaTime, rOHypoxia};
        //1.正常
        //2.异常
        //3.正常+异常
        switch (rDO) {
            case 0:
                rOHypoxiaTime = getNum(random, 0, 20);
                rOHypoxia = 0;
                break;
            case 1:
                rOHypoxiaTime = getNum(random, 21, 300);
                rOHypoxia = 1;
                break;
            case 2:
//                rOHypoxiaTime = getNum(random, 0, 300);
//                if (rOHypoxiaTime >= 21) {
//                    rOHypoxia = 1;
//                } else {
//                    rOHypoxia = 0;
//                }
//                break;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
                rOHypoxiaTime = getNum(random, 0, 20);
                rOHypoxia = 0;
                break;
        }
        ran[0] = rOHypoxiaTime;
        ran[1] = rOHypoxia;
        return ran;
    }


    /**
     * 生成一个startNum 到 endNum之间的随机数(包含endNum的随机数)
     *
     * @param startNum
     * @param endNum
     * @return
     */
    public int getNum(Random random, int startNum, int endNum) {
        if ((endNum + 1) > startNum) {
            return random.nextInt((endNum + 1) - startNum) + startNum;
        }
        return 0;
    }


}
