package com.daoyu.chat.module.chat.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseActivity;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.common.CommonTextWatcher;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.event.UpdateFRemarkEvent;
import com.daoyu.chat.module.chat.bean.LocalFriendData;
import com.daoyu.chat.module.im.module.ChatTable;
import com.daoyu.chat.module.im.module.IMConstant;
import com.daoyu.chat.module.im.module.MessageDetailTable;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 设置备注
 */
public class SettingRemarkNameActivity extends BaseActivity {

    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    @BindView(R.id.edit_remark)
    EditText editRemark;
    private String contactRemarkName;
    private String friendId;
    private UserBean.UserData userInfoData;
    private String friendNickName;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_setting_remark_name;
    }

    @Override
    protected void initEvent() {
        Intent intent = getIntent();
        contactRemarkName = intent.getStringExtra(Constant.CONTACT_REMARKS_NAME);
        friendId = intent.getStringExtra(Constant.CONTACT_FRIEND_ID);
        friendNickName = intent.getStringExtra(Constant.CONTACT_NAME);

        userInfoData = BaseApplication.getInstance().getUserInfoData();
        tvCancel.setOnClickListener(this);
        tvComplete.setOnClickListener(this);
        tvTitle.setText("设置备注名");
        tvComplete.setText("确定");
        tvComplete.setEnabled(true);
        if (!TextUtils.isEmpty(contactRemarkName)) {
            editRemark.setText(contactRemarkName);
            editRemark.setSelection(contactRemarkName.length());
            tvComplete.setEnabled(true);
        }
        editRemark.addTextChangedListener(new CommonTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String remarkName = s.toString();
                if (TextUtils.isEmpty(remarkName)) {
                    tvComplete.setEnabled(true);
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
                requestDeleteFriend();
                break;
        }
    }

    /**
     * 设置备注
     */
    private void requestDeleteFriend() {
        showLoading("设置中...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", String.valueOf(userInfoData.uid));
        params.put("user_friend_id", friendId);
        String remarks = editRemark.getText().toString();
        params.put("remarks", remarks);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_SET_FRIEND_REMARKS, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    toast.toastShow(body.msg);
                    EventBus.getDefault().post(new UpdateFRemarkEvent(remarks, friendNickName));
                    boolean containsKey = IMConstant.normalFriendMap.containsKey(friendId);
                    if (containsKey) {
                        LocalFriendData localFriendData = IMConstant.normalFriendMap.get(friendId);
                        localFriendData.remarks = remarks;
                        IMConstant.normalFriendMap.put(friendId, localFriendData);
                    }
                    MessageDetailTable table = new MessageDetailTable();
                    if (remarks.isEmpty()) {
                        table.username = friendNickName;
                    } else {
                        table.username = remarks;
                    }

                    table.updateAllAsync("chat_id = ? and user_id = ?", userInfoData.uid + "DL" + friendId, friendId).listen(rowsAffected -> {
                        Log.d("TAG", "受影响的记录数为:" + rowsAffected + "条");
                        ChatTable chatTable = new ChatTable();
                        if (remarks.isEmpty()) {
                            chatTable.username = friendNickName;
                        } else {
                            chatTable.username = remarks;
                        }

                        chatTable.updateAllAsync("chat_id = ?", userInfoData.uid + "DL" + friendId).listen(rowsAffected1 -> {
                            Log.d("TAG", "受影响的记录数为:" + rowsAffected1 + "条");
                            finish();
                        });
                    });

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
