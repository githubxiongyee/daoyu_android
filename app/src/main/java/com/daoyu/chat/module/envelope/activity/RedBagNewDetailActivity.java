package com.daoyu.chat.module.envelope.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.event.RedBagUpdateStatusEvent;
import com.daoyu.chat.module.envelope.bean.CreateChangeB;
import com.daoyu.chat.module.envelope.bean.CreateOrderBean;
import com.daoyu.chat.module.envelope.dialog.VerificationCodeDialog;
import com.daoyu.chat.module.home.bean.RedBagAdsBean;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.activity.PaymentOrderActivity;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 红包新品详情页(爆品)
 */
public class RedBagNewDetailActivity extends BaseTitleActivity implements VerificationCodeDialog.ICheckSequenceListener {

    @BindView(R.id.iv_image)
    ImageView ivImage;

    @BindView(R.id.tv_new_product)
    TextView tvNewProduct;

    @BindView(R.id.tv_description)
    TextView tvDescription;

    @BindView(R.id.tv_price)
    TextView tvPrice;

    @BindView(R.id.tv_buy)
    TextView tvBuy;

    @BindView(R.id.tv_receiving_red_envelope)
    TextView tvReceivingRedEnvelope;
    private VerificationCodeDialog verificationCodeDialog;
    private RedBagAdsBean.RedBagData redBagData;
    //private IWXAPI api;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_red_bag_new_detail;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle($$(R.string.text_receiving_red_envelope));
        tvReceivingRedEnvelope.setOnClickListener(this);
        tvBuy.setOnClickListener(this);
        Intent intent = getIntent();
        redBagData = intent.getParcelableExtra(Constant.RED_BAG_ADS);
        if (redBagData == null) return;
        ImageUtils.setNormalImage(this, redBagData.thumbPicUrl, ivImage);
        String hongbaoSta = redBagData.hongbaoSta;
        if ("N".equals(hongbaoSta)) {
            tvReceivingRedEnvelope.setText("领取红包");
            tvReceivingRedEnvelope.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            tvReceivingRedEnvelope.setText("已领取");
            tvReceivingRedEnvelope.setBackgroundColor(getResources().getColor(R.color.color_808080));
        }

        tvDescription.setText(redBagData.productDesc);
        tvNewProduct.setText(redBagData.productName);
        tvPrice.setText(String.format("¥%.2f", redBagData.originalPrice));
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_receiving_red_envelope://领取红包
                if (redBagData == null) return;
                if ("N".equals(redBagData.hongbaoSta)) {
                    showVerificationCodeDialog();
                } else {
                    toast.toastShow("您已经领取了,请明日再来!");
                }
                break;
            case R.id.tv_buy://购买
                requestChange();
                //requestCreateOrder();
                break;
        }
    }

    private void requestChange() {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        Map<String, Object> params = new HashMap<>();
        params.put("proId", redBagData.productNo);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_CREATE_CHANGE, params, this, new JsonCallback<CreateChangeB>(CreateChangeB.class) {
            @Override
            public void onSuccess(Response<CreateChangeB> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                CreateChangeB body = response.body();
                if (body.code == 1) {
                    CreateChangeB.CreateChangeData data = body.getData();
                    double bCreditTotal = data.bCreditTotal;
                    userInfoData.creditTotal = bCreditTotal;
                    BaseApplication.getInstance().saveUserInfo(userInfoData);
                    Log.e("=====orderNumber====", "=====orderNumber====///////////////////////////////" + data.bOrderNumber);
                    Intent intent = new Intent(RedBagNewDetailActivity.this, PaymentOrderActivity.class);
                    intent.putExtra(Constant.ORDER_INFO, data);
                    intent.putExtra(Constant.PRODUCT_INFO, redBagData);
                    startActivity(intent);
                } else {
                    toast.toastShow(body.msg);
                }
            }
        });
    }

    private void showVerificationCodeDialog() {
        if (verificationCodeDialog != null) {
            verificationCodeDialog.dismissAllowingStateLoss();
        }
        verificationCodeDialog = VerificationCodeDialog.getInstance(redBagData.productName);
        if (verificationCodeDialog.isAdded()) {
            verificationCodeDialog.dismissAllowingStateLoss();
        } else {
            verificationCodeDialog.show(getSupportFragmentManager(), "verificationCodeDialog");
        }
    }

    @Override
    public void onConfirm() {
        requestReceiveRed();
    }

    private void requestReceiveRed() {
        showLoading("领取中...", false);
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userInfoData.uid);
        params.put("creditVal", Math.round(redBagData.hongbaoPrice * 100));
        params.put("hongbaoId", redBagData.pId);
        params.put("creditSta", 1);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_GET_RED_PACKAGE, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    redBagData.hongbaoSta = "Y";
                    EventBus.getDefault().post(new RedBagUpdateStatusEvent(1, redBagData));
                    userInfoData.creditTotal = (double) body.data;
                    BaseApplication.getInstance().saveUserInfo(userInfoData);
                    Intent intent = new Intent(RedBagNewDetailActivity.this, ReceivingRedSuccessActivity.class);
                    intent.putExtra(Constant.RED_BAG_MONEY, redBagData.hongbaoPrice);
                    startActivity(intent);
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

    private void requestCreateOrder() {
        Map<String, Object> params = new HashMap<>();
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        params.put("uid", userInfoData.uid);
        params.put("proId", redBagData.productNo);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_CREATE_ORDER, params, this, new JsonCallback<CreateOrderBean>(CreateOrderBean.class) {
            @Override
            public void onSuccess(Response<CreateOrderBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                CreateOrderBean body = response.body();
                if (body.code == 1) {
                    CreateOrderBean.CreateOrderData data = body.getData();
                    double bCreditTotal = data.bCreditTotal * 100;
                    userInfoData.creditTotal = bCreditTotal;
                    BaseApplication.getInstance().saveUserInfo(userInfoData);
                    Intent intent = new Intent(RedBagNewDetailActivity.this, PaymentOrderActivity.class);
                    intent.putExtra(Constant.ORDER_INFO, data);
                    intent.putExtra(Constant.PRODUCT_INFO, redBagData);
                    startActivity(intent);
                }
            }
        });
    }
}
