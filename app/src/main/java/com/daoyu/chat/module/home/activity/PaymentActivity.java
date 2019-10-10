package com.daoyu.chat.module.home.activity;

import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.CommonTextWatcher;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.home.bean.QrOrderBean;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.bean.WeChatBean;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 付款
 */
public class PaymentActivity extends BaseTitleActivity implements RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R.id.edit_payment_amount)
    EditText editPaymentAmount;
    @BindView(R.id.tv_change)
    TextView tvChange;
    @BindView(R.id.tv_change_description)
    TextView tvChangeDescription;
    @BindView(R.id.tv_amounts_payable)
    TextView tvAmountsPayable;
    @BindView(R.id.tv_discount)
    TextView tvDiscount;
    @BindView(R.id.tv_confirm_payment)
    TextView tvConfirmPayment;
    @BindView(R.id.rg_payment_way)
    RadioGroup rgPaymentWay;

    private QrOrderBean.QrOrderData data;
    private double bSolePrice;
    private double creditTotal;
    private UserBean.UserData userInfoData;
    private double discountOrg;
    private IWXAPI api;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_payment;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("付款");
        Intent intent = getIntent();
        //二维码密文
        api = WXAPIFactory.createWXAPI(this, "wxcd8c9aa2163b6d9c");
        api.registerApp("wxcd8c9aa2163b6d9c");
        tvConfirmPayment.setOnClickListener(this);
        rgPaymentWay.setOnCheckedChangeListener(this);
        rgPaymentWay.check(R.id.rb_wechat);
        data = intent.getParcelableExtra(Constant.QR_VAL);
        if (data == null) return;
        tvMerchantName.setText(data.bName);

        bSolePrice = data.bSolePrice * 0.1;//折扣率

        userInfoData = BaseApplication.getInstance().getUserInfoData();
        creditTotal = userInfoData.creditTotal / 100;

        tvChange.setText(String.format("零钱:%.2f元", creditTotal));
        tvChangeDescription.setText("使用零钱最高抵扣：- ¥" + 0.00);
        tvDiscount.setText("已抵扣:-0.00");
        tvAmountsPayable.setText("0.00");
        rgPaymentWay.check(R.id.rb_chat);

        editPaymentAmount.addTextChangedListener(new CommonTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().contains(".")) {
                    if (s.toString().indexOf(".") > 5) {
                        s = s.toString().subSequence(0, 5) + s.toString().substring(s.toString().indexOf("."));
                        editPaymentAmount.setText(s);
                        editPaymentAmount.setSelection(5);
                    }
                } else {
                    if (s.toString().length() > 5) {
                        s = s.toString().subSequence(0, 5);
                        editPaymentAmount.setText(s);
                        editPaymentAmount.setSelection(5);
                    }
                }
                // 判断小数点后只能输入两位
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        editPaymentAmount.setText(s);
                        editPaymentAmount.setSelection(s.length());
                    }
                }
                //如果第一个数字为0，第二个不为点，就不允许输入
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editPaymentAmount.setText(s.subSequence(0, 1));
                        editPaymentAmount.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String money = s.toString();
                if (TextUtils.isEmpty(money)) {
                    tvConfirmPayment.setEnabled(false);
                    tvChange.setText(String.format("零钱:%.2f元", creditTotal));
                    tvChangeDescription.setText("使用零钱最高抵扣：- ¥" + 0.00);
                    tvDiscount.setText("已抵扣:-0.00");
                    tvAmountsPayable.setText("0.00");
                } else {
                    if (rgPaymentWay.getCheckedRadioButtonId() != -1) {
                        tvConfirmPayment.setEnabled(true);
                    } else {
                        tvConfirmPayment.setEnabled(false);
                    }
                    double paymoney = Double.parseDouble(money);
                    if (paymoney == 0.00) {
                        tvChange.setText(String.format("零钱:%.2f元", creditTotal));
                        tvChangeDescription.setText("使用零钱最高抵扣：- ¥" + 0.00);
                        tvDiscount.setText("已抵扣:-0.00");
                        tvAmountsPayable.setText("0.00");
                    } else {
                        discountOrg = 0.0;
                        double topDiscount = paymoney * (1 - bSolePrice);
                        tvChangeDescription.setText(String.format("使用零钱最高抵扣：- ¥%.2f", topDiscount));

                        if (creditTotal > topDiscount) {
                            discountOrg = topDiscount;
                        } else {
                            discountOrg = creditTotal;
                        }
                        tvDiscount.setText(String.format("已抵扣:-¥%.2f", discountOrg));
                        double price = paymoney - discountOrg;
                        tvAmountsPayable.setText(String.format("%.2f", price));

                    }
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_confirm_payment:
                String money = editPaymentAmount.getText().toString();
                if (TextUtils.isEmpty(money)) {
                    toast.toastShow("请输入支付金额");
                }
                int checkedRadioButtonId = rgPaymentWay.getCheckedRadioButtonId();
                if (checkedRadioButtonId == -1) {
                    toast.toastShow("请选择支付方式");
                    return;
                }
                if (checkedRadioButtonId == R.id.rb_balance) {
                    toast.toastShow("当前不支持余额支付");
                    return;
                }
                String payMoney = tvAmountsPayable.getText().toString();
                switch (checkedRadioButtonId) {
                    case R.id.rb_wechat:
                        requestWeiChatOrder(Double.parseDouble(payMoney));
                        break;
                    case R.id.rb_alipay:
                        String alipayRemark = data.bName + ",支付宝线下支付" + Math.round(discountOrg * 100) + "元";
                        requestUrl(alipayRemark, "alipay");
                        break;
                    case R.id.rb_unionPa:
                        String unionRemark = data.bName + ",云闪付线下支付" + Math.round(discountOrg * 100) + "元";
                        requestUrl(unionRemark, "union");
                        break;
                }

                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        String amount = editPaymentAmount.getText().toString();
        if (TextUtils.isEmpty(amount)) {
            tvConfirmPayment.setEnabled(false);
        } else {
            tvConfirmPayment.setEnabled(true);
        }
    }

    private void requestWeiChatOrder(double priceAmount) {
        showLoading("请稍等...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("uid", userInfoData.uid);
        params.put("orderNumber", data.bOrderNumber);
        params.put("orderMoney", Math.round(priceAmount * 100));
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_WECHAT_ORDER, params, this, new JsonCallback<WeChatBean>(WeChatBean.class) {
            @Override
            public void onSuccess(Response<WeChatBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                WeChatBean body = response.body();
                if (body.success) {
                    WeChatBean.WeChatData data = body.getData();
                    if (data == null) {
                        toast.toastShow("下单支付时发生错误,请重试");
                        hideLoading();
                        return;
                    }
                    requestReceiveRed(data);

                } else {
                    hideLoading();
                }
            }

            @Override
            public void onError(Response<WeChatBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        });
    }


    private void requestReceiveRed(WeChatBean.WeChatData data) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userInfoData.uid);
        params.put("creditVal", Math.round(discountOrg * 100) + "");
        params.put("hongbaoId", 0);
        params.put("creditSta", 2);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_GET_RED_PACKAGE, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    hideLoading();
                    userInfoData.creditTotal = (double) body.data;
                    BaseApplication.getInstance().saveUserInfo(userInfoData);
                    boolean wxAppInstalled = api.isWXAppInstalled();
                    if (wxAppInstalled) {
                        wxPay(data.appid, data.partnerid, data.prepayid, data.noncestr, data.timestamp, data.packageX, data.sign, getPackageName());
                    } else {
                        toast.toastShow("当前未安装微信!");
                    }
                } else {
                    hideLoading();
                    toast.toastShow(body.msg);
                }
            }
        });

    }

    public void wxPay(String appId, String partnerId, String prepayId, String nonceStr, String timeStamp, String packageValue, String sign, String extData) {
        PayReq req = new PayReq();
        req.appId = appId;
        req.partnerId = partnerId;
        req.prepayId = prepayId;
        req.nonceStr = nonceStr;
        req.timeStamp = timeStamp;
        req.packageValue = packageValue;
        req.sign = sign;
        req.extData = extData;
        api.sendReq(req);

    }

    private void requestUrl(String remarks, String type) {
        int payWay = -1;
        switch (type) {
            case "alipay":
                payWay = 2;
                break;
            case "union":
                payWay = 3;
                break;
        }
        String payMoney = tvAmountsPayable.getText().toString();
        double parseDouble = Double.parseDouble(payMoney);
        Map<String, Object> params = new HashMap<>();
        params.put("uId", userInfoData.uid);
        params.put("orderNumber", data.bOrderNumber);
        params.put("body", remarks);
        params.put("orderMoney", Math.round(parseDouble * 100) + "");
        if (payWay!=-1){
            params.put("payWay", payWay);
        }
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_PAYMENT_ALIPAY_UNION, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    String url = (String) body.data;
                    if (url.contains("https://qr.95516.com")) {
                        String[] split = url.split("&dy=");
                        url = split[0];
                    }
                    switch (type) {
                        case "alipay":
                            jumpAlipay(url);
                            break;
                        case "union":
                            jumpUnion(url);
                            break;
                    }
                } else {
                    toast.toastShow(body.msg);
                }
            }
        });
    }

    private void jumpUnion(String url) {
        String urlSchemeUnion = url.replace("https", "chsp");
        Uri dataUnion = Uri.parse(urlSchemeUnion);
        Intent intentUnion = new Intent(Intent.ACTION_VIEW, dataUnion);
        intentUnion.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intentUnion);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PaymentActivity.this, "没有安装云闪付APP，请下载安装", Toast.LENGTH_SHORT).show();
        }
    }

    private void jumpAlipay(String url) {
        String urlSchemeAlipay = "alipays://platformapi/startapp?appId=20000067&url=" + url;
        Uri data = Uri.parse(urlSchemeAlipay);
        Intent intent = new Intent(Intent.ACTION_VIEW, data);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            toast.toastShow("没有安装支付宝APP，请下载安装");
        }
    }


}
