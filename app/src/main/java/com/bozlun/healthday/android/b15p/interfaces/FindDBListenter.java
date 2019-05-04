package com.bozlun.healthday.android.b15p.interfaces;

import android.os.AsyncTask;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.bozlun.healthday.android.MyApp;
import com.bozlun.healthday.android.b15p.b15pdb.B15PDBCommont;
import com.bozlun.healthday.android.b15p.b15pdb.B15PHeartDB;
import com.bozlun.healthday.android.b15p.b15pdb.B15PSleepDB;
import com.bozlun.healthday.android.b15p.b15pdb.B15PStepDB;
import com.bozlun.healthday.android.commdbserver.CommDBManager;
import com.bozlun.healthday.android.siswatch.utils.WatchUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30S_SleepDataItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FindDBListenter extends AsyncTask<String, Void, String> {
    private static final String TAG = "FindDBListenter";
    private ChangeDBListenter changeDBListenter;

    public FindDBListenter(ChangeDBListenter changeDBListenter) {
        this.changeDBListenter = changeDBListenter;
    }

    String[] timeString = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
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
                case "step":
                    List<B15PStepDB> allStepDatasList = (List<B15PStepDB>) B15PDBCommont.getInstance().findStepAllDatas(mac, date);
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
                                        CommDBManager.getCommDBManager().saveCommCountStepDate("B15P", mac,
                                                date,
                                                defaultSteps);
                                    }
                                }
                            }
                            allDataList.add(hourStep);
                        }
                    }
                    jsonString = "AAA" + JSON.toJSON(allDataList).toString();
                    Log.e(TAG, "==STEP  " + jsonString);

                    break;
                case "sleep":
                    List<B15PSleepDB> allSleepDatasList = (List<B15PSleepDB>) B15PDBCommont.getInstance().findSleepAllDatas(mac, date);

                    List<W30S_SleepDataItem> allDataListSleep = new ArrayList<>();
                    if (allSleepDatasList != null && !allSleepDatasList.isEmpty()) {
                        allDataListSleep.clear();
                        for (int i = 0; i < allSleepDatasList.size(); i++) {
                            B15PSleepDB b15PSleepDB = allSleepDatasList.get(i);
                            if (b15PSleepDB != null) {
                                String sleepData = b15PSleepDB.getSleepData();
                                String sleepTime = b15PSleepDB.getSleepTime();


                                if (sleepData.substring(0, 10).equals(date)
                                        || sleepData.substring(0, 10).equals(WatchUtils.obtainAroundDate(date, true))) {

                                    Log.e(TAG, " 查询到的单个睡眠数据  " + sleepData + "   " + sleepTime + "  " + date);
                                    if ((sleepData.substring(0, 10).equals(date) && Integer.valueOf(sleepTime.substring(0, 2)) <= 16)
                                            ||(sleepData.substring(0,10).equals(WatchUtils.obtainAroundDate(date, true))&& Integer.valueOf(sleepTime.substring(0, 2)) >=21)) {

                                        W30S_SleepDataItem w30S_sleepDataItem = new W30S_SleepDataItem();
                                        w30S_sleepDataItem.setStartTime(sleepTime);
                                        w30S_sleepDataItem.setSleep_type(b15PSleepDB.getSleepType());
                                        allDataListSleep.add(w30S_sleepDataItem);
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
                    Log.e(TAG, "==SLEEP  " + jsonString);
                    break;
                case "heart":
                    List<B15PHeartDB> heartAllDatas = (List<B15PHeartDB>) B15PDBCommont.getInstance().findHeartAllDatas(mac, date);
                    List<Integer> allDataListHeart = new ArrayList<>();
                    List<Integer> heartListNew = new ArrayList<>();
                    if (heartAllDatas != null && !heartAllDatas.isEmpty()) {
                        Map<String, Integer> heartMapFa = new HashMap<>();
                        Map<String, Integer> heartMap = new LinkedHashMap<>();
                        int allValues1 = 0;
                        int valueCount1 = 0;
                        /**
                         * 设置长度为48的空的数据组
                         */
                        for (int i = 0; i < timeString.length; i++) {
                            heartMap.put(timeString[i], 0);
                        }
                        for (int j = 0; j < heartAllDatas.size(); j++) {
                            B15PHeartDB b15PHeartDB = heartAllDatas.get(j);
                            if (b15PHeartDB != null) {
                                String heartData = b15PHeartDB.getHeartData();
                                String heartTime = b15PHeartDB.getHeartTime();
                                // 判断只是取今天的心率数据
                                if (date.equals(heartData.substring(0, 10))) {
                                    int heartNumber = b15PHeartDB.getHeartNumber();
                                    //Log.e(TAG, "====进入循环内，有数据 = " + heartData.substring(11, 13) + "   " + dayTimes + "  " + heartNumber);
                                    //2019-04-22 13:59:38

//                                        ////2019-04-22 13:59:38 ------13:00
//                                        if (heartMapFa.containsKey(heartData.substring(11, 13) + ":00")
//                                                || heartMapFa.containsKey(heartData.substring(11, 13) + ":30")) {
//
//                                            //2019-04-22 13:59:38 ----- 59
//                                            String substring = heartData.substring(14, 16).trim();
//                                            if (Integer.valueOf(substring) / 60 >= 0.5) {//该小时 30 分钟以前
//                                                valueCount1++;//半小时测量的次数---用于计算半小时内的平均数
//                                                allValues1 = allValues1 + heartNumber;//半小时内的所有数据累加
//                                                if (allValues1 > 0) {
//                                                    heartMap.put(heartData.substring(11, 13) + ":00", (int) allValues1 / valueCount1);
//                                                } else {
//                                                    heartMap.put(heartData.substring(11, 13) + ":00", (int) 0);
//                                                }
//                                                heartMapFa.put(heartData.substring(11, 13) + ":00", 0);
//                                            } else {//该小时 30 分钟以后
//                                                valueCount2++;//半小时测量的次数---用于计算半小时内的平均数
//                                                allValues2 = allValues2 + heartNumber;//半小时内的所有数据累加
//                                                if (allValues2 > 0) {
//                                                    heartMap.put(heartData.substring(11, 13) + ":30", (int) allValues2 / valueCount2);
//                                                } else {
//                                                    heartMap.put(heartData.substring(11, 13) + ":30", (int) 0);
//                                                }
//                                                heartMapFa.put(heartData.substring(11, 13) + ":30", 0);
//                                            }
//                                        } else {
//                                            String substring = heartData.substring(14, 16).trim();
//                                            if (Integer.valueOf(substring) / 60 >= 0.5) {//该小时 30 分钟以前
//                                                valueCount1 = 1;
//                                                allValues1 = heartNumber;
//                                                heartMap.put(heartData.substring(11, 13) + ":00", allValues1);
//                                                heartMapFa.put(heartData.substring(11, 13) + ":00", 0);
//                                            } else {//该小时 30 分钟以后
//                                                valueCount2 = 1;
//                                                allValues2 = heartNumber;
//                                                heartMap.put(heartData.substring(11, 13) + ":30", allValues2);
//                                                heartMapFa.put(heartData.substring(11, 13) + ":30", 0);
//                                            }
//                                        }

                                    ////2019-04-22 13:59:38 ------13:00

                                    if (heartMapFa.containsKey(heartTime.substring(0, 2))) {
                                        valueCount1++;//1小时测量的次数---用于计算1小时内的平均数
                                        allValues1 = allValues1 + heartNumber;//半小时内的所有数据累加
                                        heartMapFa.put(heartTime.substring(0, 2), 0);

                                        if (allValues1 > 0) {
                                            heartMap.put(heartTime.substring(0, 2), (int) allValues1 / valueCount1);
                                        } else {
                                            heartMap.put(heartTime.substring(0, 2), (int) 0);
                                        }
                                    } else {
                                        valueCount1 = 1;
                                        allValues1 = heartNumber;
                                        heartMap.put(heartTime.substring(0, 2), allValues1);
                                        heartMapFa.put(heartTime.substring(0, 2), 0);
                                    }
                                }
                            }
                        }

                        Log.e(TAG, "=AAS===心率真实值设置完成 " + heartMap.size() + "   " + heartMap.toString());
                        allDataListHeart.clear();
                        heartListNew.clear();
                        int noZeroCount = 0;
                        int noZeroAvgCount = 0;
                        if (!heartMap.isEmpty()) {
                            List<Integer> heartValue = new ArrayList<>();
                            for (String key : heartMap.keySet()) {
                                heartValue.add(heartMap.get(key));
                            }
                            List<Map<String, Integer>> listMap = new ArrayList<>();

                            for (int i = 0; i < 48; i++) {
                                int time = 30;
                                Map<String, Integer> map = new HashMap<>();
                                if (i % 2 == 0) {//0  2  4  6      30   120   240  360
                                    time = i * 60;
                                    map.put("val", heartValue.get(i / 2));
                                } else { // 1 3 5 7    60  180  300  420
                                    time = i * 30;
                                    map.put("val", 0);
                                }
                                map.put("time", time);
                                listMap.add(map);
                            }
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
//                            if (!heartListNew.isEmpty()) {
//                                CommDBManager.getCommDBManager().saveCommHeartData("B15P", WatchUtils.getSherpBleMac(MyApp.getContext()), date,
//                                        Collections.max(heartListNew), Collections.min(heartListNew), (int) noZeroAvgCount / noZeroCount);
//                            }
                        }

                    }

                    jsonString = "CCC" + JSON.toJSON(allDataListHeart).toString();
                    Log.e(TAG, "==HEART  " + jsonString);

                    break;
            }
        }
        return jsonString;
    }


    @Override
    protected void onPostExecute(String ts) {
        super.onPostExecute(ts);
//        if (ts == null) ts = new ArrayList<>();
        if (!WatchUtils.isEmpty(ts) && ts.length() > 3) {
            Log.e(TAG, "数据返回的   " + ts);

            String substring = ts.substring(0, 3);
            String reas = ts.substring(3, ts.length());
            switch (substring) {
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
                    changeDBListenter.updataHeartDataToUIListenter(heartList);
                    break;
            }
        }

    }


    public abstract static class ChangeDBListenter<T> {
//        public abstract void updataDataToUIListenter(List<Object> ts);

        public void updataStepDataToUIListenter(List<Integer> ts) {
        }


        public void updataSleepDataToUIListenter(List<W30S_SleepDataItem> ts) {
        }


        public void updataHeartDataToUIListenter(List<Integer> ts) {
        }

    }
}
