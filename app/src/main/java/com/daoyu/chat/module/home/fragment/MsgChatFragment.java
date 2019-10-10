package com.daoyu.chat.module.home.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseFragment;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.event.ApplyFriendEvent;
import com.daoyu.chat.event.ChatListUpdateEvent;
import com.daoyu.chat.event.MessageTotalEvent;
import com.daoyu.chat.event.MsgListClearEvent;
import com.daoyu.chat.event.SwitchContactEvent;
import com.daoyu.chat.module.chat.activity.ChatActivity;
import com.daoyu.chat.module.group.activity.GroupChatActivity;
import com.daoyu.chat.module.home.activity.SearchCommonActivity;
import com.daoyu.chat.module.home.adapter.MsgChatListAdapter;
import com.daoyu.chat.module.home.bean.ScanAddFriendB;
import com.daoyu.chat.module.im.module.ChatTable;
import com.daoyu.chat.module.im.module.MessageDetailTable;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 聊天列表界面
 */
public class MsgChatFragment extends BaseFragment implements View.OnClickListener, MsgChatListAdapter.OnMsgChatListClickListener {
    @BindView(R.id.iv_search)
    ImageView ivSearch;

    @BindView(R.id.iv_address_book)
    ImageView ivAddressBook;


    @BindView(R.id.iv_add)
    ImageView ivAdd;

    @BindView(R.id.rv_msg_list)
    RecyclerView rvMsgList;

    @BindView(R.id.tv_number)
    TextView tvNumber;

    private List<ChatTable> chatTables;
    private MsgChatListAdapter msgChatListAdapter;
    private UserBean.UserData userInfoData;


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_msg_chat;
    }

    @Override
    protected void initEvent() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        chatTables = new ArrayList<>();
        userInfoData = BaseApplication.getInstance().getUserInfoData();
        msgChatListAdapter = new MsgChatListAdapter(chatTables, getContext());
        bindingRecyclerView(rvMsgList);
        rvMsgList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMsgList.setAdapter(msgChatListAdapter);
        msgChatListAdapter.setListener(this);
        initListener();
    }

    @Override
    protected void loadInitData() {
        super.loadInitData();
        chatTables.clear();
        List<ChatTable> all = LitePal.where("current_id = ?", String.valueOf(userInfoData.uid)).find(ChatTable.class);
        if (all != null && all.size() > 0) {
            chatTables.addAll(all);
        }
        sortList();
        if (chatTables == null || chatTables.size() <= 0) {
            View view = View.inflate(getContext(), R.layout.layout_msg_list_empty, null);
            mStatusLayoutManager.showCustomLayout(view);
        } else {
            mStatusLayoutManager.showSuccessLayout();
        }
        msgChatListAdapter.notifyDataSetChanged();

    }

    private void sortList() {
        if (chatTables == null || chatTables.size() <= 0) {
            msgChatListAdapter.notifyDataSetChanged();
            return;
        }
        Collections.sort(chatTables, (o1, o2) -> {
            boolean isTop1 = o1.top;
            boolean isTop2 = o2.top;
            if (isTop1 ^ isTop2) {
                return isTop1 ? -1 : 1;
            } else {
                return 0;
            }
        });
    }

    private void initListener() {
        ivSearch.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
        ivAddressBook.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_search:
                startActivity(new Intent(getActivity(), SearchCommonActivity.class));
                break;
            case R.id.iv_add:
                showTopRightPopMenu(ivAdd);
                break;
            case R.id.iv_address_book:
                EventBus.getDefault().post(new SwitchContactEvent());
                break;
        }
    }

    @Override
    public void onSlideMenuClickListener(String menu, int position, ChatTable chatTable) {
        switch (menu) {
            case "delete":
                chatTables.remove(chatTable);
                if (chatTables == null || chatTables.size() <= 0) {
                    View view = View.inflate(getContext(), R.layout.layout_msg_list_empty, null);
                    mStatusLayoutManager.showCustomLayout(view);
                } else {
                    mStatusLayoutManager.showSuccessLayout();
                }
                msgChatListAdapter.notifyDataSetChanged();
                LitePal.deleteAllAsync(ChatTable.class, "chat_id = ?", chatTable.chat_id).listen(rowsAffected -> {
                    Log.d("TAG", "受影响的记录为:" + rowsAffected);
                    EventBus.getDefault().post(new MessageTotalEvent());
                });
                LitePal.deleteAllAsync(MessageDetailTable.class, "chat_id = ?", chatTable.chat_id).listen(rowsAffected -> Log.d("TAG", "受影响的记录为:" + rowsAffected));
                break;
            case "top":
                chatTable.top = true;
                chatTable.saveAsync();
                sortList();
                msgChatListAdapter.notifyDataSetChanged();
                break;
            case "unRead":
                int number = chatTable.number;
                if (number == 0) {
                    chatTable.number = 1;
                } else {
                    chatTable.number = 0;
                }
                chatTable.saveAsync().listen(success -> {
                    EventBus.getDefault().post(new MessageTotalEvent());
                    msgChatListAdapter.notifyItemChanged(position);
                });

                break;
        }

    }

    @Override
    public void onItemClickMSGListener(int position, ChatTable chatTable) {
        if (chatTable.chat_type) {
            Intent intent = new Intent(getActivity(), GroupChatActivity.class);
            intent.putExtra(Constant.FRIEND_NAME, chatTable.username);
            intent.putExtra(Constant.FRIEND_UID, chatTable.user_id);
            intent.putExtra(Constant.FRIEND_HEADER, chatTable.avatar);
            startActivity(intent);
        } else {

            requestIsFriend(chatTable.user_id, String.valueOf(userInfoData.uid), chatTable);
          /*  String friendNickName = chatTable.username;
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra(Constant.FRIEND_NAME, friendNickName);
            intent.putExtra(Constant.FRIEND_UID, chatTable.user_id);
            intent.putExtra(Constant.FRIEND_HEADER, chatTable.avatar);
            intent.putExtra(Constant.IS_PRIVATE_HELP, chatTable.is_private);
            intent.putExtra(Constant.IS_AI, chatTable.avatar);
            startActivity(intent);*/

        }

    }

    /**
     * 查询与好友的关系
     *
     * @param fId
     * @param uId
     */
    private void requestIsFriend(String fId, String uId, ChatTable chatTable) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_friend_id", fId);
        params.put("user_id", uId);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_IS_FRIEND, params, this, new JsonCallback<ScanAddFriendB>(ScanAddFriendB.class) {
            @Override
            public void onSuccess(Response<ScanAddFriendB> response) {
                hideLoading();
                if (response == null || response.body() == null) return;
                ScanAddFriendB body = response.body();
                if (body.code == 1) {
                    ArrayList<ScanAddFriendB.ScanAddFrienData> data = body.data;
                    if (data != null && data.size() > 0) {//是好友
                        ScanAddFriendB.ScanAddFrienData scanAddFrienData = data.get(0);
                        String isAI = scanAddFrienData.isai;
                        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
                        String privateId = userInfoData.private_id;
                        String friendNickName = chatTable.username;
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra(Constant.FRIEND_NAME, privateId.equals(chatTable.user_id) ? "私人特助" : friendNickName);
                        intent.putExtra(Constant.FRIEND_UID, chatTable.user_id);
                        intent.putExtra(Constant.FRIEND_HEADER, chatTable.avatar);
                        intent.putExtra(Constant.IS_PRIVATE_HELP, privateId.equals(chatTable.user_id) ? true : false);
                        intent.putExtra(Constant.IS_AI,isAI);
                        startActivity(intent);
                    } else {
                        if ("-123456".equals(fId)) {
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra(Constant.FRIEND_NAME, "私人特助");
                            intent.putExtra(Constant.FRIEND_UID, chatTable.user_id);
                            intent.putExtra(Constant.FRIEND_HEADER, chatTable.avatar);
                            intent.putExtra(Constant.IS_PRIVATE_HELP, true);
                            intent.putExtra(Constant.IS_AI, true);
                            startActivity(intent);
                        }else {
                            toast.toastShow("她不是您的好友或已退出群聊,已自动清理");
                            chatTables.remove(chatTable);
                            msgChatListAdapter.notifyDataSetChanged();
                            LitePal.deleteAllAsync(ChatTable.class, "chat_id = ?", chatTable.chat_id).listen(rowsAffected -> {
                                Log.d("TAG", "受影响的记录为:" + rowsAffected);
                                EventBus.getDefault().post(new MessageTotalEvent());
                            });
                            LitePal.deleteAllAsync(MessageDetailTable.class, "chat_id = ?", chatTable.chat_id).listen(rowsAffected -> Log.d("TAG", "受影响的记录为:" + rowsAffected));
                        }
                    }
                }
            }

            @Override
            public void onError(Response<ScanAddFriendB> response) {
                super.onError(response);
                hideLoading();
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onApplyFriendEvent(ApplyFriendEvent event) {
        if (event != null) {
            boolean show = event.show;
            tvNumber.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsgListClearEvent(MsgListClearEvent event) {
        if (event != null) {
            String groupId = event.groupId;
            ChatTable chatTableNeedDelete = null;
            for (int i = 0; i < chatTables.size(); i++) {
                ChatTable chatTable = chatTables.get(i);
                if (chatTable.chat_type && chatTable.user_id.equals(groupId)) {
                    chatTableNeedDelete = chatTable;
                    break;
                }
            }
            if (chatTableNeedDelete == null) return;
            chatTables.remove(chatTableNeedDelete);
            msgChatListAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatListUpdateEvent(ChatListUpdateEvent event) {
        if (event != null) {
            LitePal.findAllAsync(ChatTable.class).listen(list -> {
                chatTables.clear();
                if (list != null && list.size() > 0) {
                    chatTables.addAll(list);
                }
                sortList();
                if (chatTables == null || chatTables.size() <= 0) {
                    View view = View.inflate(getContext(), R.layout.layout_msg_list_empty, null);
                    mStatusLayoutManager.showCustomLayout(view);
                } else {
                    mStatusLayoutManager.showSuccessLayout();
                }
                msgChatListAdapter.notifyDataSetChanged();
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
