package com.daoyu.chat.module.home.activity;

import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseActivity;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.login.activity.LoginActivity;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.NoLeakHandler;
import com.daoyu.chat.utils.SharedPreferenceUtil;

public class SplashActivity extends BaseActivity {

    private NoLeakHandler handler;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_splash;
    }

    @Override
    public void windowSetting() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //无title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏
    }

    @Override
    protected void initEvent() {
        BaseApplication.getInstance().initIMManager();
        handler = new NoLeakHandler(this);
        handler.sendEmptyMessageDelayed(1, 2000);
    }


    @Override
    public void handleMessage(Message message) {
        if (message == null) return;
        int what = message.what;
        if (what == 1) {
            UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
            if (userInfoData == null || TextUtils.isEmpty(userInfoData.token)) {
                if (SharedPreferenceUtil.getInstance().getBoolean(Constant.IS_FIRST_INSTALLED, true)) {
                    //第一次装
                    startActivity(new Intent(this, GuidePageAct.class));
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                finish();
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }
        }
    }
}
