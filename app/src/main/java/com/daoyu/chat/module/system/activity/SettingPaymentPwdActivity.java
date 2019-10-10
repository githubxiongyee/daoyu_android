package com.daoyu.chat.module.system.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.bean.ShippingAddressData;
import com.daoyu.chat.module.system.bean.AddAddressBean;
import com.daoyu.chat.utils.ToastUtil;
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
 * 设置支付密码
 */
public class SettingPaymentPwdActivity extends BaseTitleActivity implements PayPsdInputView.onPasswordListener {
    String payPwd = "";
    boolean isSet = false;

    @BindView(R.id.tv_forget_password)
    TextView tvForgetPassword;
    @BindView(R.id.tv_input_pwd_text)
    TextView tvInputPwdTxt;

    @BindView(R.id.psd_view)
    PayPsdInputView psdView;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_setting_payment_pwd;
    }

    @Override
    protected void initEvent() {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        payPwd = userInfoData.paySign;
        if (payPwd == null||payPwd.isEmpty()){
            setCurrentTitle("设置支付密码");
            isSet = false;
            tvForgetPassword.setVisibility(View.GONE);
            tvInputPwdTxt.setVisibility(View.GONE);
        }else {
            setCurrentTitle("修改支付密码");
            isSet = true;
            tvForgetPassword.setVisibility(View.VISIBLE);
            tvInputPwdTxt.setVisibility(View.VISIBLE);
        }

        tvForgetPassword.setOnClickListener(this);
        psdView.setComparePassword(this);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_forget_password:
                startActivity(new Intent(this, ForgetPasswordInnerActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onDifference(String oldPsd, String newPsd) {

    }

    @Override
    public void onEqual(String psd) {

    }

    @Override
    public void inputFinished(String inputPsd) {

        if (isSet){
            //已经有支付密码了，，这里是修改
            checkPwd(inputPsd);
            if (checkPwd(inputPsd)){
                startActivity(new Intent(this, InputNewPasswordActivity.class));
                finish();
            }else {
                new ToastUtil(SettingPaymentPwdActivity.this).toastShow("密码输入有误，请重试");
            }
        }else {
            //没有设置，，第一次设置的时候
            Intent intent = new Intent(this, ConfirmPayPasswordActivity.class);
            intent.putExtra(Constant.PAY_PWD,inputPsd);
            startActivity(intent);
            finish();
        }

    }

    private boolean checkPwd(String inputPsd) {
        if (ToolsUtil.md5(inputPsd).equals(payPwd)){
            return true;
        }else {
            return false;
        }
    }


}
