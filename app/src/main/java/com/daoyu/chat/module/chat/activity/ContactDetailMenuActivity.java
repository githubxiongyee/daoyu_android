package com.daoyu.chat.module.chat.activity;

import android.content.Intent;
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
import com.daoyu.chat.event.DeleteFriendEvent;
import com.daoyu.chat.module.chat.bean.LocalFriendData;
import com.daoyu.chat.module.home.bean.ScanAddFriendB;
import com.daoyu.chat.module.im.module.ApplyFriendTable;
import com.daoyu.chat.module.im.module.ChatTable;
import com.daoyu.chat.module.im.module.IMConstant;
import com.daoyu.chat.module.im.module.MessageDetailTable;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 用户详细资料菜单
 */
public class ContactDetailMenuActivity extends BaseTitleActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.tv_set_remark)
    TextView tvSetRemark;
    @BindView(R.id.tv_remand_to_friend)
    TextView tvRemandToFriend;

    @BindView(R.id.switch_black)
    Switch switchBlack;
    @BindView(R.id.tv_delete_friend)
    TextView tvDeleteFriend;
    private UserBean.UserData userInfoData;
    private ScanAddFriendB.ScanAddFrienData  dataBean;
    private int friendId;
    private String remarks;
    private String friendNickName;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_cotact_detail_menu;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("好友设置");
        Intent intent = getIntent();
        dataBean = intent.getParcelableExtra(Constant.CONTACT_FRIEND_INFO);
        if (dataBean == null) finish();
        friendId = dataBean.userFriendId;
        remarks = dataBean.fremarks;
        friendNickName = dataBean.friendNickName;
        userInfoData = BaseApplication.getInstance().getUserInfoData();
        switchBlack.setChecked("Y".equals(dataBean.blacklist));

        tvSetRemark.setOnClickListener(this);
        tvRemandToFriend.setOnClickListener(this);
        tvDeleteFriend.setOnClickListener(this);

        switchBlack.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_set_remark:
                Intent intent = new Intent(this, SettingRemarkNameActivity.class);
                intent.putExtra(Constant.CONTACT_FRIEND_ID, String.valueOf(friendId));
                intent.putExtra(Constant.CONTACT_NAME, friendNickName);
                intent.putExtra(Constant.CONTACT_REMARKS_NAME, TextUtils.isEmpty(remarks) ? "" : remarks);
                startActivity(intent);
                break;
            case R.id.tv_remand_to_friend:
                //把她推荐给朋友
                Intent intent1 = new Intent(this, SendCardToFriendAct.class);
                intent1.putExtra(Constant.CONTACT_FRIEND_INFO, dataBean);
                startActivity(intent1);
                break;
            case R.id.tv_delete_friend:
                requestDeleteFriend();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!buttonView.isPressed()) return;
        switch (buttonView.getId()) {
            case R.id.switch_black://加入黑名单
                if (isChecked) {
                    requestBlack();
                } else {
                    requestWhite();
                }
                break;
        }
    }

    /**
     * 删除好友
     */
    private void requestDeleteFriend() {
        showLoading("删除中...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", String.valueOf(userInfoData.uid));
        params.put("user_friend_id", friendId);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_DELETE_FRIEND_SINGLE, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    Map<String, LocalFriendData> normalFriendMap = IMConstant.normalFriendMap;
                    if (normalFriendMap.containsKey(String.valueOf(friendId))) {
                        normalFriendMap.remove(String.valueOf(friendId));
                    }
                    toast.toastShow(body.msg);
                    EventBus.getDefault().post(new DeleteFriendEvent());
                    LitePal.deleteAllAsync(MessageDetailTable.class, "chat_id = ?", userInfoData.uid + "DL" + friendId).listen(rowsAffected -> Log.d("TAG", "影响的记录" + rowsAffected));
                    LitePal.deleteAllAsync(ChatTable.class, "chat_id = ?", userInfoData.uid + "DL" + friendId).listen(rowsAffected -> Log.d("TAG", "影响的记录" + rowsAffected));
                    LitePal.deleteAllAsync(ApplyFriendTable.class, "userid = ?", String.valueOf(friendId)).listen(rowsAffected -> Log.d("TAG", "影响的记录" + rowsAffected));
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

    /**
     * 拉黑
     */
    private void requestBlack() {
        showLoading("请稍后...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("user_friend_id", friendId);
        params.put("user_id", String.valueOf(userInfoData.uid));
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_SET_FRIEND_BLACK, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    toast.toastShow(body.msg);
                    Map<String, LocalFriendData> normalFriendMap = IMConstant.normalFriendMap;
                    if (normalFriendMap.containsKey(String.valueOf(friendId))) {
                        normalFriendMap.put(String.valueOf(friendId), new LocalFriendData(remarks, 1));
                    }
                } else {
                    toast.toastShow(body.msg);
                    boolean checked = switchBlack.isChecked();
                    switchBlack.setChecked(!checked);
                }
            }

            @Override
            public void onError(Response<BaseBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
                boolean checked = switchBlack.isChecked();
                switchBlack.setChecked(!checked);
            }
        });
    }

    /**
     * 拉白
     */
    private void requestWhite() {
        showLoading("请稍后...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("user_friend_id", friendId);
        params.put("user_id", String.valueOf(userInfoData.uid));
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_SET_FRIEND_WHITE, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    toast.toastShow(body.msg);
                    Map<String, LocalFriendData> normalFriendMap = IMConstant.normalFriendMap;
                    if (normalFriendMap.containsKey(String.valueOf(friendId))) {
                        normalFriendMap.put(String.valueOf(friendId), new LocalFriendData(remarks, 0));
                    }
                } else {
                    toast.toastShow(body.msg);
                    boolean checked = switchBlack.isChecked();
                    switchBlack.setChecked(!checked);
                }
            }

            @Override
            public void onError(Response<BaseBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
                boolean checked = switchBlack.isChecked();
                switchBlack.setChecked(!checked);
            }
        });
    }

}
