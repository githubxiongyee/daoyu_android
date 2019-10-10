package com.daoyu.chat.module.system.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.module.login.bean.UserBean;

import butterknife.BindView;

public class AccountSecurityActivity extends BaseTitleActivity {


    @BindView(R.id.tv_setting_login_password)
    TextView tvSettingLoginPassword;
    @BindView(R.id.tv_setting_pay_password)
    TextView tvSettingPayPassword;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_account_security;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("账户安全");
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        String userPhone = userInfoData.userPhone;
       // tvPhoneNumber.setText(userPhone);
        tvSettingLoginPassword.setOnClickListener(this);
        tvSettingPayPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_setting_login_password://修改登录密码
                startActivity(new Intent(this, LoginPasswordModifyActivity.class));
                finish();
                break;
            case R.id.tv_setting_pay_password://修改支付密码
                startActivity(new Intent(this, SettingPaymentPwdActivity.class));
                finish();
                break;
        }
    }
}
