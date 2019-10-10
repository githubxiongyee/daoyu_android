package com.daoyu.chat.utils.download;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import com.daoyu.chat.common.Constant;
import com.daoyu.chat.utils.SharedPreferenceUtil;
import com.daoyu.chat.utils.ToolsUtil;

import java.io.File;

public class DownLoadUtil {

    public static void startDownload(Context context, String apkUrl, String title, String childName) {
        if (!isDownloadManagerEnable(context)) {
            try {
                //Open the specific App Info page:
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:com.android.providers.downloads"));
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                //Open the generic Apps page:
                try {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    context.startActivity(intent);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            return;
        }
        if (TextUtils.isEmpty(apkUrl)) return;

        try {
            String downLoadPath = ToolsUtil.getDownLoadPath(Constant.CM_EXTERNAL_APK_FOLDER);
            if (TextUtils.isEmpty(downLoadPath)) return;
            Uri uri = Uri.parse(apkUrl);
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            request.setTitle(title);
            request.setDescription("下载中...");
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            File file = new File(downLoadPath, childName);
            if (file.exists()) {
                file.delete();
            }
            request.setDestinationUri(Uri.fromFile(file));
            request.setDestinationInExternalPublicDir(downLoadPath, childName);
            long downloadId = downloadManager.enqueue(request);
            SharedPreferenceUtil.getInstance().putLong(Constant.DOWNLOAD_ID, downloadId);
        } catch (Exception e) {
            Toast.makeText(context, "下载出错", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static boolean isDownloadManagerEnable(Context context) {
        int state = context.getApplicationContext().getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                    state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED);
        } else {
            return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                    state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER);
        }
    }
}
