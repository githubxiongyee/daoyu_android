package com.daoyu.chat.module.chat.activity;

import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.home.dialog.ClearChatRecordDialog;
import com.daoyu.chat.module.im.module.ChatTable;
import com.daoyu.chat.module.im.module.MessageDetailTable;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.activity.FaceAct;

import org.litepal.LitePal;

import java.util.List;

import butterknife.BindView;

/**
 * 聊天设置
 */
public class ChatSettingActivity extends BaseTitleActivity implements ClearChatRecordDialog.IClearChatRecordListener, CompoundButton.OnCheckedChangeListener {
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
    private String friendId;
    private UserBean.UserData userInfoData;
    private ClearChatRecordDialog clearChatRecordDialog;
    private ChatTable chatTable;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_chat_setting;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("聊天设置");
        Intent intent = getIntent();
        friendId = intent.getStringExtra(Constant.CONTACT_FRIEND_ID);
        userInfoData = BaseApplication.getInstance().getUserInfoData();
        tvSearchChatContext.setOnClickListener(this);
        tvSetChatBg.setOnClickListener(this);
        tvClearChatRecord.setOnClickListener(this);
        switchTop.setOnCheckedChangeListener(this);
        switchNotNotify.setOnCheckedChangeListener(this);

        List<ChatTable> chatTables = LitePal.where("chat_id = ?", userInfoData.uid + "DL" + friendId).find(ChatTable.class);
        if (chatTables != null && chatTables.size() > 0) {
            chatTable = chatTables.get(0);
            switchNotNotify.setChecked(chatTable.shield);
            switchTop.setChecked(chatTable.top);
        }

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_search_chat_context://查找聊天内容
                break;
            case R.id.tv_set_chat_bg://设置聊天背景
                startActivity(new Intent(this, FaceAct.class));
                break;
            case R.id.tv_clear_chat_record://清空聊天记录
                showClearChatRecord();
                break;
        }
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
        LitePal.deleteAllAsync(MessageDetailTable.class, "chat_id = ?", userInfoData.uid + "DL" + friendId)
                .listen(rowsAffected -> toast.toastShow("聊天记录删除成功"));
        LitePal.deleteAllAsync(ChatTable.class, "chat_id = ?", userInfoData.uid + "DL" + friendId).listen(rowsAffected ->
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
