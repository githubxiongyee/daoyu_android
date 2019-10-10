package com.daoyu.chat.module.login.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.utils.ToolsUtil;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 忘记密码
 */
public class ForgetPasswordActivity extends BaseTitleActivity {

    @BindView(R.id.edit_phone)
    EditText editPhone;

    @BindView(R.id.edit_verification_code)
    EditText editVerificationCode;

    @BindView(R.id.edit_password)
    EditText editPassword;

    @BindView(R.id.edit_password_confirm)
    EditText editPasswordConfirm;

    @BindView(R.id.tv_complete)
    TextView tvComplete;

    @BindView(R.id.tv_get_verification_code)
    TextView tvGetVerificationCode;

    private CountDownTimer countDownTimer;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_forget_password;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle(R.string.text_forget_pwd);
        tvGetVerificationCode.setOnClickListener(this);
        tvComplete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_complete://完成
                String mobile = editPhone.getText().toString();
                if (TextUtils.isEmpty(mobile)) {
                    Toast.makeText(this, $$(R.string.text_please_input_phone_number), Toast.LENGTH_LONG).show();
                    return;
                }
                if (!mobile.startsWith("1")) {
                    Toast.makeText(this, $$(R.string.text_phone_number_error), Toast.LENGTH_LONG).show();
                    return;
                }
                if (mobile.length() != 11) {
                    Toast.makeText(this, $$(R.string.text_phone_number_error), Toast.LENGTH_LONG).show();
                    return;
                }
                String verificationCode = editVerificationCode.getText().toString();
                if (TextUtils.isEmpty(verificationCode)) {
                    Toast.makeText(this, $$(R.string.text_verification_code_not_null), Toast.LENGTH_LONG).show();
                    return;
                }
                String password = editPassword.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(this, $$(R.string.text_please_input_pwd), Toast.LENGTH_LONG).show();
                    return;
                }
                String confirmPwd = editPasswordConfirm.getText().toString();
                if (TextUtils.isEmpty(confirmPwd)) {
                    Toast.makeText(this, $$(R.string.text_please_input_confirm_pwd), Toast.LENGTH_LONG).show();
                    return;
                }
                if (!password.equals(confirmPwd)) {
                    Toast.makeText(this, $$(R.string.text_not_inconsistent_pwd), Toast.LENGTH_LONG).show();
                    return;
                }
                requestForgetPassword(mobile, verificationCode, password);
                break;
            case R.id.tv_get_verification_code://获取验证码
                String mobilePhone = editPhone.getText().toString();
                if (TextUtils.isEmpty(mobilePhone)) {
                    Toast.makeText(this, $$(R.string.text_please_input_phone_number), Toast.LENGTH_LONG).show();
                    return;
                }
                if (!mobilePhone.startsWith("1")) {
                    Toast.makeText(this, $$(R.string.text_phone_number_error), Toast.LENGTH_LONG).show();
                    return;
                }
                if (mobilePhone.length() != 11) {
                    Toast.makeText(this, $$(R.string.text_phone_number_error), Toast.LENGTH_LONG).show();
                    return;
                }
                requestSendVerificationCode(mobilePhone);
                countDownTimerStart();
                break;
        }
    }


    /**
     * 短信验证码定时器
     */
    private void countDownTimerStart() {
        tvGetVerificationCode.setEnabled(false);
        if (countDownTimer != null) {
            countDownTimer.onFinish();
            countDownTimer = null;
        }
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (tvGetVerificationCode == null) return;
                String format = String.format($$(R.string.forget_password_countdown_seconds), String.valueOf(millisUntilFinished / 1000));
                tvGetVerificationCode.setText(format);
            }

            public void onFinish() {
                if (tvGetVerificationCode == null) return;
                tvGetVerificationCode.setText($$(R.string.text_reset_get_code));
                tvGetVerificationCode.setEnabled(true);
            }
        }.start();
    }


    /**
     * 请求短信验证码
     *
     * @param mobile 手机号
     */
    private void requestSendVerificationCode(String mobile) {
        CHttpUtils.getInstance().requestGetFormServer(UrlConfig.URL_SEND_VERIFICATION_CODE + mobile, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    toast.toastShow(body.msg);
                } else {
                    toast.toastShow(body.msg);
                }
            }
        });
    }

    /**
     * 请求忘记密码完成接口
     *
     * @param mobile           手机号
     * @param verificationCode 短信验证码
     * @param password         密码
     */
    private void requestForgetPassword(String mobile, String verificationCode, String password) {
        showLoading($$(R.string.text_wait), false);
        Map<String, Object> params = new HashMap<>();
        params.put("mobile", mobile);
        params.put("password", ToolsUtil.md5(password));
        params.put("code", verificationCode);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_FORGET_PWD, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    toast.toastShow(body.msg);
                    Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                    intent.putExtra(Constant.PHONE_NUMBER, mobile);
                    startActivity(intent);
                    finish();
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
