package com.gionee.autocall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class MyTelephonyActivity extends AppCompatActivity {
    Thread thread = null;
    TelephonyManager telMgr;
    private Button button1, button2, button3, button4;
    private CheckBox isAutoAnswer;
    private CheckBox isAutoEnd;
    private EditText spaceTime;
    private EditText autoEndTime;
    public static int i = 0;
    public static String TAG = "MyTelephonyActivity";
    static boolean isAutoAnswerTell = false;
    private static long lastIncomingTelTime = -1;
    private int maxSpaceTime = 60;// 2秒
    private WritableWorkbook mWritableWorkbook = null;
    private CheckBox mIsDistinguishEndCb;
    private EditText mIsDistinguishEndEt;
    private int mDistinguishTime;
    private boolean mIsDistinguishCBCheck;
    private TelephonyManager mTelephonyManager;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_abouts:
                About.showAboutDialog(this);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_my_telephony);
        isAutoAnswer = (CheckBox) findViewById(R.id.isAutoAnswer);
        autoEndTime = (EditText) findViewById(R.id.autoEndTime);
        isAutoEnd = (CheckBox) findViewById(R.id.isAutoend);
        mIsDistinguishEndCb = (CheckBox) findViewById(R.id.isDistinguishEndCB);
        mIsDistinguishEndEt = (EditText) findViewById(R.id.isDistinguishEndET);
        isAutoAnswer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Log.i(TAG, "checked!");
                    isAutoAnswerTell = true;
                } else {
                    Log.i(TAG, "noChecked!");
                    isAutoAnswerTell = false;
                }
            }
        });

        mIsDistinguishEndCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsDistinguishCBCheck = isChecked;
                //设置是否识别后自动挂断
                if (isChecked) {
                    mIsDistinguishEndEt.setEnabled(true);
                    mIsDistinguishEndEt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            mDistinguishTime = Integer.parseInt(s.toString());
                        }
                    });
                } else {
                    mIsDistinguishEndEt.setEnabled(false);
                    mIsDistinguishEndEt.removeTextChangedListener(null);
                }
            }
        });
        isAutoEnd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Util.sIsAutoEndCall = isChecked;
                autoEndTime.setEnabled(isChecked);
                if (isChecked) {
                    autoEndTime.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String input = s.toString().trim();
                            if (!input.isEmpty()) {
                                Util.sIsAutoEndCall = isAutoEnd.isChecked();
                                Util.sCallInDuration = Integer.parseInt(input);
                            } else {
                                Util.sIsAutoEndCall = false;
                            }
                        }
                    });
                }
            }
        });
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new OnClickListener() {// 开始
            public void onClick(View v) {
                button1.setEnabled(false);
                mIsDistinguishEndEt.setEnabled(false);
                mIsDistinguishEndCb.setEnabled(false);
//				isAutoAnswer.setClickable(false);
                spaceTime = (EditText) findViewById(R.id.spaceTime);
                maxSpaceTime = Integer.parseInt(spaceTime.getText().toString());
                Log.i(TAG, "button1 OnClickListener() start");
                isAutoEnd.setEnabled(false);
                autoEndTime.setEnabled(false);
                telMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                telMgr.listen(new CallStateListener(), CallStateListener.LISTEN_CALL_STATE);
            }
        });
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new OnClickListener() {// 结束
            public void onClick(View v) {
                lastIncomingTelTime = -1;
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), ViewFile.class);
                mIntent.putExtra("fileName",
                        Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "AutoEnd" + File.separator + "gioneeTelephony" + ".txt");
                startActivity(mIntent);
            }
        });
        button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // 清除测试结果
                lastIncomingTelTime = -1;
                i = 0;
                String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "AutoEnd" + File.separator + "/gioneeTelephony" + ".txt";
                System.out.println(dir);
                File mDel = new File(dir);
                if (mDel.exists()) {
                    mDel.delete();
                    Toast.makeText(getApplicationContext(), "测试结果已经清除", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart------------");
        if (!button1.isEnabled()) {
            Log.i(TAG, "onStart  true-----------");
            telMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            telMgr.listen(new CallStateListener(), CallStateListener.LISTEN_CALL_STATE);
        }
    }

    private class AutoAnswerTell extends Thread {
        @Override
        public void run() {
            Log.i(TAG, "enter thread");
            if (isAutoAnswerTell) { // 自动接听电话
                SystemClock.sleep(500);
                WindowManager mWindowManager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
                Display mDisplay = mWindowManager.getDefaultDisplay();
                DisplayMetrics dm = new DisplayMetrics();
                mDisplay.getMetrics(dm);
                int w = dm.widthPixels;
                int h = dm.heightPixels;
                SystemClock.sleep(4500);
                Log.i(TAG, "touchScreen start");
                Log.i(TAG, "w:" + w + " and " + 481 * w / 720 + ",h:" + h + " and " + 143 * h / 1280);
                // TouchUnit.clickOnScreen(336, 364);
                TouchUnit.clickOnScreen(481 * w / 720, 143 * h / 1280);
                SystemClock.sleep(1500);
                Log.i(TAG, "swipe to the right");
                // TouchUnit.clickOnScreen(268, 580); //点击接听(下拉状态栏后)
                TouchUnit.drag(379 * w / 720, 958 * h / 1280, 591 * w / 720, 958 * h / 1280, 1);
                // TouchUnit.clickOnScreen(481*w/720, 175*h/1280);
                Log.i(TAG, "AutoAnswer start----");
            }
        }
    }

    public class CallStateListener extends PhoneStateListener {
        Util gioneeUtil = new Util();
        StringBuffer result = new StringBuffer();
        ITelephony mITelephony;
        int fail = 0;

        @Override
        public void onCallStateChanged(final int state, String incomingNumber) {
            mTelephonyManager = (TelephonyManager) MyTelephonyActivity.this.getSystemService(TELEPHONY_SERVICE);
            try {
                Class<?> c = Class.forName(mTelephonyManager.getClass().getName());
                Method m = c.getDeclaredMethod("getITelephony", (Class[]) null);
                m.setAccessible(true);
                mITelephony = (ITelephony) m.invoke(mTelephonyManager);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            if (state == TelephonyManager.CALL_STATE_IDLE) {// 挂断
                Log.e("IDLE", incomingNumber);
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {// 接听
                //增加接通电话后定时自动挂断电话的功能
                if (Util.sIsAutoEndCall) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SystemClock.sleep(Util.sCallInDuration * 1000);
                            endCall();
                        }
                    }).start();
                }
                Log.e("OFFHOOK", incomingNumber);
            } else if (state == TelephonyManager.CALL_STATE_RINGING) {
                // 来电
                if (lastIncomingTelTime == -1) {
                    lastIncomingTelTime = System.currentTimeMillis();
                    Log.i(TAG, "lastIncomingTelTime=" + lastIncomingTelTime);
                } else {// 不是第一次来电
                    long currentTimeMills = System.currentTimeMillis();
                    long internalTime = currentTimeMills - lastIncomingTelTime;
                    if (internalTime < 3 * 1000) {// 过滤掉时间间隔很近的状态改变
                        Log.i(TAG, "internalTime=" + internalTime + ";被过滤掉");
                        return;
                    }
                    long spaceTime = System.currentTimeMillis() - lastIncomingTelTime;
                    lastIncomingTelTime = System.currentTimeMillis();
                    Log.i(TAG, "spaceTime=" + spaceTime);
                    Log.i(TAG, "maxSpaceTime=" + maxSpaceTime);
                    long maxSpaceTimeMill = maxSpaceTime * 1000;
                    Log.i(TAG, "maxSpaceTimeMill=" + maxSpaceTimeMill);
                    if (spaceTime > maxSpaceTimeMill) {
                        Log.i(TAG, "spaceTime>maxSpaceTimeMill" + spaceTime + ">" + maxSpaceTimeMill);
                        String testresult = "\n*********FAIL START*****************";
                        testresult = testresult + "\n" + gioneeUtil.getTime() + "--" + incomingNumber + "-->" + "测试次数：" + i;
                        testresult = testresult + "\n*********FAIL END*****************";
                        Log.i(TAG, "Fail:" + i);
                        gioneeUtil.WriteResulttoSDcard(testresult, 0, 0);
                    }
                }
                i++;
                Log.i(TAG, "incoming:incomingNumber=" + incomingNumber + "=" + gioneeUtil.getTime());
                CallLogBean callLogBean1 = new CallLogBean(0, 0, gioneeUtil.getTime());
                //callLogBean1.setCol(0);
                //callLogBean1.setRow(1);
                //callLogBean1.setContent(util.getTime());
                CallLogBean callLogBean2 = new CallLogBean(1, 0, incomingNumber);
                //callLogBean2.setCol(1);
                //callLogBean2.setRow(1);
                //callLogBean2.setContent(incomingNumber);
                ArrayList<CallLogBean> list = new ArrayList<CallLogBean>();
                list.add(callLogBean1);
                list.add(callLogBean2);
                writeLog(list);
                String testresult = "\n" + gioneeUtil.getTime() + "--" + incomingNumber + "-->" + "测试次数：" + i;
                Log.i(TAG, "Success:" + i);
                gioneeUtil.WriteResulttoSDcard(testresult, 0, 0);
                Log.i(TAG, "isAutoAnswerTell=" + isAutoAnswerTell);
            }
            if (mIsDistinguishCBCheck) {
                //识别后定时挂断
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(mDistinguishTime * 1000);
                            if (state == TelephonyManager.CALL_STATE_RINGING) {
                                endCall();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            } else {
                //自动接听
                thread = new Thread(new AutoAnswerTell());
                thread.start();
            }
            Log.i(TAG, "AutoAnswer end----");
            super.onCallStateChanged(state, incomingNumber);
        }

        private void endCall() {
            if (mTelephonyManager != null && mITelephony != null) {
                int state = mTelephonyManager.getCallState();
                if (TelephonyManager.CALL_STATE_OFFHOOK == state || TelephonyManager.CALL_STATE_RINGING == state) {
                    try {
                        mITelephony.endCall();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }


    /**
     * 将含有拨号信息的所有对象写入到xls文件中
     *
     * @param mList 含拨号信息对象的集合
     */
    private synchronized void writeLog(ArrayList<CallLogBean> mList) {
        WritableWorkbook workBook = null;
        int mPoint = 0;
        try {
            String parent = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "AutoEnd" + File.separator;
            Calendar calendar = Calendar.getInstance();
            String timeC = "," + calendar.get(Calendar.YEAR) + "," + (calendar.get(Calendar.MONTH) + 1) + "," + calendar.get(Calendar.DAY_OF_MONTH)//
                    + "," + calendar.get(Calendar.HOUR_OF_DAY) + "," + calendar.get(Calendar.MINUTE) + "," + calendar.get(Calendar.SECOND);
            String fileName = "AutoEndRecord.xls";
            File file = new File(parent, fileName);
            WritableSheet sheet = null;
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }
            if (file.exists()) {//添加新内容
                Workbook workbook1 = Workbook.getWorkbook(file);
                workBook = Workbook.createWorkbook(file, workbook1);
                sheet = workBook.getSheet(0);
                String contents = sheet.getWritableCell(5, 1).getContents();
                if (contents != null && !contents.isEmpty()) {
                    mPoint = Integer.parseInt(contents);
                }
            } else {
                boolean newFile = file.createNewFile();
                workBook = Workbook.createWorkbook(file);
                sheet = workBook.createSheet("EndCallLog", 0);
                sheet.addCell(new Label(0, 0, "接听时间"));
                sheet.addCell(new Label(0, 1, "接听号码"));
                sheet.addCell(new Label(5, 0, "记录"));
                sheet.addCell(new Label(5, 1, "0"));
            }
            int row = mPoint + 1;//用于记录上一次最后保存的位置,以便追加数据
            for (CallLogBean callLogBean : mList) {
                Label mLabel = new Label(callLogBean.col, row, callLogBean.content);
                sheet.addCell(mLabel);
            }
            sheet.addCell(new Label(5, 1, row + ""));
            workBook.write();
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } finally {
            if (workBook != null) {
                try {
                    workBook.close();
                    workBook = null;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
