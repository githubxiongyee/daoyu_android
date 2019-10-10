package com.daoyu.chat.module.mine.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseActivity;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.common.CommonTextWatcher;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

public class NickSettingActivity extends BaseActivity {
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    @BindView(R.id.edit_nick)
    EditText editNick;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_nick_setting;
    }

    @Override
    protected void initEvent() {
        tvTitle.setText("昵称");
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        String nickName = userInfoData.nickName;
        if (!TextUtils.isEmpty(nickName)) {
            editNick.setText(nickName);
        }
        tvCancel.setOnClickListener(this);
        tvComplete.setOnClickListener(this);
        editNick.addTextChangedListener(new CommonTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String nick = s.toString();
                if (TextUtils.isEmpty(nick)) {
                    tvComplete.setEnabled(false);
                } else {
                    tvComplete.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_complete:
                String nick = editNick.getText().toString();
                if (TextUtils.isEmpty(nick)) {
                    toast.toastShow("请输入昵称");
                    return;
                }
                requestSaveNick(nick);
                break;
        }
    }

    /**
     * 请求修改昵称接口
     *
     * @param nick
     */
    private void requestSaveNick(String nick) {
        showLoading("保存中...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("nickName", nick);
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
                    UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
                    userInfoData.nickName = nick;
                    BaseApplication.getInstance().saveUserInfo(userInfoData);
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
}
