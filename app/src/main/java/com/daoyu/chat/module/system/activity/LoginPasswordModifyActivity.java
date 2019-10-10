package com.daoyu.chat.module.system.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.CommonTextWatcher;
import com.daoyu.chat.utils.ToolsUtil;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 登录密码修改
 */
public class LoginPasswordModifyActivity extends BaseTitleActivity {

    @BindView(R.id.edit_old_pwd)
    EditText editOldPwd;

    @BindView(R.id.edit_new_password)
    EditText editNewPassword;

    @BindView(R.id.edit_confirm_new_password)
    EditText editConfirmNewPassword;

    @BindView(R.id.tv_complete)
    TextView tvComplete;

    private boolean oldPwd = false;
    private boolean newPwd = false;
    private boolean confrimNewPwd = false;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_login_password_modify;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("修改登录密码");
        tvComplete.setOnClickListener(this);

        editOldPwd.addTextChangedListener(new CommonTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String oldPwds = s.toString();
                if (TextUtils.isEmpty(oldPwds)) {
                    oldPwd = false;
                    tvComplete.setEnabled(false);
                    return;
                } else {
                    oldPwd = true;
                    if (oldPwd && newPwd && confrimNewPwd) {
                        tvComplete.setEnabled(true);
                    }
                }
            }
        });
        editNewPassword.addTextChangedListener(new CommonTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String newPwds = s.toString();
                if (TextUtils.isEmpty(newPwds)) {
                    newPwd = false;
                    tvComplete.setEnabled(false);
                    return;
                } else {
                    newPwd = true;
                    if (oldPwd && newPwd && confrimNewPwd) {
                        tvComplete.setEnabled(true);
                    }
                }
            }
        });
        editConfirmNewPassword.addTextChangedListener(new CommonTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String confirmNewPwds = s.toString();
                if (TextUtils.isEmpty(confirmNewPwds)) {
                    confrimNewPwd = false;
                    tvComplete.setEnabled(false);
                    return;
                } else {
                    confrimNewPwd = true;
                    if (oldPwd && newPwd && confrimNewPwd) {
                        tvComplete.setEnabled(true);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_complete:
                String oldPwd = editOldPwd.getText().toString();
                if (TextUtils.isEmpty(oldPwd)) {
                    toast.toastShow("请输入旧密码");
                    return;
                }
                String newPwd = editNewPassword.getText().toString();
                if (TextUtils.isEmpty(newPwd)) {
                    toast.toastShow("请输入新密码");
                    return;
                }
                String confirmPwd = editConfirmNewPassword.getText().toString();
                if (TextUtils.isEmpty(confirmPwd)) {
                    toast.toastShow("请再次输入新密码");
                    return;
                }
                if (!newPwd.equals(confirmPwd)) {
                    toast.toastShow("两次密码输入不一致");
                    return;
                }
                requestModifyPwd(oldPwd, newPwd);
                break;
        }
    }

    private void requestModifyPwd(String oldPwd, String newPwd) {
        showLoading("修改中...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("mobile", BaseApplication.getInstance().getUserInfoData().userPhone);
        params.put("oldPassword", ToolsUtil.md5(oldPwd));
        params.put("password", ToolsUtil.md5(newPwd));
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_MODIFY_LOGIN_PWD, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    finish();
                    toast.toastShow(body.msg);
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
}
