package com.bozlun.healthday.android.h9.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bozlun.healthday.android.R;
import com.bozlun.healthday.android.h9.bean.HeartDataBean;

import java.util.List;

/**
 * Created by admin on 2017/4/15.
 */
//心率测试
public class H9HeathtestAdapter extends BaseAdapter {

    List<HeartDataBean.ManualBean> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public H9HeathtestAdapter(Context context, List<HeartDataBean.ManualBean> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }


    /**
     * 组件集合，对应list.xml中的控件
     *
     * @author Administrator
     */
    public final class Zujian {
        public TextView title;
        public TextView info;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * 获得某一位置的数据
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    /**
     * 获得唯一标识
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Zujian zujian = null;
        if (convertView == null) {
            zujian = new Zujian();
            //获得组件，实例化组件
            convertView = layoutInflater.inflate(R.layout.xinlv_testview_item, null);
            zujian.title = (TextView) convertView.findViewById(R.id.text_data_show);
            zujian.info = (TextView) convertView.findViewById(R.id.text_data_showbpm);
            convertView.setTag(zujian);
        } else {
            zujian = (Zujian) convertView.getTag();
        }
        //绑定数据
        zujian.title.setText(data.get(position).getRtc());
        zujian.info.setText(String.valueOf(data.get(position).getHeartRate()));
        return convertView;
    }
}