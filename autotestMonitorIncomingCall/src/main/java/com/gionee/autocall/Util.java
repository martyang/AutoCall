package com.gionee.autocall;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    public static int sCallInDuration = 10;
    public static boolean sIsAutoEndCall = false;

    public String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd-HH:mm:ss:SSS");
        Date curDate = new Date(System.currentTimeMillis());
        String time = formatter.format(curDate);
        return time;
    }

    public void WriteResulttoSDcard(String result, int All_count, int succ_count) {
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "AutoEnd" + File.separator + "gioneeTelephony" + ".txt";
        System.out.println("写入方法执行" + dir);
        Log.i(MyTelephonyActivity.TAG, "写入方法执行:" + dir);
        File mFile = new File(dir);
        try {
            if (!mFile.exists()) {
                mFile.createNewFile();
            }
            FileWriter fw = new FileWriter(mFile, true);
            fw.flush();
            fw.write(result);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
