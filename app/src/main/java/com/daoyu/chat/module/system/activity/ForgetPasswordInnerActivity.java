package com.daoyu.chat.module.system.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.daoyu.chat.view.PayPsdInputView;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 忘记密码内部
 */
public class ForgetPasswordInnerActivity extends BaseTitleActivity {
    @BindView(R.id.tv_mobile_phone)
    TextView tvTel;
    @BindView(R.id.tv_text1)
    TextView tvAgain;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.psd_view)
    PayPsdInputView msgInput;

    private CountDownTimer countDownTimer;

    String netVerification = "";

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_forget_password_inner;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("忘记密码");
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        tvTel.setText(getTelTxt(getTelTxt(userInfoData.userPhone)));

        countDownTimerStart();
        requestSendVerificationCode(userInfoData.userPhone);
        msgInput.setComparePassword(new PayPsdInputView.onPasswordListener() {
            @Override
            public void onDifference(String oldPsd, String newPsd) {

            }

            @Override
            public void onEqual(String psd) {

            }

            @Override
            public void inputFinished(String inputPsd) {
                /*if (checkVerification(inputPsd)){
                    startActivity(new Intent(ForgetPasswordInnerActivity.this,InputNewPasswordActivity.class));
                }*/
                checkVerification(inputPsd);
            }
        });
        tvAgain.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
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

    private String getTelTxt(String tel) {
        String telTxt = tel.substring(0, 3);
        telTxt = telTxt + "****";
        telTxt = telTxt + tel.substring(7, 11);
        return telTxt;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_text1:
                //重新发送验证码
                UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
                requestSendVerificationCode(userInfoData.userPhone);
                countDownTimerStart();
                break;
            case R.id.tv_confirm:
                startActivity(new Intent(ForgetPasswordInnerActivity.this,InputNewPasswordActivity.class));
                finish();
                break;
        }
    }
    /**
     * 短信验证码定时器
     */
    private void countDownTimerStart() {
        tvAgain.setEnabled(false);
        if (countDownTimer != null) {
            countDownTimer.onFinish();
            countDownTimer = null;
        }
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (tvAgain == null) return;
                String format = String.format($$(R.string.forget_password_countdown_seconds2), String.valueOf(millisUntilFinished / 1000));
                tvAgain.setText(format);
            }

            public void onFinish() {
                if (tvAgain == null) return;
                tvAgain.setText($$(R.string.text_reset_get_new_code));
                tvAgain.setEnabled(true);
            }
        }.start();
    }

    private boolean checkVerification(String str) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        Map<String, Object> params = new HashMap<>();
        params.put("mobile", userInfoData.userPhone);
        params.put("code", str);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_CHECK_PAY_PWD, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    tvConfirm.setEnabled(true);
                    userInfoData.paySign="";//只要忘记，确认完验证码，后台就会把那条密码从数据库移除
                    //toast.toastShow(body.msg);
                } else {
                    toast.toastShow(body.msg);
                    tvConfirm.setEnabled(false);

                }
            }

            @Override
            public void onError(Response<BaseBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        });
        return false;
    }

}
