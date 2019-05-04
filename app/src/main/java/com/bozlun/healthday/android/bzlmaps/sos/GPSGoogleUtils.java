package com.bozlun.healthday.android.bzlmaps.sos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.bozlun.healthday.android.Commont;
import com.bozlun.healthday.android.MyApp;
import com.bozlun.healthday.android.bzlmaps.mapdb.GoogleMapBean;
import com.bozlun.healthday.android.siswatch.utils.WatchUtils;
import com.bozlun.healthday.android.util.ToastUtil;
import com.bozlun.healthday.android.util.VerifyUtil;
import com.bozlun.healthday.android.w30s.utils.httputils.RequestPressent;
import com.bozlun.healthday.android.w30s.utils.httputils.RequestView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import java.util.List;


/**
 * Created by Administrator on 2018/4/17.
 * 获取用户的地理位置
 */
public class GPSGoogleUtils implements RequestView {

    private static GPSGoogleUtils instance;
    private Context mContext;
    private boolean isConnected = false;


    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;


    private GPSGoogleUtils(Context context) {
        this.mContext = context;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3 * 1000);
        mLocationRequest.setFastestInterval(3 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        requestPressent = new RequestPressent();
        requestPressent.attach(this);

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        isConnected = true;
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            //Toast.makeText(getApplicationContext(), "Permission to access the location is missing.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        if (mLastLocation != null) {
                            //启用位置更新
                            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//                        Log.e("GPS", "onConnectionFailed");
                    }
                }).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();// 连接Google Play服务

    }


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Commont.GPSCOUNT++;
            if (Commont.GPSCOUNT <= 2) {
                updateWithNewLocation(location, mContext);
            } else {
                stopLocationUpdates();
                //Commont.isGPSed = false;
            }

        }

    };


    double latitude;
    double longitude;
    String stringpersonContent;

    private void updateWithNewLocation(Location location, Context context) {
//        Log.e("GPS", "updateLocation");
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.e("GPS", "updateLocation" + "latitude=" + latitude + "longitude=" + longitude);
        stringpersonContent = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "personContent", "");
        if (WatchUtils.isEmpty(stringpersonContent))
            stringpersonContent = "大侠，救命啊！ ";
//        Intent intent = new Intent();
//        intent.setAction("com.example.bozhilun.android.bzlmaps.sos.SENDSMS");
//        intent.putExtra("msm", stringpersonContent.trim());
//        intent.putExtra("gps", dizhi.trim());
//        context.sendBroadcast(intent);

        //用ＧＯＯＧＬＥ標
        String TW_ADDRESS_1 = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
        String TW_ADDRESS_2 = "&language=zh-TW&key=AIzaSyA7L-qbDChhv2sG1Nq2y_611LJ0ZJwRK8M";
        boolean zh = VerifyUtil.isZh(context);

        if (zh) {
            Boolean zhonTW = mContext.getResources().getConfiguration().locale.getCountry().equals("TW");
            if (zhonTW) {
                TW_ADDRESS_2 = "&language=zh-TW&key=AIzaSyA7L-qbDChhv2sG1Nq2y_611LJ0ZJwRK8M";
            } else {
                TW_ADDRESS_2 = "&language=zh-CN&key=AIzaSyA7L-qbDChhv2sG1Nq2y_611LJ0ZJwRK8M";
            }
        } else {
            TW_ADDRESS_2 = "&language=en&key=AIzaSyA7L-qbDChhv2sG1Nq2y_611LJ0ZJwRK8M";
        }
        String getUrl = TW_ADDRESS_1 + latitude + "," + longitude + TW_ADDRESS_2;

        Log.e("=======", "url  " + getUrl);
//
//        //用百度標的
//        final String ADDRESS_URL = "http://api.map.baidu.com/geocoder/v2/?ak=wEy1csKVT2vIotSr3W8N9GXq&callback=renderReverse&location=";
//
//        final String ADDRESS_URL2 = "&output=json&pois=0";
//
//        getUrl = ADDRESS_URL + "39.075981" + "," + "-100.680995" + ADDRESS_URL2;
//
//
//        //回來是英文的
//        final String EN_ADDRESS_1 = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
//
//        //繁體中文
//        final String EN_ADDRESS_2 = "&language=en&key=AIzaSyA7L-qbDChhv2sG1Nq2y_611LJ0ZJwRK8M";
//
//        getUrl = EN_ADDRESS_1 + "39.075981" + "," + "-100.680995" + EN_ADDRESS_2;
        sendLocationAdressRequest2(getUrl);
    }

    private void sendLocationAdressRequest2(String url) {
        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x01, url, mContext, 0);
        }
    }

    private RequestPressent requestPressent;


    /**
     * 启用定位更新
     */
    public boolean startLocationUpdates(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (isConnected && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);
            return true;
        } else {
            if (mGoogleApiClient != null) mGoogleApiClient.connect();// 连接Google Play服务
            return false;
        }
    }

    /**
     * 停止定位更新
     */
    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null && locationListener != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
            if (mGoogleApiClient != null) mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }
    }

    public static GPSGoogleUtils getInstance(Context context) {
        instance = new GPSGoogleUtils(context);
//        if (instance == null) {
//            instance = new GPSGoogleUtils(context);
//        }
        return instance;
    }


    /**
     * 数据请求的
     *
     * @param what
     */
    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
        if (object != null && !WatchUtils.isEmpty(object.toString())) {
            Log.e("GPS", "-----返回-" + object.toString());
            GoogleMapBean googleMapBean = new Gson().fromJson(object.toString(), GoogleMapBean.class);
            if (googleMapBean.getStatus().equals("OK")) {
                Log.e("========", "ok 过来了");
                List<GoogleMapBean.ResultsBean> results = googleMapBean.getResults();
                if (results != null && !results.isEmpty()) {
                    Log.e("========", "results 过来了");
                    GoogleMapBean.ResultsBean resultsBean = results.get(0);
                    GoogleMapBean.ResultsBean resultsBean1 = results.get(1);
                    if (resultsBean != null) {

                        String formatted_address = resultsBean.getFormatted_address();
                        if (WatchUtils.isEmpty(formatted_address)) {
                            if (resultsBean1 != null) {
                                formatted_address = resultsBean1.getFormatted_address();
                            }
                        }
                        if (WatchUtils.isEmpty(formatted_address)) return;
                        Log.e("========", formatted_address.trim());
                        Intent intent = new Intent();
                        intent.setAction("com.example.bozhilun.android.sos.SENDSMS");
                        intent.putExtra("msm", stringpersonContent.trim()+"  " + latitude + "," + longitude);
                        intent.putExtra("gps", formatted_address.trim());
                        mContext.sendBroadcast(intent);
                    }
                }
            }
        }

    }

    @Override
    public void failedData(int what, Throwable e) {
        ToastUtil.showShort(mContext,"errer:"+e.toString());
//        Log.e("GPS", "-----返回-ERRER" + e.toString());
//        String dizhi = latitude + "," + longitude;
//        Intent intent = new Intent();
//        intent.setAction("com.example.bozhilun.android.bzlmaps.sos.SENDSMS");
//        intent.putExtra("msm", stringpersonContent.trim());
//        intent.putExtra("gps", dizhi.trim());
//        mContext.sendBroadcast(intent);
    }

    @Override
    public void closeLoadDialog(int what) {

    }
}