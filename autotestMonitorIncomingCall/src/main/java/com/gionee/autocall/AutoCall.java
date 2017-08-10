package com.gionee.autocall;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
监控来电app：
1.增加来电识别后设置时间自动挂断选项
 */
public class AutoCall extends Activity {

    private EditText numberView = null;
    private EditText countView = null;
    private EditText timeoutView = null;
    private EditText durationView = null;
    private ITelephony iTelephony;
    private TelephonyManager tm;
    private Context mContext;
    private int successfuled = 0;
    private int failed = 0;
    private int count;
    private int duration;
    private String number;
    private AudioManager mAudioManager;
    // private Spinner sp;
    private int timeout;
    int simCardId;
    Boolean isOpen = true;
    File recordFile = null;
    File sdcardPath = null;
    String[] phoneType = null;
    String[] simState = null;
    public Boolean isCall = true;
    Handler handler;
    String strr, mes;
    Button button, end;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);
        String title = getResources().getString(R.string.app_name);
        setTitle(title);
        // outputView=(EditText)findViewById(R.id.output);
        mContext = this.getApplicationContext();
        numberView = (EditText) findViewById(R.id.number);
        numberView.setText("10086");
        countView = (EditText) findViewById(R.id.count);
        countView.setText("3");
        timeoutView = (EditText) findViewById(R.id.gap_time);
        timeoutView.setText("15");
        durationView = (EditText) findViewById(R.id.call_time);
        durationView.setText("15");
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int id = this.getResources().getIdentifier("com.gionee.autoanswer.R.id.start".replace(".R.id.", ":id/"), null, null);
        Log.i("song", "song:" + getMethods() + " id:" + id);

        // 目前选择SIM卡拨打电话功能暂时未能实现，该代码先注释掉
        // sp = (Spinner)findViewById(R.id.spinner1);
        // ArrayAdapter<CharSequence> adapter =
        // ArrayAdapter.createFromResource(this, R.array.colors,
        // android.R.layout.simple_spinner_item);
        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // sp.setAdapter(adapter);
        // sp.setOnItemSelectedListener(
        // new OnItemSelectedListener() {
        // public void onItemSelected(
        // AdapterView<?> parent, View view, int position, long id) {
        // simCardId = position;
        // }
        // public void onNothingSelected(AdapterView<?> parent) {
        // }
        // });
        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    button.setEnabled(true);
                    end.setEnabled(false);
                    TextView tv = (TextView) findViewById(R.id.result);
                    // 获取测试机IMEI号码
                    String ID = tm.getDeviceId();
                    // Get current time获取当前时间
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss", Locale.CHINA);
                    Date curDate = new Date(System.currentTimeMillis());
                    String str = formatter.format(curDate);
                    // get result取得测试结果
                    String count_total = mContext.getResources().getString(
                            R.string.Tota_times);
                    String successful = mContext.getResources().getString(
                            R.string.successful);
                    String fail = mContext.getResources().getString(
                            R.string.failed);

                    mes = count_total + ": " + count + "\n" + successful + ": "
                            + successfuled + "\n" + fail + ": " + failed + "\n"
                            + "测试时间" + ": " + str + "\n" + "测试卡类型" + ": "
                            + tm.getNetworkOperatorName();
                    // 广播测试结果
                    Toast.makeText(AutoCall.this, mes, Toast.LENGTH_LONG).show();
                    handler.sendEmptyMessage(1);
                    // 获取测试结果并且写入SD卡

                    strr = mes + "\n" + "测试机IMEI：" + ID;
                    // handler.sendEmptyMessage(0);
                    tv.setText(strr);
                    if (Environment.getExternalStorageState().equals(
                            android.os.Environment.MEDIA_MOUNTED)) {
                        // 获取SD卡路径
                        File fil = new File(
                                android.os.Environment
                                        .getExternalStorageDirectory()
                                        + "/CallTestResult.txt");
                        File xlsFile = new File(android.os.Environment.getExternalStorageDirectory() + File.separator + "AutoEnd" + File.separator + "AutoEndLog.xls");

                        // write file
                        try {
                            FileOutputStream fileOS = new FileOutputStream(fil);
                            fileOS.write(strr.getBytes());
                            fileOS.close();
                            BufferedWriter buf = new BufferedWriter(
                                    new OutputStreamWriter(fileOS));
                            buf.write(strr, 0, strr.length());
                            buf.flush();
                            buf.close();
                            //Log.e("tangcm", "111111111111111111111");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            //Log.e("tangcm", "46gfuhgfuyytuttendkhdfl");
                        } catch (IOException e) {
                            e.printStackTrace();
                            //Log.e("tangcm", "222222222");
                        }
                    } else {
                        handler.sendEmptyMessage(2);
                        Toast.makeText(AutoCall.this, "没有SD卡,将无法保存测试结果到SD卡",
                                Toast.LENGTH_SHORT).show();
                    }

                }
            }
        };
        // 开始按钮监听器
        button = (Button) findViewById(R.id.start);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                button.setEnabled(false);
                end.setEnabled(true);
                String a = durationView.getText().toString();
                int i = Integer.valueOf(a).intValue();
                //if (i < 15) {
                //    showdialog();
                //    button.setEnabled(true);
                //} else {
                successfuled = 0;
                number = numberView.getText().toString().trim();
                count = Integer.parseInt(countView.getText().toString().trim());
                timeout = Integer.parseInt(timeoutView.getText().toString()
                        .trim());
                duration = Integer.parseInt(durationView.getText().toString()
                        .trim());
                Log.i("guoru", "count1" + count);
                new Thread(new startCall()).start();
                //}

            }
        });
        // 结束按钮监听器
        end = (Button) findViewById(R.id.end);
        end.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // isCall=false;
                button.setEnabled(true);
                end.setEnabled(false);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }

    public void showdialog() {
        AlertDialog builder = new AlertDialog.Builder(AutoCall.this)
                .setTitle("通话时长小于15秒，请重新设置 ")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }
                )
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }
                )
                .create();
        builder.show();
    }

    public class startCall extends Thread implements Runnable {
        public void run() {
            // Perform action on click
            // outputView.setText("Start!");
            try {
                tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                Class<?> c = Class.forName(tm.getClass().getName());
                try {
                    Method m = c.getDeclaredMethod("getITelephony",
                            (Class[]) null);
                    m.setAccessible(true);
                    iTelephony = (ITelephony) m.invoke(tm);
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                System.out.println(e.toString());
            }
            Log.i("guoru", "count2" + count);
            // 开始拨打电话
            for (int i = 0; i < count; i++) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                        + number));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (i > 0) {
                    SystemClock.sleep(timeout * 1000);
                }
                startActivity(intent);
                try {
                    int timeElapsed = 0;
                    int state = iTelephony.getCallState();

                    while (state != TelephonyManager.CALL_STATE_OFFHOOK
                            && (timeElapsed < duration * 1000)) {
                        SystemClock.sleep(100);
                        timeElapsed += 100;
                        state = iTelephony.getCallState();
                    }

                    if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        mAudioManager.setSpeakerphoneOn(!mAudioManager
                                .isSpeakerphoneOn());
                        SystemClock.sleep(duration * 1500);
                        iTelephony.endCall();
                        successfuled++;

                    }
                } catch (Exception e) {
                    failed++;
                }
                handler.sendEmptyMessage(0);
            }


        }
    }

    /*
     * protected void onRestart() { super.onRestart(); Log.i("guoru","restart");
     * count = 0; successfuled = 0; failed = 0; }
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            final AlertDialog isExit = new AlertDialog.Builder(this).create();
            isExit.setTitle("系统提示");
            isExit.setMessage("确定要退出吗?");
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case AlertDialog.BUTTON1:
                            NotificationManager notificationManager = (NotificationManager) AutoCall.this
                                    .getSystemService(NOTIFICATION_SERVICE);
                            notificationManager.cancel(0);
                            String packagename = getPackageName();
                            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                            finish();
                            manager.killBackgroundProcesses(packagename);
                            android.os.Process.killProcess(android.os.Process
                                    .myPid());
                            break;
                        case AlertDialog.BUTTON2:
                            isExit.cancel();
                            break;
                        default:
                            break;
                    }
                }
            };
            isExit.setButton("确定", listener);
            isExit.setButton2("取消", listener);
            isExit.show();

        }
        return false;
    }

    public String getMethods() {
        TelephonyManager tm = (TelephonyManager) this.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String str = "aaaa";
        Method[] methods = tm.getClass().getDeclaredMethods();


        for (Method me : methods) {
            if (me.getName().equals("getITelephony")) {
                str += me.getName();
                str += "" + me.getReturnType();
                str += "" + me.getGenericReturnType();
                me.setAccessible(true);
                try {
                    me.invoke(tm);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return str;
    }


}
