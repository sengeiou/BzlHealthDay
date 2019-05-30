package com.bozlun.healthday.android.siswatch.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.bozlun.healthday.android.Commont;
import com.bozlun.healthday.android.MyApp;
import com.bozlun.healthday.android.R;
import com.bozlun.healthday.android.activity.wylactivity.wyl_util.ScreenShot;
import com.bozlun.healthday.android.bean.UserInfoBean;
import com.bozlun.healthday.android.bleutil.MyCommandManager;
import com.bozlun.healthday.android.util.Common;
import com.bozlun.healthday.android.util.MyLogUtil;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.bozlun.healthday.android.util.ToastUtil;
import com.google.gson.Gson;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IAllSetDataListener;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.listener.data.ISocialMsgDataListener;
import com.veepoo.protocol.model.datas.AllSetData;
import com.veepoo.protocol.model.datas.FunctionSocailMsgData;
import com.veepoo.protocol.model.datas.PersonInfoData;
import com.veepoo.protocol.model.enums.EAllSetType;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.enums.ESex;
import com.veepoo.protocol.model.settings.AllSetSetting;
import com.veepoo.protocol.model.settings.CustomSetting;
import com.veepoo.protocol.model.settings.CustomSettingData;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.TELEPHONY_SERVICE;
import static com.tencent.mm.sdk.platformtools.MMApplicationContext.getPackageName;


/**
 * Created by Administrator on 2017/7/18.
 */

public class WatchUtils {

    private static final String TAG = "WatchUtils";

    public static final String WATCH_CONNECTED_STATE_ACTION = "com.example.bozhilun.android.siswatch.alarm.connectstate";  //判断蓝牙是否连接的广播action
    public static final String WATCH_GETWATCH_STEPS_ACTION = "com.example.bozhilun.android.siswatch.alarm.steps";   //步数的广播action
    public static final String WATCH_OPENTAKE_PHOTO_ACTION = "com.example.bozhilun.android.siswatch.takephoto";     //拍照指令的action
    public static final String WACTH_DISCONNECT_BLE_ACTION = "com.example.bozhilun.android.siswatch.bledisconnect";    //断开连接成功的action

    public static final String B30_CONNECTED_ACTION = "com.example.bozhilun.android.b30.connected"; //已经连接
    public static final String B30_DISCONNECTED_ACTION = "com.example.bozhilun.android.b30.disconnected";  //断开连接

    public static final String B31_CONNECTED_ACTION = "com.example.bozhilun.android.b31.connected"; //B31设备已经连接

    //B31HRV的数据读取完了，
    public static final String B31_HRV_COMPLETE = "com.example.bozhilun.android.b31.hrv_complete";
    //B31血氧的数据读取完了
    public static final String B31_SPO2_COMPLETE = "com.example.bozhilun.android.b31.spo2_complete";


    //卡路里的常量
    public static double kcalcanstanc = 65.4;  //计算卡路里常量

    static Gson gson = new Gson();


    /**
     * 搜索判断蓝牙名称过滤
     *
     * @param input
     * @return
     */
//    private static final String B18I_BLENAME = "B18I";  //B18I
//    private static final String H9_BLENAME = "H9"; //H9手表名字标识  保存时后面+X
//    private static final String B15PNAME = "B15P";  //B15P
//    public static final String B30_NAME = "B30";  //B30
//    public static final String W30_NAME = "W30";     //W30
//    public static final String B36_NAME = "B36";     //B36
//    public static final String RINGMII_NAME = "Ringmii";   //盖德蓝牙名称
//    public static final String H8_NAME = "H8";
//    public static final String B31_NAME = "B31";        //B31手环
//    public static final String B31S_NAME = "B31S";        //B31S手环
//    public static final String S500_NAME = "500S";        //500S手环

    private static final String B18I_BLENAME = "B18I";  //B18I
    public static final String H9_BLENAME = "H9"; //H9手表名字标识  保存时后面+X
    public static final String W06_BLENAME = "W06X"; //H9手表名字标识  保存时后面+X
    public static final String B30_NAME = "B30";  //B30
    public static final String W30_NAME = "W30";     //W30
    public static final String W31_NAME = "W31";        //W31
    public static final String B36_NAME = "B36";     //B36
    public static final String RINGMII_NAME = "Ringmii";   //盖德蓝牙名称
    public static final String H8_NAME = "H8";
    public static final String B31_NAME = "B31";        //B31手环
    public static final String B31S_NAME = "B31S";        //B31S手环
    public static final String S500_NAME = "500S";        //B31S手环

    /**
     * 搜索 ，根据蓝牙名字过滤
     *
     * @param bleName
     * @return
     */
    public static boolean verBleNameForSearch(String bleName) {
        String []FilterNamas= new String[]
                {"B15P","Lefun","F1","F1S","F2","F3","F3S","F4","F6","F6S","F7", "F7S","F9","F9S","F10","F11","F12","F13","F15","F16","F18","M3","W3"};
        Set<String> set = new HashSet<>(Arrays.asList(FilterNamas));
        if (set.contains(bleName)){
            return true;
        }else {
            return false;
        }
//        if ((bleName.length() >= 2 && bleName.substring(0, 2).equals(H8_NAME))
//                ||(bleName.length() >= 2 && bleName.substring(0, 2).equals(H9_BLENAME))
//                ||(bleName.length() >= 3 && bleName.substring(0, 3).equals(W30_NAME))
//                ||(bleName.length() >= 7 && bleName.substring(0, 7).equals(RINGMII_NAME))
//                ||(bleName.length() >= 3 && bleName.substring(0, 3).equals(B30_NAME))
//                ||(bleName.length() >= 3 && bleName.substring(0, 3).equals(B36_NAME))
//                ||(bleName.length() >= 3 && bleName.substring(0, 3).equals(B31_NAME))
//                || (bleName.length() == 2 && bleName.equals(W3_BLENAME))
//                || bleName.length() >= 4 && bleName.substring(0, 4).equals(W06_BLENAME)
//                || (bleName.length() >= 4 && bleName.substring(0, 4).equals(B31S_NAME))
//                || (bleName.length() >= 4 && bleName.substring(0, 4).equals(S500_NAME))) {
//        if (bleName.length() == 2 && bleName.equals(W3_BLENAME)) {
//            return true;
//        } else {
//            return false;
//        }

    }

    /**
     * 判断是否是维亿魄系列的，B30，B31,B36
     *
     * @param bName
     * @return
     */
    public static boolean isVPBleDevice(String bName) {
        String[] bleArray = new String[]{B30_NAME, B31_NAME, B36_NAME, B31S_NAME, S500_NAME};
        Set<String> set = new HashSet<>(Arrays.asList(bleArray));
        return set.contains(bName);
    }


    // 字符串的非空
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input) || "null".equals(input))
            return true;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }


    /**
     * 利用正则表达式判断字符串是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }


    /**
     * 计算手环闹钟的重复时间
     *
     * @param repeat 000000
     * @return 周日..周一
     */
    public static String obtainAlarmDate(Context context, String repeat) {
        if (repeat == null || repeat.length() != 7 || repeat.equals("0000000")) {
            return context.getResources().getString(R.string.repeat_once);
        }
        String[] week = context.getResources().getStringArray(R.array.WeekItems);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            if (repeat.charAt(i) == '1') {
                builder.append(week[i]);
                builder.append(",");
            }
        }
        String result = builder.toString();
        return result.substring(0, result.length() - 1);
    }


    /**
     * 获取当前系统时间 毫秒数
     *
     * @return
     */
    public static Long getNowTime() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间，格式为 :yyyy-MM-dd
     */
    public static String getCurrentDate() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return dateFormat.format(now);
    }

    /**
     * 根据类型获取指定日期
     *
     * @param type 0_今天 1_昨天 2_前天
     * @return "yyyy-MM-dd"
     */
    public static String obtainFormatDate(int type) {
        Date date = new Date();// 当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);// 初始化日历
        calendar.add(Calendar.DATE, 0 - type);// 0,-1,-2
        date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sdf.format(date);
    }

    /**
     * 根据当前日期获取左右一天的日期
     *
     * @param date 条件日期
     * @param left true_前一天 false_后一天
     * @return 计算后的日期
     */
    public static String obtainAroundDate(String date, boolean left) {
        if (date.equals(obtainFormatDate(0)) && !left) {
            return date;//如果传入的日期==今天,而且要求后一天数据,直接返回今天
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date dateTemp = null;
        try {
            dateTemp = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dateTemp == null) return "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTemp);// 初始化日历
        calendar.add(Calendar.DATE, left ? -1 : 1);
        dateTemp = calendar.getTime();
        return sdf.format(dateTemp);
    }


    /**
     * 根据当前日期获取左右一天的日期
     *
     * @param date 条件日期
     * @param left true_前一天 false_后一天
     * @return 计算后的日期
     */
    public static String obtainAroundDate(String date, boolean left, int nor) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date dateTemp = null;
        try {
            dateTemp = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dateTemp == null) return "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTemp);// 初始化日历
        calendar.add(Calendar.DATE, left ? -1 : 1);
        dateTemp = calendar.getTime();
        return sdf.format(dateTemp);
    }


    /**
     * 获取当前时间，格式为 :yyyy-MM-dd
     *
     * @return
     */
    public static String getCurrentDate3() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        String date = dateFormat.format(now);
        return date;
    }

    /**
     * 获取当前时间，格式为 :yyyy-MM-dd
     *
     * @return
     */
    public static String getCurrentDate4() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String date = dateFormat.format(now);
        return date;
    }

    /**
     * 获取yyyy-MM-dd HH:mm:ss格式时间
     *
     * @return
     */
    public static String getCurrentDate1() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String date = dateFormat.format(now);
        return date;
    }

    /**
     * 获取H:mm:ss格式时间
     *
     * @return
     */
    public static String getCurrentDate2() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        String date = dateFormat.format(now);
        return date;
    }

    /**
     * 格式化时间
     *
     * @param format
     * @param date
     * @return
     */
    public static String getLongToDate(String format, long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
        return dateFormat.format(date);
    }

    /**
     * 日期型的string转long
     *
     * @param date
     * @return
     */
    public static long getStrDateToLong(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            return sdf.parse(date).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    /**
     * 判断两个日期是否相等
     *
     * @param str2
     * @return
     */
    public static boolean isEquesValue(String str2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        try {
            long startLongTime = sdf.parse(getCurrentDate()).getTime();
            long endLongTime = sdf.parse(str2).getTime();

            //结束日期减开始日期
            long diffDay = endLongTime - startLongTime;

            return diffDay == 0;


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 计算间隔时间
     *
     * @param startTime 开启日期
     * @param endTime   结束日期
     * @return
     * @cycleNum
     */
    public static long intervalTime(long startTime, long endTime, int cycleNum) {
        int i = (int) ((endTime - startTime) / 86400000 / cycleNum);
        i = (endTime - startTime) / 86400000 % cycleNum == 0 ? i - 1 : i;
        long resultTim = i * 86400000L * cycleNum;
        return resultTim;
    }


    /**
     * 日期相差多少，string类型
     *
     * @param startTime
     * @param endTime
     * @return 毫秒
     */
    public static int intervalTime(String startTime, String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        try {
            long startLongTime = sdf.parse(startTime).getTime();
            long endLongTime = sdf.parse(endTime).getTime();

            //结束日期减开始日期
            long diffDay = endLongTime - startLongTime;

            return (int) (diffDay > 0 ? diffDay / 86400000L : diffDay == 0 ? 0 : diffDay / 86400000L);


        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }


    /**
     * 日期相差多少，string类型
     *
     * @param startTime
     * @param endTime
     * @return 毫秒
     */
    public static int intervalTimeStr(String startTime, String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        try {
            long startLongTime = sdf.parse(startTime).getTime();
            long endLongTime = sdf.parse(endTime).getTime();

            //结束日期减开始日期
            long diffDay = endLongTime - startLongTime;
            return (int) (diffDay > 0 ? diffDay / 60000 : diffDay == 0 ? 0 : diffDay / 60000);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }


    /**
     * 计算第几周
     *
     * @param startDate
     * @param currDate
     * @return
     */
    public static int babyWeeks(String startDate, String currDate) {
        Log.e(TAG, "-------开始=" + startDate + "--当前时间=" + currDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        try {
            //转long
            long startLongDate = sdf.parse(startDate).getTime();
            long currLongDate = sdf.parse(currDate).getTime();

            //相差多少
            long dirrLong = currLongDate - startLongDate;
            if (dirrLong < 0)
                return -1;
            //转换成天
            int dirrDay = (int) (dirrLong / 86400000L);
            return dirrDay % 7 == 0 ? dirrDay / 7 : dirrDay < 7 ? 1 : getRemData(dirrDay);

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    private static int getRemData(int date) {
        String result = div(date, 7, 2) + "";
        String beforeD = StringUtils.substringBefore(result, ".");
        return Integer.valueOf(beforeD.trim()) + 1;

    }

    /**
     * 获取当前日期的long型
     *
     * @return
     */
    public static long getCurrDayLong() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        try {
            return sdf.parse(getCurrentDate()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取指定日期的long型
     *
     * @return
     */
    public static long getCurrDayLong(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        try {
            return sdf.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 得到几天前的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static Date getDateBefore(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }


    /**
     * 比较两个日期的大小
     *
     * @param currDay 当前日期
     * @param comDay  需要比较的日期
     * @return
     */
    public static boolean comPariDate(String currDay, String comDay) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        try {
            //转换成long类型
            long currLongDay = sdf.parse(currDay).getTime();
            //需要比较的时间
            long comLongDay = sdf.parse(comDay).getTime();

            return comLongDay > currLongDay;


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 比较两个日期的大小 yyyy-MM-dd HH:MM:SS格式
     *
     * @param currDay 当前日期
     * @param comDay  需要比较的日期
     * @return
     */
    public static boolean comPariDateDetail(String currDay, String comDay) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        try {
            //转换成long类型
            long currLongDay = sdf.parse(currDay).getTime();
            //需要比较的时间
            long comLongDay = sdf.parse(comDay).getTime();

            return comLongDay > currLongDay;


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 除法运算
     *
     * @param v1
     * @param v2
     * @param scale
     * @return
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        if (v2 <= 0) {
            return 0;
        } else {
            BigDecimal b1 = new BigDecimal(Double.toString(v1));
            BigDecimal b2 = new BigDecimal(Double.toString(v2));
            return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
    }

    /**
     * 两个double相乘
     *
     * @param v1
     * @param v2
     * @return
     */
    public static Double mul(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.multiply(b2).doubleValue();
    }


//    //判断错误类型
//    public static String getMessage(Object error,Context context){
//        if(error instanceof ConnectTimeoutException){  //连接超时
//            return context.getResources().getString(R.string.error_timeout);
//        }else if(error instanceof ConnectException){  	//客户端请求超时
//            return context.getResources().getString(R.string.error_connetexection);
//        }else if(error instanceof SocketTimeoutException){	//服务器响应超时,客户端已请求，服务器未响应
//            return context.getResources().getString(R.string.error_sockettimeout);
//        }else if(error instanceof JSONException){	//JSON解析异常
//            return context.getResources().getString(R.string.error_jsonexection);
//        }else if(error instanceof Resources.NotFoundException){	//404 地址为找到
//            return context.getResources().getString(R.string.error_notfound);
//        }
//        return context.getResources().getString(R.string.error_message);
//    }


    /**
     * j计算步长
     *
     * @param
     * @param height
     * @return
     */
    public static double getStepLong(int height) {
        double stepLong;
        if (height < 155) {

            //  stepLong = (height *20)/(42*100);
            stepLong = WatchUtils.div(WatchUtils.mul(Double.valueOf(height), Double.valueOf(20)),
                    WatchUtils.mul(Double.valueOf(42), Double.valueOf(100)), 2);
        } else if (height >= 155 && height < 174) {

//            stepLong = (height *13)/(28*100);

            stepLong = WatchUtils.div(WatchUtils.mul(Double.valueOf(height), Double.valueOf(13)),
                    WatchUtils.mul(Double.valueOf(28), Double.valueOf(100)), 2);

        } else {
            //  stepLong = (height *19)/(42*100);
            stepLong = WatchUtils.div(WatchUtils.mul(Double.valueOf(height), Double.valueOf(19)),
                    WatchUtils.mul(Double.valueOf(42), Double.valueOf(100)), 2);
        }

        return stepLong;

    }

    /**
     * 计算路程
     */
    public static double getDistants(int step, double stepLong) {

        return WatchUtils.div(WatchUtils.mul(Double.valueOf(step), stepLong), Double.valueOf(1000), 2);

    }


    /**
     * 计算路程
     */
    public static double getDistants(int step, int height) {

        return WatchUtils.div(WatchUtils.mul(Double.valueOf(step), getStepLong(height)), Double.valueOf(1000), 2);

    }


    /**
     * 计算卡里路
     */
    public static double getKcal(int step, double stepLong) {
        double distans = WatchUtils.div(WatchUtils.mul(Double.valueOf(step), stepLong), Double.valueOf(1000), 5);
        return WatchUtils.mul(distans, 65.4);
    }


    /**
     * 计算卡里路
     */
    public static double getKcal(int step, int height) {
        double distans = WatchUtils.div(WatchUtils.mul(Double.valueOf(step), getStepLong(height)), Double.valueOf(1000), 5);
        return WatchUtils.mul(distans, 65.4);
    }


    /**
     * 获取应用版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager packM = context.getPackageManager();
            PackageInfo packInfo = packM.getPackageInfo(context.getPackageName(), 0);

            return packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;

    }

    /**
     * 获取手机IME码
     */
    @SuppressLint("MissingPermission")
    public static String getPhoneInfo(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();

    }


    // 根据年月日计算年龄,birthTimeString:"1994-11-14"
    public static int getAgeFromBirthTime2(String birthTimeString) {
        // 先截取到字符串中的年、月、日
        Log.d("-------birthTimeString-", birthTimeString);
        String strs[] = birthTimeString.trim().split("-");
        int selectYear = Integer.parseInt(strs[0]);
        int selectMonth = Integer.parseInt(strs[1]);
        int selectDay = Integer.parseInt(strs[2]);
        // 得到当前时间的年、月、日
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayNow = cal.get(Calendar.DATE);

        // 用当前年月日减去生日年月日
        int yearMinus = yearNow - selectYear;
        int monthMinus = monthNow - selectMonth;
        int dayMinus = dayNow - selectDay;

        int age = yearMinus;// 先大致赋值
        if (yearMinus < 0) {// 选了未来的年份
            age = 0;
        } else if (yearMinus == 0) {// 同年的，要么为1，要么为0
            if (monthMinus < 0) {// 选了未来的月份
                age = 0;
            } else if (monthMinus == 0) {// 同月份的
                if (dayMinus < 0) {// 选了未来的日期
                    age = 0;
                } else if (dayMinus >= 0) {
                    age = 1;
                }
            } else if (monthMinus > 0) {
                age = 1;
            }
        } else if (yearMinus > 0) {
            if (monthMinus < 0) {// 当前月>生日月
            } else if (monthMinus == 0) {// 同月份的，再根据日期计算年龄
                if (dayMinus < 0) {
                } else if (dayMinus >= 0) {
                    age = age - 1;
                }
            } else if (monthMinus > 0) {
                age = age - 1;
            }
        }
        return age;
    }


    // 根据年月日计算年龄,birthTimeString:"1994-11-14"
    public static int getAgeFromBirthTime(String birthTimeString) {

        MyLogUtil.d("---生日---", birthTimeString);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if (birthTimeString.contains(".")) {
            formatter = new SimpleDateFormat("yyyy.MM.dd");
        } else if (birthTimeString.contains("-")) {
            formatter = new SimpleDateFormat("yyyy-MM-dd");
        }

        Date date = null;
        try {
            date = formatter.parse(birthTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
            Log.d("-----年龄啊，", age + "");
            return age;
        }
        return 0;
    }


    /**
     * 转换为高8位
     *
     * @param v
     * @return
     */
    public static byte loUint16(short v) {
        return (byte) (v & 255);
    }

    /**
     * 转换为低8位
     *
     * @param v
     * @return
     */
    public static byte hiUint16(short v) {
        return (byte) (v >> 8);
    }

    //获取闹钟的action
    public static IntentFilter regeditAlarmBraod() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.bozhilun.android.siswatch.alarm");
        return intentFilter;
    }

    /**
     * 蓝牙连接状态的action
     */
    public static IntentFilter h8ConnectState() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WatchUtils.WATCH_CONNECTED_STATE_ACTION);
        intentFilter.addAction(WatchUtils.WATCH_GETWATCH_STEPS_ACTION);
        intentFilter.addAction(WatchUtils.WATCH_OPENTAKE_PHOTO_ACTION);
        return intentFilter;
    }

    /**
     * 公里转换为英里 1英里(mi)=1.609344公里(km)、1公里(km)=0.6213712英里(mi)
     *
     * @param km
     * @param
     * @return
     */
    public static double kmToMi(double km) {
        double tempkmmi = 0.62;
        return WatchUtils.mul(km, tempkmmi);
    }

    /**
     * 检测服务是否处于运行状态
     *
     * @param servicename
     * @param context
     * @return
     */
    public static boolean isServiceRunning(String servicename, Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : infos) {
            if (servicename.equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 断开H8共用处理
     */
    public static void disCommH8() {
        MyApp.getInstance().getWatchBluetoothService().disconnect();//断开蓝牙
        MyCommandManager.deviceDisconnState = true;
        MyCommandManager.ADDRESS = null;
        MyCommandManager.DEVICENAME = null;
        SharedPreferencesUtils.saveObject(MyApp.getContext(), Commont.BLENAME, "");
        SharedPreferencesUtils.saveObject(MyApp.getContext(), Commont.BLEMAC, "");
        MyApp.getInstance().setMacAddress(null);// 清空全局
        SharedPreferencesUtils.setParam(MyApp.getContext(), "stepsnum", "0");
    }

    public static String getAppInfo(Context mContext) {
        try {
            String pkName = mContext.getPackageName();
            String versionName = mContext.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            int versionCode = mContext.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
            return pkName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //版本名称
    public static String getVersionName(Context mContext) {
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = mContext.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            String version = packInfo.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 判断本应用是否存活
     * 如果需要判断本应用是否在后台还是前台用getRunningTask
     */
    public static boolean isAPPALive(Context mContext, String packageName) {
        boolean isAPPRunning = false;
        // 获取activity管理对象
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        // 获取所有正在运行的app
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        // 遍历，进程名即包名
        for (ActivityManager.RunningAppProcessInfo appInfo : appProcessInfoList) {
            if (packageName.equals(appInfo.processName)) {
                isAPPRunning = true;
                break;
            }
        }
        return isAPPRunning;
    }


    //分享
    public static void shareCommData(Activity activity) {
        String filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + System.currentTimeMillis() + ".png";
        ScreenShot.shoot(activity, new File(filePath));
        Common.showShare(activity, null, false, filePath);
    }


    /**
     * 保留小数点后两位
     *
     * @param data
     * @return
     */
    public static String resrevedDeci(String data) {
        if (!data.contains(".")) {
            return data;
        } else {
            //截取小数点前的数据
            String beforeDeci = StringUtils.substringBefore(data, ".");
            //截取小数点后的数据
            String afterDeci = StringUtils.substringAfter(data, ".");

            if (afterDeci.length() > 2) {
                return (Integer.valueOf(beforeDeci) == 0 ? 0 : beforeDeci) + "." + afterDeci.substring(0, 2);
            } else {
                return (Integer.valueOf(beforeDeci) == 0 ? 0 : beforeDeci) + "." + afterDeci;
            }
        }
    }


    //判断公英制
    public static boolean judgeUnit2(Context context) {
        String unitKm = (String) SharedPreferencesUtils.getParam(context, Commont.H8_UNIT, "km");
        if (WatchUtils.isEmpty(unitKm))
            return false;
        if (unitKm.equals(Commont.H8_KM)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 判断公英制
     *
     * @param context
     * @return
     */
    public static boolean judgeUnit(Context context) {
        return (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);
    }


    //公制转英制
    public static double unitToImperial(double kmData) {
        double unitCons = 0.6214;
        return mul(kmData, unitCons);
    }

    //公制转英制
    public static double unitToImperial(double kmData, int deci) {
        double unitCons = 0.6214;
        double result = mul(kmData, unitCons);
        //截取小数点后的数据
        String deciAfter = StringUtils.substringAfter(result + "", ".");
        if (deci >= deciAfter.length()) {
            return result;
        } else { //1.1234
            String interStr = deciAfter.substring(0, deci);
            return Double.valueOf(StringUtils.substringBefore(result + "", ".") + "." + interStr);
        }
    }


    /**
     * 公制转英制后，保留小数点后2位
     *
     * @param kmData
     * @param deci   无意义
     * @return
     */
    public static double doubleunitToImperial(double kmData, int deci) {
        double unitCons = 0.6214;
        double imperialData = mul(kmData, unitCons);
        String tmpImper = String.valueOf(imperialData);
        //小数点前的数据
        String deciBefore = StringUtils.substringBefore(tmpImper, ".");
        //截取小数点后的数据
        String deciAfter = StringUtils.substringAfter(tmpImper, ".");
        //保留小数点后2位
        if (deciAfter.length() >= 2) {
            //直接截取前两位
            String afterD = deciAfter.substring(0, 2);
            return Double.valueOf(deciBefore + "." + afterD);
        } else {
            return imperialData;
        }
    }


    /**
     * 计算英制身高
     */
    public static String obtainHeight(String mHeight) {
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
    public static String obtainWeight(String mWeight) {
        int tmpWid = Integer.valueOf(mWeight.trim());
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


    /**
     * 身高转换，有的账号身高会返回 cm
     *
     * @param height
     * @return
     */
    public static int userHeightConver(String height) {
        if (height.contains("cm")) {
            String newHeight = height.substring(0, height.length() - 2);
            return Integer.valueOf(newHeight);
        } else {
            return Integer.valueOf(height.trim());
        }
    }


    public static void verServerCode(Context context, String code) {
        String txt = null;
        int serverCode;
        try {
            serverCode = Integer.valueOf(code);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showToast(context, code);
            return;
        }

        switch (serverCode) {
            case 1:     //成功
                txt = context.getResources().getString(R.string.submit_success);
                break;
            case 2:     //失败
                txt = "失败";
                break;
            case 3:     //用户已被注册
                txt = context.getResources().getString(R.string.yonghuzhej);
                break;
            case 4:     //用户名或密码错误
                txt = context.getResources().getString(R.string.miamacuo);
                break;
            case 5:     //服务异常
                txt = context.getResources().getString(R.string.ssdk_sms_dialog_error_desc_100);
                break;
            case 6:     //用户不存在或验证码失效
                txt = "用户不存在或验证码失效";
                break;
            case 7:     //关键参数不能为空
                txt = context.getResources().getString(R.string.ssdk_sms_dialog_error_desc_103);
                break;
            case 10:       //无数据
                txt = context.getResources().getString(R.string.string_no_data);
                break;
            case 11:        //日期格式错误
                txt = "日期格式错误";
                break;
            case 12:        //json格式错误
                txt = "json格式错误";
                break;
            case 13:        //该用户不存在
                txt = "该用户不存在";
                break;
            case 14:    //没有申请记录
                txt = "没有申请记录";
                break;
            case 15:    //验证码次数已用完,请明天再请求
                txt = context.getResources().getString(R.string.ssdk_sms_dialog_error_desc_107);
                break;
            case 16:     //验证码错误
                txt = context.getResources().getString(R.string.yonghuzdffhej);
                break;
            case 99999:     //网络异常
                txt = context.getResources().getString(R.string.ssdk_sina_web_net_error);
                break;

        }

        ToastUtil.showToast(context, txt);
    }

    /**
     * 跳转至应用宝
     *
     * @param context
     * @param packName
     */
    public static void gotoAppStory(Context context, String packName) {
        Uri uri = Uri.parse("market://details?id=" + packName);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            intent.setClassName("com.tencent.android.qqdownloader", "com.tencent.pangu.link.LinkProxyActivity");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    // 使用Intent queryIntentActivities 判断
    //判断手机内是否安装指定应用
    public static boolean isClientInstalled(Context context, String appPackageName) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setPackage(appPackageName);
        i.setType("image/*");
        try {
            PackageManager pm = context.getPackageManager();
            @SuppressLint("WrongConstant") List<?> ris = pm.queryIntentActivities(i, PackageManager.GET_ACTIVITIES);
            return ris != null && ris.size() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 判断是否是B36
     *
     * @param context
     * @return
     */
    public static boolean isB36Device(Context context) {
        //蓝牙名字
        String bleName = (String) SharedPreferencesUtils.readObject(context, Commont.BLENAME);
        if (!WatchUtils.isEmpty(bleName) && bleName.equals("B36")) {
            return true;
        } else if (!WatchUtils.isEmpty(bleName) && bleName.equals("W30")) {
            return true;
        } else if (!WatchUtils.isEmpty(bleName) && bleName.equals("H9")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取保存的蓝牙mac地址
     *
     * @param context
     * @return
     */
    public static String getSherpBleMac(Context context) {
        //蓝牙名字
        String bleName = (String) SharedPreferencesUtils.readObject(context, Commont.BLEMAC);
        return !isEmpty(bleName) ? bleName : null;
    }


    /**
     * 获取保存的蓝牙名字
     *
     * @param context
     * @return
     */
    public static String getSherpBleName(Context context) {
        //蓝牙名字
        String bleName = (String) SharedPreferencesUtils.readObject(context, Commont.BLENAME);
        return !isEmpty(bleName) ? bleName : null;
    }


    /**
     * 判断是否是B36和其它设备
     *
     * @param context
     * @return
     */
    public static boolean isB36Device(Context context, String bName) {
        //蓝牙名字
        String bleName = (String) SharedPreferencesUtils.readObject(context, Commont.BLENAME);
        if (!WatchUtils.isEmpty(bleName) &&
                (bleName.equals("B36")
                        || bleName.equals("500S")
                        || bleName.equals("B31S")
                        || bleName.equals(bName))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否是B36的女性用户
     * 保存的性别非空，在登录成功后调用此方法
     *
     * @param context
     * @return
     */
    public static boolean isB36SexWomen(Context context) {
        //蓝牙名字
        String bleName = (String) SharedPreferencesUtils.readObject(context, Commont.BLENAME);
        //登录过用户的性别
        String uSex = (String) SharedPreferencesUtils.getParam(context, Commont.USER_SEX, "");
        if (!WatchUtils.isEmpty(bleName) && !WatchUtils.isEmpty(uSex)) {
            if (bleName.equals("B36") && uSex.equals("F")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }


    //获取用户的信息

    /**
     * B30系列的用户bean
     *
     * @param goalStep 目标步数
     * @return
     */
    public static PersonInfoData getUserPerson(int goalStep) {
        PersonInfoData personInfoData = null;
        //同步用户信息
        String userData = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.USER_INFO_DATA);
        if (isEmpty(userData))
            return null;
        UserInfoBean userInfoBean = JSON.parseObject(userData, UserInfoBean.class);
        if (userInfoBean == null) {
            return null;
        }
        //体重
        String tmpWeight = userInfoBean.getWeight();
        int userWeight;
        if (tmpWeight.contains("kg")) {
            userWeight = Integer.valueOf(StringUtils.substringBefore(tmpWeight, "kg").trim());
        } else {
            userWeight = Integer.valueOf(tmpWeight.trim());
        }

        //身高
        String tmpHeight = userInfoBean.getHeight();
        int userHeight;
        if (tmpHeight.contains("cm")) {
            userHeight = Integer.valueOf(StringUtils.substringBefore(tmpHeight, "cm").trim());
        } else {
            userHeight = Integer.valueOf(tmpHeight.trim());
        }
        String tmpSex = userInfoBean.getSex();
        //性别
        ESex eSex = tmpSex.equals("M") ? ESex.valueOf("MAN") : ESex.valueOf("WOMEN");
        //年龄
        String userBirthday = userInfoBean.getBirthday();
        int userAge = WatchUtils.getAgeFromBirthTime(userBirthday);
        personInfoData = new PersonInfoData(eSex, userHeight, userWeight, userAge, goalStep);
        return personInfoData;

    }

    /**
     * 根据日期获取是周几
     *
     * @param context
     * @param week
     * @return
     */
    public static String getWeekData(Context context, int week) {
        String weekData = null;
        switch (week) {
            case 0:     //周日
                weekData = context.getResources().getString(R.string.sunday);
                break;
            case 1:     //周一
                weekData = context.getResources().getString(R.string.monday);
                break;
            case 2:     //周二
                weekData = context.getResources().getString(R.string.tuesday);
                break;
            case 3:     //周三
                weekData = context.getResources().getString(R.string.wednesday);
                break;
            case 4:     //周日
                weekData = context.getResources().getString(R.string.thursday);
                break;
            case 5:     //周五
                weekData = context.getResources().getString(R.string.friday);
                break;
            case 6:     //周六
                weekData = context.getResources().getString(R.string.saturday);
                break;

        }
        return weekData;
    }


    //根据秒转时分秒
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = "00" + ":" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }


    //获取手机的状态信息正常，震动，静音
    public static int getPhoneStatus() {
        int PHONE_STATUS = AudioManager.RINGER_MODE_NORMAL;
        AudioManager audioManager = (AudioManager) MyApp.getInstance().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int ringMode = audioManager.getRingerMode();
            //audioManager.getStreamVolume()
            Log.e(TAG, "-------手环模式=" + ringMode);
            switch (ringMode) {
                case AudioManager.RINGER_MODE_NORMAL:
                    //普通模式
                    PHONE_STATUS = AudioManager.RINGER_MODE_NORMAL;
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    //振动模式
                    PHONE_STATUS = AudioManager.RINGER_MODE_VIBRATE;
                    break;
                case AudioManager.RINGER_MODE_SILENT:
                    //静音模式
                    PHONE_STATUS = AudioManager.RINGER_MODE_SILENT;
                    break;
            }

        }
        return PHONE_STATUS;
    }

    /**
     * 判断str1中包含str2的个数
     *
     * @param str1
     * @param str2
     * @return counter
     */
    private static int num = 0;

    public static int countStr(String str1, char str2) {
        // 出现次数
        num = 0;
        // 循环遍历每个字符，判断是否是字符 a ，如果是，累加次数
        for (int i = 0; i < str1.length(); i++) {
            // 获取每个字符，判断是否是字符a
            if (str1.charAt(i) == str2) {
                // 累加统计次数
                num++;
            }
        }
        return num;
    }

    /**
     * 计算手环闹钟的重复时间
     *
     * @param repeat 000000
     * @return 周日..周一
     */
    public static String obtainB15PAlarmDate(Context context, String repeat) {
        if (repeat == null || repeat.length() != 7 || repeat.equals("0000000")) {
//            return context.getResources().getString(R.string.repeat_once);
            return "";
        }
        String[] week = context.getResources().getStringArray(R.array.B15PWeekItems);
        StringBuilder builder = new StringBuilder();
        //0000100
        for (int i = 0; i < 7; i++) {
            if (repeat.charAt(i) == '1') {
                builder.append(week[i]);
                builder.append(",");
            }
        }
        String result = builder.toString();
        return result.substring(0, result.length() - 1);
    }

    /**
     * 设置消息提醒的开关
     *
     * @param context
     */
    public static void setCommSocailMsgSetting(Context context) {
        boolean isOpenPhone = (boolean) SharedPreferencesUtils.getParam(context, Commont.ISPhone, false);
        boolean isMsg = (boolean) SharedPreferencesUtils.getParam(context, Commont.ISMsm, false);
        boolean isWeChat = (boolean) SharedPreferencesUtils.getParam(context, Commont.ISWechart, false);
        boolean isQQ = (boolean) SharedPreferencesUtils.getParam(context, Commont.ISQQ, false);
        boolean isFaceBook = (boolean) SharedPreferencesUtils.getParam(context, Commont.ISFacebook, false);
        boolean isTwitter = (boolean) SharedPreferencesUtils.getParam(context, Commont.ISTwitter, false);
        boolean isLinkin = (boolean) SharedPreferencesUtils.getParam(context, Commont.ISLINE, false);
        boolean isWhats = (boolean) SharedPreferencesUtils.getParam(context, Commont.ISWhatsApp, false);
        boolean isLine = (boolean) SharedPreferencesUtils.getParam(context, Commont.ISLINE, false);
        boolean isSkype = (boolean) SharedPreferencesUtils.getParam(context, Commont.ISSkype, false);
        boolean isOther = (boolean) SharedPreferencesUtils.getParam(context, Commont.ISOther, false);

        FunctionSocailMsgData socailMsgData = new FunctionSocailMsgData();
        //电话提醒
        if (isOpenPhone) {
            socailMsgData.setPhone(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setPhone(EFunctionStatus.SUPPORT_CLOSE);
        }
        //短信
        if (isMsg) {
            socailMsgData.setMsg(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setMsg(EFunctionStatus.SUPPORT_CLOSE);
        }
        //微信
        if (isWeChat) {
            socailMsgData.setWechat(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setWechat(EFunctionStatus.SUPPORT_CLOSE);
        }
        //QQ
        if (isQQ) {
            socailMsgData.setQq(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setQq(EFunctionStatus.SUPPORT_CLOSE);
        }
        //新浪 不支持
        socailMsgData.setSina(EFunctionStatus.UNSUPPORT);
        //facebook
        if (isFaceBook) {
            socailMsgData.setFacebook(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setFacebook(EFunctionStatus.SUPPORT_CLOSE);
        }

        if (isTwitter) {
            socailMsgData.setTwitter(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setTwitter(EFunctionStatus.SUPPORT_CLOSE);
        }
        //flicker  不支持
        socailMsgData.setFlickr(EFunctionStatus.UNSUPPORT);
        if (isLinkin) {
            socailMsgData.setLinkin(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setLinkin(EFunctionStatus.SUPPORT_CLOSE);
        }

        if (isWhats) {
            socailMsgData.setWhats(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setWhats(EFunctionStatus.SUPPORT_CLOSE);
        }
        if (isLine) {
            socailMsgData.setLine(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setLine(EFunctionStatus.SUPPORT_CLOSE);
        }

        //instagram
        socailMsgData.setInstagram(EFunctionStatus.SUPPORT_OPEN);
        //snapchat
        socailMsgData.setSnapchat(EFunctionStatus.SUPPORT_OPEN);
        if (isSkype) {
            socailMsgData.setSkype(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setSkype(EFunctionStatus.SUPPORT_CLOSE);
        }
        //gmail
        socailMsgData.setGmail(EFunctionStatus.SUPPORT_OPEN);

        //isOther
        socailMsgData.setOther(isOther ? EFunctionStatus.SUPPORT_OPEN : EFunctionStatus.SUPPORT_CLOSE);


        Log.e(TAG, "-------------socailMsgData=" + socailMsgData.toString());

        MyApp.getInstance().getVpOperateManager().settingSocialMsg(iBleWriteResponse, new ISocialMsgDataListener() {
            @Override
            public void onSocialMsgSupportDataChange(FunctionSocailMsgData functionSocailMsgData) {
                Log.d(TAG, "-----设置-=" + functionSocailMsgData.toString());
            }
        }, socailMsgData);

    }


    //开关设置
    public static void setSwitchCheck() {
        //运动过量提醒 B31不支持
        EFunctionStatus isOpenSportRemain = EFunctionStatus.UNSUPPORT;
        //血压/心率播报 B31不支持
        EFunctionStatus isOpenVoiceBpHeart = EFunctionStatus.UNSUPPORT;
        //查找手表  B31不支持
        EFunctionStatus isOpenFindPhoneUI;
        //秒表功能  支持
        EFunctionStatus isOpenStopWatch;
        //低压报警 支持
        EFunctionStatus isOpenSpo2hLowRemind = EFunctionStatus.SUPPORT_OPEN;
        //肤色功能 支持
        EFunctionStatus isOpenWearDetectSkin = EFunctionStatus.SUPPORT_OPEN;

        //自动接听来电 不支持
        EFunctionStatus isOpenAutoInCall = EFunctionStatus.UNSUPPORT;
        //自动检测HRV 支持
        EFunctionStatus isOpenAutoHRV = EFunctionStatus.SUPPORT_OPEN;
        //断连提醒 支持
        EFunctionStatus isOpenDisconnectRemind;
        //SOS  不支持
        EFunctionStatus isOpenSOS;// = EFunctionStatus.UNSUPPORT;


        //保存的状态
        boolean isSystem = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
        boolean is24Hour = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.IS24Hour, false);//是否为24小时制
        boolean isAutomaticHeart = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISAutoHeart, true);//自动测量心率
        boolean isAutomaticBoold = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISAutoBp, true);//自动测量血压
        boolean isSecondwatch = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSecondwatch, false);//秒表
        boolean isWearCheck = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISWearcheck, true);//佩戴
        boolean isFindPhone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISFindPhone, false);//查找手机
        boolean CallPhone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISCallPhone, false);//来电
        boolean isDisconn = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISDisAlert, false);//断开连接提醒
        boolean isSos = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISHelpe, false);//sos


        //秒表功能
        if (isSecondwatch) {
            isOpenStopWatch = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isOpenStopWatch = EFunctionStatus.SUPPORT_CLOSE;
        }
        //断连提醒
        if (isDisconn) {
            isOpenDisconnectRemind = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isOpenDisconnectRemind = EFunctionStatus.SUPPORT_CLOSE;
        }

        //查找手机
        if (isFindPhone) {
            isOpenFindPhoneUI = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isOpenFindPhoneUI = EFunctionStatus.SUPPORT_CLOSE;
        }

        //sos
        if (isSos) {
            isOpenSOS = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isOpenSOS = EFunctionStatus.SUPPORT_CLOSE;
        }

        CustomSetting customSetting = new CustomSetting(true, isSystem, is24Hour, isAutomaticHeart,
                isAutomaticBoold, isOpenSportRemain, isOpenVoiceBpHeart, isOpenFindPhoneUI, isOpenStopWatch, isOpenSpo2hLowRemind,
                isOpenWearDetectSkin, isOpenAutoInCall, isOpenAutoHRV, isOpenDisconnectRemind, isOpenSOS);
        Log.e(TAG, "--------customSetting=" + customSetting.toString());

        MyApp.getInstance().getVpOperateManager().changeCustomSetting(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {

            }
        }, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {
                Log.e(TAG, "----customSettingData=" + customSettingData.toString());
            }
        }, customSetting);


        //B31血氧夜间检测
        //boolean isB31NightSpo2Check = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(),Commont.B31_Night_BPOxy,false);
        AllSetSetting allSetSetting = new AllSetSetting(EAllSetType.SPO2H_NIGHT_AUTO_DETECT,
                22, 0, 8, 0, 0, 1);
        MyApp.getInstance().getVpOperateManager().settingSpo2hAutoDetect(iBleWriteResponse, new IAllSetDataListener() {
            @Override
            public void onAllSetDataChangeListener(AllSetData allSetData) {

            }
        }, allSetSetting);


    }


    private static IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };


    /**
     * 判断请求返回是否成功200成功
     *
     * @param str
     * @return
     */
    public static boolean isNetRequestSuccess(String str) {
        boolean isSuccess = false;
        try {
            JSONObject jsonObject = new JSONObject(str);
            int resultCode = jsonObject.getInt("code");
            if (resultCode == 200)
                isSuccess = true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return isSuccess;
    }

    static String[] timeStr = new String[]{"00:00","00:30","01:00","01:30","02:00","02:30","03:00","03:30","04:00","04:30","05:00","05:30","06:00",
            "06:30","07:00","07:30","08:00","08:30","09:00","09:30","10:00","10:30","11:00","11:30","12:00","12:30","13:00","13:30","14:00",
            "14:30","15:00","15:30","16:00","16:30","17:00","17:30","18:00","18:30","19:00","19:30","20:00","20:30","21:00","21:30","22:00",
            "22:30","23:00","23:30"};


    public static Map<String,String> setHalfDateMap(){
        Map<String,String>  map = new HashMap<>();
        for(int i = 0;i<timeStr.length;i++){
            map.put(timeStr[i],0+"");
        }
        return map;

    }


}
