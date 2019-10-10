package com.daoyu.chat.module.group.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.group.adapter.LordTransferAdapter;
import com.daoyu.chat.module.group.bean.GroupInfoBean;
import com.daoyu.chat.module.group.bean.UsersHeaderBean;
import com.daoyu.chat.utils.ToolsUtil;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 群主转让
 */
public class LordTransferActivity extends BaseActivity implements LordTransferAdapter.OnItemClickListener {
    @BindView(R.id.edit_search)
    EditText editSearch;
    @BindView(R.id.rv_contact)
    RecyclerView rvContact;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    private ArrayList<UsersHeaderBean.UsersHeaderData> userHeaders;
    private GroupInfoBean.GroupInfoData groupInfoData;
    private LordTransferAdapter lordTransferAdapter;
    private UsersHeaderBean.UsersHeaderData usersHeaderCurrent = null;
    private String adminid;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_lord_transfer_group;
    }

    @Override
    protected void initEvent() {
        tvTitle.setText("群主转让");
        Intent intent = getIntent();
        userHeaders = intent.getParcelableArrayListExtra(Constant.GROUP_MEMBER_ALL);
        groupInfoData = intent.getParcelableExtra(Constant.CONTACT_GROUP_INFO);
        adminid = groupInfoData.adminid;
        rvContact.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        if (userHeaders == null || userHeaders.size() <= 0 || groupInfoData == null) {
            finish();
            return;
        }

        tvCancel.setOnClickListener(this);
        tvComplete.setOnClickListener(this);
        tvComplete.setEnabled(false);
        tvComplete.setText("确定");

        if (!TextUtils.isEmpty(adminid)) {
            int position = -1;
            for (int i = 0; i < userHeaders.size(); i++) {
                UsersHeaderBean.UsersHeaderData usersHeaderData = userHeaders.get(i);
                if (usersHeaderData.userId.equals(adminid)) {
                    position = i;
                    break;
                }
            }
            userHeaders.remove(position);
        }

        for (int i = 0; i < userHeaders.size(); i++) {
            UsersHeaderBean.UsersHeaderData usersHeaderData = userHeaders.get(i);
            String nickName = usersHeaderData.nickName;
            if (!TextUtils.isEmpty(nickName)) {
                char firstChar = nickName.charAt(0);
                if (Character.isLowerCase(firstChar) || Character.isUpperCase(firstChar)) {
                    usersHeaderData.firstLetter = String.valueOf(firstChar).toUpperCase();
                } else if (Character.isDigit(firstChar)) {
                    usersHeaderData.firstLetter = "#";
                } else if (ToolsUtil.isChinese(firstChar)) {
                    String[] chinesePinyin = PinyinHelper.toHanyuPinyinStringArray(firstChar);
                    String s = chinesePinyin[0];
                    String letter = String.valueOf(s.charAt(0)).toUpperCase();
                    usersHeaderData.firstLetter = letter;
                } else {
                    usersHeaderData.firstLetter = "#";
                }
            } else {
                usersHeaderData.firstLetter = "#";
            }
        }
        if (userHeaders == null || userHeaders.size() <= 0) return;
        Collections.sort(userHeaders);

        lordTransferAdapter = new LordTransferAdapter(this, userHeaders);
        lordTransferAdapter.setListener(this);
        rvContact.setAdapter(lordTransferAdapter);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_complete:
                if (usersHeaderCurrent == null) return;
                requestLordTransferGroupChat(usersHeaderCurrent.userId);
                break;
        }
    }


    private void requestLordTransferGroupChat(String uid) {
        showLoading("创建中...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", uid);
        params.put("group_id", groupInfoData.group_id);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_LORD_TRANSFER, params, this, new JsonCallback<GroupInfoBean>(GroupInfoBean.class) {
            @Override
            public void onSuccess(Response<GroupInfoBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                GroupInfoBean body = response.body();
                if (body.code == 1) {
                    toast.toastShow(body.msg);
                    finish();
                } else {
                    toast.toastShow(body.msg);
                }
            }

            @Override
            public void onError(Response<GroupInfoBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        },UrlConfig.BASE_GROUP_URL);
    }

    @Override
    public void onItemViewClick(UsersHeaderBean.UsersHeaderData usersHeaderData, int position) {
        if (usersHeaderData == null) return;
        usersHeaderCurrent = usersHeaderData;
        tvComplete.setEnabled(true);
    }
}
