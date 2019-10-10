package com.daoyu.chat.module.system.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.system.dialog.QuitFixPayPwdDialog;
import com.daoyu.chat.utils.ToolsUtil;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.daoyu.chat.view.PayPsdInputView;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 再次确认密码
 */
public class ConfirmNewPasswordActivity extends BaseTitleActivity implements QuitFixPayPwdDialog.OnItemViewClickListener {
    String updatePwd = "";
    @BindView(R.id.psd_view)
    PayPsdInputView psdView;
    private QuitFixPayPwdDialog quitFixPayPwdDialog;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_input_new_password;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("再次确认密码");

        Intent intent = getIntent();
        if (intent == null) return;
        updatePwd = (String) intent.getExtras().get("update_pay_pwd");

        psdView.setComparePassword(new PayPsdInputView.onPasswordListener() {
            @Override
            public void onDifference(String oldPsd, String newPsd) {

            }

            @Override
            public void onEqual(String psd) {

            }

            @Override
            public void inputFinished(String inputPsd) {
                if (inputPsd.equals(updatePwd)){
                    requestUpdatePayPwd(inputPsd);
                }else {
                   toast.toastShow("两次输入不一致，请重试");
                   psdView.cleanPsd();
                }
            }
        });
    }
    private void requestUpdatePayPwd(String inputPsd) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        Map<String, Object> params = new HashMap<>();
        params.put("uId", userInfoData.uid);
        params.put("paySign", ToolsUtil.md5(inputPsd));
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_SET_PAY_PWD, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    if (body.data != null) {
                        userInfoData.paySign=""+ToolsUtil.md5(inputPsd);
                        startActivity(new Intent(ConfirmNewPasswordActivity.this, AccountSecurityActivity.class));
                        finish();
                    }
                    toast.toastShow("修改成功");

                } else {
                    toast.toastShow(body.msg);
                }
            }

            @Override
            public void onError(Response<BaseBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                showQuitFixDialog();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showQuitFixDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showQuitFixDialog() {
        if (quitFixPayPwdDialog != null) {
            quitFixPayPwdDialog.dismissAllowingStateLoss();
        }
        quitFixPayPwdDialog = QuitFixPayPwdDialog.getInstance();
        if (!quitFixPayPwdDialog.isAdded()) {
            quitFixPayPwdDialog.show(getSupportFragmentManager(), "quitFixPayPwdDialog");
        } else {
            quitFixPayPwdDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onConfirm() {
        startActivity(new Intent(ConfirmNewPasswordActivity.this, AccountSecurityActivity.class));
        finish();
    }

    @Override
    public void onCancel() {

    }
}
