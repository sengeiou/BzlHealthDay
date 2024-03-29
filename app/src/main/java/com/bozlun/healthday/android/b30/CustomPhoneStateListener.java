package com.bozlun.healthday.android.b30;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.bozlun.healthday.android.util.HangUpTelephonyUtil;

public class CustomPhoneStateListener extends PhoneStateListener {

    private Context mContext;

    public CustomPhoneStateListener(Context context) {
        mContext = context;
    }

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);
        Log.d("call", "CustomPhoneStateListener onServiceStateChanged: " + serviceState);
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        Log.d("call", "CustomPhoneStateListener state: "
              + state + " incomingNumber: " + incomingNumber);
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:      // 电话挂断
                HangUpTelephonyUtil.endCall(mContext);
                break;
            case TelephonyManager.CALL_STATE_RINGING:   // 电话响铃
                Log.d("call", "CustomPhoneStateListener onCallStateChanged endCall");
                HangUpTelephonyUtil.endCall(mContext);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:   // 来电接通 或者 去电  但是没法区分
                HangUpTelephonyUtil.endCall(mContext);
                break;
        }
    }
}