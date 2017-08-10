package com.gionee.autocall.action;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.gionee.autocall.bean.CallParams;
import com.gionee.autocall.util.Util;

import java.lang.reflect.Method;

import static android.content.Context.TELEPHONY_SERVICE;

public class CallHelper {
    private Context mContext;
    private long offHook_wait_time;
    private CallListener listener = new CallListener() {
    };

    public CallHelper(Context context) {
        this.mContext = context;
    }

    public void setCallListener(CallListener listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }

    public void call(String number, CallParams params) {
        dial(number);
        try {
            waitOffHook();
            listener.onOffHook(Util.getTime());
            Util.setHandOn(mContext, params.isSpeakOn);
            for (int j = 0; j < params.call_time * 100; j++) {
                SystemClock.sleep(10);
                if (!listener.setCancelFlag()) {
                    listener.onCancel();
                    return;
                }
            }
            getTelPhony().endCall();
            listener.onEndCall(Util.getTime());
        } catch (Exception e) {
            listener.onException(e);
        }

    }

    private void waitOffHook() throws android.os.RemoteException {
        long start = System.currentTimeMillis();
        ITelephony iTelephony = getTelPhony();
        while (iTelephony.getCallState() != TelephonyManager.CALL_STATE_OFFHOOK) {
            SystemClock.sleep(1);
            if (!listener.setCancelFlag()) {
                listener.onCancel();
                return;
            }
        }
        long end = System.currentTimeMillis();
        offHook_wait_time = end - start;
    }

    private void dial(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private ITelephony getTelPhony() {
        ITelephony iTelephony = null;
        try {
            TelephonyManager telMgr = (TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE);
            Method m = telMgr.getClass().getDeclaredMethod("getITelephony", (Class[]) null);
            m.setAccessible(true);
            iTelephony = (ITelephony) m.invoke(telMgr);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return iTelephony;
    }

    public abstract class CallListener {

        public void onOffHook(String time) {
        }

        public void onEndCall(String time) {
        }

        public void onException(Exception e) {
        }

        public boolean setCancelFlag() {
            return false;
        }

        public void onCancel() {

        }
    }

}
