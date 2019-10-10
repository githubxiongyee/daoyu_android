package com.daoyu.chat.module.home.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseActivity;
import com.daoyu.chat.utils.download.DownloadReceiver;


public class AndroidOPermissionActivity extends BaseActivity {

    public static final int INSTALL_PACKAGES_REQUESTCODE = 1;
    private AlertDialog mAlertDialog;
    public static DownloadReceiver.AndroidOInstallPermissionListener sListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 26) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, INSTALL_PACKAGES_REQUESTCODE);
        } else {
            finish();
        }

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_permiss_install;
    }

    @Override
    protected void initEvent() {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case INSTALL_PACKAGES_REQUESTCODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (sListener != null) {
                        sListener.permissionSuccess();
                        finish();
                    }
                } else {
                    //startInstallPermissionSettingActivity();
                    showDialog();
                }
                break;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("为了正常升级潜言，请点击设置按钮，允许安装未知来源应用");
        builder.setPositiveButton("设置", (dialogInterface, i) -> {
            startInstallPermissionSettingActivity();
            mAlertDialog.dismiss();
        });
        builder.setNegativeButton("取消", (dialogInterface, i) -> {
            if (sListener != null) {
                sListener.permissionFail();
            }
            mAlertDialog.dismiss();
            finish();
        });
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // 授权成功
            if (sListener != null) {
                sListener.permissionSuccess();
            }
        } else {
            // 授权失败
            if (sListener != null) {
                sListener.permissionFail();
            }
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sListener = null;
    }
}
