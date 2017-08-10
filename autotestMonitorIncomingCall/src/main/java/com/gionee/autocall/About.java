package com.gionee.autocall;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class About {

    /**
     * 显示关于对话框
     */
    public static void showAboutDialog(Activity mContext) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         final View content = inflater.inflate(R.layout.dialog_about, null, false);
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Snackbar.make(content, "songyc@gionee.com", Snackbar.LENGTH_LONG).setAction("QQ", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                         Snackbar.make(content, "229530236", Snackbar.LENGTH_LONG).setAction("关闭", new View.OnClickListener() {
//                             @Override
//                             public void onClick(View v) {
//
//                             }
//                         }).show();
//                    }
//                }).show();
            }
        });
        TextView version = (TextView) content.findViewById(R.id.batchSelectMsg);
        try {
            String name = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(),
                    0).versionName;
            version.setText(mContext.getString(R.string.action_version) + " " + name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        DialogHelper.create(mContext, mContext.getString(R.string.app_name), null, new DialogHelper.OnBeforeCreate() {
            @Override
            public void setOther(AlertDialog.Builder builder) {
                builder.setView(content);
                builder.setPositiveButton("确定", null);
            }
        }).show();
    }

}
