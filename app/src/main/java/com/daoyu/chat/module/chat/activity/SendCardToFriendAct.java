package com.daoyu.chat.module.chat.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.chat.dialog.SendCardDialog;
import com.daoyu.chat.module.home.adapter.RecentlyContactAdapter;
import com.daoyu.chat.module.home.bean.ScanAddFriendB;
import com.daoyu.chat.module.im.module.ChatTable;
import com.daoyu.chat.module.im.module.IMConstant;
import com.daoyu.chat.module.im.module.MessageDetailTable;
import com.daoyu.chat.module.login.bean.UserBean;
import com.dy.dyim.android.core.LocalUDPDataSender;
import com.google.gson.Gson;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 发送给
 */
public class SendCardToFriendAct extends BaseTitleActivity implements BaseQuickAdapter.OnItemClickListener, SendCardDialog.OnSendCardToFriendListener {
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.rv_recently_friends)
    RecyclerView rvRecentlyFriends;
    RecentlyContactAdapter adapter;
    private ArrayList<ChatTable> chatTables;
    private ScanAddFriendB.ScanAddFrienData dataBean;
    private SendCardDialog sendCardDialog;

    @Override
    protected int getLayoutResId() {
        return R.layout.act_send_card_to_friend;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("发送给");
        Intent intent = getIntent();
        dataBean = intent.getParcelableExtra(Constant.CONTACT_FRIEND_INFO);
        rvRecentlyFriends.setLayoutManager(new LinearLayoutManager(this));
        chatTables = new ArrayList<>();
        adapter = new RecentlyContactAdapter(R.layout.item_search_contact, chatTables);
        View headerView = LayoutInflater.from(this).inflate(R.layout.item_send_to_recently_text, null);
        adapter.setHeaderView(headerView);
        rvRecentlyFriends.setAdapter(adapter);
        adapter.setListener(this);
        tvSearch.setOnClickListener(this);

        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        List<ChatTable> all = LitePal.where("current_id = ?", String.valueOf(userInfoData.uid)).find(ChatTable.class);
        chatTables.addAll(all);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_search:
                Intent intent = new Intent(this, SendCardSearchActivity.class);
                intent.putExtra(Constant.CONTACT_FRIEND_INFO, dataBean);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (chatTables == null || chatTables.size() <= 0) return;
        if (position == 0) return;
        ChatTable chatTable = chatTables.get(position - 1);
        showSendCardDialog(chatTable.user_id, chatTable.avatar, chatTable.username);


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
