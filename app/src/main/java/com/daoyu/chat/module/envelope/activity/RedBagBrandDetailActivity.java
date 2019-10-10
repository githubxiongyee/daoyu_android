package com.daoyu.chat.module.envelope.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.event.RedBagUpdateStatusEvent;
import com.daoyu.chat.module.envelope.dialog.VerificationCodeDialog;
import com.daoyu.chat.module.home.bean.RedBagAdsBean;
import com.daoyu.chat.module.login.bean.UserBean;
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
 * 红包品牌详情页
 */
public class RedBagBrandDetailActivity extends BaseTitleActivity implements VerificationCodeDialog.ICheckSequenceListener {
    @BindView(R.id.tv_receiving_red_envelope)
    TextView tvReceivingRedEnvelope;
    @BindView(R.id.iv_image)
    ImageView ivImage;
    private VerificationCodeDialog verificationCodeDialog;
    public static RedBagAdsBean.RedBagData redBagData;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_red_bag_brand_detail;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle($$(R.string.text_receiving_red_envelope));
        tvReceivingRedEnvelope.setOnClickListener(this);
        redBagData = getIntent().getParcelableExtra(Constant.RED_BAG_ADS);
        if (redBagData == null) return;
        ImageUtils.setNormalImage(this, redBagData.thumbPicUrl, ivImage);
        String hongbaoSta = redBagData.hongbaoSta;
        if ("N".equals(hongbaoSta)) {
            tvReceivingRedEnvelope.setText("领取红包");
            tvReceivingRedEnvelope.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            tvReceivingRedEnvelope.setAlpha(1.0f);
        } else {
            tvReceivingRedEnvelope.setText("已领取");
            //tvReceivingRedEnvelope.setBackgroundColor(getResources().getColor(R.color.color_808080));
            tvReceivingRedEnvelope.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            tvReceivingRedEnvelope.setAlpha(0.5f);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_receiving_red_envelope:
                if (redBagData == null) return;
                String hongbaoSta = redBagData.hongbaoSta;
                if ("N".equals(hongbaoSta)) {
                    showVerificationCodeDialog();
                } else {
                    toast.toastShow("您已经领取了,请明日再来!");
                }
                break;
        }
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
                    //toast.toastShow(body.msg);
                    redBagData.hongbaoSta = "Y";
                    EventBus.getDefault().post(new RedBagUpdateStatusEvent(0, redBagData));
                    userInfoData.creditTotal = (double) body.data;
                    BaseApplication.getInstance().saveUserInfo(userInfoData);
                    Intent intent = new Intent(RedBagBrandDetailActivity.this, ReceivingRedSuccessActivity.class);
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

}
