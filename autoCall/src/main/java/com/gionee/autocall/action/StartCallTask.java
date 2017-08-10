package com.gionee.autocall.action;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.gionee.autocall.bean.CallLogBean;
import com.gionee.autocall.bean.CallParams;
import com.gionee.autocall.util.Util;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static android.app.ActivityThread.TAG;
import static android.content.Context.TELEPHONY_SERVICE;


class StartCallTask extends AsyncTask<Void, Object, Void> {
    private Util util = null;
    private Context mContext;
    private IAutoCallAction iAction;
    private CallParams params;
    private int cycleIndex = 0;
    private int successFled = 0;
    private int failed = 0;
    private ITelephony iTelephony;

    StartCallTask(Context context, IAutoCallAction mAction, CallParams params) {
        mContext = context;
        iTelephony = getTelPhony();
        this.mContext = context;
        this.iAction = mAction;
        this.params = params;
        util = new Util();
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
        if (values.length > 0) {
            int flag = (int) values[0];
            switch (flag) {
                case 2:
                    iAction.updateViews();
                    break;
                case 1:
                    if (values.length > 1) {
                        Toast.makeText(mContext, values[1].toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected Void doInBackground(Void... flag) {
        try {
            for (int i = 0; i < params.count; i++) {
                startRound();
            }
        } catch (RuntimeException e) {
            Util.i(e.toString());
            iAction.updateViews();
        }
        publishProgress(2);
        return null;
    }

    private void startRound() {
        long beginTime = System.currentTimeMillis();
        util.WriteResultToSdCard("\n" + "---------" + Util.getTime() + "测试开始-------");
        cycleIndex++;
        ArrayList<CallLogBean> mList = new ArrayList<>();
        int times = 0;
        for (int i = 0; i < params.numbers.length; i++) {
            waitGapTime(i);
            dial(params.numbers[i]);
            Util.i("拔号=" + Util.getTime());
            times = handlerCall(times);
            iAction.writeResult(successFled, failed, new WriteResultListener() {
                @Override
                public void onFinish(boolean b, String success) {
                    if (!b) {
                        publishProgress(1, success);
                    }
                }
            });
            mList.add(new CallLogBean(0, i + 1, Util.getTime()));
            mList.add(new CallLogBean(1, i + 1, params.numbers[i]));
            mList.add(new CallLogBean(2, i + 1, cycleIndex + ""));
        }
        iAction.exportExcel(mList);
        waitUntilRoundFinish(beginTime);
        util.WriteResultToSdCard("\n" + "---------" + Util.getTime() + "测试结束-------");
    }

    private int handlerCall(int times) {
        try {
            long wait_offHook_time = waitOffHook();
            times++;
            String test_result = "\n第" + cycleIndex + "轮第" + times + "次呼通时间:" + Util.getTime();
            Util.i("接通=" + Util.getTime());
            util.WriteResultToSdCard(test_result);
            Util.setHandOn(mContext, params.isSpeakOn);
            long targetTime = params.call_time * 1000 - wait_offHook_time + System.currentTimeMillis();
            while (System.currentTimeMillis() < targetTime) {
                SystemClock.sleep(1);
                if (!AutoCallAction.isTest) {
                    throw new RuntimeException("停止测试");
                }
            }
            iTelephony.endCall();
            Util.i("挂断=" + Util.getTime());
            test_result = "\n第" + cycleIndex + "轮第" + times + "次挂断时间:" + Util.getTime();
            util.WriteResultToSdCard(test_result);
            successFled++;
            if (cycleIndex == params.count && times == params.numbers.length) {
                AutoCallAction.isTest = false;
                publishProgress(2);
            }
        } catch (Exception e) {
            failed++;
        }
        return times;
    }

    private long waitOffHook() throws android.os.RemoteException {
        long start = System.currentTimeMillis();
        while (iTelephony.getCallState() != TelephonyManager.CALL_STATE_OFFHOOK) {
            SystemClock.sleep(1);
        }
        long end = System.currentTimeMillis();
        return end - start;
    }

    private void waitGapTime(int i) {
        if (i > 0) {
            long targetTime=System.currentTimeMillis()+params.gap_time * 1000;
            while(System.currentTimeMillis()<targetTime){
                SystemClock.sleep(10);
                if (!AutoCallAction.isTest){
                    throw new RuntimeException("停止测试");
                }
            }
        }
    }

    private void waitUntilRoundFinish(long beginTime) {
        long finishTime = System.currentTimeMillis();
        long read_call_time = finishTime - beginTime;
        int times = params.numbers.length - 1;
        long sleepTime = params.call_time_sum * 1000 - params.numbers.length * read_call_time - times * params.gap_time * 1000;
        Log.e(TAG, "call_time_sum:" + params.call_time_sum + ",read_call_time:" + read_call_time + ",gap_time" + params.gap_time + ",sleepTime:" + sleepTime);
        if (sleepTime > 0) {
            SystemClock.sleep(sleepTime);
        } else {
            publishProgress(1, "通话总时长小于每次通话时长总和");
        }
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

}
