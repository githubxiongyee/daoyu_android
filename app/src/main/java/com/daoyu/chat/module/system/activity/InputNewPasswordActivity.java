package com.daoyu.chat.module.system.activity;

import android.content.Intent;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.view.PayPsdInputView;

import butterknife.BindView;

/**
 * 输入新密码class
 */
public class InputNewPasswordActivity extends BaseTitleActivity {

    @BindView(R.id.psd_view)
    PayPsdInputView psdView;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_input_new_password;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("输入新密码");
        psdView.setComparePassword(new PayPsdInputView.onPasswordListener() {
            @Override
            public void onDifference(String oldPsd, String newPsd) {

            }

            @Override
            public void onEqual(String psd) {

            }

            @Override
            public void inputFinished(String inputPsd) {
                Intent intent = new Intent(InputNewPasswordActivity.this, ConfirmNewPasswordActivity.class);
                intent.putExtra("update_pay_pwd",inputPsd);
                startActivity(intent);
                finish();
            }
        });

    }

}
