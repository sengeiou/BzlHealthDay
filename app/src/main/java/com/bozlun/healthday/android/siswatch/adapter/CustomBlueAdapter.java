package com.bozlun.healthday.android.siswatch.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bozlun.healthday.android.R;
import com.bozlun.healthday.android.siswatch.bean.CustomBlueDevice;
import com.bozlun.healthday.android.siswatch.utils.WatchUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/10/31.
 */

/**
 * 搜索页面适配器
 */
public class CustomBlueAdapter extends RecyclerView.Adapter<CustomBlueAdapter.CustomBlueViewHolder> {

    private List<CustomBlueDevice> customBlueDeviceList;
    private Context mContext;
    public OnSearchOnBindClickListener onBindClickListener;

    public void setOnBindClickListener(OnSearchOnBindClickListener onBindClickListener) {
        this.onBindClickListener = onBindClickListener;
    }

    public CustomBlueAdapter(List<CustomBlueDevice> customBlueDeviceList, Context mContext) {
        this.customBlueDeviceList = customBlueDeviceList;
        this.mContext = mContext;
    }

    @Override
    public CustomBlueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_bluedevice, null);
        return new CustomBlueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomBlueViewHolder holder, int position) {
        BluetoothDevice bluetoothDevice = customBlueDeviceList.get(position).getBluetoothDevice();
        if (bluetoothDevice != null) {
            //蓝牙名称
            holder.bleNameTv.setText(customBlueDeviceList.get(position).getBluetoothDevice().getName());
            //mac地址
            holder.bleMacTv.setText(customBlueDeviceList.get(position).getBluetoothDevice().getAddress());
            //信号
            holder.bleRiisTv.setText("" + customBlueDeviceList.get(position).getRssi() + "");
            //展示图片
            String bleName = customBlueDeviceList.get(position).getBluetoothDevice().getName();
            if (!WatchUtils.isEmpty(bleName)) {
                if (WatchUtils.verBleNameForSearch(bleName)){
//                    holder.img.setImageResource(R.mipmap.b15p_xiaotu);
                    //B、L、F、M、W
                    if (bleName.length()>1&&!bleName.equals("F6")){
                        if (bleName.substring(0,1).equals("B")){
                            holder.img.setImageResource(R.mipmap.ic_series_b);
                        }else if (bleName.substring(0,1).equals("L")){
                            holder.img.setImageResource(R.mipmap.ic_series_l);
                        }else if (bleName.substring(0,1).equals("F")){
                            holder.img.setImageResource(R.mipmap.ic_series_f);
                        }else if (bleName.substring(0,1).equals("M")){
                            holder.img.setImageResource(R.mipmap.ic_series_m);
                        }else if (bleName.substring(0,1).equals("W")){
                            holder.img.setImageResource(R.mipmap.ic_series_w);
                        }
                    }else {
                        holder.img.setImageResource(R.mipmap.img_f6);
                    }

//                    if (bleName.equals("F6")){
//                        holder.img.setImageResource(R.mipmap.img_f6);
//                    }else {
//                        holder.img.setImageResource(R.mipmap.b15p_xiaotu);
//                    }

                }
//                if ((bleName.length() >= 2 && bleName.equals("W3"))
//                        || (bleName.length() >= 3 && bleName.equals("B15P"))) { //B15P手环
//                    holder.img.setImageResource(R.mipmap.b15p_xiaotu);
//                } else if ((bleName.length()>=2&&bleName.substring(0, 2).equals("H9"))) {    //H9手表
////                    holder.img.setImageResource(R.mipmap.seach_h9);
//                }else if (bleName.length()>=4&&bleName.substring(0, 4).equals("W06X")) {    //H9手表----其他名字  W06X
//                    holder.img.setImageResource(R.mipmap.seach_h9);
//                } else if (bleName.length()>=3&&bleName.substring(0, 3).equals("W30")) {    //W30s手表
//                    holder.img.setImageResource(R.mipmap.w30_searchlist_icon);
//                } else if (bleName.length()>=2&&bleName.substring(0, 3).equals("B30")) { //B30手环
//                    holder.img.setImageResource(R.mipmap.ic_b30_search);
//                }
// else if (bleName.length()>=7&&bleName.equals("Ringmii")) {
//                    holder.img.setImageResource(R.mipmap.hx_search);
//                } else if(bleName.substring(0,3).equals("B36")){  //B36
//                    holder.img.setImageResource(R.mipmap.ic_b36_search);
//                }
//                else if(bleName.substring(0,3).equals("B31")){  //B31
//                    holder.img.setImageResource(R.mipmap.ic_b31_search);
//                }else if (bleName.length() >= 4 && bleName.substring(0, 4).equals("B31S")) {  //B31
//                    holder.img.setImageResource(R.mipmap.ic_b31_search);
//                } else if (bleName.length() >= 4 && bleName.substring(0, 4).equals("500S")) {  //B31
//                    holder.img.setImageResource(R.mipmap.ic_seach_500s);
//                }
////                else if(bleName.substring(0,4).equals("B18I")){    //B18I手环 bzolun
////                    holder.img.setImageResource(R.mipmap.icon_b18i_scanshow);
////                }
//                else {
//
//                    if (customBlueDeviceList.get(position).getCompanyId() == 160
//                            || bleName.substring(0, 2).equals("H8") ||(bleName.length()>=6 && bleName.substring(0, 6).equals("bozlun"))
//                            ) {   //H8手表
//                        holder.img.setImageResource(R.mipmap.h8_search);
//                    }
//                }
            }

            //绑定按钮
            holder.circularProgressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onBindClickListener != null) {
                        int position = holder.getLayoutPosition();
                        onBindClickListener.doBindOperator(position);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return customBlueDeviceList.size();
    }

    class CustomBlueViewHolder extends RecyclerView.ViewHolder {

        TextView bleNameTv, bleMacTv, bleRiisTv;
        ImageView img;  //显示手表或者手环图片
        Button circularProgressButton;

        public CustomBlueViewHolder(View itemView) {
            super(itemView);
            bleNameTv = (TextView) itemView.findViewById(R.id.blue_name_tv);
            bleMacTv = (TextView) itemView.findViewById(R.id.snmac_tv);
            bleRiisTv = (TextView) itemView.findViewById(R.id.rssi_tv);
            img = (ImageView) itemView.findViewById(R.id.img_logo);
            circularProgressButton = itemView.findViewById(R.id.bind_btn);
        }
    }

    public interface OnSearchOnBindClickListener {
        void doBindOperator(int position);
    }
}
