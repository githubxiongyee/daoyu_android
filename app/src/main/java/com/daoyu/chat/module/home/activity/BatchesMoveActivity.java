package com.daoyu.chat.module.home.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseActivity;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.common.OnItemClickListener;
import com.daoyu.chat.module.home.adapter.BatchesMoveAdapter;
import com.daoyu.chat.module.home.bean.ContactFriendBean;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.ToolsUtil;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;
import com.umeng.analytics.MobclickAgent;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class BatchesMoveActivity extends BaseActivity implements OnItemClickListener {

    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    @BindView(R.id.rv_contact)
    RecyclerView rvContact;
    private UserBean.UserData userInfoData;
    private ArrayList<ContactFriendBean.ContactFriendData> contactClassLists;
    private BatchesMoveAdapter adapter;
    private List<String> ids = new ArrayList<>();
    private String type;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_batches_move;
    }

    @Override
    protected void initEvent() {
        ids.clear();
        userInfoData = BaseApplication.getInstance().getUserInfoData();
        Intent intent = getIntent();
        String label = intent.getStringExtra(Constant.CONTACT_LABEL);
        switch (label) {
            case "life":
                type = "S";
                break;
            case "work":
                type = "J";
                break;
        }
        tvTitle.setText("选择联系人");
        tvComplete.setText("完成");
        tvComplete.setEnabled(false);
        tvCancel.setOnClickListener(this);
        tvComplete.setOnClickListener(this);
        rvContact.setLayoutManager(new LinearLayoutManager(this));
        contactClassLists = new ArrayList<>();
        adapter = new BatchesMoveAdapter(this, contactClassLists);
        adapter.setListener(this);
        rvContact.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_complete:
                showLoading("请稍后...", false);
                StringBuffer sb = new StringBuffer();
                for (String id : ids) {
                    sb.append(id + ",");
                }
                requestCommitBatchesToLable(type, sb.toString(), String.valueOf(userInfoData.uid));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        requestContactLists();
    }

    private void requestContactLists() {
        showLoading("加载中...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userInfoData.uid);
        params.put("ftype", type);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_SELECT_WHITE_LIST_BY_TYPE, params, this, new JsonCallback<ContactFriendBean>(ContactFriendBean.class) {
            @Override
            public void onSuccess(Response<ContactFriendBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                ContactFriendBean body = response.body();
                if (body.success) {
                    ArrayList<ContactFriendBean.ContactFriendData> data = body.data;
                    contactClassLists.clear();
                    if (data != null && data.size() > 0) {
                        for (int i = 0; i < data.size(); i++) {
                            ContactFriendBean.ContactFriendData contactFriendData = data.get(i);
                            String friendNickName = contactFriendData.friendNickName;
                            if (!TextUtils.isEmpty(friendNickName)) {
                                char firstChar = friendNickName.charAt(0);
                                if (Character.isLowerCase(firstChar) || Character.isUpperCase(firstChar)) {
                                    contactFriendData.firstLetter = String.valueOf(firstChar).toUpperCase();
                                } else if (Character.isDigit(firstChar)) {
                                    contactFriendData.firstLetter = "#";
                                } else if (ToolsUtil.isChinese(firstChar)) {
                                    String[] chinesePinyin = PinyinHelper.toHanyuPinyinStringArray(firstChar);
                                    String s = chinesePinyin[0];
                                    String letter = String.valueOf(s.charAt(0)).toUpperCase();
                                    contactFriendData.firstLetter = letter;
                                } else {
                                    contactFriendData.firstLetter = "#";
                                }
                            } else {
                                contactFriendData.firstLetter = "#";
                            }

                        }
                        Collections.sort(data);
                        contactClassLists.addAll(data);
                        adapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onError(Response<ContactFriendBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        });
    }

    @Override
    public void onItemClick(Object data, int position) {
        if (contactClassLists != null && contactClassLists.size() > 0) {
            boolean isChecked = false;
            for (ContactFriendBean.ContactFriendData contactFriendData : contactClassLists) {
                if (contactFriendData.checked) {
                    isChecked = true;
                    break;
                }
            }
            ids.clear();
            for (ContactFriendBean.ContactFriendData contactFriendData : contactClassLists) {
                if (contactFriendData.checked) {
                    ids.add(String.valueOf(contactFriendData.userFriendId));
                }
            }
            if (isChecked) {
                tvComplete.setEnabled(true);
                tvComplete.setText(String.format("完成(%d)", ids.size()));
            } else {
                tvComplete.setEnabled(false);
                ids.clear();
                tvComplete.setText("完成");
            }


        }
    }

    private void requestCommitBatchesToLable(String type, String ids, String userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("ftype", type);
        params.put("user_friends", ids);
        params.put("user_id", userId);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_BATCHES_ADD_TO_LABEL, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
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
