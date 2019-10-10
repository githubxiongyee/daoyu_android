package com.daoyu.chat.module.envelope.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;

import butterknife.BindView;

/**
 * 领取红包成功
 */
public class ReceivingRedSuccessActivity extends BaseTitleActivity {

    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_look_reb_bag)
    TextView tvLookRebBag;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_receiving_red_success;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle($$(R.string.text_receiving_red_envelope));
        tvLookRebBag.setOnClickListener(this);
        Intent intent = getIntent();
        double originalPrice = intent.getDoubleExtra(Constant.RED_BAG_MONEY, 0.00);
        tvMoney.setText(String.format("¥%s", originalPrice));

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_look_reb_bag:
                //startActivity(new Intent(this, ChangeDetailListActivity.class));
                finish();
                break;
        }
    }
}
