package com.daoyu.chat.module.home.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.CommonTextWatcher;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.chat.bean.SendRedBagBean;
import com.daoyu.chat.module.home.dialog.PaymentPwdDialog;
import com.daoyu.chat.module.im.module.ChatTable;
import com.daoyu.chat.module.im.module.IMConstant;
import com.daoyu.chat.module.im.module.MessageDetailTable;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.system.activity.SettingPaymentPwdActivity;
import com.daoyu.chat.utils.ToolsUtil;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.dy.dyim.android.core.LocalUDPDataSender;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

import org.litepal.LitePal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 扫码给钱
 */
public class ScanSendMoneyActivity extends BaseTitleActivity implements PaymentPwdDialog.OnPaymentResult {

    @BindView(R.id.edit_rad_bag_money)
    EditText editRadBagMoney;

    @BindView(R.id.edit_greetings)
    EditText editGreetings;

    @BindView(R.id.tv_money)
    TextView tvMoney;

    @BindView(R.id.tv_send)
    TextView tvSend;
    @BindView(R.id.tv_money_error)
    TextView tvMoneyError;
    private String friendId;
    private PaymentPwdDialog paymentPwdDialog;
    private UserBean.UserData userInfoData;
    private String greetings;
    private String money;
    private String friendName="";
    private String friendHeader="";

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_send_scan_money;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("扫一扫转账");
        Intent intent = getIntent();
        friendId = intent.getStringExtra("fId");
        //friendName = intent.getStringExtra(Constant.CONTACT_NAME);
        //friendHeader = intent.getStringExtra(Constant.FRIEND_HEADER);
        if (TextUtils.isEmpty(friendId)) finish();
        editRadBagMoney.addTextChangedListener(new CommonTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().contains(".")) {
                    if (s.toString().indexOf(".") > 3) {
                        s = s.toString().subSequence(0, 3) + s.toString().substring(s.toString().indexOf("."));
                        editRadBagMoney.setText(s);
                        editRadBagMoney.setSelection(3);
                    }
                } else {
                    if (s.toString().length() > 3) {
                        s = s.toString().subSequence(0, 3);
                        editRadBagMoney.setText(s);
                        editRadBagMoney.setSelection(3);
                    }
                }
                // 判断小数点后只能输入两位
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        editRadBagMoney.setText(s);
                        editRadBagMoney.setSelection(s.length());
                    }
                }
                //如果第一个数字为0，第二个不为点，就不允许输入
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editRadBagMoney.setText(s.subSequence(0, 1));
                        editRadBagMoney.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String money = s.toString();
                if (TextUtils.isEmpty(money)) {
                    tvMoney.setText("0.00");
                    tvMoneyError.setVisibility(View.GONE);
                    tvSend.setEnabled(false);
                } else {
                    String m = editRadBagMoney.getText().toString().trim();
                    if (m.length() == 1 && ".".equals(m)) {
                        s.clearSpans();
                        editRadBagMoney.setText("");
                        return;
                    }
                    Float moneys = Float.valueOf(m);
                    if (moneys > 200) {
                        tvSend.setEnabled(false);
                        tvMoneyError.setVisibility(View.VISIBLE);
                    } else if (moneys == 0.00) {
                        tvSend.setEnabled(false);
                    } else {
                        tvMoneyError.setVisibility(View.GONE);
                        tvSend.setEnabled(true);
                    }
                    tvMoney.setText(m);
                }
            }
        });
        tvSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_send:
                money = editRadBagMoney.getText().toString();
                greetings = editGreetings.getText().toString();
                if (TextUtils.isEmpty(greetings)) {
                    greetings = "恭喜发财,大吉大利!";
                }
                userInfoData = BaseApplication.getInstance().getUserInfoData();
                String paySign = userInfoData.paySign;
                if (TextUtils.isEmpty(paySign)) {
                    //设置支付密码
                    startActivity(new Intent(ScanSendMoneyActivity.this, SettingPaymentPwdActivity.class));
                    return;
                }
                requestVerificationPayPassword(money);
                break;

        }
    }

    private void requestSendMoney(String amount, String remarks, String friendId, String uid) {
        showLoading("请稍后...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        //params.put("remarks", remarks);
        params.put("user_friend_id", friendId);
        params.put("user_id", uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_SCAN_SEND_MONEY, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    if (body.data == null) return;
                    toast.toastShow(body.msg);
                    finish();
                } else {
                    toast.toastShow(body.msg);
                }
            }
        });

    }

    private void requestVerificationPayPassword(String money) {
        if (paymentPwdDialog != null) {
            paymentPwdDialog.dismissAllowingStateLoss();
        }
        paymentPwdDialog = PaymentPwdDialog.getInstance(money);
        if (!paymentPwdDialog.isAdded()) {
            paymentPwdDialog.show(getSupportFragmentManager(), "paymentPwdDialog");
        } else {
            paymentPwdDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onPayResult(String result) {
        String paySign = userInfoData.paySign;
        if (paySign.equals(ToolsUtil.md5(result))) {
            requestSendMoney(money, greetings, friendId, String.valueOf(userInfoData.uid));
        } else {
            //e10adc3949ba59abbe56e057f20f883e
            toast.toastShow("支付密码错误");
        }

    }


    /**
     * 发送红包消息
     *
     * @param message {"userid":"6","userImage":"http:\/\/image.futruedao.com\/Fu8l7Ybf9BjUYQ-ZEK4MmEQqCBgp","time":"1565772882099",
     *                "content":"90","chattype":1,"username":"招聘扣扣给我考虑考虑要不要继续加","type":7,"redPackage":"恭喜发财，大吉大利"}
     */
    private void sendMoneyMessage(String message, String redId) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        String uid = String.valueOf(userInfoData.uid);
        String headImg = userInfoData.headImg;
        String time = String.valueOf(System.currentTimeMillis());

        Map<String, Object> params = new HashMap<>();
        params.put("userid", uid);
        params.put("userImage", headImg);
        params.put("time", time);
        params.put("content", redId);
        params.put("chattype", 1);
        params.put("username", userInfoData.nickName);
        params.put("type", 7);
        params.put("redPackage", message);


        MessageDetailTable sendMessage = new MessageDetailTable();
        sendMessage.chat_id = userInfoData.uid + "DL" + friendId;
        sendMessage.message_type = IMConstant.MessageType.REDPAPER;
        sendMessage.message_state = IMConstant.MessageStatus.DELIVERING;
        sendMessage.avatar = headImg;
        sendMessage.user_id = uid;
        sendMessage.chat_type = 1;
        sendMessage.red_id = redId;
        sendMessage.message_time = time;
        sendMessage.message = message;

        sendMessage.saveAsync().listen(success -> {
            if (success) {
                Log.d("TAG", "保存成功");
            }
        });
        List<ChatTable> chatTables = LitePal.where("chat_id = ?", userInfoData.uid + "DL" + friendId).find(ChatTable.class);

        if (chatTables == null || chatTables.size() <= 0) {
            ChatTable chatTable = new ChatTable();
            chatTable.mobile = userInfoData.userPhone;
            chatTable.chat_id = sendMessage.chat_id;
            chatTable.number = 0;
            chatTable.is_read = true;
            chatTable.last_message = "[" + message + "]";
            chatTable.avatar = friendHeader;
            chatTable.user_id = friendId;
            chatTable.username = friendName;
            chatTable.top = false;
            chatTable.current_id = String.valueOf(userInfoData.uid);
            chatTable.message_type = IMConstant.MessageType.REDPAPER;
            chatTable.message_time = time;
            chatTable.saveAsync().listen(success -> {
                if (success) {
                    Log.d("TAG", "保存成功!");
                }
            });
        } else {
            ChatTable chatTable = chatTables.get(0);
            chatTable.last_message = "[" + message + "]";
            chatTable.is_read = true;
            chatTable.number = 0;
            chatTable.username = friendName;
            chatTable.avatar = friendHeader;
            chatTable.current_id = String.valueOf(userInfoData.uid);
            chatTable.message_time = time;
            chatTable.saveAsync().listen(success -> Log.d("TAG", "更新成功!"));

        }
        doSendMessage(new Gson().toJson(params), friendId);
    }

    @SuppressLint("StaticFieldLeak")
    private void doSendMessage(String msg, String friendId) {
        new LocalUDPDataSender.SendCommonDataAsync(this, msg, friendId) {
            @Override
            protected void onPostExecute(Integer code) {
                if (code == 0) {
                    finish();
                }
            }
        }.execute();
    }
}
