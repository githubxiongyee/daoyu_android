package com.daoyu.chat.module.group.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseActivity;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.common.CommonTextWatcher;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.group.bean.GroupInfoBean;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 修改群名称
 */
public class ModifyGroupNameActivity extends BaseActivity {

    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    @BindView(R.id.edit_nick)
    EditText editNick;
    private GroupInfoBean.GroupInfoData groupInfoData;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_nick_setting;
    }

    @Override
    protected void initEvent() {
        tvTitle.setText("修改群昵称");
        Intent intent = getIntent();
        groupInfoData = intent.getParcelableExtra(Constant.CONTACT_GROUP_INFO);
        if (groupInfoData == null) {
            finish();
            return;
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
        editNick.setText(groupInfoData.groupname);
        editNick.setSelection(groupInfoData.groupname.length());
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
                    toast.toastShow("请输入群名称");
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
        params.put("group_name", nick);
        params.put("group_id", groupInfoData.group_id);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_MODIFY_GROUP_NAME, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    toast.toastShow(body.msg);
                    Intent data = new Intent();
                    data.putExtra(Constant.GROUP_NAME, nick);
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
