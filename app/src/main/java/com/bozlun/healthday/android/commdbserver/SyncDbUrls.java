package com.bozlun.healthday.android.commdbserver;

/**
 * Created by Admin
 * Date 2019/3/6
 * 上传和下载缓存的数据，数据库中保存的数据 地址
 */
public class SyncDbUrls {

    //base地址
    static final String SYNC_BASE_URL = "http://47.90.83.197:9070/Watch/";

    //上传总步数
    public static String uploadCountStepUrl(){
        return SYNC_BASE_URL+"sportDay/submit";
    }
    //下载步数
    public static String downloadCountStepUrl(){
        return SYNC_BASE_URL + "sportDay/getList";
    }


    //上传心率
    public static String uploadHeartUrl(){
        return SYNC_BASE_URL + "heartRateDay/submit";
    }

    //下载心率
    public static String downloadHeartUrl(){
        return SYNC_BASE_URL + "heartRateDay/getList";
    }



    //上传睡眠
    public static String uploadSleepUrl(){
        return SYNC_BASE_URL + "sleepDay/submit";
    }

    //下载睡眠
    public static String downloadSleepUrl(){
        return SYNC_BASE_URL + "sleepDay/getList";
    }


    //上传血压
    public static String uploadBloodUrl(){
        return SYNC_BASE_URL + "bloodPressureDay/submit";
    }

    //下载血压
    public static String downloadBloodUrl(){
        return SYNC_BASE_URL + "bloodPressureDay/getList";

    }

    //上传血氧
    public static String uploadBloodOxyUrl(){
        return SYNC_BASE_URL + "bloodOxygenDay/submit";
    }

    //下载血氧
    public static String downloadBloodOxyUrl(){
        return SYNC_BASE_URL + "bloodOxygenDay/getList";
    }
}
