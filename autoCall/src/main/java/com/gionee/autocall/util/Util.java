package com.gionee.autocall.util;


import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.TELEPHONY_SERVICE;


public class Util {
    public static void i(String text) {
        Log.i(Constants.TAG, text);
    }

    public static String getTime() {
        return getTime("yyyy-MM-dd-HH:mm:ss:SSS");
    }

    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        @SuppressLint("HardwareIds") String id = tm.getDeviceId();
        return id;
    }

    public static String getNetWorkType(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        return tm.getNetworkOperatorName();
    }

    public static String getTime(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    public static void setHandOn(Context context, boolean isOpen) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setSpeakerphoneOn(isOpen);
    }

    public void WriteResultToSdCard(String result) {
        File mFile = new File(Environment.getExternalStorageDirectory() + File.separator + "AutoCallActivity" + File.separator + "gioneeTelephonyCall.txt");
        try {
            if (!mFile.getParentFile().exists()) {
                boolean mkdirs = mFile.getParentFile().mkdirs();
                Util.i(mkdirs+"");
            }
            FileWriter fw = new FileWriter(mFile, true);
            fw.flush();
            fw.write(result);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File createxlsFile() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "AutoCallActivity" + File.separator + "gioneeTelephonyCall.xls");
        if (!file.exists()) {
            try {
                boolean mkdirs = file.getParentFile().mkdirs();
                boolean newFile = file.createNewFile();
                Util.i(mkdirs+""+newFile+"");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public String getMethods(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
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
