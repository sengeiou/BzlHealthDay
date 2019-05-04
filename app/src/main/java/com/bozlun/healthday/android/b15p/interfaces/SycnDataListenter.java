package com.bozlun.healthday.android.b15p.interfaces;//package com.example.bozhilun.android.b15p.interfaces;
//
//import com.example.bozhilun.android.MyApp;
//import com.example.bozhilun.android.siswatch.utils.WatchUtils;
//import com.tjdL4.tjdmain.Dev;
//import com.tjdL4.tjdmain.L4M;
//import com.tjdL4.tjdmain.contr.Health_HeartBldPrs;
//import com.tjdL4.tjdmain.contr.Health_Sleep;
//import com.tjdL4.tjdmain.contr.Health_TodayPedo;
//
//public class SycnDataListenter extends Dev.UpdateUiListenerImpl {
//    private static final String TAG = "SycnDataListenter";
//    private String type = Dev.L4UI_PageDATA_PEDO;
//    private String dateStr = WatchUtils.getCurrentDate();//今天;
//    private SycnChangeUIListenter sycnChangeUIListenter = null;
//
//
//    /**
//     * 这里要用饿汉式的加载不能，用懒汉模式
//     *
//     * @param type
//     * @param dateStr
//     * @param sycnChangeUIListenter
//     * @return
//     */
//    public static SycnDataListenter newInstance(String type,
//                                                String dateStr,
//                                                SycnChangeUIListenter sycnChangeUIListenter) {
//        return new SycnDataListenter(type, dateStr, sycnChangeUIListenter);
//    }
//
//
//    public void setDateStr(String dateStr) {
//        this.dateStr = dateStr;
//    }
//
//    private SycnDataListenter(String type, String dateStr, SycnChangeUIListenter sycnChangeUIListenter) {
//        if (!WatchUtils.isEmpty(type)) this.type = type;
//        if (!WatchUtils.isEmpty(dateStr)) this.dateStr = dateStr;
//        this.sycnChangeUIListenter = sycnChangeUIListenter;
//    }
//
//
//
//    @Override
//    public void UpdateUi(int i, String s) {
//        String tempAddr = L4M.GetConnectedMAC();
//        if (tempAddr != null) {
//            switch (type) {
//                case Dev.L4UI_PageDATA_PEDO:
//                    System.out.println("步数");
//                    Health_TodayPedo.TodayStepPageData stepData = Health_TodayPedo.GetHealth_Data(tempAddr, dateStr, MyApp.getContext());
//                    if (stepData != null) {
//                        sycnChangeUIListenter.B15P_ChangeUI(0x11, stepData,dateStr,true);
//                        System.out.println("步数同步啦");
//                    }
//                    break;
//                case Dev.L4UI_PageDATA_SLEEP:
//                    System.out.println("睡眠");
//                    Health_Sleep.HealthSleepData sleepData = Health_Sleep.GetSleep_Data(MyApp.getContext(), tempAddr, WatchUtils.obtainAroundDate(dateStr, true), "21:00:00", "16:00:00");
//                    if (sleepData != null) {
//                        sycnChangeUIListenter.B15P_ChangeUI(0x12,sleepData, dateStr,true);
//                        System.out.println("睡眠同步啦");
//                    }
//                    break;
//                case Dev.L4UI_PageDATA_HEARTRATE:
//                    System.out.println("心率");
//                    Health_HeartBldPrs.HeartPageData mHeartData = Health_HeartBldPrs.GetHeart_Data(tempAddr, dateStr, MyApp.getContext());
//                    if (mHeartData != null) {
//                        sycnChangeUIListenter.B15P_ChangeUI(0x13, mHeartData,dateStr,true);
//                        System.out.println("心率同步啦");
//                    }
//                    break;
//            }
//        }
//    }
//
//
//    /**
//     * Dev.L4UI_PageDATA_PEDO
//     * Dev.L4UI_PageDATA_SLEEP
//     * Dev.L4UI_PageDATA_HEARTRATE
//     *
//     * @param type
//     */
//   public void getAllDatas(String type){
//        String tempAddr = L4M.GetConnectedMAC();
//        if (tempAddr != null) {
//            switch (type) {
//                case Dev.L4UI_PageDATA_PEDO:
//                    System.out.println("步数");
//                    Health_TodayPedo.TodayStepPageData stepData = Health_TodayPedo.GetHealth_Data(tempAddr, dateStr, MyApp.getContext());
//                    if (stepData != null) {
//                        sycnChangeUIListenter.B15P_ChangeUI(0x11, stepData,dateStr,false);
//                        System.out.println("步数同步啦");
//                    }
//                    break;
//                case Dev.L4UI_PageDATA_SLEEP:
//                    System.out.println("睡眠");
//                    Health_Sleep.HealthSleepData sleepData = Health_Sleep.GetSleep_Data(MyApp.getContext(), tempAddr, WatchUtils.obtainAroundDate(dateStr, true), "21:00:00", "16:00:00");
//                    if (sleepData != null) {
//                        sycnChangeUIListenter.B15P_ChangeUI(0x12, sleepData,dateStr,false);
//                        System.out.println("睡眠同步啦");
//                    }
//                    break;
//                case Dev.L4UI_PageDATA_HEARTRATE:
//                    System.out.println("心率");
//                    Health_HeartBldPrs.HeartPageData mHeartData = Health_HeartBldPrs.GetHeart_Data(tempAddr, dateStr, MyApp.getContext());
//                    if (mHeartData != null) {
//                        sycnChangeUIListenter.B15P_ChangeUI(0x13, mHeartData,dateStr,false);
//                        System.out.println("心率同步啦");
//                    }
//                    break;
//            }
//        }
//    }
//
//}
