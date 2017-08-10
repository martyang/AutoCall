package com.gionee.autocall.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.gionee.autocall.R;
import com.gionee.autocall.action.AutoCallAction;
import com.gionee.autocall.bean.CallParams;
import com.gionee.autocall.util.About;
import com.gionee.autocall.util.Condition;
import com.gionee.autocall.util.DialogHelper;

/*
自动化app新需求：
自动拨打app：
1.增加一个免提的自动勾选选项
2.通话间隔及各类时间设置，我们可以自由编辑
 */
public class AutoCallActivity extends AppCompatActivity implements IAutoCall, OnClickListener {
    private EditText mNumber_et, mCount_et, mGap_time_et, mCall_time_et, mCall_time_sum_et;
    private CheckBox mIsSpeakerOpen;
    Button mStartBtn;
    AutoCallAction mAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        verifyStoragePermissions(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);
        mAction = new AutoCallAction(this);
        mAction.preparePath();
        CallParams params = mAction.getParams();
        setTitle(getResources().getString(R.string.app_name));
        mNumber_et = (EditText) findViewById(R.id.number);
        mNumber_et.setText(params.number);
        mCount_et = (EditText) findViewById(R.id.count);
        mCount_et.setText(params.count + "");
        mGap_time_et = (EditText) findViewById(R.id.gap_time);
        mGap_time_et.setText(params.gap_time + "");
        mCall_time_et = (EditText) findViewById(R.id.call_time);
        mCall_time_et.setText(params.call_time + "");
        mCall_time_sum_et = (EditText) findViewById(R.id.call_time_sum);
        mCall_time_sum_et.setText(params.call_time_sum + "");
        mIsSpeakerOpen = (CheckBox) findViewById(R.id.is_speaker_phone_open);
        mIsSpeakerOpen.setChecked(params.isSpeakOn);
        mStartBtn = (Button) findViewById(R.id.start);
        mStartBtn.setOnClickListener(this);
        updateViews();
        Condition.setScreenTimeOut(this, 1800000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                if (!AutoCallAction.isTest) {
                    TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
                    if (tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT) {
                        Toast.makeText(getApplicationContext(), "请先插入卡", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    CallParams p = getCallParams();
                    if (p.call_time + p.gap_time > p.call_time_sum / p.numbers.length) {
                        showDialog("总时长太小，请重新设置");
                        return;
                    }
                    mAction.setParams(p);
                    AutoCallAction.isTest = true;
                    mAction.start();
                } else {
                    AutoCallAction.isTest = false;
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                updateViews();
                break;

            default:
                break;
        }
    }

    private CallParams getCallParams() {
        String number = mNumber_et.getText().toString().trim();
        int count = Integer.parseInt(mCount_et.getText().toString().trim());
        int timeout = Integer.parseInt(mGap_time_et.getText().toString().trim());
        int duration = Integer.parseInt(mCall_time_et.getText().toString().trim());
        int duration_sum = Integer.parseInt(mCall_time_sum_et.getText().toString().trim());
        boolean isSpeakOn = mIsSpeakerOpen.isChecked();

        String[] numbers;
        if (number.contains(",")) {
            numbers = number.split(",");
        } else {
            numbers = new String[]{number};
        }
        return new CallParams().setNumber(number).setSpeakOn(isSpeakOn).setNumbers(numbers).setCount(count).setGap_time(timeout).setCall_time(duration).setCall_time_sum(duration_sum);

    }

    @Override
    public void updateViews() {
        boolean isTest = AutoCallAction.isTest;
        mStartBtn.setText(isTest ? "停止" : "开始");
        mIsSpeakerOpen.setEnabled(!isTest);
        setViewsEnable(!isTest, mNumber_et, mCount_et, mGap_time_et, mCall_time_et, mCall_time_sum_et);
    }

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
            case R.id.reports:
                mAction.showCheckResult();
                break;
            case R.id.clearReport:
                mAction.clearResult();
                break;
            case R.id.restore_default:
                assertRestoreBtnParameters();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void restoreDefaultParameters() {
        mNumber_et.setText(R.string.default_number);
        mCount_et.setText(R.string.default_count);
        mGap_time_et.setText(R.string.default_timeout);
        mCall_time_et.setText(R.string.default_duration);
        mCall_time_sum_et.setText(R.string.default_duration_sum);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            DialogHelper.create(this, "系统提示", "确定要退出吗?", new DialogHelper.OnBeforeCreate() {
                @Override
                public void setOther(AlertDialog.Builder builder) {
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    });
                    builder.setNegativeButton("取消", null);
                }
            }).show();
        }
        return false;
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.CALL_PHONE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    //输入限制弹窗显示
    public void showDialog(String tipsString) {
        AlertDialog builder = new AlertDialog.Builder(AutoCallActivity.this)
                .setTitle(tipsString)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).create();
        builder.show();
    }

    public void assertRestoreBtnParameters() {
        AlertDialog builder = new AlertDialog.Builder(AutoCallActivity.this)
                .setTitle("确定要恢复默认参数")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        restoreDefaultParameters();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).create();
        builder.show();
    }


    private void setViewsEnable(boolean isEnable, View... v) {
        for (View view : v) {
            view.setEnabled(isEnable);
        }
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }
}

