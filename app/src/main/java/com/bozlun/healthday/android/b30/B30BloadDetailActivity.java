package com.bozlun.healthday.android.b30;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bozlun.healthday.android.MyApp;
import com.bozlun.healthday.android.R;
import com.bozlun.healthday.android.b30.adapter.B30BloadDetailAdapter;
import com.bozlun.healthday.android.b30.b30view.B30CusBloadView;
import com.bozlun.healthday.android.b30.b30view.CusB30CusBloadView;
import com.bozlun.healthday.android.b30.bean.B30HalfHourDao;
import com.bozlun.healthday.android.siswatch.WatchBaseActivity;
import com.bozlun.healthday.android.siswatch.utils.WatchUtils;
import com.bozlun.healthday.android.util.Constant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.veepoo.protocol.model.datas.HalfHourBpData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * B30血压详情界面
 */
public class B30BloadDetailActivity extends WatchBaseActivity {

    @BindView(R.id.cusB30BpView)
    CusB30CusBloadView cusB30BpView;

    /**
     * 跳转到B30BloadDetailActivity,并附带参数
     *
     * @param context 启动源上下文
     * @param date    附带的参数:日期
     */
    public static void startAndParams(Context context, String date) {
        Intent intent = new Intent(context, B30BloadDetailActivity.class);
        intent.putExtra(Constant.DETAIL_DATE, date);
        context.startActivity(intent);
    }

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;
    @BindView(R.id.b30DetailBloadView)
    B30CusBloadView b30DetailBloadView;
    @BindView(R.id.b30DetailLowestBloadTv)
    TextView b30DetailLowestBloadTv;
    @BindView(R.id.b30DetailHeightBloadTv)
    TextView b30DetailHeightBloadTv;
    @BindView(R.id.b30DetailLowestBloadDateTv)
    TextView b30DetailLowestBloadDateTv;
    @BindView(R.id.b30DetailHeightBloadDateTv)
    TextView b30DetailHeightBloadDateTv;
    @BindView(R.id.b30DetailBloadRecyclerView)
    RecyclerView b30DetailBloadRecyclerView;
    @BindView(R.id.bloadCurrDateTv)
    TextView bloadCurrDateTv;

    private B30BloadDetailAdapter b30BloadDetailAdapter;
    private List<HalfHourBpData> dataList;

    /**
     * 当前显示的日期(数据根据日期加载)
     */
    private String currDay;
    /**
     * Json工具类
     */
    private Gson gson;


    private List<Map<Integer, Integer>> resultMap = new ArrayList<>();

    //数据源
    private List<Map<String,Map<Integer,Integer>>> cusResultMap;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_bload_detail);
        ButterKnife.bind(this);
        initViews();
        initData();
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.bloodpressure));
//        commentB30ShareImg.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        b30DetailBloadRecyclerView.setLayoutManager(layoutManager);
        b30DetailBloadRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        dataList = new ArrayList<>();
        b30BloadDetailAdapter = new B30BloadDetailAdapter(B30BloadDetailActivity.this, dataList);
        b30DetailBloadRecyclerView.setAdapter(b30BloadDetailAdapter);

        gson = new Gson();
        currDay = getIntent().getStringExtra(Constant.DETAIL_DATE);

        cusResultMap = new ArrayList<>();
    }

    private void initData() {
        bloadCurrDateTv.setText(currDay);
        String mac = MyApp.getInstance().getMacAddress();
        String bp = B30HalfHourDao.getInstance().findOriginData(mac, currDay, B30HalfHourDao
                .TYPE_BP);
        List<HalfHourBpData> bpData = gson.fromJson(bp, new TypeToken<List<HalfHourBpData>>() {
        }.getType());


//        b30DetailBloadView.setDataMap(obtainBloodMap(bpData));
//        b30DetailBloadView.setScale(true);

        resultMap.clear();
        cusResultMap.clear();
        if (bpData != null && bpData.size() > 0) {
            for (HalfHourBpData halfHourBpData : bpData) {
                Map<Integer, Integer> mp = new ArrayMap<>();
                mp.put(halfHourBpData.lowValue, halfHourBpData.highValue);
                //resultMap.add(mp);

                Map<String,Map<Integer,Integer>> mMap = new HashMap<>();
                mMap.put(halfHourBpData.getTime().getColck(),mp);
                cusResultMap.add(mMap);
            }
            //b30DetailBloadView.setBPDataMap(resultMap);
            //cusB30BpView.setYValues(yValueList);
            cusB30BpView.setxVSize(dataList.size());
            cusB30BpView.setResultMapData(cusResultMap);

        } else {
            //b30DetailBloadView.setBPDataMap(resultMap);
            cusB30BpView.setxVSize(0);
            cusB30BpView.setResultMapData(cusResultMap);
        }


        //展示数据
        dataList.clear();
        if (bpData != null && !bpData.isEmpty()) {
            Collections.sort(bpData, new Comparator<HalfHourBpData>() {
                @Override
                public int compare(HalfHourBpData o1, HalfHourBpData o2) {
                    return o2.getTime().getColck().compareTo(o1.getTime().getColck());
                }
            });
            dataList.addAll(bpData);
            ArrayList<Integer> hightList = new ArrayList<>();
            ArrayList<Integer> lowList = new ArrayList<>();
            for (int i = 0; i < dataList.size(); i++) {
                hightList.add(i, dataList.get(i).getHighValue());
                lowList.add(i, dataList.get(i).getLowValue());
            }
            //最高血压
            int hightValue = Collections.max(hightList);
            int hightValue_low = lowList.get(hightList.indexOf(hightValue));
            String hightTime = dataList.get(hightList.indexOf(hightValue)).time.getColck();
            b30DetailHeightBloadTv.setText(hightValue + "/" + hightValue_low);
            b30DetailHeightBloadDateTv.setText(hightTime);

            //最低血压
            int lowValue = Collections.min(lowList);
            int lowValue_hight = hightList.get(lowList.indexOf(lowValue));
            String lowTime = dataList.get(lowList.indexOf(lowValue)).time.getColck();

            b30DetailLowestBloadTv.setText(lowValue_hight + "/" + lowValue);
            b30DetailLowestBloadDateTv.setText(lowTime);
        } else {
            b30DetailHeightBloadTv.setText("--");
            b30DetailHeightBloadDateTv.setText("--");
            b30DetailLowestBloadTv.setText("--");
            b30DetailLowestBloadDateTv.setText("--");
        }
        b30BloadDetailAdapter.notifyDataSetChanged();
    }

    /**
     * 统计血压数据源
     *
     * @param bpData 手环源数据
     * @return Map结果: String:日期 Point:x低压_y高压
     */
    private Map<String, Point> obtainBloodMap(List<HalfHourBpData> bpData) {
        if (bpData == null || bpData.isEmpty()) return null;
        Map<String, Point> dataMap = new HashMap<>();
        for (HalfHourBpData item : bpData) {
            String time = item.getTime().getColck();
            Point point = new Point(item.getLowValue(), item.getHighValue());
            dataMap.put(time, point);
        }
        return dataMap;
    }


    @OnClick({R.id.commentB30BackImg, R.id.commentB30ShareImg, R.id.bloadCurrDateLeft,
            R.id.bloadCurrDateRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.commentB30ShareImg:
                WatchUtils.shareCommData(B30BloadDetailActivity.this);
                break;
            case R.id.bloadCurrDateLeft:   //切换上一天数据
                changeDayData(true);
                break;
            case R.id.bloadCurrDateRight:   //切换下一天数据
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
        currDay = date;
        initData();
    }

}
