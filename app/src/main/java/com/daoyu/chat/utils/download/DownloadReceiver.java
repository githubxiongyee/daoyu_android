package com.daoyu.chat.utils.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.home.activity.AndroidOPermissionActivity;
import com.daoyu.chat.utils.SharedPreferenceUtil;
import com.facebook.stetho.common.LogUtil;

import java.io.File;

public class DownloadReceiver extends BroadcastReceiver {
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getAction();
        long downloadId = SharedPreferenceUtil.getInstance().getLong(Constant.DOWNLOAD_ID, 0);
        long currentId = intent.getExtras().getLong(DownloadManager.EXTRA_DOWNLOAD_ID);
        if (downloadId == 0 || currentId != downloadId) return;

        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            Cursor cursor = downloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    int fileUriIdx = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                    String fileUri = cursor.getString(fileUriIdx);
                    String fileName = null;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        if (fileUri != null) {
                            fileName = Uri.parse(fileUri).getPath();
                        }
                    } else {
                        int fileNameIdx = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                        fileName = cursor.getString(fileNameIdx);
                    }
                    LogUtil.e("path", fileName);

                    boolean haveInstallPermission;
                    // 兼容Android 8.0
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        //先获取是否有安装未知来源应用的权限
                        haveInstallPermission = context.getPackageManager().canRequestPackageInstalls();
                        if (!haveInstallPermission) {//没有权限
                            // 弹窗，并去设置页面授权
                            String finalFileName = fileName;
                            final AndroidOInstallPermissionListener listener = new AndroidOInstallPermissionListener() {
                                @Override
                                public void permissionSuccess() {
                                    installApk(context, finalFileName);
                                }

                                @Override
                                public void permissionFail() {
                                    Toast.makeText(context, "授权失败，无法安装应用", Toast.LENGTH_SHORT).show();
                                }
                            };

                            AndroidOPermissionActivity.sListener = listener;
                            Intent intent1 = new Intent(context, AndroidOPermissionActivity.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent1);

                        } else {
                            installApk(context, fileName);
                        }
                    } else {
                        installApk(context, fileName);
                    }
                }
            }
        }
    }

    public interface AndroidOInstallPermissionListener {
        void permissionSuccess();

        void permissionFail();
    }


    private void installApk(Context context, String filePath) {
        if (context == null || TextUtils.isEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        Intent install = new Intent(Intent.ACTION_VIEW);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(context, "com.daoyu.chat.fileprovider", file);
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            install.setDataAndType(contentUri, "application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            install.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        SharedPreferenceUtil.getInstance().putLong(Constant.DOWNLOAD_ID, 0);
        context.startActivity(install);
    }
}
