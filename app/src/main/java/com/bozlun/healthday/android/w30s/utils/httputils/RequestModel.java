package com.bozlun.healthday.android.w30s.utils.httputils;

import android.content.Context;

import com.bozlun.healthday.android.net.OkHttpObservable;
import com.bozlun.healthday.android.rxandroid.CommonSubscriber;
import com.bozlun.healthday.android.rxandroid.SubscriberOnNextListener;

/**
 * Created by Administrator on 2018/4/3.
 */

public class RequestModel{

    public void getJSONObjectModelData(String url, Context mContext, String jsonObject, final SubscriberOnNextListener<String> subscriberOnNextListener,CustumListener custumListener){
        CommonSubscriber subscriber = new CommonSubscriber(subscriberOnNextListener,mContext);
        subscriber.setCustumListener(custumListener);
        OkHttpObservable.getInstance().getData(subscriber,url,jsonObject);
    }


    public void getJSONObjectModelData(String url, Context mContext, final SubscriberOnNextListener<String> subscriberOnNextListener,CustumListener custumListener){
        CommonSubscriber subscriber = new CommonSubscriber(subscriberOnNextListener,mContext);
        subscriber.setCustumListener(custumListener);
        OkHttpObservable.getInstance().getNoParamData(subscriber,url);
    }


}
