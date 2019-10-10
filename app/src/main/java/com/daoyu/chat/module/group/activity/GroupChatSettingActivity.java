package com.daoyu.chat.module.group.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.common.OnItemClickListener;
import com.daoyu.chat.event.ClosePageEvent;
import com.daoyu.chat.module.group.adapter.GroupMemberAdapter;
import com.daoyu.chat.module.group.bean.GroupInfoBean;
import com.daoyu.chat.module.group.bean.UsersHeaderBean;
import com.daoyu.chat.module.home.activity.ContactDetailsActivity;
import com.daoyu.chat.module.home.dialog.ClearChatRecordDialog;
import com.daoyu.chat.module.im.module.ChatTable;
import com.daoyu.chat.module.im.module.MessageDetailTable;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.activity.FaceAct;
import com.daoyu.chat.service.MqttService;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 群聊设置
 */
public class GroupChatSettingActivity extends BaseTitleActivity implements OnItemClickListener, ClearChatRecordDialog.IClearChatRecordListener, CompoundButton.OnCheckedChangeListener {
    private static final int MODIFY_GROUP_NAME = 100;
    private static final int MODIFY_MY_GROUP_NICK = 101;
    private static final int MODIFY_GROUP_ANNOUNCEMENT = 102;
    @BindView(R.id.text_group_member)
    TextView textGroupMember;
    @BindView(R.id.rv_group_member)
    RecyclerView rvGroupMember;
    @BindView(R.id.tv_group_chat_name)
    TextView tvGroupChatName;
    @BindView(R.id.cl_group_name)
    ConstraintLayout clGroupName;
    @BindView(R.id.tv_my_group_nick)
    TextView tvMyGroupNick;
    @BindView(R.id.cl_my_group_name)
    ConstraintLayout clMyGroupName;
    @BindView(R.id.cl_lord_transfer)
    ConstraintLayout clLordTransfer;
    @BindView(R.id.cl_group_qr_code)
    ConstraintLayout clGroupQrCode;
    @BindView(R.id.tv_group_announcement_status)
    TextView tvGroupAnnouncementStatus;
    @BindView(R.id.tv_group_announcement_context)
    TextView tvGroupAnnouncementContext;
    @BindView(R.id.cl_group_announcement)
    ConstraintLayout clGroupAnnouncement;
    @BindView(R.id.tv_search_chat_context)
    TextView tvSearchChatContext;
    @BindView(R.id.switch_not_notify)
    Switch switchNotNotify;
    @BindView(R.id.switch_top)
    Switch switchTop;
    @BindView(R.id.tv_set_chat_bg)
    TextView tvSetChatBg;
    @BindView(R.id.tv_clear_chat_record)
    TextView tvClearChatRecord;
    @BindView(R.id.tv_exit_group)
    TextView tvExitGroup;


    private GroupInfoBean.GroupInfoData groupInfoData;
    private String adminid;
    private UserBean.UserData userInfoData;
    private String currentUid;
    private boolean isGroupAdmin = false;
    private ArrayList<UsersHeaderBean.UsersHeaderData> userHeaders;
    private ArrayList<UsersHeaderBean.UsersHeaderData> userAllHeaders;
    private ArrayList<UsersHeaderBean.UsersHeaderData> userRealHeaders;

    private GroupMemberAdapter memberAdapter;
    private ClearChatRecordDialog clearChatRecordDialog;
    private ChatTable chatTable;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_group_chat_setting;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("聊天设置");
        Intent intent = getIntent();
        groupInfoData = intent.getParcelableExtra(Constant.CONTACT_GROUP_INFO);
        if (groupInfoData == null) {
            finish();
            return;
        }
        initListener();
        userHeaders = new ArrayList<>();
        userAllHeaders = new ArrayList<>();
        userRealHeaders = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 5) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rvGroupMember.setLayoutManager(gridLayoutManager);
        memberAdapter = new GroupMemberAdapter(this, userHeaders);
        memberAdapter.setListener(this);
        rvGroupMember.setAdapter(memberAdapter);

        userInfoData = BaseApplication.getInstance().getUserInfoData();
        adminid = groupInfoData.adminid;
        currentUid = String.valueOf(userInfoData.uid);
        if (TextUtils.isEmpty(adminid)) {
            adminid = "";
        }
        if (adminid.equals(currentUid)) {
            //是群主
            isGroupAdmin = true;
            clLordTransfer.setVisibility(View.VISIBLE);
            clGroupName.setEnabled(true);
            tvExitGroup.setText("解散群");
        } else {
            //不是群主
            isGroupAdmin = false;
            clLordTransfer.setVisibility(View.GONE);
            clGroupName.setEnabled(false);
            tvExitGroup.setText("退出群");
        }
        tvGroupChatName.setText(groupInfoData.groupname);

        ArrayList<String> users = groupInfoData.users;
        int currentPosition = -1;
        if (users != null && users.size() > 0) {
            for (int i = 0; i < users.size(); i++) {
                String s = users.get(i);
                if (currentUid.equals(s)) {
                    currentPosition = i;
                    break;
                }
            }
        }
        if (currentPosition != -1) {
            ArrayList<String> usersnickname = groupInfoData.usersnickname;
            String userNick = usersnickname.get(currentPosition);
            if (!TextUtils.isEmpty(userNick)) {
                if ("-".equals(userNick)) {
                    tvMyGroupNick.setText(userInfoData.nickName);
                } else {
                    tvMyGroupNick.setText(userNick);
                }
            }
        }

        String groupnotice = groupInfoData.groupnotice;
        if (".".equals(groupnotice)) {
            tvGroupAnnouncementContext.setVisibility(View.GONE);
            tvGroupAnnouncementStatus.setText("未设置");
        } else {
            tvGroupAnnouncementContext.setVisibility(View.VISIBLE);
            tvGroupAnnouncementContext.setText(groupnotice);
            tvGroupAnnouncementStatus.setText("");
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < users.size(); i++) {
            sb.append(users.get(i) + ",");
        }
        String trim = sb.toString().trim();
        if (!TextUtils.isEmpty(trim)) {
            String ids = trim.substring(0, trim.length() - 1);
            requestGetUsersHeader(ids);
        }

        List<ChatTable> chatTables = LitePal.where("chat_id = ?", userInfoData.uid + "GL" + groupInfoData.group_id).find(ChatTable.class);
        if (chatTables != null && chatTables.size() > 0) {
            chatTable = chatTables.get(0);
            switchNotNotify.setChecked(chatTable.shield);
            switchTop.setChecked(chatTable.top);
        }


    }

    private void initListener() {
        clGroupName.setOnClickListener(this);
        clMyGroupName.setOnClickListener(this);
        clLordTransfer.setOnClickListener(this);
        clGroupQrCode.setOnClickListener(this);
        clGroupAnnouncement.setOnClickListener(this);
        tvSearchChatContext.setOnClickListener(this);
        tvSetChatBg.setOnClickListener(this);
        tvClearChatRecord.setOnClickListener(this);
        tvExitGroup.setOnClickListener(this);
        switchTop.setOnCheckedChangeListener(this);
        switchNotNotify.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        Intent intent = new Intent();
        intent.putExtra(Constant.CONTACT_GROUP_INFO, groupInfoData);
        switch (view.getId()) {
            case R.id.cl_group_name:
                intent.setClass(this, ModifyGroupNameActivity.class);
                startActivityForResult(intent, MODIFY_GROUP_NAME);
                break;
            case R.id.cl_my_group_name:
                intent.setClass(this, ModifyMyGroupNickActivity.class);
                intent.putExtra(Constant.GROUP_MY_NICK, tvMyGroupNick.getText().toString().trim());
                startActivityForResult(intent, MODIFY_MY_GROUP_NICK);
                break;
            case R.id.cl_lord_transfer:
                if (userRealHeaders == null || userRealHeaders.size() <= 0) return;
                intent.setClass(this, LordTransferActivity.class);
                intent.putParcelableArrayListExtra(Constant.GROUP_MEMBER_ALL, userRealHeaders);
                intent.putExtra(Constant.CONTACT_GROUP_INFO, groupInfoData);
                startActivity(intent);
                finish();
                break;
            case R.id.cl_group_qr_code:
                intent.setClass(this, GroupQRCodeActivity.class);
                startActivity(intent);
                break;
            case R.id.cl_group_announcement:
                intent.setClass(this, ModifyGroupAnnouncementActivity.class);
                intent.putExtra(Constant.GROUP_SET, isGroupAdmin);
                startActivityForResult(intent, MODIFY_GROUP_ANNOUNCEMENT);
                break;
            case R.id.tv_search_chat_context:
                break;
            case R.id.tv_clear_chat_record:
                showClearChatRecord();
                break;
            case R.id.tv_set_chat_bg:
                startActivity(new Intent(this, FaceAct.class));
                break;
            case R.id.tv_exit_group:
                if (isGroupAdmin) {
                    requestDeleteGroup();
                } else {
                    requestExitGroup();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MODIFY_GROUP_NAME:
                    if (data == null) return;
                    String groupName = data.getStringExtra(Constant.GROUP_NAME);
                    tvGroupChatName.setText(groupName);
                    break;
                case MODIFY_MY_GROUP_NICK:
                    if (data == null) return;
                    String myNickGroup = data.getStringExtra(Constant.GROUP_MY_NICK);
                    tvMyGroupNick.setText(myNickGroup);
                    break;
                case MODIFY_GROUP_ANNOUNCEMENT:
                    if (data == null) return;
                    String notic = data.getStringExtra(Constant.GROUP_NIOTICE);
                    tvGroupAnnouncementContext.setVisibility(View.VISIBLE);
                    tvGroupAnnouncementStatus.setText("");
                    tvGroupAnnouncementContext.setText(notic);
                    break;
            }
        }

    }

    /**
     * 获取群用户信息 头像
     *
     * @param ids
     */
    private void requestGetUsersHeader(String ids) {
        showLoading("加载中...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("auth_token", userInfoData.token);
        params.put("user_ids", ids);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_GET_USERS_HEADER, params, this, new JsonCallback<UsersHeaderBean>(UsersHeaderBean.class) {
            @Override
            public void onSuccess(Response<UsersHeaderBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                UsersHeaderBean body = response.body();
                if (body.code == 1) {
                    ArrayList<UsersHeaderBean.UsersHeaderData> data = body.data;
                    if (data == null || data.size() <= 0) return;
                    int size = data.size();
                    textGroupMember.setText(String.format("群成员(%d)", size));
                    userRealHeaders.addAll(data);
                    if (isGroupAdmin) {
                        if (size > 12) {
                            for (int i = 0; i < 12; i++) {
                                UsersHeaderBean.UsersHeaderData usersHeaderData = data.get(i);
                                userHeaders.add(usersHeaderData);
                            }
                            userHeaders.add(new UsersHeaderBean.UsersHeaderData("邀请", R.drawable.groupchat_setting_add, 1));
                            userHeaders.add(new UsersHeaderBean.UsersHeaderData("移除", R.drawable.groupchat_setting_remove, 2));
                            userHeaders.add(new UsersHeaderBean.UsersHeaderData("更多", R.drawable.groupchat_setting_more_member, 3));
                        } else {
                            userHeaders.addAll(data);
                            userHeaders.add(new UsersHeaderBean.UsersHeaderData("邀请", R.drawable.groupchat_setting_add, 1));
                            userHeaders.add(new UsersHeaderBean.UsersHeaderData("移除", R.drawable.groupchat_setting_remove, 2));
                        }
                    } else {
                        if (size > 13) {
                            for (int i = 0; i < 13; i++) {
                                UsersHeaderBean.UsersHeaderData usersHeaderData = data.get(i);
                                userHeaders.add(usersHeaderData);
                            }
                            userHeaders.add(new UsersHeaderBean.UsersHeaderData("邀请", R.drawable.groupchat_setting_add, 1));
                            userHeaders.add(new UsersHeaderBean.UsersHeaderData("更多", R.drawable.groupchat_setting_more_member, 3));
                        } else {
                            userHeaders.addAll(data);
                            userHeaders.add(new UsersHeaderBean.UsersHeaderData("邀请", R.drawable.groupchat_setting_add, 1));
                        }
                    }

                    memberAdapter.notifyDataSetChanged();

                    userAllHeaders.addAll(data);
                    if (isGroupAdmin) {
                        userAllHeaders.add(new UsersHeaderBean.UsersHeaderData("邀请", R.drawable.groupchat_setting_add, 1));
                        userAllHeaders.add(new UsersHeaderBean.UsersHeaderData("移除", R.drawable.groupchat_setting_remove, 2));
                    } else {
                        userAllHeaders.add(new UsersHeaderBean.UsersHeaderData("邀请", R.drawable.groupchat_setting_add, 1));
                    }
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

    @Override
    public void onItemClick(Object data, int position) {
        if (data == null) return;
        if (data instanceof UsersHeaderBean.UsersHeaderData) {
            UsersHeaderBean.UsersHeaderData usersHeaderData = (UsersHeaderBean.UsersHeaderData) data;
            if (userRealHeaders == null || userRealHeaders.size() <= 0) return;
            if (groupInfoData == null) return;
            int type = usersHeaderData.type;
            if (type == 0) {
                Intent intent = new Intent(this, ContactDetailsActivity.class);
                intent.putExtra(Constant.CONTACT_ID, usersHeaderData.userId);
                startActivity(intent);
            } else if (type == 1) {//邀请
                Intent intent = new Intent(this, InviteGroupMemberActivity.class);
                intent.putParcelableArrayListExtra(Constant.GROUP_MEMBER_ALL, userRealHeaders);
                intent.putExtra(Constant.CONTACT_GROUP_INFO, groupInfoData);
                startActivity(intent);
                finish();
            } else if (type == 2) {//移除
                Intent intent = new Intent(this, RemoveGroupMemberActivity.class);
                intent.putParcelableArrayListExtra(Constant.GROUP_MEMBER_ALL, userRealHeaders);
                intent.putExtra(Constant.CONTACT_GROUP_INFO, groupInfoData);
                startActivity(intent);
                finish();
            } else if (type == 3) {//更多
                Intent intent = new Intent(this, GroupMemberAllActivity.class);
                intent.putParcelableArrayListExtra(Constant.GROUP_MEMBER_ALL, userAllHeaders);
                startActivity(intent);
                finish();
            }

        }
    }

    /**
     * 删除群
     */
    private void requestDeleteGroup() {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupInfoData.group_id);
        params.put("user_id", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_DELETE_GROUP, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.code == 1) {
                    toast.toastShow(body.msg);
                    if (groupInfoData != null) {
                        ArrayList<String> users = groupInfoData.users;
                        if (users != null && users.size() > 0) {
                            String[] createIds = new String[users.size()];
                            for (int i = 0; i <users.size(); i++) {
                                createIds[i] = users.get(i);
                            }
                            Map<String, Object> params = new HashMap<>();
                            params.put("users", createIds);
                            params.put("group", groupInfoData.group_id);
                            MqttService.publish(new Gson().toJson(params), "grouprefuse");
                        }
                    }

                    LitePal.deleteAllAsync(MessageDetailTable.class, "chat_id = ?", userInfoData.uid + "GL" + groupInfoData.group_id).listen(rowsAffected -> Log.d("TAG", "影响的记录" + rowsAffected));
                    LitePal.deleteAllAsync(ChatTable.class, "chat_id = ?", userInfoData.uid + "GL" + groupInfoData.group_id).listen(rowsAffected -> Log.d("TAG", "影响的记录" + rowsAffected));
                    EventBus.getDefault().post(new ClosePageEvent());
                    finish();
                } else {
                    toast.toastShow(body.msg);
                }
            }
        }, UrlConfig.BASE_GROUP_URL);
    }

    /**
     * 退出群
     */
    private void requestExitGroup() {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupInfoData.group_id);
        params.put("user_id", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_EXIT_GROUP, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.code == 1) {
                    toast.toastShow(body.msg);
                    Map<String, Object> params = new HashMap<>();
                    params.put("users", new String[]{String.valueOf(userInfoData.uid)});
                    params.put("group", groupInfoData.group_id);
                    MqttService.publish(new Gson().toJson(params), "grouprefuse");
                    LitePal.deleteAllAsync(MessageDetailTable.class, "chat_id = ?", userInfoData.uid + "GL" + groupInfoData.group_id).listen(rowsAffected -> Log.d("TAG", "影响的记录" + rowsAffected));
                    LitePal.deleteAllAsync(ChatTable.class, "chat_id = ?", userInfoData.uid + "GL" + groupInfoData.group_id).listen(rowsAffected -> Log.d("TAG", "影响的记录" + rowsAffected));
                    EventBus.getDefault().post(new ClosePageEvent());
                    finish();
                } else {
                    toast.toastShow(body.msg);
                }
            }
        }, UrlConfig.BASE_GROUP_URL);
    }

    private void showClearChatRecord() {
        if (clearChatRecordDialog != null) {
            clearChatRecordDialog.dismissAllowingStateLoss();
        }
        clearChatRecordDialog = ClearChatRecordDialog.getInstance();
        if (!clearChatRecordDialog.isAdded()) {
            clearChatRecordDialog.show(getSupportFragmentManager(), "clearChatRecordDialog");
        } else {
            clearChatRecordDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onClearChatRecord() {
        LitePal.deleteAllAsync(MessageDetailTable.class, "chat_id = ?", userInfoData.uid + "GL" + groupInfoData.group_id)
                .listen(rowsAffected -> toast.toastShow("聊天记录删除成功"));
        LitePal.deleteAllAsync(ChatTable.class, "chat_id = ?", userInfoData.uid + "GL" + groupInfoData.group_id).listen(rowsAffected ->
                toast.toastShow("聊天记录删除成功")
        );
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!buttonView.isPressed()) return;
        if (chatTable == null) return;
        switch (buttonView.getId()) {
            case R.id.switch_top:
                chatTable.top = isChecked;
                chatTable.save();
                break;
            case R.id.switch_not_notify:
                chatTable.shield = isChecked;
                chatTable.save();
                break;
        }
    }
}
