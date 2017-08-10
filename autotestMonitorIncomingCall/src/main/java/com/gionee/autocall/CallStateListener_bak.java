package com.gionee.autocall;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallStateListener_bak extends PhoneStateListener {
    private String TAG = "Gionee.Telephony";
    private Util util = new Util();
//    private StringBuffer sb = new StringBuffer();
    private static int i = 0;
    int fail = 0;

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        if (state == TelephonyManager.CALL_STATE_IDLE)//挂断
        {
            Log.i("IDLE", incomingNumber);
        } else if (state == TelephonyManager.CALL_STATE_OFFHOOK)//接听
        {
            Log.i("OFFHOOK", incomingNumber);

        } else if (state == TelephonyManager.CALL_STATE_RINGING)//来电
        {
            i++;
            Log.i(TAG, "incoming:incomingNumber=" + incomingNumber + "=" + util.getTime());
//            sb.append("PASS");
            String testResult = "\n" + util.getTime() + "--" + incomingNumber + "-->" + "测试次数：" + i;
            util.WriteResulttoSDcard(testResult, 0, 0);

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
