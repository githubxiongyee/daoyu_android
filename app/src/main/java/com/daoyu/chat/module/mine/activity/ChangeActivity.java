package com.daoyu.chat.module.mine.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.module.envelope.activity.ChangeDetailListActivity;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.bean.ChangeBean;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 零钱
 */
public class ChangeActivity extends BaseTitleActivity {
    @BindView(R.id.tv_change_amount)
    TextView tvChangeAmount;
    @BindView(R.id.tv_change_detail)
    TextView tvChangeDetail;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_change;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("零钱");
        tvChangeDetail.setOnClickListener(this);
        requestChange();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_change_detail://零钱明细
                startActivity(new Intent(this, ChangeDetailListActivity.class));
                break;
        }
    }

    private void requestChange() {
        showLoading("请稍等...", false);
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_CHANGE_INFO, params, this, new JsonCallback<ChangeBean>(ChangeBean.class) {
            @Override
            public void onSuccess(Response<ChangeBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                ChangeBean body = response.body();
                if (body.success) {
                    if (body.data == null) {
                        tvChangeAmount.setText(String.format("¥%.2f", 0.0));
                        return;
                    }
                    Double price = body.data;
                    tvChangeAmount.setText(String.format("¥%.2f", price));
                    userInfoData.creditTotal = price*100;
                    BaseApplication.getInstance().saveUserInfo(userInfoData);
                }
            }

            @Override
            public void onError(Response<ChangeBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        });
    }
}
