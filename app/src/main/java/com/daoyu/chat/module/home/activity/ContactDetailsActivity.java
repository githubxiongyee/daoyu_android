package com.daoyu.chat.module.home.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.event.DeleteFriendEvent;
import com.daoyu.chat.event.UpdateFRemarkEvent;
import com.daoyu.chat.module.chat.activity.ChatActivity;
import com.daoyu.chat.module.chat.activity.ContactDetailMenuActivity;
import com.daoyu.chat.module.chat.bean.LocalFriendData;
import com.daoyu.chat.module.home.bean.BaseBean2;
import com.daoyu.chat.module.home.bean.FriendInfoBean;
import com.daoyu.chat.module.home.bean.ScanAddFriendB;
import com.daoyu.chat.module.home.dialog.ChooseLabelDialog;
import com.daoyu.chat.module.im.module.ApplyFriendTable;
import com.daoyu.chat.module.im.module.IMConstant;
import com.daoyu.chat.module.im.module.Message;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.bean.GetUserValueB;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.daoyu.chat.view.CircleImageView;
import com.dy.dyim.android.core.LocalUDPDataSender;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 联系人资料详情
 */
public class ContactDetailsActivity extends BaseTitleActivity implements ChooseLabelDialog.IChooseLabelType {

    @BindView(R.id.iv_friend_header)
    CircleImageView ivFriendHeader;

    @BindView(R.id.tv_friend_name)
    TextView tvFriendName;

    @BindView(R.id.tv_friend_id)
    TextView tvFriendId;


    @BindView(R.id.tv_signature)
    TextView tvSignature;

    @BindView(R.id.tv_add_to_friend)
    TextView tvAddToFriend;

    @BindView(R.id.tv_label)
    TextView tvLabel;
    @BindView(R.id.cl_label)
    ConstraintLayout clLabel;
    @BindView(R.id.cl_signature)
    ConstraintLayout clSignature;

    private String contactId;
    private String contactType;
    private String label;

    private ChooseLabelDialog chooseLabelDialog;
    private UserBean.UserData userInfoData;

    private FriendInfoBean.FriendInfoData friendInfoData;

    public static ApplyFriendTable applyFriendData;

    private boolean isFriends = false;
    private ScanAddFriendB.ScanAddFrienData scanAddFrienData;
    private boolean isApply;
    private String currentUid;
    private boolean isNearBy = false;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_contact_details;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        requestIsFriend(contactId, String.valueOf(userInfoData.uid), true);
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("详细资料");
        userInfoData = BaseApplication.getInstance().getUserInfoData();
        currentUid = String.valueOf(userInfoData.uid);
        Intent intent = getIntent();
        contactId = intent.getStringExtra(Constant.CONTACT_ID);
        isApply = intent.getBooleanExtra(Constant.CONTACT_APPLY, false);
        isNearBy = intent.getBooleanExtra(Constant.CONTACT_NEAR_BY, false);
        if (TextUtils.isEmpty(contactId)) {
            finish();
            return;
        }
        initListener();
    }


    private void friendTypeShow() {
        switch (contactType) {
            case "add"://添加
                hideMenu();
                tvAddToFriend.setText("加为好友");
                clLabel.setVisibility(View.GONE);
                break;
            case "approved"://通过验证
                hideMenu();
                tvAddToFriend.setText("通过验证");
                clLabel.setVisibility(View.GONE);
                break;
            case "message"://发消息
                clLabel.setVisibility(View.VISIBLE);
                tvAddToFriend.setText("发消息");
                break;
        }
        if (currentUid.equals(contactId)) {
            tvAddToFriend.setVisibility(View.GONE);
            clLabel.setVisibility(View.GONE);
        } else {
            tvAddToFriend.setVisibility(View.VISIBLE);
        }
    }

    private void initListener() {
        tvAddToFriend.setOnClickListener(this);
        clLabel.setOnClickListener(this);
        ivFriendHeader.setOnClickListener(this);
    }

    /**
     * 查看好友是否可添加
     */
    private void requestGetValue(String fId) {
        Map<String, Object> params = new HashMap<>();
        params.put("code", "invisible");
        params.put("user_id", fId);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_GET_USER_VALUE, params, ContactDetailsActivity.this, new JsonCallback<GetUserValueB>(GetUserValueB.class) {
            @Override
            public void onSuccess(Response<GetUserValueB> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                GetUserValueB body = response.body();
                if (body.getCode() == 1) {
                    //toast.toastShow(body.getMsg());
                    if (body.getData().size() == 0 || body.getData() == null) {
                        //为空的时候，直接所有人可加
                        requestFriendListContactMine(String.valueOf(userInfoData.uid), contactId);
                    } else {
                        if (body.getData().get(0).getSettingValue().equals("1")) {
                            //addRangeTxt("所有人可加我为好友");
                            requestFriendListContactMine(String.valueOf(userInfoData.uid), contactId);
                        } else if (body.getData().get(0).getSettingValue().equals("2")) {
                            //addRangeTxt("加我时需要验证");
                        } else if (body.getData().get(0).getSettingValue().equals("3")) {
                            //addRangeTxt("不允许加我为好友");
                            toast.toastShow(getResources().getString(R.string.str_cannot_add_friend));
                        }
                    }

                } else {
                    toast.toastShow(body.getMsg());
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_add_to_friend:
                switch (contactType) {
                    case "add"://添加
                        requestGetValue(contactId);
                        break;
                    case "approved"://通过验证
                        approved();
                        break;
                    case "message"://发消息
                        Intent intent = new Intent(this, ChatActivity.class);
                        if (friendInfoData == null) return;
                        if (scanAddFrienData == null) return;
                        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
                        String privateId = userInfoData.private_id;
                        intent.putExtra(Constant.FRIEND_NAME, friendInfoData.nickName);
                        intent.putExtra(Constant.CONTACT_REMARKS_NAME, tvFriendName.getText().toString());
                        intent.putExtra(Constant.FRIEND_UID, contactId);
                        String headImg = friendInfoData.headImg;
                        intent.putExtra(Constant.FRIEND_HEADER, TextUtils.isEmpty(headImg) ? "" : headImg);
                        intent.putExtra(Constant.IS_PRIVATE_HELP, privateId.equals(contactId) ? true : false);
                        intent.putExtra(Constant.IS_AI, scanAddFrienData.isai);
                        startActivity(intent);
                        break;
                }

                break;
            case R.id.iv_menu:
                if (scanAddFrienData == null) return;
                Intent intent = new Intent(this, ContactDetailMenuActivity.class);
                intent.putExtra(Constant.CONTACT_FRIEND_INFO, scanAddFrienData);
                startActivity(intent);
                break;
            case R.id.cl_label://标签
                showLabelDialog();
                break;
            case R.id.iv_friend_header:
                Intent intentHeader = new Intent(this, FriendHeaderActivity.class);
                String headImg = friendInfoData.headImg;
                intentHeader.putExtra(Constant.CONTACT_HEADER, TextUtils.isEmpty(headImg) ? "" : headImg);
                startActivity(intentHeader);
                break;
        }
    }


    private void approved() {
        if (applyFriendData != null) {
            if (applyFriendData.isNearBy == null || applyFriendData.isNearBy.isEmpty()){
                if (applyFriendData.isNearBy==null){
                    applyFriendData.status = "2";
                    applyFriendData.saveAsync().listen(success -> Log.d("TAG", "更新成功"));
                }
                requestAddToFriendList(String.valueOf(friendInfoData.id), String.valueOf(userInfoData.uid));
            }else {
                if (applyFriendData.isNearBy.equals("Y")) {
                    requestAddNearby(String.valueOf(friendInfoData.id));
                } else {
                    if (applyFriendData.isNearBy.equals("N")) {
                        applyFriendData.status = "2";
                        applyFriendData.saveAsync().listen(success -> Log.d("TAG", "更新成功"));
                    }
                    requestAddToFriendList(String.valueOf(friendInfoData.id), String.valueOf(userInfoData.uid));
                }
            }

        }

    }

    private void requestAddNearby(String userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_friend_id", userId);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_NEARBY_PEOPLE_ADD_GET_MONEY, params, this, new JsonCallback<BaseBean2>(BaseBean2.class) {
            @Override
            public void onSuccess(Response<BaseBean2> response) {
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean2 body = response.body();
                if (body.code == 1) {
                    applyFriendData.status = "2";
                    applyFriendData.saveAsync().listen(success -> Log.d("TAG", "更新成功"));

                }
                toast.toastShow(body.msg);
                finish();
            }

            @Override
            public void onError(Response<BaseBean2> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        });

    }

    private void requestAddToFriendList(String friendId, String userid) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_friend_id", friendId);
        params.put("user_id", userid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_ADD_CONTACT, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.code == 1) {
                    Map<String, LocalFriendData> normalFriendMap = IMConstant.normalFriendMap;
                    if (!normalFriendMap.containsKey(friendId)) {
                        normalFriendMap.put(friendId, new LocalFriendData(null, 0));
                    }
                    if (!isFriends) {
                        sendAddFriendMessage("我同意了你的朋友验证请求,现在我们可以开始聊天了!", String.valueOf(friendInfoData.id));
                        sendTextMessage("我同意了你的朋友验证请求,现在我们可以开始聊天了!", String.valueOf(friendInfoData.id));
                        finish();
                    } else {
                        requestIsFriend(contactId, String.valueOf(userInfoData.uid), false);
                        clLabel.setVisibility(View.VISIBLE);
                        tvAddToFriend.setText("发消息");
                        contactType = "message";

                    }
                } else {
                    toast.toastShow(body.msg);
                    finish();
                }
            }
        });
    }


    /**
     * 发送文本消息
     *
     * @param message
     */
    private void sendTextMessage(String message, String friendUid) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        String uid = String.valueOf(userInfoData.uid);
        String headImg = userInfoData.headImg;
        String time = String.valueOf(System.currentTimeMillis());
        Message messageData = new Message(uid, headImg, time,
                message, 1, TextUtils.isEmpty(userInfoData.nickName) ? userInfoData.userPhone : userInfoData.nickName, 1);

        doSendQosMessage(new Gson().toJson(messageData), friendUid);
    }

    /**
     * 发送通过或回执
     *
     * @param message
     */
    private void sendAddFriendMessage(String message, String friendUid) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        String uid = String.valueOf(userInfoData.uid);
        String headImg = userInfoData.headImg;
        String time = String.valueOf(System.currentTimeMillis());
        Message messageData = new Message(uid, headImg, time,
                message, 1, TextUtils.isEmpty(userInfoData.nickName) ? userInfoData.userPhone : userInfoData.nickName, IMConstant.MessageType.ANSWERFRIEND);

        doSendQosMessage(new Gson().toJson(messageData), friendUid);
    }

    private void showLabelDialog() {
        if (chooseLabelDialog != null) {
            chooseLabelDialog.dismissAllowingStateLoss();
        }
        chooseLabelDialog = ChooseLabelDialog.getInstance();
        if (!chooseLabelDialog.isAdded()) {
            chooseLabelDialog.show(getSupportFragmentManager(), "chooseLabelDialog");
        } else {
            chooseLabelDialog.dismissAllowingStateLoss();
        }
    }

    /**
     * 发送QOS消息
     *
     * @param msg
     * @param friendId
     */
    private void doSendQosMessage(String msg, String friendId) {
        LocalUDPDataSender.getInstance(this).sendCommonData(msg, friendId);
    }

    private void requestUpdateLabel(String type) {
        showLoading("修改中...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("user_friend_id", contactId);
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
                    tvLabel.setText("S".equals(type) ? "生活类" : "工作类");
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
    public void onChooseLabel(String label) {
        switch (label) {
            case "life":
                requestUpdateLabel("S");
                break;
            case "work":
                requestUpdateLabel("J");
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteFriendEvent(DeleteFriendEvent event) {
        if (event != null) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateFRemarkEvent(UpdateFRemarkEvent event) {
        if (event != null) {
            String frmarks = event.frmarks;
            if (!TextUtils.isEmpty(frmarks)) {
                tvFriendName.setText(frmarks);
            } else {
                tvFriendName.setText(friendInfoData.nickName);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 通过ID获取用户信息
     */
    private void requestGetUserInfo() {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", contactId);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_GET_USER_INFO_BY_ID, params, this, new JsonCallback<FriendInfoBean>(FriendInfoBean.class) {
            @Override
            public void onSuccess(Response<FriendInfoBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                FriendInfoBean body = response.body();
                if (body.code == 1) {
                    ArrayList<FriendInfoBean.FriendInfoData> data = body.data;
                    if (data == null || data.size() <= 0) {
                        finish();
                    }
                    friendInfoData = data.get(0);
                    if (friendInfoData == null) {
                        finish();
                        return;
                    }
                    initUserView();
                } else {
                    toast.toastShow(body.msg);
                }
            }

            @Override
            public void onError(Response<FriendInfoBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        });
    }

    private void initUserView() {
        if (friendInfoData == null) return;
        ImageUtils.setNormalImage(ContactDetailsActivity.this, friendInfoData.headImg + "?imageMogr2/thumbnail/200/quality/40", R.drawable.my_user_default, R.drawable.my_user_default, ivFriendHeader);
        String sex = friendInfoData.sex;
        String mobile = friendInfoData.mobile;
        signTel(mobile);
        tvFriendName.setText(friendInfoData.nickName);
        if (TextUtils.isEmpty(sex)) {
            tvFriendId.setCompoundDrawables(null, null, null, null);
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.selector_sex_show_img);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvFriendName.setCompoundDrawables(null, null, drawable, null);
            if ("M".equals(sex)) {
                tvFriendName.setEnabled(false);
            } else {
                tvFriendName.setEnabled(true);
            }
        }

        String signature = friendInfoData.remarks;
        tvSignature.setText(TextUtils.isEmpty(signature) ? "" : signature);
    }

    private void signTel(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            tvFriendId.setVisibility(View.GONE);
            return;
        }
        if (mobile.length() == 11) {
            mobile = mobile.substring(0, 3);
            mobile = mobile + " **** ****";
        } else {
            mobile = mobile + " **** ****";
        }
        tvFriendId.setVisibility(View.VISIBLE);
        tvFriendId.setText(String.format("ID:%s", mobile));
    }

    /**
     * 查询朋友关系
     *
     * @param fId 朋友uid
     * @param uId 自己的id
     */
    private void requestIsFriend(String fId, String uId, boolean init) {
        showLoading("加载中...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("user_friend_id", fId);
        params.put("user_id", uId);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_IS_FRIEND, params, this, new JsonCallback<ScanAddFriendB>(ScanAddFriendB.class) {
            @Override
            public void onSuccess(Response<ScanAddFriendB> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                ScanAddFriendB body = response.body();
                if (body.code == 1) {
                    ArrayList<ScanAddFriendB.ScanAddFrienData> data = body.data;
                    if (data != null && data.size() > 0) {//是好友
                        scanAddFrienData = data.get(0);
                        String fremarks = scanAddFrienData.fremarks;
                        String isAI = scanAddFrienData.isai;
                        if ("Y".equals(isAI)) {
                            hideMenu();
                        } else {
                            showMenu();
                        }
                        label = scanAddFrienData.ftype;
                        if (!TextUtils.isEmpty(label)) {
                            switch (label) {
                                case "N":
                                    tvLabel.setText("未分组");
                                    break;
                                case "J":
                                    tvLabel.setText("工作类");
                                    break;
                                case "S":
                                    tvLabel.setText("生活类");
                                    break;
                            }
                        } else {
                            tvLabel.setText("未分组");
                        }
                        contactType = "message";
                        friendTypeShow();
                        String friendNickName = scanAddFrienData.friendNickName;
                        tvFriendName.setText(TextUtils.isEmpty(fremarks) ? friendNickName : fremarks);
                    } else {//不是好友
                        if (isApply) {
                            contactType = "approved";
                        } else {
                            contactType = "add";
                        }
                        friendTypeShow();
                    }
                    if (init) {
                        //获取用户信息
                        requestGetUserInfo();
                    }

                } else {
                    hideLoading();
                }
            }

            @Override
            public void onError(Response<ScanAddFriendB> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        });
    }

    /**
     * 查询对方列表中时候有我
     *
     * @param fId
     * @param uId
     */
    private void requestFriendListContactMine(String fId, String uId) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_friend_id", fId);
        params.put("user_id", uId);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_IS_FRIEND, params, this, new JsonCallback<ScanAddFriendB>(ScanAddFriendB.class) {
            @Override
            public void onSuccess(Response<ScanAddFriendB> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                ScanAddFriendB body = response.body();
                if (body.code == 1) {
                    ArrayList<ScanAddFriendB.ScanAddFrienData> data = body.getData();
                    if (data == null || data.size() <= 0) {//不是好友
                        isFriends = false;
                        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
                        Map<String, Object> params = new HashMap<>();
                        params.put("username", userInfoData.nickName);
                        params.put("avatar", userInfoData.headImg);
                        params.put("userid", String.valueOf(userInfoData.uid));
                        params.put("content", userInfoData.userPhone);
                        params.put("type", 10);
                        params.put("time", String.valueOf(System.currentTimeMillis()));
                        if (isNearBy) {
                            params.put("isNearBy", "Y");
                            params.put("chattype", 1);
                        }
                        String jsonApply = new Gson().toJson(params);
                        doSendQosMessage(jsonApply, String.valueOf(contactId));
                        toast.toastShow("好友请求申请已发出,请耐心等待回应");
                        finish();
                    } else {//是好友
                        isFriends = true;
                        requestAddToFriendList(String.valueOf(friendInfoData.id), String.valueOf(userInfoData.uid));
                    }
                }
            }

            @Override
            public void onError(Response<ScanAddFriendB> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        });
    }


}
