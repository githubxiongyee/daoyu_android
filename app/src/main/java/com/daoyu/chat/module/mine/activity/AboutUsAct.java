package com.daoyu.chat.module.mine.activity;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.home.activity.CommonWebViewActivity;
import com.daoyu.chat.module.home.utils.UpgradeUtil;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.bean.UpgradeApkB;
import com.daoyu.chat.utils.ToolsUtil;
import com.daoyu.chat.utils.download.DownLoadUtil;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.leon.channel.helper.ChannelReaderUtil;
import com.lzy.okgo.model.Response;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;

import butterknife.BindView;

public class AboutUsAct extends BaseTitleActivity {
    @BindView(R.id.cl_understand)
    ConstraintLayout clUnderstand;
    @BindView(R.id.cl_net)
    ConstraintLayout clNet;
    @BindView(R.id.cl_update)
    ConstraintLayout clUpdate;
    @BindView(R.id.cl_agreement)
    ConstraintLayout clAgreement;
    @BindView(R.id.tv_version)
    TextView tvVersion;

    @Override
    protected int getLayoutResId() {
        return R.layout.act_about_us;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("关于我们");
        clUnderstand.setOnClickListener(this);
        clNet.setOnClickListener(this);
        clUpdate.setOnClickListener(this);
        clAgreement.setOnClickListener(this);
        tvVersion.setText("版本号V"+ ToolsUtil.getVersionName(this));
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.cl_understand:
                startActivity(new Intent(this, UnderstandAct.class));
                break;
            case R.id.cl_net:
                //官网
                Intent intent2 = new Intent(this, CommonWebViewActivity.class);
                intent2.putExtra(Constant.WEB_VIEW_URL, "http://futruedao.com/");
                intent2.putExtra(Constant.WEB_VIEW_TITLE, "官网");
                startActivity(intent2);
                break;
            case R.id.cl_update:
                requestPermission(Permission.Group.STORAGE);
                break;
            case R.id.cl_agreement:
                Intent intent = new Intent(this, CommonWebViewActivity.class);
                intent.putExtra(Constant.WEB_VIEW_URL, "http://futruedao.com:8080/nq/guide/daoyu");
                intent.putExtra(Constant.WEB_VIEW_TITLE, "用户注册协议");
                startActivity(intent);
                break;
        }
    }

    private void requestUpgrade() {
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_UPGRADE, null, AboutUsAct.this, new JsonCallback<UpgradeApkB>(UpgradeApkB.class) {
            @Override
            public void onSuccess(Response<UpgradeApkB> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                UpgradeApkB body = response.body();
                if (body.getCode() == 1) {
                    if (body.getData() == null || body.getData().size() == 0) return;
                    UpgradeApkB.DataBean dataBean = body.getData().get(0);
                    String remarks = dataBean.getRemarks();
                    String msg = TextUtils.isEmpty(remarks) ? "" : remarks;
                    String apkUrl = dataBean.getUrl().trim();
                    String ver = dataBean.getConstantName();
                    if (UpgradeUtil.getVersionName(AboutUsAct.this).equals(ver)) {
                       toast.toastShow("当前已是最新版本");
                    } else {
                        if (TextUtils.isEmpty(apkUrl)) return;
                        String channel = ChannelReaderUtil.getChannel(getApplicationContext());
                        if (!TextUtils.isEmpty(channel)) {
                            UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
                            String userPhone = userInfoData.userPhone;
                            if ("18028767791".equals(userPhone)) {
                                Log.d("TAG", "渠道包账号18028767791不升级");
                                return;
                            }
                        }
                        showUploadDialog(msg, apkUrl);
                        return;
                    }
                } else {
                    toast.toastShow(body.getMsg());
                }
            }
        });
    }

    private void showUploadDialog(String msg, final String apkUrl) {
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(AboutUsAct.this);
        normalDialog.setTitle("版本升级");
        normalDialog.setMessage(msg);
        normalDialog.setPositiveButton("升级", (dialog, which) -> {
            dialog.dismiss();
            DownLoadUtil.startDownload(AboutUsAct.this, apkUrl, "潜言", "daoyu.apk");
            toast.toastShow("正在下载，请在通知栏查看进度");
                });
        normalDialog.show();
    }

    @Override
    public void onPermissionGrant(List<String> permissions) {
        if (AndPermission.hasPermissions(this, Permission.Group.STORAGE)) {
            requestUpgrade();
        }
    }
}
