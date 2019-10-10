package com.daoyu.chat.module.login.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.event.ChatListUpdateEvent;
import com.daoyu.chat.module.home.activity.CommonWebViewActivity;
import com.daoyu.chat.module.home.activity.MainActivity;
import com.daoyu.chat.module.im.module.ChatTable;
import com.daoyu.chat.module.im.module.IMConstant;
import com.daoyu.chat.module.login.bean.PrivateHelpBean;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.system.utils.SystemUtil;
import com.daoyu.chat.utils.ToolsUtil;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;
import com.yanzhenjie.permission.runtime.Permission;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class RegisteredActivity extends BaseTitleActivity {

    @BindView(R.id.edit_nick)
    EditText editNick;

    @BindView(R.id.edit_phone)
    EditText editPhone;

    @BindView(R.id.edit_verification_code)
    EditText editVerificationCode;

    @BindView(R.id.edit_password)
    EditText editPassword;

    @BindView(R.id.edit_password_confirm)
    EditText editPasswordConfirm;

    @BindView(R.id.tv_registered)
    TextView tvRegistered;

    @BindView(R.id.tv_protocol)
    TextView tvProtocol;

    @BindView(R.id.tv_get_verification_code)
    TextView tvGetVerificationCode;

    @BindView(R.id.tv_country)
    TextView tvCountry;

    @BindView(R.id.tv_re_locate)
    TextView tvReLocate;

    @BindView(R.id.edit_invitation_code)
    EditText editInvitationCode;

    @BindView(R.id.tv_personal)
    TextView tvPersonal;//隐私协议

    private CountDownTimer countDownTimer;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_registered;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("注册");
        tvRegistered.setOnClickListener(this);
        tvProtocol.setOnClickListener(this);
        tvGetVerificationCode.setOnClickListener(this);
        tvReLocate.setOnClickListener(this);

        tvPersonal.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_get_verification_code:
                String mobilePhone = editPhone.getText().toString();
                if (TextUtils.isEmpty(mobilePhone)) {
                    Toast.makeText(this, $$(R.string.text_please_input_phone_number), Toast.LENGTH_LONG).show();
                    return;
                }
                if (!mobilePhone.startsWith("1")) {
                    Toast.makeText(this, $$(R.string.text_phone_number_error), Toast.LENGTH_LONG).show();
                    return;
                }
                if (mobilePhone.length() != 11) {
                    Toast.makeText(this, $$(R.string.text_phone_number_error), Toast.LENGTH_LONG).show();
                    return;
                }
                countDownTimerStart();
                requestSendVerificationCode(mobilePhone);
                break;
            case R.id.iv_header:
                requestPermission(Permission.Group.CAMERA, Permission.Group.STORAGE);
                break;
            case R.id.tv_registered:
                String mobile = editPhone.getText().toString();
                if (TextUtils.isEmpty(mobile)) {
                    Toast.makeText(this, $$(R.string.text_please_input_phone_number), Toast.LENGTH_LONG).show();
                    return;
                }
                if (!mobile.startsWith("1")) {
                    Toast.makeText(this, $$(R.string.text_phone_number_error), Toast.LENGTH_LONG).show();
                    return;
                }
                if (mobile.length() != 11) {
                    Toast.makeText(this, $$(R.string.text_phone_number_error), Toast.LENGTH_LONG).show();
                    return;
                }
                String verificationCode = editVerificationCode.getText().toString();
                if (TextUtils.isEmpty(verificationCode)) {
                    Toast.makeText(this, $$(R.string.text_verification_code_not_null), Toast.LENGTH_LONG).show();
                    return;
                }
                String password = editPassword.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(this, $$(R.string.text_please_input_pwd), Toast.LENGTH_LONG).show();
                    return;
                }
                String confirmPwd = editPasswordConfirm.getText().toString();
                if (TextUtils.isEmpty(confirmPwd)) {
                    Toast.makeText(this, $$(R.string.text_please_input_confirm_pwd), Toast.LENGTH_LONG).show();
                    return;
                }
                if (!password.equals(confirmPwd)) {
                    Toast.makeText(this, $$(R.string.text_not_inconsistent_pwd), Toast.LENGTH_LONG).show();
                    return;
                }
                String invitactionCode = editInvitationCode.getText().toString();
                if (!TextUtils.isEmpty(invitactionCode)) {
                    if (invitactionCode.length() != 8) {
                        toast.toastShow("请输入正确的邀请码");
                        return;
                    }
                }
                requestDetectionMobile(mobile, verificationCode, password, invitactionCode);
                break;
            case R.id.tv_protocol:
                Intent intent = new Intent(this, CommonWebViewActivity.class);
                intent.putExtra(Constant.WEB_VIEW_URL, "http://futruedao.com:8080/nq/guide/daoyu");
                intent.putExtra(Constant.WEB_VIEW_TITLE, "用户注册协议");
                startActivity(intent);
                break;
            case R.id.tv_personal:
                Intent intent1 = new Intent(this, CommonWebViewActivity.class);
                intent1.putExtra(Constant.WEB_VIEW_URL, "http://www.futruedao.com/agreement.html");
                intent1.putExtra(Constant.WEB_VIEW_TITLE, "隐私政策");
                startActivity(intent1);
                break;
        }
    }


    /**
     * 短信验证码定时器
     */
    private void countDownTimerStart() {
        tvGetVerificationCode.setEnabled(false);
        if (countDownTimer != null) {
            countDownTimer.onFinish();
            countDownTimer = null;
        }
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (tvGetVerificationCode == null) return;
                String format = String.format($$(R.string.forget_password_countdown_seconds), String.valueOf(millisUntilFinished / 1000));
                tvGetVerificationCode.setText(format);
            }

            public void onFinish() {
                if (tvGetVerificationCode == null) return;
                tvGetVerificationCode.setText($$(R.string.text_reset_get_code));
                tvGetVerificationCode.setEnabled(true);
            }
        }.start();
    }


    /**
     * 请求短信验证码
     *
     * @param mobile 手机号
     */
    private void requestSendVerificationCode(String mobile) {
        CHttpUtils.getInstance().requestGetFormServer(UrlConfig.URL_SEND_VERIFICATION_CODE + mobile, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    toast.toastShow(body.msg);
                } else {
                    toast.toastShow(body.msg);
                }
            }
        });
    }

    /**
     * 检测手机是否已注册
     *
     * @param mobile
     */
    private void requestDetectionMobile(String mobile, String verificationCode, String password, String invitactionCode) {
        showLoading($$(R.string.text_wait), false);

        CHttpUtils.getInstance().requestGetFormServer(UrlConfig.URL_DETECTION_MOBILE + mobile, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    requestRegistered(mobile, verificationCode, password, invitactionCode);
                } else {
                    hideLoading();
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
     * 注册接口
     *
     * @param mobile           手机号
     * @param verificationCode 短信验证码
     * @param password         密码 MD5
     */
    private void requestRegistered(String mobile, String verificationCode, String password, String invitactionCode) {
        showLoading($$(R.string.text_wait), false);
        Map<String, Object> params = new HashMap<>();
        params.put("mobile", mobile);
        String md5 = ToolsUtil.md5(password);
        params.put("password", md5);
        params.put("code", verificationCode);
        params.put("agent", "ANDROID");
        params.put("udid", SystemUtil.getIMEI(this));
        params.put("appversion", 1);
        params.put("cmd", "register");
        params.put("osversion", SystemUtil.getSystemVersion());
        params.put("appid", "com.daoyu.chat");
        if(!TextUtils.isEmpty(invitactionCode)){
            params.put("inviteCode",invitactionCode);
        }
        params.put("addressX",0);
        params.put("addressY",0);


        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_REGISTERED, params, this, new JsonCallback<UserBean>(UserBean.class) {
            @Override
            public void onSuccess(Response<UserBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                UserBean body = response.body();
                if (body.success) {
                    toast.toastShow(body.msg);
                    UserBean.UserData userData = body.data;
                    BaseApplication.getInstance().saveUserInfo(userData);
                    requestPrivateHelpId(String.valueOf(userData.uid), userData);
                } else {
                    hideLoading();
                    toast.toastShow(body.msg);
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
                                    startActivity(new Intent(RegisteredActivity.this, MainActivity.class));
                                    finish();
                                }
                            });
                        } else {
                            ChatTable chatTable = chatTables.get(0);
                            chatTable.chat_id = uid + "DL" + data.id;
                            chatTable.is_private = true;
                            chatTable.saveOrUpdateAsync("is_private = ?", "1").listen(success -> {
                                hideLoading();
                                startActivity(new Intent(RegisteredActivity.this, MainActivity.class));
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
                                startActivity(new Intent(RegisteredActivity.this, MainActivity.class));
                                finish();
                            }
                        });
                    } else {
                        ChatTable chatTable = chatTables.get(0);
                        chatTable.chat_id = uid + "DL" + "-123456";
                        chatTable.is_private = true;
                        chatTable.saveOrUpdateAsync("is_private = ?", "1").listen(success -> {
                            hideLoading();
                            startActivity(new Intent(RegisteredActivity.this, MainActivity.class));
                            finish();
                        });

                    }
                }
            }
        });
    }
}
