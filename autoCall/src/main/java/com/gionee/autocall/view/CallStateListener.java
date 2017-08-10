package com.gionee.autocall.view;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.gionee.autocall.util.Util;

public class CallStateListener extends PhoneStateListener {
    private String TAG = "suse";
    private Util gioneeUtil = new Util();
    private static int i = 0;
    int fail = 0;
    ITelephony mITelephony;

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        if (state == TelephonyManager.CALL_STATE_IDLE)//挂断
        {
            Log.e("IDLE", incomingNumber);
            Log.i(TAG, "IDLE" + incomingNumber);
        } else if (state == TelephonyManager.CALL_STATE_OFFHOOK)//接听
        {
            Log.e("OFFHOOK", incomingNumber);
            Log.i(TAG, "OFFHOOK" + incomingNumber);
        } else if (state == TelephonyManager.CALL_STATE_RINGING)//来电
        {
            i++;
            Log.i(TAG, "incoming:incomingNumber=" + incomingNumber + "=" + gioneeUtil.getTime());
//        	result.append("PASS");
//        	String testresult = "\n"+gioneeUtil.getTime()+"--"+incomingNumber+"-->"+"测试次数："+i;
//        	gioneeUtil.WriteResultToSdCard(testresult,0,0);

            /*if(testTelephony.this.checkedId==R.id.rbtnAutoAccept)
            {
                try {
                    //需要<uses-permission android:name="android.permission.MODIFY_PHONE_STATE" />
                    PhoneUtils.getITelephony(telMgr).silenceRinger();//静铃
                    PhoneUtils.getITelephony(telMgr).answerRingingCall();//自动接听

                } catch (Exception e) {
                    Log.e("error",e.getMessage());
                }
            }
            else if(testTelephony.this.checkedId==R.id.rbtnAutoReject)
            {
                try {
                    PhoneUtils.getITelephony(telMgr).endCall();//挂断
                    PhoneUtils.getITelephony(telMgr).cancelMissedCallsNotification();//取消未接显示
                } catch (Exception e) {
                    Log.e("error",e.getMessage());
                }
            }
        }  */
            super.onCallStateChanged(state, incomingNumber);
        }
    }
}