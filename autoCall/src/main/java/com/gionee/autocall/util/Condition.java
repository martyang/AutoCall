package com.gionee.autocall.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;

import java.lang.reflect.Method;

public class Condition {
    public static void set(Context context) {
        String pkgName = context.getPackageName();
        addToAllowBootWhiteLIst(context, pkgName);
        addToWhileList(context, pkgName);
        setScreenTimeOut(context, 1800000);
        setRotate(context, false);
        setStayOn(context, true);
    }

    private static final Uri ROSTER_CONTENT_URI = Uri
            .parse("content://com.amigo.settings.RosterProvider/rosters");

    public static void addToAllowBootWhiteLIst(Context mContext, String pkgName) {
        if (!isInAllowbootList(mContext,pkgName)) {
            ContentValues values = new ContentValues();
            values.put("usertype", "allowboot");
            values.put("packagename", pkgName);
            values.put("status", "1");
            mContext.getContentResolver().insert(ROSTER_CONTENT_URI, values);
        }
    }

    public static void setScreenTimeOut(Context context, int timeOut) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeOut);
    }

    public static void setRotate(Context context, boolean isOpen) {
        try {
            Class<?> aClass = Class.forName("com.android.internal.view.RotationPolicy");
            Method set = aClass.getMethod("setRotationLock", Context.class, boolean.class);
            set.invoke(null, context, !isOpen);
        } catch (Exception e){
            Util.i(e.toString());
        }
    }

    public static void setStayOn(Context context, boolean isOpen) {
        Settings.Global.putInt(context.getContentResolver(),
                Settings.Global.STAY_ON_WHILE_PLUGGED_IN, isOpen ? 1 : 0);
    }

    public static boolean isInWhiteList(Context context, String pkgName) {
        boolean result = false;
        Cursor cursor = context.getContentResolver().query(ROSTER_CONTENT_URI,
                null, "usertype='oneclean' AND packagename='" + pkgName + "'", null, null);
        if (cursor != null) {
            result = cursor.getCount() > 0;
//            Util.i("oneclean" + cursor.getCount() + "");
            cursor.close();
        }
        return result;
    }

    public static boolean isInAllowbootList(Context context, String pkgName) {
        boolean result = false;
        Cursor cursor = context.getContentResolver().query(ROSTER_CONTENT_URI,
                null, "usertype='allowboot' AND packagename='" + pkgName + "'", null, null);
        if (cursor != null) {
            result = cursor.getCount() > 0;
            cursor.close();
        }
        return result;
    }


    public static void addToWhileList(Context context, String pkgName) {
        if (!isInWhiteList(context, pkgName)) {
            ContentValues cv = new ContentValues();
            cv.put("packagename", pkgName);
            cv.put("usertype", "oneclean");
            cv.put("status", "2");
            context.getContentResolver().insert(ROSTER_CONTENT_URI, cv);
        }
    }


}
