package com.gionee.autocall.action;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

import com.gionee.autocall.bean.CallLogBean;
import com.gionee.autocall.bean.CallParams;
import com.gionee.autocall.bean.ResultBean;
import com.gionee.autocall.util.FileUtil;
import com.gionee.autocall.util.Preference;
import com.gionee.autocall.util.Util;
import com.gionee.autocall.view.IAutoCall;
import com.gionee.autocall.view.ViewFileActivity;

import java.io.File;
import java.util.ArrayList;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


public class AutoCallAction implements IAutoCallAction {
    private Context mContext;
    private IAutoCall iAutoCall;
    private File mTxtFile = null;
    private File mFileDir;
    private CallParams params;
    private int mPoint;
    public static boolean isTest = false;


    public AutoCallAction(IAutoCall iAutoCall ) {
        this.mContext = iAutoCall.getContext();
        this.iAutoCall = iAutoCall;
    }

    @Override
    public void writeResult(int successFled, int failed, WriteResultListener listener) {
        String id = Util.getIMEI(mContext);
        String type = Util.getNetWorkType(mContext);
        String time = Util.getTime("yyyy年MM月dd日   HH:mm:ss");
        ResultBean bean = new ResultBean().setDeviceID(id).setTestTime(time).setCount_fail(params.count).setNetworkType(type).setCount_fail(failed).setCount_success(successFled).setCall_times(params.numbers.length).setCount_total(params.count);
        String result = bean.getResult();
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            FileUtil.appendFile(mTxtFile, result);
            if (listener != null) {
                listener.onFinish(true, "success");
            }
        } else {
            if (listener != null) {
                listener.onFinish(false, "没有SD卡,将无法保存测试结果到SD卡");
            }
        }
    }

    public void preparePath() {
        String mDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "AutoCallActivity";
        mFileDir = new File(mDirPath);
        mTxtFile = new File(mDirPath + File.separator + "CallTestResult.txt");
        if (!mFileDir.exists()) {
            mFileDir.mkdirs();
        }
    }

    public void clearResult() {
        if (mFileDir.exists()) {
            for (File file : mFileDir.listFiles()) {
                file.delete();
            }
            mFileDir.delete();
        }
        if (!mFileDir.exists()) {
            Toast.makeText(mContext, "测试结果已经清除", Toast.LENGTH_LONG).show();
        }
    }

    public void showCheckResult() {
        Intent mIntent = new Intent(mContext, ViewFileActivity.class);
        mIntent.putExtra("fileName", mTxtFile.getAbsolutePath());
        mContext.startActivity(mIntent);
    }

    public void setParams(CallParams params) {
        this.params = params;
        Preference.putString("number", params.number);
        Preference.putInt("count", params.count);
        Preference.putInt("call_time", params.call_time);
        Preference.putInt("call_time_sum", params.call_time_sum);
        Preference.putInt("gap_time", params.gap_time);
        Preference.putInt("isSpeakOn", params.isSpeakOn ? 1 : 0);
    }

    public CallParams getParams() {
        String number = Preference.getString("number", "10086,10010");
        int count = Preference.getInt("count", 3);
        int call_time = Preference.getInt("call_time", 10);
        int call_time_sum = Preference.getInt("call_time_sum", 60);
        int gap_time = Preference.getInt("gap_time", 20);
        int isSpeakOn = Preference.getInt("isSpeakOn", 0);
        return new CallParams().setNumber(number).setCount(count).setCall_time(call_time).setCall_time_sum(call_time_sum).setGap_time(gap_time).setSpeakOn(isSpeakOn == 1);
    }

    public void exportExcel(ArrayList<CallLogBean> mList) {
        WritableWorkbook workBook = null;
        try {
            //Environment.getExternalStorageDirectory().getAbsolutePath()
            String fileName = "AutoCallRecord.xls";
            File file = new File(mFileDir, fileName);
            WritableSheet sheet;
            if (file.isFile() && file.exists()) {
                Workbook workbook1 = Workbook.getWorkbook(file);
                workBook = Workbook.createWorkbook(file, workbook1);
                sheet = workBook.getSheet(0);
                String contents = sheet.getWritableCell(5, 0).getContents();
                if (contents != null && !contents.isEmpty()) {
                    mPoint = Integer.parseInt(contents);
                }
            } else {
                workBook = Workbook.createWorkbook(file);
                sheet = workBook.createSheet("CallLog", 0);
                sheet.addCell(new Label(5, 0, "0"));
                sheet.addCell(new Label(0, 0, "拨号时间"));
                sheet.addCell(new Label(1, 0, "拨出号码"));
                sheet.addCell(new Label(2, 0, "拨打次数"));
            }
            int row = mPoint;//用于保存
            for (CallLogBean callLogBean : mList) {
                row = callLogBean.row + mPoint;
                Label mLabel = new Label(callLogBean.col, row, callLogBean.content);
                sheet.addCell(mLabel);
            }
            sheet.addCell(new Label(5, 0, row + ""));
            workBook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workBook != null) {
                try {
                    workBook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void updateViews() {
        iAutoCall.updateViews();
    }

    public void start() {
        new StartCallTask(mContext, this, params).execute();
    }
}
