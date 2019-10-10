package com.daoyu.chat.module.mine.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.module.login.activity.LoginActivity;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.bean.GetUserValueB;
import com.daoyu.chat.module.system.activity.AccountSecurityActivity;
import com.daoyu.chat.module.system.activity.PrivacySettingActivity;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

public class SystemActivity extends BaseTitleActivity {

    @BindView(R.id.switch_message)
    Switch switchMessage;
    @BindView(R.id.tv_account_security)
    TextView tvAccountSecurity;
    @BindView(R.id.tv_privacy_setting)
    TextView tvPrivacySetting;
    @BindView(R.id.tv_about_us)
    TextView tvAboutUs;
    @BindView(R.id.tv_language_settings)
    TextView tvLanguageSettings;
    @BindView(R.id.tv_logout)
    TextView tvLogout;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_system;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle($$(R.string.text_setting));
        tvAccountSecurity.setOnClickListener(this);
        tvPrivacySetting.setOnClickListener(this);
        tvAboutUs.setOnClickListener(this);
        tvLanguageSettings.setOnClickListener(this);
        tvLogout.setOnClickListener(this);

        initMsgNoticeListener();

        requestGetValue();
    }

    private void initMsgNoticeListener() {
        switchMessage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isChecked()) {
                requestSetValue(1);
            } else {
                requestSetValue(2);
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_account_security:
                startActivity(new Intent(this, AccountSecurityActivity.class));
                break;
            case R.id.tv_privacy_setting:
                startActivity(new Intent(this, PrivacySettingActivity.class));
                break;
            case R.id.tv_about_us:
                startActivity(new Intent(this, AboutUsAct.class));
                break;
            case R.id.tv_language_settings:
                startActivity(new Intent(this, LanguageSettingAct.class));
                break;
            case R.id.tv_logout://退出登录
                BaseApplication.getInstance().loginOut();
                BaseApplication.getInstance().clearActivityList();
                startActivity(new Intent(SystemActivity.this, LoginActivity.class));
                break;
        }
    }

    private void requestSetValue(int noticeValue) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        Map<String, Object> params = new HashMap<>();
        params.put("code", "messageReminding");
        params.put("module", "im");
        params.put("remark", "msg_notice_test");
        params.put("user_id", userInfoData.uid);
        params.put("value", noticeValue);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_SET_USER_VALUE, params, SystemActivity.this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.code == 1) {
                    //toast.toastShow(body.msg);
                } else {
                    toast.toastShow(body.msg);
                }
            }
        });
    }

    private void requestGetValue() {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        Map<String, Object> params = new HashMap<>();
        params.put("code", "messageReminding");
        params.put("user_id", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_GET_USER_VALUE, params, SystemActivity.this, new JsonCallback<GetUserValueB>(GetUserValueB.class) {
            @Override
            public void onSuccess(Response<GetUserValueB> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                GetUserValueB body = response.body();
                if (body.getCode() == 1) {
                    if (body.getData().size() == 0 || body.getData() == null)return;
                    GetUserValueB.DataBean dataBean = body.getData().get(0);
                    if (dataBean.getSettingValue().equals("1")){
                        switchMessage.setChecked(true);
                    }else {
                        switchMessage.setChecked(false);
                    }
                } else {
                    toast.toastShow(body.getMsg());
                }
            }
        });
    }

}
