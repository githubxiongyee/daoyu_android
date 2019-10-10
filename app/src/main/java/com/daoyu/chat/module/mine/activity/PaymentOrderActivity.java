package com.daoyu.chat.module.mine.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.envelope.bean.CreateChangeB;
import com.daoyu.chat.module.envelope.bean.CreateOrderBean;
import com.daoyu.chat.module.home.bean.RedBagAdsBean;
import com.daoyu.chat.module.home.dialog.PaymentPwdDialog;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.bean.DefaultAddressBean;
import com.daoyu.chat.module.mine.bean.ShippingAddressData;
import com.daoyu.chat.module.mine.bean.WeChatBean;
import com.daoyu.chat.module.system.activity.AddAddressActivity;
import com.daoyu.chat.module.system.activity.AddressManageActivity;
import com.daoyu.chat.module.system.activity.SettingPaymentPwdActivity;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.utils.ToolsUtil;
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
import butterknife.ButterKnife;

public class PaymentOrderActivity extends BaseTitleActivity implements PaymentPwdDialog.OnPaymentResult {
    private static final int REQUEST_ADDRESS = 8945;
    private static final int REQUEST_ADDRESS_LIST = 9784;
    @BindView(R.id.tv_confirm_payment)
    TextView tvConfirmPayment;
    @BindView(R.id.tv_sign)
    TextView tvSign;
    @BindView(R.id.tv_price_amount)
    TextView tvPriceAmount;
    @BindView(R.id.tv_favorable)
    TextView tvFavorable;
    @BindView(R.id.cl_bottom)
    ConstraintLayout clBottom;
    @BindView(R.id.tv_add_address)
    TextView tvAddAddress;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_tel)
    TextView tvTel;
    @BindView(R.id.cl_address_info)
    ConstraintLayout clAddressInfo;
    @BindView(R.id.tv_order_number)
    TextView tvOrderNumber;
    @BindView(R.id.iv_goods_image)
    ImageView ivGoodsImage;
    @BindView(R.id.tv_goods_name)
    TextView tvGoodsName;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_number)
    TextView tvNumber;

    @BindView(R.id.tv_price_money)
    TextView tvPriceMoney;

    @BindView(R.id.tv_freight)
    TextView tvFreight;
    @BindView(R.id.tv_change)
    TextView tvChange;
    @BindView(R.id.tv_change_rule)
    TextView tvChangeRule;
    private RedBagAdsBean.RedBagData productInfo;
    private CreateChangeB.CreateChangeData changeInfo;

    private double priceAmount;
    private UserBean.UserData userInfoData;
    private int adid = -1;
    private double discount;


    double creditTotal;
    double originalPrice;

    private boolean isOrder = false;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_payment_order;
    }

    @Override
    protected void initEvent() {
        setToolBarColor(R.color.colorWhite);
        setCurrentTitle("确认支付");
        api = WXAPIFactory.createWXAPI(this, "wxcd8c9aa2163b6d9c");
        api.registerApp("wxcd8c9aa2163b6d9c");
        userInfoData = BaseApplication.getInstance().getUserInfoData();
        tvTitle.setTextColor(getResources().getColor(R.color.color_1A1A1A));
        titleBackIv.setImageResource(R.drawable.btn_back);
        tvConfirmPayment.setOnClickListener(this);
        tvAddAddress.setOnClickListener(this);
        clAddressInfo.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent == null) return;
        productInfo = intent.getParcelableExtra(Constant.PRODUCT_INFO);

        changeInfo = intent.getParcelableExtra(Constant.ORDER_INFO);

        if (changeInfo == null) {
            finish();
            return;
        }
        if (productInfo == null) {//订单
            isOrder = true;
        } else {//直接购买
            isOrder = false;
        }
        //获取默认地址
        requestDefaultAddress();
        //设置商品信息
        tvGoodsName.setText(isOrder ? changeInfo.proName : productInfo.productName);
        ImageUtils.setRoundCornerImageView(this, isOrder ? changeInfo.proImg : productInfo.thumbPicUrl, R.drawable.ic_placeholder, ivGoodsImage);
        originalPrice = isOrder ? changeInfo.orderPrice : productInfo.originalPrice;
        tvPrice.setText(String.format("¥%s", originalPrice));
        tvPriceMoney.setText(String.format("¥%s", originalPrice));
        tvFreight.setText("¥0.00");
        creditTotal = userInfoData.creditTotal / 100;
        double salePrice = isOrder ? changeInfo.salePrice : productInfo.salePrice;
        double difference = originalPrice - salePrice;//差价
        //折扣计算方式
        discount = 0.0;
        if (difference > creditTotal) {
            discount = creditTotal;
        } else {
            discount = difference;
        }
        //tvChange.setText(String.format("红包余额:%.2f元", creditTotal));
        tvChange.setText(String.format("零钱:%.2f元", creditTotal));

        //tvChangeRule.setText(String.format("-¥%.2f", originalPrice));
        tvChangeRule.setText(String.format("使用零钱最多抵扣:-¥%.2f", difference));

        tvOrderNumber.setText(String.format("订单编号:%s", isOrder ? changeInfo.orderNumber : changeInfo.bOrderNumber));
        priceAmount = originalPrice - discount;
        /*tvPriceAmount.setText("0.00");
        tvFavorable.setText(String.format("已抵扣¥%.2f", originalPrice));*/
        tvPriceAmount.setText(String.format("%.2f元", priceAmount));
        tvFavorable.setText(String.format("已抵扣¥%.2f", discount));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADDRESS) {
            if (resultCode == RESULT_OK) {
                if (data == null) return;
                ShippingAddressData shippingAddressData = data.getParcelableExtra(Constant.ADDRESS_BEAN);
                if (shippingAddressData == null) return;
                String aAddress = shippingAddressData.aAddress;
                adid = shippingAddressData.id;
                String[] split = aAddress.split("=\\+=");
                tvAddress.setText(split[0] + split[1]);
                tvName.setText(shippingAddressData.aNick);
                tvTel.setText(shippingAddressData.aPhone);
                clAddressInfo.setVisibility(View.VISIBLE);
                tvAddAddress.setVisibility(View.GONE);
            }
        } else if (requestCode == REQUEST_ADDRESS_LIST) {
            if (resultCode == RESULT_OK) {
                if (data == null) return;
                ShippingAddressData shippingAddressData = data.getParcelableExtra(Constant.ADDRESS_BEAN);
                if (shippingAddressData == null) return;
                String aAddress = shippingAddressData.aAddress;
                adid = shippingAddressData.id;
                String[] split = aAddress.split("=\\+=");
                tvAddress.setText(split[0] + split[1]);
                tvName.setText(shippingAddressData.aNick);
                tvTel.setText(shippingAddressData.aPhone);
                clAddressInfo.setVisibility(View.VISIBLE);
                tvAddAddress.setVisibility(View.GONE);

            }
        }
    }

    private void requestDefaultAddress() {
        if (userInfoData == null) return;
        showLoading("请稍后...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("uId", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_GET_DEFAULT_ADDRESS, params, this, new JsonCallback<DefaultAddressBean>(DefaultAddressBean.class) {
            @Override
            public void onSuccess(Response<DefaultAddressBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                DefaultAddressBean body = response.body();
                if (body.success) {
                    ShippingAddressData shippingAddressData = body.data;
                    if (shippingAddressData == null) {
                        tvAddAddress.setVisibility(View.VISIBLE);
                        clAddressInfo.setVisibility(View.GONE);
                        return;
                    }
                    adid = shippingAddressData.id;
                    String aAddress = shippingAddressData.aAddress;
                    String[] split = aAddress.split("=\\+=");
                    String area = split[0];
                    String address = split[1];
                    tvAddress.setText(area + address);
                    tvName.setText(shippingAddressData.aNick);
                    tvTel.setText(shippingAddressData.aPhone);
                    clAddressInfo.setVisibility(View.VISIBLE);
                    tvAddAddress.setVisibility(View.GONE);
                } else {
                    tvAddAddress.setVisibility(View.VISIBLE);
                    clAddressInfo.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Response<DefaultAddressBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        });
    }

    private PaymentPwdDialog paymentPwdDialog;

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

    @BindView(R.id.rg_payment_way)
    RadioGroup rgPaymentWay;

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_confirm_payment:
                /*if (originalPrice > creditTotal) {
                    toast.toastShow("红包金额不足");
                } else {
                    if (adid == -1) {
                        toast.toastShow("请选择地址");
                        return;
                    }
                    userInfoData = BaseApplication.getInstance().getUserInfoData();
                    String paySign = userInfoData.paySign;
                    if (TextUtils.isEmpty(paySign)) {
                        //设置支付密码
                        startActivity(new Intent(PaymentOrderActivity.this, SettingPaymentPwdActivity.class));
                        return;
                    }
                    requestVerificationPayPassword("0.00");

                }*/
                if (adid == -1) {
                    toast.toastShow("请选择地址");
                    return;
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
                switch (checkedRadioButtonId) {
                    case R.id.rb_wechat:
                        requestWeiChatOrder();
                        break;
                    case R.id.rb_alipay:
                        requestBindAddress(null, false);

                        break;
                    case R.id.rb_unionPa:
                        requestBindAddress(null, false);

                        break;
                }
                break;

            case R.id.tv_add_address:
                Intent intent = new Intent(this, AddAddressActivity.class);
                intent.putExtra(Constant.PAYMENT_INTO, true);
                startActivityForResult(intent, REQUEST_ADDRESS);
                break;
            case R.id.cl_address_info:
                Intent intentAddress = new Intent(this, AddressManageActivity.class);
                intentAddress.putExtra(Constant.PAYMENT_INTO, true);
                startActivityForResult(intentAddress, REQUEST_ADDRESS_LIST);
                break;
        }
    }

    private void requestWeiChatOrder() {
        showLoading("请稍等...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("uid", userInfoData.uid);
        params.put("orderNumber", isOrder ? changeInfo.orderNumber : changeInfo.bOrderNumber);
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
                    requestBindAddress(data, true);

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

    private void requestBindAddress() {
        Map<String, Object> params = new HashMap<>();
        params.put("orderNumber", isOrder ? changeInfo.orderNumber : changeInfo.bOrderNumber);
        params.put("adid", adid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_BIND_ADDRESS, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    //红包兑换
                    requestRedPackageChange();
                } else {
                    toast.toastShow(body.msg);
                    hideLoading();
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

    private void requestRedPackageChange() {
        Map<String, Object> params = new HashMap<>();
        params.put("orderNumber", isOrder ? changeInfo.orderNumber : changeInfo.bOrderNumber);
        params.put("orderCredit", originalPrice * 100);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_RED_PACKAGE_CHANGE, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    toast.toastShow("兑换成功");
                    finish();
                } else {
                    toast.toastShow(body.msg);
                    hideLoading();
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

    private IWXAPI api;

    private void requestBindAddress(WeChatBean.WeChatData data, boolean isWeichat) {
        Map<String, Object> params = new HashMap<>();
        params.put("orderNumber", isOrder ? changeInfo.orderNumber : changeInfo.bOrderNumber);
        params.put("adid", adid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_BIND_ADDRESS, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    if (discount == 0) {
                        if (isWeichat) {
                            hideLoading();
                            boolean wxAppInstalled = api.isWXAppInstalled();
                            if (wxAppInstalled) {
                                wxPay(data.appid, data.partnerid, data.prepayid, data.noncestr, data.timestamp, data.packageX, data.sign, getPackageName());
                            } else {
                                toast.toastShow("当前未安装微信!");
                            }
                        } else {
                            int checkedRadioButtonId = rgPaymentWay.getCheckedRadioButtonId();
                            if (checkedRadioButtonId == R.id.rb_alipay) {
                                String alipayRemark = "支付宝线上支付" + Math.round(priceAmount * 100) + "元";
                                requestUrl(alipayRemark, "alipay");
                            } else {
                                String unionRemark = "云闪付线下支付" + Math.round(priceAmount * 100) + "元";
                                requestUrl(unionRemark, "union");
                            }
                        }

                    } else {
                        requestReceiveRed(data, isWeichat);

                    }
                } else {
                    toast.toastShow(body.msg);
                    hideLoading();
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

    private void requestReceiveRed(WeChatBean.WeChatData data, boolean isWeichat) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userInfoData.uid);
        params.put("creditVal", Math.round(discount * 100) + "");
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
                    if (isWeichat) {
                        boolean wxAppInstalled = api.isWXAppInstalled();
                        if (wxAppInstalled) {
                            wxPay(data.appid, data.partnerid, data.prepayid, data.noncestr, data.timestamp, data.packageX, data.sign, getPackageName());
                        } else {
                            toast.toastShow("当前未安装微信!");
                        }
                    } else {
                        int checkedRadioButtonId = rgPaymentWay.getCheckedRadioButtonId();
                        if (checkedRadioButtonId == R.id.rb_alipay) {
                            String alipayRemark = "支付宝线上支付" + Math.round(priceAmount * 100) + "元";
                            requestUrl(alipayRemark, "alipay");
                        } else {
                            String unionRemark = "云闪付线下支付" + Math.round(priceAmount * 100) + "元";
                            requestUrl(unionRemark, "union");
                        }
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

        finish();
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
            Toast.makeText(PaymentOrderActivity.this, "没有安装云闪付APP，请下载安装", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestUrl(String remarks, String type) {//支付宝2 银联 3 微信 1
        int payWay = -1;
        switch (type) {
            case "alipay":
                payWay = 2;
                break;
            case "union":
                payWay = 3;
                break;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("uId", userInfoData.uid);
        params.put("orderNumber", isOrder ? changeInfo.orderNumber : changeInfo.bOrderNumber);
        params.put("body", remarks);
        params.put("orderMoney", Math.round(priceAmount * 100));
        if (payWay != -1) {
            params.put("payWay", payWay);
        }
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_PAYMENT_ALIPAY_UNION, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    String url = (String) body.data;
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

    @Override
    public void onPayResult(String result) {
        String paySign = userInfoData.paySign;
        if (paySign.equals(ToolsUtil.md5(result))) {
            requestBindAddress();
        } else {
            toast.toastShow("支付密码错误");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
