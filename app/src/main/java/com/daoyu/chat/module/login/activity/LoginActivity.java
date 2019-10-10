package com.daoyu.chat.module.login.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseActivity;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.event.ChatListUpdateEvent;
import com.daoyu.chat.module.home.activity.MainActivity;
import com.daoyu.chat.module.home.activity.UsageGuidelinesActivity;
import com.daoyu.chat.module.im.module.ChatTable;
import com.daoyu.chat.module.im.module.IMConstant;
import com.daoyu.chat.module.login.bean.PrivateHelpBean;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.system.utils.SystemUtil;
import com.daoyu.chat.utils.SharedPreferenceUtil;
import com.daoyu.chat.utils.ToolsUtil;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.edit_pwd)
    EditText editPwd;
    @BindView(R.id.tv_forget_password)
    TextView tvForgetPassword;
    @BindView(R.id.tv_registered)
    TextView tvRegistered;
    @BindView(R.id.tv_login)
    TextView tvLogin;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initEvent() {
        Intent intent = getIntent();
        if (intent != null) {
            String mobile = intent.getStringExtra(Constant.PHONE_NUMBER);
            if (TextUtils.isEmpty(mobile)) {
                editPhone.setText(mobile);
            }
        }
        tvForgetPassword.setOnClickListener(this);
        tvRegistered.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        SharedPreferenceUtil.getInstance().putBoolean(Constant.IS_FIRST_INSTALLED, false);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_forget_password:
                startActivity(new Intent(this, ForgetPasswordActivity.class));
                break;
            case R.id.tv_registered:
                startActivity(new Intent(this, RegisteredActivity.class));
                break;
            case R.id.tv_login:
                String phoneNumber = editPhone.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)) {
                    toast.toastShow($$(R.string.text_please_input_phone_number));
                    return;
                }
                if (phoneNumber.length() != 11) {
                    toast.toastShow($$(R.string.text_phone_number_error));
                    return;
                }
                String password = editPwd.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    toast.toastShow($$(R.string.text_please_input_pwd));
                    return;
                }
                requestLogin(phoneNumber, password);
                break;
        }
    }

    /**
     * 请求登录接口
     *
     * @param mobile   手机号码
     * @param password 密码
     */
    private void requestLogin(String mobile, String password) {
        showLoading($$(R.string.text_loginning), false);
        Map<String, Object> params = new HashMap<>();
        params.put("mobile", mobile);
        params.put("password", ToolsUtil.md5(password));
        params.put("agent", "ANDROID");
        params.put("udid", SystemUtil.getIMEI(this));
        params.put("appversion", 1);
        params.put("cmd", "login");
        params.put("osversion", SystemUtil.getSystemVersion());
        params.put("appid", "com.daoyu.chat");
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_LOGIN, params, this, new JsonCallback<UserBean>(UserBean.class) {
            @Override
            public void onSuccess(Response<UserBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                UserBean userBean = response.body();
                if (userBean.success) {
                    UserBean.UserData userData = userBean.data;
                    BaseApplication.getInstance().saveUserInfo(userData);
                    requestPrivateHelpId(String.valueOf(userData.uid), userData);
                } else {
                    hideLoading();
                    toast.toastShow(userBean.msg);
                }
            }

            @Override
            public void onError(Response<UserBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        });
    }

    private void requestPrivateHelpId(String uid, UserBean.UserData userData) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_GET_FRIEND_AI, params, this, new JsonCallback<PrivateHelpBean>(PrivateHelpBean.class) {
            @Override
            public void onSuccess(Response<PrivateHelpBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                PrivateHelpBean body = response.body();
                if (body.success) {
                    PrivateHelpBean.PrivateHelpData data = body.data;
                    if (data != null) {
                        int id = data.id;
                        userData.private_id = String.valueOf(id);
                        BaseApplication.getInstance().saveUserInfo(userData);
                        List<ChatTable> chatTables = LitePal.where("is_private = ?", "1").find(ChatTable.class);
                        if (chatTables == null || chatTables.size() <= 0) {
                            ChatTable chatTable = new ChatTable();
                            chatTable.chat_id = uid + "DL" + data.id;
                            chatTable.is_read = true;
                            chatTable.last_message = "欢迎来到红包王国";
                            chatTable.avatar = "";
                            chatTable.number = 0;
                            chatTable.user_id = String.valueOf(id);
                            chatTable.username = "私人特助";
                            chatTable.top = true;
                            chatTable.current_id = uid;
                            chatTable.is_private = true;
                            chatTable.message_type = IMConstant.MessageType.TEXT;
                            chatTable.message_time = String.valueOf(System.currentTimeMillis());
                            chatTable.saveAsync().listen(success -> {
                                hideLoading();
                                if (success) {
                                    EventBus.getDefault().post(new ChatListUpdateEvent());
                                    if (SharedPreferenceUtil.getInstance().getBoolean(Constant.IS_FIRST_IN, true)){
                                        startActivity(new Intent(LoginActivity.this, UsageGuidelinesActivity.class));
                                    }else {
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    }
                                    finish();
                                }
                            });
                        } else {
                            ChatTable chatTable = chatTables.get(0);
                            chatTable.chat_id = uid + "DL" + data.id;
                            chatTable.is_private = true;
                            chatTable.saveOrUpdateAsync("is_private = ?", "1").listen(success -> {
                                hideLoading();
                                if (SharedPreferenceUtil.getInstance().getBoolean(Constant.IS_FIRST_IN, true)){
                                    startActivity(new Intent(LoginActivity.this, UsageGuidelinesActivity.class));
                                }else {
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                }
                                finish();
                            });
                        }
                    }

                } else {
                    List<ChatTable> chatTables = LitePal.where("is_private = ?", "1").find(ChatTable.class);
                    userData.private_id = "-123456";
                    BaseApplication.getInstance().saveUserInfo(userData);
                    if (chatTables == null || chatTables.size() <= 0) {
                        ChatTable chatTable = new ChatTable();
                        chatTable.chat_id = uid + "DL" + "-123456";
                        chatTable.is_read = true;
                        chatTable.last_message = "欢迎来到红包王国";
                        chatTable.avatar = "";
                        chatTable.number = 0;
                        chatTable.user_id = "-123456";
                        chatTable.username = "私人特助";
                        chatTable.top = true;
                        chatTable.is_private = true;
                        chatTable.current_id = uid;
                        chatTable.message_type = IMConstant.MessageType.TEXT;
                        chatTable.message_time = String.valueOf(System.currentTimeMillis());
                        chatTable.saveAsync().listen(success -> {
                            hideLoading();
                            if (success) {
                                EventBus.getDefault().post(new ChatListUpdateEvent());
                                if (SharedPreferenceUtil.getInstance().getBoolean(Constant.IS_FIRST_IN, true)){
                                    startActivity(new Intent(LoginActivity.this, UsageGuidelinesActivity.class));
                                }else {
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                }
                                finish();
                            }
                        });
                    } else {
                        ChatTable chatTable = chatTables.get(0);
                        chatTable.chat_id = uid + "DL" + "-123456";
                        chatTable.is_private = true;
                        chatTable.saveOrUpdateAsync("is_private = ?", "1").listen(success -> {
                            hideLoading();
                            if (SharedPreferenceUtil.getInstance().getBoolean(Constant.IS_FIRST_IN, true)){
                                startActivity(new Intent(LoginActivity.this, UsageGuidelinesActivity.class));
                            }else {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                            finish();
                        });

                    }
                }
            }
        });
    }


}
