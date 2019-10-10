package com.daoyu.chat.module.home.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * 升级
 */
public class UpgradeUtil {
    public static String getVersionName(Context context) {
        // 获取包信息
        String versionName = "";
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (packageInfo != null) {
                versionName = packageInfo.versionName;
                Log.i("info", "=====versionName=====" + versionName);


            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
