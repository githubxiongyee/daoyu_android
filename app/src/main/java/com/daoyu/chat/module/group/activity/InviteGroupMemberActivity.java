package com.daoyu.chat.module.group.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.daoyu.chat.common.OnItemClickListener;
import com.daoyu.chat.module.group.adapter.CreateGroupChatAdapter;
import com.daoyu.chat.module.group.adapter.SelectorContactAdapter;
import com.daoyu.chat.module.group.bean.GroupInfoBean;
import com.daoyu.chat.module.group.bean.UsersHeaderBean;
import com.daoyu.chat.module.home.bean.ContactFriendBean;
import com.daoyu.chat.module.im.module.ChatTable;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.service.MqttService;
import com.daoyu.chat.utils.QiNiuUtil;
import com.daoyu.chat.utils.ToolsUtil;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.othershe.combinebitmap.CombineBitmap;
import com.othershe.combinebitmap.layout.WechatLayoutManager;
import com.othershe.combinebitmap.listener.OnProgressListener;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 邀请成为群成员
 */
public class InviteGroupMemberActivity extends BaseActivity implements CreateGroupChatAdapter.OnItemClickListener, OnItemClickListener {
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    @BindView(R.id.edit_search)
    EditText editSearch;
    @BindView(R.id.rv_contact)
    RecyclerView rvContact;
    @BindView(R.id.rv_checked)
    RecyclerView rvChecked;

    private ArrayList<ContactFriendBean.ContactFriendData> contactFriends;
    private ArrayList<ContactFriendBean.ContactFriendData> checkedFriends;
    private CreateGroupChatAdapter adapter;
    private SelectorContactAdapter selectorContactAdapter;
    private UserBean.UserData userInfoData;
    private GroupInfoBean.GroupInfoData groupInfoData;
    private ArrayList<UsersHeaderBean.UsersHeaderData> userHeaders;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_start_group_chat;
    }

    @Override
    protected void initEvent() {
        tvTitle.setText("邀请好友");
        Intent intent = getIntent();
        userHeaders = intent.getParcelableArrayListExtra(Constant.GROUP_MEMBER_ALL);
        groupInfoData = intent.getParcelableExtra(Constant.CONTACT_GROUP_INFO);
        if (userHeaders == null || userHeaders.size() <= 0 || groupInfoData == null) {
            finish();
            return;
        }

        userInfoData = BaseApplication.getInstance().getUserInfoData();
        tvComplete.setEnabled(false);

        tvComplete.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        editSearch.addTextChangedListener(new CommonTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 5) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rvChecked.setLayoutManager(gridLayoutManager);
        checkedFriends = new ArrayList<>();
        selectorContactAdapter = new SelectorContactAdapter(this, checkedFriends);
        selectorContactAdapter.setListener(this);
        rvChecked.setAdapter(selectorContactAdapter);
        tvComplete.setText(String.format("完成(%d)", checkedFriends.size()));

        contactFriends = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvContact.setLayoutManager(manager);
        adapter = new CreateGroupChatAdapter(this, contactFriends);
        adapter.setListener(this);
        rvContact.setAdapter(adapter);
        requestContactFriend();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_complete:
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < checkedFriends.size(); i++) {
                    ContactFriendBean.ContactFriendData contactFriendData = checkedFriends.get(i);
                    sb.append(contactFriendData.userFriendId + ",");
                }
                String trim = sb.toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    toast.toastShow("请选择需要添加的好友");
                    return;
                }
                String[] createIds = new String[checkedFriends.size()];
                for (int i = 0; i < checkedFriends.size(); i++) {
                    createIds[i] = String.valueOf(checkedFriends.get(i).userFriendId);
                }
                String friendId = trim.substring(0, trim.length() - 1);
                requestAddGroupChat(friendId, createIds);
                break;
        }
    }

    private void requestContactFriend() {
        showLoading("加载中...", false);
        Map<String, Object> params = new HashMap<>();
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        params.put("auth_token", userInfoData.token);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_CONTACT_FRIEND_LIST, params, this, new JsonCallback<ContactFriendBean>(ContactFriendBean.class) {
            @Override
            public void onSuccess(Response<ContactFriendBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                ContactFriendBean body = response.body();
                ArrayList<ContactFriendBean.ContactFriendData> contactFriendList = body.data;
                contactFriends.clear();
                if (contactFriendList != null && contactFriendList.size() > 0) {
                    List<ContactFriendBean.ContactFriendData> positions = new ArrayList<>();
                    positions.clear();
                    for (int i = 0; i < userHeaders.size(); i++) {
                        UsersHeaderBean.UsersHeaderData usersHeaderData = userHeaders.get(i);
                        String userId = usersHeaderData.userId;
                        for (int j = 0; j < contactFriendList.size(); j++) {
                            ContactFriendBean.ContactFriendData contactFriendData = contactFriendList.get(j);
                            if (userId.equals(String.valueOf(contactFriendData.userFriendId))) {
                                positions.add(contactFriendData);
                            }
                        }
                    }
                    if (positions.size() > 0) {
                        for (int i = 0; i < positions.size(); i++) {
                            ContactFriendBean.ContactFriendData contactFriendData = positions.get(i);
                            contactFriendList.remove(contactFriendData);
                        }
                    }

                    for (int i = 0; i < contactFriendList.size(); i++) {
                        ContactFriendBean.ContactFriendData contactFriendData = contactFriendList.get(i);
                        String remarks = contactFriendData.fremarks;
                        String friendNickName = TextUtils.isEmpty(remarks) ? contactFriendData.friendNickName : remarks;
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
                    if (contactFriendList == null || contactFriendList.size() <= 0) return;
                    Collections.sort(contactFriendList);
                    contactFriends.addAll(contactFriendList);
                    adapter.notifyDataSetChanged();

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
    public void onItemViewClick(ContactFriendBean.ContactFriendData contactClassData, int position) {
        if (contactClassData == null) return;
        boolean checked = contactClassData.checked;
        contactClassData.checked = !checked;
        adapter.notifyDataSetChanged();
        if (contactClassData.checked) {
            if (!checkedFriends.contains(contactClassData)) {
                checkedFriends.add(contactClassData);
            }
        } else {
            if (checkedFriends.contains(contactClassData)) {
                checkedFriends.remove(contactClassData);
            }
        }
        selectorContactAdapter.notifyDataSetChanged();
        int size = checkedFriends.size();
        if (size < 1) {
            tvComplete.setEnabled(false);
            tvComplete.setText(String.format("完成(%d)", size));
        } else {
            tvComplete.setEnabled(true);
            tvComplete.setText(String.format("完成(%d)", size));
        }

    }

    @Override
    public void onItemClick(Object data, int position) {
        if (data == null) return;
        if (data instanceof ContactFriendBean.ContactFriendData) {
            if (checkedFriends == null || checkedFriends.size() <= 0) return;
            checkedFriends.remove(position);
            selectorContactAdapter.notifyDataSetChanged();

            ContactFriendBean.ContactFriendData contactFriendData = (ContactFriendBean.ContactFriendData) data;
            contactFriendData.checked = false;
            adapter.notifyDataSetChanged();
            int size = checkedFriends.size();
            if (size < 1) {
                tvComplete.setEnabled(false);
                tvComplete.setText(String.format("完成(%d)", size));
            } else {
                tvComplete.setEnabled(true);
                tvComplete.setText(String.format("完成(%d)", size));
            }
        }
    }

    private void requestAddGroupChat(String friendID, String[] createIds) {
        showLoading("请稍后...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("friends", friendID);
        params.put("group_id", groupInfoData.group_id);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_ADD_CHAT_FROUP, params, this, new JsonCallback<GroupInfoBean>(GroupInfoBean.class) {
            @Override
            public void onSuccess(Response<GroupInfoBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                GroupInfoBean body = response.body();
                if (body.code == 1) {
                    toast.toastShow(body.msg);
                    groupInfoData = body.data;
                    if (groupInfoData == null) return;
                    Map<String, Object> params = new HashMap<>();
                    params.put("users", createIds);
                    params.put("group", groupInfoData.group_id);
                    MqttService.publish(new Gson().toJson(params), "groupinvite");
                    //上传图片bitmap
                    ArrayList<String> users = groupInfoData.users;
                    if (users == null || users.size() <= 0) return;
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < users.size(); i++) {
                        stringBuffer.append(users.get(i) + ",");
                    }
                    String trim = stringBuffer.toString().trim();
                    if (!TextUtils.isEmpty(trim)) {
                        String ids = trim.substring(0, trim.length() - 1);
                        requestGetUsersHeader(ids);
                    }
                } else {
                    finish();
                    toast.toastShow(body.msg);
                }
            }

            @Override
            public void onError(Response<GroupInfoBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
                finish();
            }
        }, UrlConfig.BASE_GROUP_URL);
    }


    private void requestGetUsersHeader(String ids) {
        Map<String, Object> params = new HashMap<>();
        params.put("auth_token", userInfoData.token);
        params.put("user_ids", ids);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_GET_USERS_HEADER, params, this, new JsonCallback<UsersHeaderBean>(UsersHeaderBean.class) {
            @Override
            public void onSuccess(Response<UsersHeaderBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                UsersHeaderBean body = response.body();
                if (body.code == 1) {
                    ArrayList<UsersHeaderBean.UsersHeaderData> data = body.data;
                    if (data == null || data.size() <= 0) return;
                    List<String> imageList = new ArrayList<>();
                    imageList.clear();
                    for (int i = 0; i < data.size(); i++) {
                        UsersHeaderBean.UsersHeaderData usersHeaderData = data.get(i);
                        String headImg = usersHeaderData.headImg;
                        if (TextUtils.isEmpty(headImg)) continue;
                        int size = imageList.size();
                        if (size < 9) {
                            imageList.add(headImg);
                        } else {
                            break;
                        }

                    }
                    String[] images = new String[imageList.size()];
                    for (int i = 0; i < imageList.size(); i++) {
                        images[i] = imageList.get(i);
                    }
                    getBitMapHeader(images);

                } else {
                    toast.toastShow(body.msg);
                    hideLoading();
                }

            }

            @Override
            public void onError(Response<UsersHeaderBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        });
    }

    private void getBitMapHeader(String[] images) {
        CombineBitmap.init(this)
                .setLayoutManager(new WechatLayoutManager())
                .setSize(180)
                .setGap(2)
                .setGapColor(Color.parseColor("#FFFFFF"))
                .setUrls(images)
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(Bitmap bitmap) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] bytes = baos.toByteArray();
                        QiNiuUtil.requestByteData(bytes, String.valueOf(System.currentTimeMillis()), (url, status) -> {
                            if (status) {
                                if (groupInfoData == null) return;
                                requestBindGroupHeader(url);
                            }
                        }, ".png");
                    }
                }).build();
    }

    private void requestBindGroupHeader(String url) {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupInfoData.group_id);
        params.put("group_url", url);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_BIND_GROUP_USER, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.code == 1) {
                    ChatTable chatTable = new ChatTable();
                    chatTable.avatar = url;
                    chatTable.message_time = String.valueOf(System.currentTimeMillis());
                    chatTable.updateAllAsync("chat_id = ?", userInfoData.uid + "GL" + groupInfoData.group_id).listen(rowsAffected -> {
                        Log.d("TAG", "受影响的记录数" + rowsAffected);
                        finish();
                    });
                } else {
                    toast.toastShow(body.msg);
                    hideLoading();
                }
            }

            @Override
            public void onError(Response<BaseBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        }, UrlConfig.BASE_GROUP_URL);
    }


}
