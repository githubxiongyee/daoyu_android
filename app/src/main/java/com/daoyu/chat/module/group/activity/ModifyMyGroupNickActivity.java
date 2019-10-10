package com.daoyu.chat.module.group.activity;

import android.content.Intent;
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
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.group.bean.GroupInfoBean;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 修改我的群昵称
 */
public class ModifyMyGroupNickActivity extends BaseActivity {

    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    @BindView(R.id.edit_nick)
    EditText editNick;
    private GroupInfoBean.GroupInfoData groupInfoData;
    private String groupNick;
    private UserBean.UserData userInfoData;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_nick_setting;
    }

    @Override
    protected void initEvent() {
        tvTitle.setText("我的群昵称");
        Intent intent = getIntent();
        groupInfoData = intent.getParcelableExtra(Constant.CONTACT_GROUP_INFO);
        groupNick = intent.getStringExtra(Constant.GROUP_MY_NICK);
        if (groupInfoData == null || TextUtils.isEmpty(groupNick)) {
            finish();
            return;
        }
        userInfoData = BaseApplication.getInstance().getUserInfoData();
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
        editNick.setText(groupNick);
        editNick.setSelection(groupNick.length());
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
                    toast.toastShow("请输入我的群昵称");
                    return;
                }
                requestSaveGroupNick(nick);
                break;
        }
    }

    /**
     * 请求修改昵称接口
     *
     * @param nick
     */
    private void requestSaveGroupNick(String nick) {
        showLoading("保存中...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("nickname", nick);
        params.put("group_id", groupInfoData.group_id);
        params.put("user_id", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_MODIFY_MY_GROUP_NICK, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    toast.toastShow(body.msg);
                    Intent data = new Intent();
                    data.putExtra(Constant.GROUP_MY_NICK, nick);
                    setResult(RESULT_OK, data);
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
        },UrlConfig.BASE_GROUP_URL);
    }
}
