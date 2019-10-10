package com.daoyu.chat.module.home.activity;

import android.view.View;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseTitleActivity;

import butterknife.BindView;

/**
 * 支付成功
 */
public class PaymentSuccessActivity extends BaseTitleActivity {

    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_payment_way)
    TextView tvPaymentWay;
    @BindView(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R.id.tv_complete)
    TextView tvComplete;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_payment_success;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("支付成功");
        tvComplete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_complete:
                finish();
                break;
        }
    }
}
