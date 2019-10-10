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
 * 群公告设置
 */
public class ModifyGroupAnnouncementActivity extends BaseActivity {

    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    @BindView(R.id.edit_signature)
    EditText editSignature;
    private GroupInfoBean.GroupInfoData groupInfoData;
    private boolean isAdmin;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_modify_group_announcement;
    }

    @Override
    protected void initEvent() {

        Intent intent = getIntent();
        groupInfoData = intent.getParcelableExtra(Constant.CONTACT_GROUP_INFO);
        isAdmin = intent.getBooleanExtra(Constant.GROUP_SET, false);
        if (groupInfoData == null) {
            finish();
            return;
        }

        tvCancel.setOnClickListener(this);
        tvComplete.setOnClickListener(this);
        editSignature.addTextChangedListener(new CommonTextWatcher() {
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

        String groupnotice = groupInfoData.groupnotice;
        if (TextUtils.isEmpty(groupnotice)) {
            groupnotice = "未设置";

        }
        if (".".equals(groupnotice)) {
            groupnotice = "未设置";
        }
        editSignature.setText(groupnotice);
        editSignature.setSelection(groupnotice.length());
        if (isAdmin) {
            tvTitle.setText("设置群公告");
            editSignature.setEnabled(true);
            tvComplete.setText("确定");
            tvComplete.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setText("群公告");
            editSignature.setEnabled(false);
            tvComplete.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_complete:
                String signature = editSignature.getText().toString();
                if (TextUtils.isEmpty(signature)) {
                    toast.toastShow("请输入群公告");
                    return;
                }
                requestSettingGroupNotic(signature);
                break;
        }
    }


    private void requestSettingGroupNotic(String signature) {
        showLoading("保存中...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupInfoData.group_id);
        params.put("notice", signature);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_GROUP_SET_NOTICE, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.code == 1) {
                    toast.toastShow(body.msg);
                    Intent data = new Intent();
                    data.putExtra(Constant.GROUP_NIOTICE, signature);
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
