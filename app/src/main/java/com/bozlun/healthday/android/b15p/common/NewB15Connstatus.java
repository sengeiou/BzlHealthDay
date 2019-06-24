package com.bozlun.healthday.android.b15p.common;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bozlun.healthday.android.MyApp;
import com.tjdL4.tjdmain.L4M;

/**
 * Created by Admin
 * Date 2019/6/3
 */
public class NewB15Connstatus extends Service {


    private NewB15Connstatus newB15Connstatus = null;


    @Override
    public void onCreate() {
        super.onCreate();


    }


    public L4M.BTStReceiver getDataReceiver() {
        return DataReceiver;
    }


    private L4M.BTStReceiver DataReceiver = new L4M.BTStReceiver() {
        @Override
        public void OnRec(String InfType, String Info) {
            if (Info.contains("Connecting")) {      //连接中

            } else if (Info.contains("BT_BLE_Connected")) { //连接成功了
                MyApp.b15pIsFirstConntent = true;//是第一次链接

            } else if (Info.contains("close")) {

            }
        }
    };








    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
