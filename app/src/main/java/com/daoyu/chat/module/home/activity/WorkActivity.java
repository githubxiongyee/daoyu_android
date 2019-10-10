package com.daoyu.chat.module.home.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.home.adapter.LifeAdapter;
import com.daoyu.chat.module.home.bean.ContactFriendBean;
import com.daoyu.chat.module.home.dialog.SingleChooseLabelDialog;
import com.daoyu.chat.module.login.bean.UserBean;
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

public class WorkActivity extends BaseTitleActivity implements LifeAdapter.OnItemClickListener,SingleChooseLabelDialog.IChooseLabelType, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.rv_life)
    RecyclerView rvLife;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    private UserBean.UserData userInfoData;
    private ArrayList<ContactFriendBean.ContactFriendData> contactClassLists;
    private ArrayList<ContactFriendBean.ContactFriendData> allContactClassLists;
    private LifeAdapter adapter;
    private ContactFriendBean.ContactFriendData currentContact;
    private SingleChooseLabelDialog singleChooseLabelDialog;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_life;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("工作类");
        showRightAdd();
        userInfoData = BaseApplication.getInstance().getUserInfoData();
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefresh.setOnRefreshListener(this);
        rvLife.setLayoutManager(new LinearLayoutManager(this));
        contactClassLists = new ArrayList<>();
        allContactClassLists = new ArrayList<>();
        adapter = new LifeAdapter(this, contactClassLists);
        rvLife.setAdapter(adapter);
        adapter.setListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        swipeRefresh.setRefreshing(true);
        requestLifeClass();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.iv_right:
                Intent intent = new Intent(this, BatchesMoveActivity.class);
                intent.putExtra(Constant.CONTACT_LABEL, "work");
                startActivity(intent);
                break;
        }
    }

    private void requestLifeClass() {
        Map<String, Object> params = new HashMap<>();
        params.put("ftype", "J");
        params.put("user_id", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_CLASS_CONTACT, params, this, new JsonCallback<ContactFriendBean>(ContactFriendBean.class) {
            @Override
            public void onSuccess(Response<ContactFriendBean> response) {
                if (isActivityFinish) return;
                swipeRefresh.setRefreshing(false);
                if (response == null || response.body() == null) return;
                ContactFriendBean body = response.body();
                if (body.success) {
                    ArrayList<ContactFriendBean.ContactFriendData> data = body.data;
                    contactClassLists.clear();
                    if (data != null && data.size() > 0) {
                        for (int i = 0; i < data.size(); i++) {
                            ContactFriendBean.ContactFriendData contactFriendData = data.get(i);
                            String fremarks = contactFriendData.fremarks;
                            String friendNickName = TextUtils.isEmpty(fremarks) ? contactFriendData.friendNickName : fremarks;
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
                        if (data != null && data.size() > 0) {
                            Collections.sort(data);
                            contactClassLists.addAll(data);
                            allContactClassLists.addAll(data);
                        }
                        adapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onError(Response<ContactFriendBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                swipeRefresh.setRefreshing(false);
            }
        });
    }


    @Override
    public void onItemViewClick(ContactFriendBean.ContactFriendData contactClassData, int position) {
        if (contactClassData == null) return;
        Intent intent = new Intent(this, ContactDetailsActivity.class);
        intent.putExtra(Constant.CONTACT_ID, String.valueOf(contactClassData.userFriendId));
        startActivity(intent);
    }

    @Override
    public void onMoveViewClick(ContactFriendBean.ContactFriendData contactClassData, int position) {
        currentContact = contactClassData;
        showChangeClassDialog("work");
    }

    @Override
    public void onEditViewAfterTextChanged(Editable s) {
        String input = s.toString().trim();
        contactClassLists.clear();
        if (TextUtils.isEmpty(input)) {
            contactClassLists.addAll(allContactClassLists);
        } else {
            for (int i = 0; i < allContactClassLists.size(); i++) {
                ContactFriendBean.ContactFriendData contactFriendData = allContactClassLists.get(i);
                String friendNickName = contactFriendData.friendNickName;
                if (!TextUtils.isEmpty(friendNickName)) {
                    if (friendNickName.contains(input)) {
                        if (!contactClassLists.contains(contactFriendData)) {
                            contactClassLists.add(contactFriendData);
                        }
                    }
                }
                String fremarks = contactFriendData.fremarks;
                if (!TextUtils.isEmpty(fremarks)) {
                    if (fremarks.contains(input)) {
                        if (!contactClassLists.contains(contactFriendData)) {
                            contactClassLists.add(contactFriendData);
                        }
                    }
                }

            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showChangeClassDialog(String type) {
        if (singleChooseLabelDialog != null) {
            singleChooseLabelDialog.dismissAllowingStateLoss();
        }
        singleChooseLabelDialog = SingleChooseLabelDialog.getInstance(type);
        if (!singleChooseLabelDialog.isAdded()) {
            singleChooseLabelDialog.show(getSupportFragmentManager(), "singleChooseLabelDialog");
        } else {
            singleChooseLabelDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onChooseLabel(String label) {
        if (currentContact == null) return;
        if (contactClassLists == null || contactClassLists.size() <= 0) return;
        requestUpdateLabel("S", currentContact);
    }

    private void requestUpdateLabel(String type, ContactFriendBean.ContactFriendData currentContact) {
        showLoading("修改中...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("user_friend_id", currentContact.userFriendId);
        params.put("user_id", userInfoData.uid);
        params.put("ftype", type);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_SET_FRIEND_CATEGORY, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    contactClassLists.remove(currentContact);
                    adapter.notifyDataSetChanged();
                    toast.toastShow(body.msg);
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
    public void onRefresh() {
        requestLifeClass();
    }
}
