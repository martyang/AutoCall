package com.gionee.autocall.util;


import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


public class FileUtil {
    private static final String TAG = "AutoCallActivity";

    public static void appendFile(File file, String text) {
        try {
            FileOutputStream fileOS = new FileOutputStream(file, true);
            BufferedWriter buf = new BufferedWriter(
                    new OutputStreamWriter(fileOS));
            buf.write(text, 0, text.length());
            buf.flush();
            Log.i(TAG, "写文件了");
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
    }
}
