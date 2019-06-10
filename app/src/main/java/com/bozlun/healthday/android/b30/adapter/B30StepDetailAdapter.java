package com.bozlun.healthday.android.b30.adapter;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bozlun.healthday.android.Commont;
import com.bozlun.healthday.android.MyApp;
import com.bozlun.healthday.android.R;
import com.bozlun.healthday.android.b15p.activity.B15PStepDetailActivity;
import com.bozlun.healthday.android.siswatch.utils.WatchUtils;
import com.bozlun.healthday.android.util.Constant;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.tjdL4.tjdmain.ctrls.DisEnergy;
import com.veepoo.protocol.model.datas.HalfHourSportData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by Administrator on 2018/8/6.
 */

/**
 * 运动步数详情adapter
 */
public class B30StepDetailAdapter extends RecyclerView.Adapter<B30StepDetailAdapter.B30StepDetailViewHolder> {

    private Context mContext;
    private List<HalfHourSportData> list;
//    private int flagCode;

    public B30StepDetailAdapter(Context mContext, List<HalfHourSportData> list) {
        this.mContext = mContext;
        this.list = list;
//        this.flagCode = code;
    }

    @NonNull
    @Override
    public B30StepDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_b30_step_detail_layout, parent, false);
        B30StepDetailViewHolder holder = new B30StepDetailViewHolder(view);
        return holder;
    }

    //{date='2018-08-17', allPackage=65, packageNumber=44, mTime=TimeData [2018-08-17 14:25:00],
    // rateValue=77, sportValue=34, stepValue=0, highValue=0, lowValue=0, wear=63, tempOne=4, tempTwo=-1,
    // calValue=0.0, disValue=0.0, calcType=2}

    //辅助为每个时间段添加前一段时间
    String[] timeString = {"01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00",
            "09:00", "10:00", "11:00", "12:00", "13:00",
            "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", "24:00"};

    @Override
    public void onBindViewHolder(@NonNull B30StepDetailViewHolder holder, int position) {
//        if (position + 1 < 24) {
//            //时间
//            holder.timeTv.setText(list.get(position).getTime().getColck() + "-" + timeString[(position + 1)]);
//        } else {
//            holder.timeTv.setText(list.get(position).getTime().getColck() + "-01:00");
//        }

        String colck = list.get(position).getTime().getColck();
        if (!WatchUtils.isEmpty(colck)) {
            String[] split = colck.split("[:]");

            int num = Integer.valueOf(split[0]);
            if (num < 24) {
                //时间
                holder.timeTv.setText(list.get(position).getTime().getColck() + "-" + timeString[num]);
            } else {
                holder.timeTv.setText(list.get(position).getTime().getColck() + "-" + timeString[0]);
            }
        }

        Log.e("---- 数据 ", list.get(position).getDisValue() + "     ===    " + list.get(position).getCalValue());
        //holder.timeTv.setText(list.get(position).getTime().getColck());
        //holder.kcalTv.setText(list.get(position).getStepValue() + "");

//        if(flagCode == 1){  //步数
//            holder.kcalTv.setText(list.get(position).getStepValue()+"");
//        }else if(flagCode == 2){    //里程
//            holder.kcalTv.setText(list.get(position).getDisValue()+"");
//        }else if(flagCode == 3){    //卡路里
//            holder.kcalTv.setText(list.get(position).getCalValue()+"");
//        }

        int stepValue = list.get(position).getStepValue();
        setTyprData(holder, stepValue, Commont.TYPE_DATAS);
    }


    void setTyprData(B30StepDetailViewHolder holder, int step, int type) {

        if (type == 0) {//步数

            Drawable drawable = MyApp.getContext().getResources().getDrawable(R.mipmap.ic_deatil_tep);
            holder.kcalTv.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            holder.kcalTv.setText(step + "");
        } else {


            String uHeight = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "userheight", "170");
            String uWeight = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "userweight", "60");
            //获取卡路里 ka
            //step：   步数
            //height：身高 cm
            //weight：体重
            int calorieWithSteps = (int) DisEnergy.getCalorieWithSteps(step, Integer.valueOf(uHeight), Integer.valueOf(uWeight));
            int distanceWithStep = (int) DisEnergy.getDistanceWithStep(step, Integer.valueOf(uHeight));


            Log.e("返回的卡路里等  ", calorieWithSteps + "   " + distanceWithStep);
//            BigDecimal bdK = new BigDecimal((double) (distanceWithStep / 1000.00));
//            BigDecimal setScaleK = bdK.setScale(1, RoundingMode.DOWN);
//
//            BigDecimal bdD = new BigDecimal((double) (calorieWithSteps / 1000.00));
//            BigDecimal setScaleD = bdD.setScale(0, RoundingMode.DOWN);
//
//            String disValue = setScaleK.toString();
//            String calValue = setScaleD.toString();
//            Log.e("======", distanceWithStep + "   " + calorieWithSteps
//                    + "\n" + setScaleD.toString() + "  " + setScaleK.toString());

//
            boolean isMetric = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);
            if (!isMetric) {

                BigDecimal bdDS = new BigDecimal((double) (Double.valueOf(distanceWithStep) * Constant.METRIC_FT));
                BigDecimal setScaleDS = bdDS.setScale(1, RoundingMode.DOWN);
                distanceWithStep = setScaleDS.intValue();
            }
//        disValue = Math.round(disValue * 100) / 100;
            Log.d("bobo", "calValue: " + calorieWithSteps + ",disValue: " + distanceWithStep);
            String disStr = isMetric ?"m" : "ft";

            if (type == 1) {//距离
                Drawable drawable = MyApp.getContext().getResources().getDrawable(R.mipmap.ic_deatil_dis);
                holder.kcalTv.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                holder.kcalTv.setText(distanceWithStep + disStr);
            } else {//卡路里
                Drawable drawable = MyApp.getContext().getResources().getDrawable(R.mipmap.ic_deatil_kcl);
                holder.kcalTv.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                holder.kcalTv.setText(calorieWithSteps + "cal");
            }
        }


    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class B30StepDetailViewHolder extends RecyclerView.ViewHolder {

        TextView timeTv, kcalTv;
        ImageView img;

        public B30StepDetailViewHolder(View itemView) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.itemB30StepDetailTimeTv);
            kcalTv = itemView.findViewById(R.id.itemB30StepDetailKcalTv);
            img = itemView.findViewById(R.id.itemB30StepDetailImg);
        }
    }
}
