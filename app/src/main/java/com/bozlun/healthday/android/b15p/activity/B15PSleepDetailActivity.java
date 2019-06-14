package com.bozlun.healthday.android.b15p.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.bozlun.healthday.android.MyApp;
import com.bozlun.healthday.android.R;
import com.bozlun.healthday.android.b15p.b15pdb.B15PDBCommont;
import com.bozlun.healthday.android.b15p.b15pdb.B15PSleepDB;
import com.bozlun.healthday.android.b30.b30view.B15PCusSleepView;
import com.bozlun.healthday.android.siswatch.WatchBaseActivity;
import com.bozlun.healthday.android.siswatch.utils.WatchUtils;
import com.bozlun.healthday.android.util.Constant;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30S_SleepDataItem;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 睡眠详情
 */
public class B15PSleepDetailActivity extends WatchBaseActivity {

    private static final String TAG = "B30SleepDetailActivity";

    /**
     * 跳转到B30SleepDetailActivity,并附带参数
     *
     * @param context 启动源上下文
     * @param date    附带的参数:日期
     */
    public static void startAndParams(Context context, String date) {
        Intent intent = new Intent(context, B15PSleepDetailActivity.class);
        intent.putExtra(Constant.DETAIL_DATE, date);
        context.startActivity(intent);
    }

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;
    @BindView(R.id.detailSleepQuitRatingBar)
    RatingBar detailSleepQuitRatingBar;
    @BindView(R.id.detailCusSleepView)
    B15PCusSleepView detailCusSleepView;
    @BindView(R.id.detailAllSleepTv)
    TextView detailAllSleepTv;
    @BindView(R.id.detailAwakeNumTv)
    TextView detailAwakeNumTv;
    @BindView(R.id.detailStartSleepTv)
    TextView detailStartSleepTv;
    @BindView(R.id.detailAwakeTimeTv)
    TextView detailAwakeTimeTv;
    @BindView(R.id.detailDeepTv)
    TextView detailDeepTv;
    @BindView(R.id.detailHightSleepTv)
    TextView detailHightSleepTv;
    @BindView(R.id.sleepCurrDateTv)
    TextView sleepCurrDateTv;
    @BindView(R.id.sleepSeekBar)
    SeekBar sleepSeekBar;

    private List<Integer> listValue;

    /**
     * 当前显示的日期(数据根据日期加载)
     */
    private String currDay;
    /**
     * Json工具类
     */
    private Gson gson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b15p_sleep_detail_layout);
        ButterKnife.bind(this);
        initViews();
        initData();
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.sleep));
//        commentB30ShareImg.setVisibility(View.VISIBLE);
        detailSleepQuitRatingBar.setMax(5);
        detailSleepQuitRatingBar.setNumStars(5);
        detailSleepQuitRatingBar.setRating(0);
//        detailSleepQuitRatingBar.setRating(5);
        // detailSleepQuitRatingBar.setRating(100);

        listValue = new ArrayList<>();
        gson = new Gson();
        currDay = getIntent().getStringExtra(Constant.DETAIL_DATE);
    }

    private void initData() {
        sleepCurrDateTv.setText(currDay);
        String mac = MyApp.getInstance().getMacAddress();
        if (WatchUtils.isEmpty(mac))
            return;

        List<W30S_SleepDataItem> sleepDataItemList = new ArrayList<>();
        List<B15PSleepDB> sleepAllDatas = B15PDBCommont.getInstance().findSleepAllDatas(mac, currDay);
        if (sleepAllDatas != null && !sleepAllDatas.isEmpty()) {

            for (int i = 0; i < sleepAllDatas.size(); i++) {
                B15PSleepDB b15PSleepDB = sleepAllDatas.get(i);
                //2019-04-24 00:41:00
                String sleepDatas = b15PSleepDB.getSleepData();
                String sleepTime = b15PSleepDB.getSleepTime();
                if ((sleepDatas.substring(0, 10).equals(currDay)
                        && Integer.valueOf(sleepTime.substring(0, 2)) <= 16)
                        || (sleepDatas.substring(0, 10).equals(WatchUtils.obtainAroundDate(currDay, true))
                        && Integer.valueOf(sleepTime.substring(0, 2)) >= 21)){
                    String sleepType = b15PSleepDB.getSleepType();
                    W30S_SleepDataItem w30S_sleepDataItem = new W30S_SleepDataItem();
                    w30S_sleepDataItem.setSleep_type(sleepType);
                    w30S_sleepDataItem.setStartTime(sleepTime);
                    sleepDataItemList.add(w30S_sleepDataItem);
                }
            }

            if ( !sleepDataItemList.isEmpty()) {
                Log.e(TAG, "===" + sleepDataItemList.toString());

                sleepCalculation(sleepDataItemList, currDay);
            } else {
                if (detailCusSleepView != null)
                    detailCusSleepView.setSleepList(new ArrayList<Integer>());
//                if (detailSleepQuitRatingBar!=null){
//                    detailSleepQuitRatingBar.setVisibility(View.INVISIBLE);
//                }
                if (detailAllSleepTv != null) detailAllSleepTv.setText("--");//睡眠时长
                if (detailAwakeNumTv != null) detailAwakeNumTv.setText("--");//苏醒次数
                if (detailStartSleepTv != null) detailStartSleepTv.setText("--");//入睡时间
                if (detailAwakeTimeTv != null) detailAwakeTimeTv.setText("--");//苏醒时间
                if (detailDeepTv != null) detailDeepTv.setText("--");//深度睡眠
                if (detailHightSleepTv != null) detailHightSleepTv.setText("--");// 浅度睡眠
            }
        } else {
            if (detailCusSleepView != null)
                detailCusSleepView.setSleepList(new ArrayList<Integer>());
//            if (detailSleepQuitRatingBar!=null){
//                detailSleepQuitRatingBar.setVisibility(View.INVISIBLE);
//            }
            if (detailAllSleepTv != null) detailAllSleepTv.setText("--");//睡眠时长
            if (detailAwakeNumTv != null) detailAwakeNumTv.setText("--");//苏醒次数
            if (detailStartSleepTv != null) detailStartSleepTv.setText("--");//入睡时间
            if (detailAwakeTimeTv != null) detailAwakeTimeTv.setText("--");//苏醒时间
            if (detailDeepTv != null) detailDeepTv.setText("--");//深度睡眠
            if (detailHightSleepTv != null) detailHightSleepTv.setText("--");// 浅度睡眠
        }

//
//        String sleep = B30HalfHourDao.getInstance().findOriginData(mac, currDay, B30HalfHourDao.TYPE_SLEEP);
//        Log.e("===获取到的睡眠 ", (WatchUtils.isEmpty(sleep) ? "苏数据" : sleep));
//
//        List<W30S_SleepDataItem> sleepData = JSON.parseObject(sleep, new TypeReference<List<W30S_SleepDataItem>>() {
//        });// Json 转List


//        SleepData sleepData = gson.fromJson(sleep, SleepData.class);
//        showSleepChartView(sleepData);
//        int sleepQulity = sleepData == null ? 0 : sleepData.getSleepQulity();
//        detailSleepQuitRatingBar.setMax(5);
//        detailSleepQuitRatingBar.setNumStars(sleepQulity);
//        detailSleepQuitRatingBar.setVisibility(sleepQulity == 0 ? View.INVISIBLE : View.VISIBLE);
//        //detailSleepQuitRatingBar.setEnabled(false);
//
//        String time = sleepData == null ? "--" : (sleepData.getAllSleepTime() / 60) + "H" +
//                (sleepData.getAllSleepTime() % 60) + "m";
//        detailAllSleepTv.setText(time);//睡眠时长
//        String count = sleepData == null ? "--" : "" + sleepData.getWakeCount();
//        detailAwakeNumTv.setText(count);//苏醒次数
//        String down = sleepData == null ? "--" : sleepData.getSleepDown().getDateForSleepshow();
//        detailStartSleepTv.setText(down);//入睡时间
//        String up = sleepData == null ? "--" : sleepData.getSleepUp().getDateForSleepshow();
//        detailAwakeTimeTv.setText(up);//苏醒时间
//        String deep = sleepData == null ? "--" : sleepData.getDeepSleepTime() / 60 + "H" +
//                (sleepData.getDeepSleepTime() % 60) + "m";
//        detailDeepTv.setText(deep);//深度睡眠
//        String low = sleepData == null ? "--" : sleepData.getLowSleepTime() / 60 + "H" +
//                (sleepData.getLowSleepTime() % 60) + "m";
//        detailHightSleepTv.setText(low);// 浅度睡眠
    }


    //    int ALLTIME = 0;//睡眠总时间
    int AWAKE = 0;//清醒的次数
    //    int DEEP = 0;//深睡
//    int SHALLOW = 0;//浅睡
    String startSleepTime = "00:00";//入睡时间
    String endSleepTime = "00:00";//起床时间

    /**
     * 睡眠计算
     */
    void sleepCalculation(List<W30S_SleepDataItem> sleepDataList, String dateStr) {
        if (!sleepDataList.isEmpty()) {
//                        ALLTIME = 0;//睡眠总时间
            AWAKE = 0;//清醒的次数
//                        DEEP = 0;//深睡
//                        SHALLOW = 0;//浅睡
            startSleepTime = "";
            endSleepTime = "";
            StringBuilder strSleep = new StringBuilder("");

            //是否第一次次进入睡眠状态
            //目的是为了处理睡眠入睡之前大部分的清醒睡眠
            boolean isIntoSleep = false;

            for (int i = 0; i < sleepDataList.size() - 1; i++) {
                String startTime = null;
                String startTimeLater = null;
                String sleep_type = null;
                //Log.e(TAG, "======= 遍历睡眠数据 " + sleepDataList.get(i).getStartTime() + "======" +
//                                        (sleepDataList.get(i).getSleep_type().equals("0") ? "===清醒" : sleepDataList.get(i).getSleep_type().equals("1") ? "---->>>浅睡" : "===>>深睡"));

                if (i == 0) startSleepTime = sleepDataList.get(i).getStartTime();
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
                    if (isIntoSleep){
                        AWAKE++;
//                                Log.e(TAG, "====0===" + all_m);
                        for (int j = 1; j <= all_m; j++) {
                            strSleep.append("2");
                        }
                    }

                } else if (sleep_type.equals("1")) {
                    isIntoSleep = true;
                    //潜水
//                                SHALLOW = SHALLOW + all_m;
//                                ALLTIME = ALLTIME + all_m;
//                                Log.e(TAG, "====1===" + all_m);
                    for (int j = 1; j <= all_m; j++) {
                        strSleep.append("0");
                    }
                } else if (sleep_type.equals("2")) {
                    isIntoSleep = true;
                    //深水
//                                DEEP = DEEP + all_m;
//                                ALLTIME = ALLTIME + all_m;
//                                Log.e(TAG, "====2===" + all_m);
                    for (int j = 1; j <= all_m; j++) {
                        strSleep.append("1");
                    }
                }
            }

            if (!TextUtils.isEmpty(strSleep)) {

                Log.e(TAG, strSleep.toString().length() + " 睡眠 \n" + strSleep.toString());
                /**
                 * 显示睡眠图标
                 */
                showSleepChartView(strSleep.toString(), dateStr);
            } else {
                if (detailCusSleepView != null)
                    detailCusSleepView.setSleepList(new ArrayList<Integer>());
//                if (detailSleepQuitRatingBar!=null){
//                    detailSleepQuitRatingBar.setVisibility(View.INVISIBLE);
//                }
                if (detailAllSleepTv != null) detailAllSleepTv.setText("--");//睡眠时长
                if (detailAwakeNumTv != null) detailAwakeNumTv.setText("--");//苏醒次数
                if (detailStartSleepTv != null) detailStartSleepTv.setText("--");//入睡时间
                if (detailAwakeTimeTv != null) detailAwakeTimeTv.setText("--");//苏醒时间
                if (detailDeepTv != null) detailDeepTv.setText("--");//深度睡眠
                if (detailHightSleepTv != null) detailHightSleepTv.setText("--");// 浅度睡眠
            }
        } else {
            if (detailCusSleepView != null)
                detailCusSleepView.setSleepList(new ArrayList<Integer>());
//            if (detailSleepQuitRatingBar!=null){
//                detailSleepQuitRatingBar.setVisibility(View.INVISIBLE);
//            }
            if (detailAllSleepTv != null) detailAllSleepTv.setText("--");//睡眠时长
            if (detailAwakeNumTv != null) detailAwakeNumTv.setText("--");//苏醒次数
            if (detailStartSleepTv != null) detailStartSleepTv.setText("--");//入睡时间
            if (detailAwakeTimeTv != null) detailAwakeTimeTv.setText("--");//苏醒时间
            if (detailDeepTv != null) detailDeepTv.setText("--");//深度睡眠
            if (detailHightSleepTv != null) detailHightSleepTv.setText("--");// 浅度睡眠
        }
    }


    //        private void showSleepChartView(final SleepData sleepData) {
    private void showSleepChartView(String sleepLin, String dateStr) {
        //图标绘制时    浅睡  0    深睡 1   清醒 2
        int xing = WatchUtils.countStr(sleepLin, '2');
        int qian = WatchUtils.countStr(sleepLin, '0');
        int shen = WatchUtils.countStr(sleepLin, '1');
        int allSleep = shen + qian;
        Log.e(TAG, "===  " + xing + "  " + qian + "   " + shen + "  " + allSleep);
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


        double sleep_times = Double.valueOf(S);
        if (detailSleepQuitRatingBar!=null){
//            detailSleepQuitRatingBar.setVisibility(View.VISIBLE);
//            detailSleepQuitRatingBar.setEnabled(false);
            if(0<sleep_times&&sleep_times<=0.8){
                detailSleepQuitRatingBar.setRating(1);

            }else if (0.8<sleep_times&&sleep_times<=1.6){
                detailSleepQuitRatingBar.setRating(2);

            }else if (1.6<sleep_times&&sleep_times<=2.4){
                detailSleepQuitRatingBar.setRating(3);

            }else if (2.4<sleep_times&&sleep_times<=3.2){
                detailSleepQuitRatingBar.setRating(4);

            }else if (3.2<sleep_times){
                detailSleepQuitRatingBar.setRating(5);

            }else {
                detailSleepQuitRatingBar.setRating(0);
            }
        }



        String time = (allSleep == 0 ? "--" : ((allSleep / 60) + "H" + (allSleep % 60) + "m"));
        detailAllSleepTv.setText(time);//睡眠时长

        detailAwakeNumTv.setText(AWAKE + "");//苏醒次数

        detailStartSleepTv.setText(startSleepTime);//入睡时间
        detailAwakeTimeTv.setText(endSleepTime);//苏醒时间

        String deep = (shen == 0 ? "--" : ((shen / 60) + "H" + (shen % 60) + "m"));
        detailDeepTv.setText(deep);//深度睡眠

        String low = (qian == 0 ? "--" : ((qian / 60) + "H" + (qian % 60) + "m"));
        detailHightSleepTv.setText(low);// 浅度睡眠


//        if (sleepData != null) {
//            String slleepLin = sleepData.getSleepLine();
//            for (int i = 0; i < slleepLin.length(); i++) {
//                if (i <= slleepLin.length() - 1) {
//                    int subStr = Integer.valueOf(slleepLin.substring(i, i + 1));
//                    listValue.add(subStr);
//                }
//            }
//            listValue.add(0, 2);
//            listValue.add(0);
//            listValue.add(2);
//        }
        listValue.clear();
        if (!WatchUtils.isEmpty(sleepLin)) {
            if (WatchUtils.isEmpty(sleepLin) || sleepLin.length() < 2) {
                if (detailCusSleepView != null)
                    detailCusSleepView.setSleepList(new ArrayList<Integer>());
                return;
            }
            for (int i = 0; i < sleepLin.length(); i++) {
                if (i <= sleepLin.length() - 1) {
                    int subStr = Integer.valueOf(sleepLin.substring(i, i + 1));
                    listValue.add(subStr);
                }
            }
            listValue.add(0, 2);
            listValue.add(0);
            listValue.add(2);
        }
        if (listValue.size() > 0) {
            detailCusSleepView.setSeekBarShow(false);
            detailCusSleepView.setSleepList(listValue);
            sleepSeekBar.setEnabled(false);
            sleepSeekBar.setProgress(-2);
//            sleepSeekBar.setMax(listValue.size());
//            sleepSeekBar.setProgress(-2);
//            sleepSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    if (progress == listValue.size())
//                        return;
//                     Log.e(TAG,"-------progress="+progress+"--="+sleepData.getSleepDown().getColck());
//
////                    int sleepHour = sleepData.getSleepDown().getHour() * 60;
////                    int sleepMine = sleepData.getSleepDown().getMinute();
////                    //入睡时间 分钟
////                    int sleepDownT = sleepHour + sleepMine;
////                    int currD = sleepDownT + ((progress == 0 ? -1 : progress - 1) * 5);   //当前的分钟
////                    //转换成时：分
////                    int hour = (int) Math.floor(currD / 60);
////                    if (hour >= 24)
////                        hour = hour - 24;
////                    int mine = currD % 60;
//
////                    detailCusSleepView.setSleepDateTxt((hour == 0 ? "00" : (hour < 10 ? "0" + hour : hour)) + ":" + (mine == 0 ? "00" : (mine < 10 ? "0" + mine : mine)) + "");
//                    detailCusSleepView.setSeekBarSchdue(progress);
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//                    Log.e(TAG, "---------onStartTrackingTouch-");
//                    detailCusSleepView.setSeekBarShow(true, 0);
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//                    Log.e(TAG, "---------onStopTrackingTouch-");
//                }
//            });
        } else {
            detailCusSleepView.setSleepList(new ArrayList<Integer>());
        }
    }

    @OnClick({R.id.commentB30BackImg, R.id.commentB30ShareImg, R.id.sleepCurrDateLeft, R.id.sleepCurrDateRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.commentB30ShareImg:   //分享
                WatchUtils.shareCommData(B15PSleepDetailActivity.this);
                break;
            case R.id.sleepCurrDateLeft:   //切换上一天数据
                if (detailSleepQuitRatingBar!=null)detailSleepQuitRatingBar.setRating(0);
                changeDayData(true);
                break;
            case R.id.sleepCurrDateRight:   //切换下一天数据
                changeDayData(false);
                break;
        }
    }

    /**
     * 根据日期切换数据
     */
    private void changeDayData(boolean left) {
        String date = WatchUtils.obtainAroundDate(currDay, left);
        if (date.equals(currDay) || date.isEmpty()) {
            return;// 空数据,或者大于今天的数据就别切了
        }
        if (detailSleepQuitRatingBar!=null)detailSleepQuitRatingBar.setRating(0);
        currDay = date;
        initData();
    }

}
