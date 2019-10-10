package com.daoyu.chat.module.mine.activity;

import android.text.TextUtils;
import android.widget.RadioGroup;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

public class SexSettingActivity extends BaseTitleActivity implements RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.rg_sex)
    RadioGroup rgSex;
    private UserBean.UserData userInfoData;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_sex_setting;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("设置性别");
        userInfoData = BaseApplication.getInstance().getUserInfoData();
        String sex = userInfoData.sex;
        if (TextUtils.isEmpty(sex)) {
            sex = "not chose";
        }
        switch (sex) {
            case "男":
                rgSex.check(R.id.rb_man);
                break;
            case "女":
                rgSex.check(R.id.rb_woman);
                break;
            default:
                rgSex.clearCheck();
                break;
        }
        rgSex.setOnCheckedChangeListener(this);
    }

    /**
     * 请求设置性别接口
     *
     * @param sex
     */
    private void requestSettingSex(String sex) {
        showLoading("保存中...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("sex", "男".equals(sex) ? "M" : "F");
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        params.put("uid",userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_UPDATE_USER_INFO, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    userInfoData.sex =  "男".equals(sex) ? "M" : "F";
                    BaseApplication.getInstance().saveUserInfo(SexSettingActivity.this.userInfoData);
                    toast.toastShow(body.msg);
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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == -1) {
            toast.toastShow("请选择您的性别");
            return;
        }
        String sex = null;
        switch (checkedId) {
            case R.id.rb_man:
                sex = "男";
                break;
            case R.id.rb_woman:
                sex = "女";
                break;
        }
        if (TextUtils.isEmpty(sex)) return;
        requestSettingSex(sex);
    }
}
