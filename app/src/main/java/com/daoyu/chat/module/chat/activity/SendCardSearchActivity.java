package com.daoyu.chat.module.chat.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseActivity;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.common.CommonTextWatcher;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.chat.adapter.SearchListAdapter;
import com.daoyu.chat.module.chat.dialog.SendCardDialog;
import com.daoyu.chat.module.home.bean.ContactFriendBean;
import com.daoyu.chat.module.home.bean.ScanAddFriendB;
import com.daoyu.chat.module.im.module.ChatTable;
import com.daoyu.chat.module.im.module.IMConstant;
import com.daoyu.chat.module.im.module.MessageDetailTable;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.dy.dyim.android.core.LocalUDPDataSender;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class SendCardSearchActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, SearchListAdapter.OnSearchContactClickListener,SendCardDialog.OnSendCardToFriendListener {

    @BindView(R.id.edit_search)
    EditText editSearch;
    @BindView(R.id.rv_contact)
    RecyclerView rvContact;
    @BindView(R.id.cb_dev)
    CheckBox cbDev;
    @BindView(R.id.rv_group)
    RecyclerView rvGroup;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    private ArrayList<ContactFriendBean.ContactFriendData> contactFriendLists;
    private ArrayList<ContactFriendBean.ContactFriendData> contactFriendAll;
    private ArrayList<ContactFriendBean.ContactFriendData> contactFriendFour;

    private ArrayList<ContactFriendBean.ContactFriendData> searchContactFriendAll;
    private ArrayList<ContactFriendBean.ContactFriendData> searchContactFriendFour;
    private SearchListAdapter adapterContact;
    private ScanAddFriendB.ScanAddFrienData dataBean;
    private SendCardDialog sendCardDialog;
    private boolean notSearch = true;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_send_card_search;
    }

    @Override
    protected void initEvent() {
        Intent intent = getIntent();
        dataBean = intent.getParcelableExtra(Constant.CONTACT_FRIEND_INFO);
        LinearLayoutManager layoutContact = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rvContact.setLayoutManager(layoutContact);
        contactFriendLists = new ArrayList<>();
        contactFriendAll = new ArrayList<>();
        contactFriendFour = new ArrayList<>();
        searchContactFriendAll = new ArrayList<>();
        searchContactFriendFour = new ArrayList<>();

        adapterContact = new SearchListAdapter(this, contactFriendLists);
        adapterContact.setListener(this);
        rvContact.setAdapter(adapterContact);
        if (contactFriendLists == null || contactFriendLists.size() <= 4) {
            cbDev.setVisibility(View.GONE);
        } else {
            cbDev.setVisibility(View.VISIBLE);
        }
        editSearch.addTextChangedListener(new CommonTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String trim = s.toString().trim();
                contactFriendLists.clear();
                if (TextUtils.isEmpty(trim)) {
                    notSearch = true;
                    contactFriendLists.addAll(contactFriendFour);
                } else {
                    notSearch = false;
                    for (int i = 0; i < contactFriendAll.size(); i++) {
                        ContactFriendBean.ContactFriendData contactFriendData = contactFriendAll.get(i);
                        String friendNickName = contactFriendData.friendNickName;
                        if (friendNickName.contains(trim)) {
                            if (!searchContactFriendAll.contains(contactFriendData)) {
                                searchContactFriendAll.add(contactFriendData);
                            }
                        }
                        String fremarks = contactFriendData.fremarks;
                        if (!TextUtils.isEmpty(fremarks)) {
                            if (fremarks.contains(trim)) {
                                if (!searchContactFriendAll.contains(contactFriendData)) {
                                    searchContactFriendAll.add(contactFriendData);
                                }
                            }
                        }
                    }
                    if (searchContactFriendAll != null && searchContactFriendAll.size() > 0) {
                        if (searchContactFriendAll.size() <= 4) {
                            cbDev.setVisibility(View.GONE);
                            searchContactFriendFour.addAll(searchContactFriendAll);
                        } else {
                            cbDev.setVisibility(View.VISIBLE);
                            for (int j = 0; j < 4; j++) {
                                searchContactFriendFour.add(searchContactFriendAll.get(j));
                            }
                        }
                        contactFriendLists.addAll(searchContactFriendFour);
                    }
                }
                adapterContact.notifyDataSetChanged();
            }
        });

        LinearLayoutManager layoutGroup = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rvGroup.setLayoutManager(layoutGroup);
        cbDev.setOnCheckedChangeListener(this);
        tvCancel.setOnClickListener(this);

        requestContactFriend();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
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
                hideLoading();
                if (response == null || response.body() == null) return;
                ContactFriendBean body = response.body();
                ArrayList<ContactFriendBean.ContactFriendData> contactFriendList = body.data;
                if (contactFriendList != null && contactFriendList.size() > 0) {
                    int size = contactFriendList.size();
                    int position = -1;
                    for (int i = 0; i < size; i++) {
                        ContactFriendBean.ContactFriendData contactFriendData = contactFriendList.get(i);
                        int userFriendId = contactFriendData.userFriendId;
                        if (dataBean.userFriendId == userFriendId) {
                            position = i;
                            break;
                        }
                    }
                    if (position != -1) {
                        contactFriendList.remove(position);
                    }
                    size = contactFriendList.size();
                    if (size <= 4) {
                        cbDev.setVisibility(View.GONE);
                        contactFriendFour.addAll(contactFriendList);
                    } else {
                        cbDev.setVisibility(View.VISIBLE);
                        contactFriendAll.addAll(contactFriendList);
                        for (int i = 0; i < 4; i++) {
                            contactFriendFour.add(contactFriendList.get(i));
                        }
                    }
                    contactFriendLists.clear();
                    contactFriendLists.addAll(contactFriendFour);
                    adapterContact.notifyDataSetChanged();
                }


            }

            @Override
            public void onError(Response<ContactFriendBean> response) {
                super.onError(response);
                hideLoading();
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!buttonView.isPressed()) return;
        switch (buttonView.getId()) {
            case R.id.cb_dev:
                contactFriendLists.clear();
                if (isChecked) {
                    cbDev.setText("收起");
                    if (notSearch) {
                        contactFriendLists.addAll(contactFriendAll);
                    } else {
                        contactFriendLists.addAll(searchContactFriendAll);
                    }
                } else {
                    cbDev.setText("更多联系人");
                    if (notSearch) {
                        contactFriendLists.addAll(contactFriendFour);
                    } else {
                        contactFriendLists.addAll(searchContactFriendFour);
                    }
                }
                adapterContact.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onSearchContactClickListener(ContactFriendBean.ContactFriendData contactFriendData, int position) {
        if (contactFriendData == null) return;
        showSendCardDialog(String.valueOf(contactFriendData.userFriendId), contactFriendData.headImg, contactFriendData.friendNickName);
    }


    private void showSendCardDialog(String userId, String header, String name) {
        if (sendCardDialog != null) {
            sendCardDialog.dismissAllowingStateLoss();
        }
        sendCardDialog = SendCardDialog.getInstance(userId, header, name);
        if (!sendCardDialog.isAdded()) {
            sendCardDialog.show(getSupportFragmentManager(), "sendCardDialog");
        } else {
            sendCardDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onSendCard(String userId, String userName, String header) {
        sendCard(userId, userName, header);
    }

    /**
     * 发送名片
     */
    private void sendCard(String userId, String userName, String header) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        String uid = String.valueOf(userInfoData.uid);
        String headImg = userInfoData.headImg;
        String time = String.valueOf(System.currentTimeMillis());

        Map<String, Object> params = new HashMap<>();
        params.put("userid", uid);
        params.put("userImage", headImg);
        params.put("time", time);
        params.put("content", dataBean.userFriendId);
        params.put("chattype", 1);
        params.put("username", userInfoData.nickName);
        params.put("type", IMConstant.MessageType.CARD);
        params.put("card", dataBean.friendNickName);
        params.put("cardImage", dataBean.headImg);

        MessageDetailTable sendMessage = new MessageDetailTable();
        String chatId = userInfoData.uid + "DL" + userId;
        sendMessage.chat_id = chatId;

        sendMessage.message_type = IMConstant.MessageType.CARD;
        sendMessage.message_state = IMConstant.MessageStatus.DELIVERING;
        sendMessage.avatar = headImg;
        sendMessage.user_id = uid;
        sendMessage.chat_type = 1;
        sendMessage.message_time = time;
        sendMessage.message = String.valueOf(dataBean.userFriendId);
        sendMessage.card_image = dataBean.headImg;
        sendMessage.card_name = dataBean.friendNickName;
        sendMessage.saveAsync().listen(success -> LitePal.where("chat_id = ?", userInfoData.uid + "DL" + userId).findAsync(MessageDetailTable.class).listen(list -> {

        }));

        List<ChatTable> chatTables = LitePal.where("chat_id = ?", sendMessage.chat_id).find(ChatTable.class);

        if (chatTables == null || chatTables.size() <= 0) {
            ChatTable chatTable = new ChatTable();
            chatTable.mobile = userInfoData.userPhone;
            chatTable.chat_id = sendMessage.chat_id;
            chatTable.number = 0;
            chatTable.is_read = true;
            chatTable.last_message = "[名片]";
            chatTable.avatar = header;
            chatTable.user_id = String.valueOf(userId);
            chatTable.username = userName;
            chatTable.top = false;
            chatTable.current_id = String.valueOf(userInfoData.uid);
            chatTable.message_type = IMConstant.MessageType.CARD;
            chatTable.message_time = time;
            chatTable.saveAsync().listen(success -> {
                if (success) {
                    Log.d("TAG", "保存成功!");
                }
            });
        } else {
            ChatTable chatTable = chatTables.get(0);
            chatTable.is_read = true;
            chatTable.last_message = "[名片]";
            chatTable.number = 0;
            chatTable.message_time = time;
            chatTable.username = userName;
            chatTable.avatar = header;
            chatTable.current_id = String.valueOf(userInfoData.uid);
            chatTable.saveAsync().listen(success -> Log.d("TAG", "更新成功!"));
        }
        doSendMessage(new Gson().toJson(params), userId);

    }

    @SuppressLint("StaticFieldLeak")
    private void doSendMessage(String msg, String friendId) {
        new LocalUDPDataSender.SendCommonDataAsync(this, msg, friendId) {
            @Override
            protected void onPostExecute(Integer code) {
                if (code == 0) {
                    finish();
                } else {
                    finish();
                }
            }
        }.execute();
    }
}
