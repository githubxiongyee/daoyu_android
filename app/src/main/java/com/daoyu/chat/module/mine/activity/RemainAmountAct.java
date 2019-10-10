package com.daoyu.chat.module.mine.activity;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.bean.ReMainBean;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

//余额
public class RemainAmountAct extends BaseTitleActivity {
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.cl_remain_detail)
    ConstraintLayout clRemainDetail;

    @Override
    protected int getLayoutResId() {
        return R.layout.act_remain_amount;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("余额", R.color.colorWhite);
        setToolBarColor(R.color.color_EEA432);
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        int type = userInfoData.type;
        if (type == 1) {
            tvMoney.setText("0.00");
        } else {
            requestRemainAmount();
        }
        clRemainDetail.setOnClickListener(this);
    }

    private void requestRemainAmount() {
        Map<String, Object> params = new HashMap<>();
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_REMAIN_AMOUNT, params, this, new JsonCallback<ReMainBean>(ReMainBean.class) {
            @Override
            public void onSuccess(Response<ReMainBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                ReMainBean body = response.body();
                if (body.code == 1) {
                    ReMainBean.ReMainData data = body.data;
                    if (data == null) return;
                    Double amountRealpay = data.amountRealpay;
                    tvMoney.setText(String.format("%.2f", amountRealpay));
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.cl_remain_detail:
                startActivity(new Intent(this,RemainDetailListAct.class));
                break;
        }
    }
}
