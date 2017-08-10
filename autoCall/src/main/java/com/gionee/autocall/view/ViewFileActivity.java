package com.gionee.autocall.view;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.gionee.autocall.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class ViewFileActivity extends Activity {
    private static final String TAG = "ViewFileActivity";
    private String fileNameString;
    private static final String GB2312 = "GB2312";
    private static final String utf8 = "UTF-8";
    private static final String defaultCode = utf8;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filebrowser);
        try {
            fileNameString = getIntent().getExtras().getString("fileName");
            reCodeAndShow(defaultCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reCodeAndShow(String code) {
        TextView tv = (TextView) findViewById(R.id.view_contents);
        String fileString = getStringFromFile(code);
        if (fileString == null || fileString.equals("")) {
            tv.setText("测试结果为空");
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setTextSize(25);
        } else {
            tv.setText(fileString);
        }
    }

    //从文件得到字符串
    public String getStringFromFile(String code) {
        try {
            StringBuilder sb = new StringBuilder();
            FileInputStream fInputStream = new FileInputStream(new File(fileNameString));
            Log.e(TAG, "getStringFromFile 路径: " + fileNameString);
            //java.io.InputStreamReader 直接将编码作为参数引入内部函数处理
            InputStreamReader inputStreamReader = new InputStreamReader(fInputStream, code);
            @SuppressWarnings("resource")
            BufferedReader in = new BufferedReader(inputStreamReader);
            if (new File(fileNameString).exists()) {
                while (in.ready()) {
                    sb.append(in.readLine()).append("\n");
                }
                in.close();
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}