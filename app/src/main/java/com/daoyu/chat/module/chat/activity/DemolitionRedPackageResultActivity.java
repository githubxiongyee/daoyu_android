package com.daoyu.chat.module.chat.activity;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.chat.bean.RedPackageBean;
import com.daoyu.chat.module.im.module.MessageDetailTable;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.daoyu.chat.view.CircleImageView;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 领取红包结果
 */
public class DemolitionRedPackageResultActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_header)
    CircleImageView ivHeader;
    @BindView(R.id.tv_form_rad_package)
    TextView tvFormRadPackage;
    @BindView(R.id.tv_greetings)
    TextView tvGreetings;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_unit_RNB)
    TextView tvUnitRNB;
    @BindView(R.id.rl_money)
    RelativeLayout rlMoney;
    private MessageDetailTable message;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_demolition_red_package_result;
    }

    @Override
    protected void initEvent() {
        Intent intent = getIntent();
        message = intent.getParcelableExtra(Constant.MESSAGE_RED_PACKAGE_INFO);
        double moeny = intent.getDoubleExtra("moeny", 0.0);
        if (message == null) {
            finish();
        }
        tvMoney.setText(String.format("%.2f",moeny));
        requestGetRedPackage(message.red_id);
        ivBack.setOnClickListener(v -> finish());


    }

    private void requestGetRedPackage(String id) {
        showLoading("加载中...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_GET_RED_PACKAGE_DETAILS, params, this, new JsonCallback<RedPackageBean>(RedPackageBean.class) {
            @Override
            public void onSuccess(Response<RedPackageBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                RedPackageBean body = response.body();
                if (body.success) {
                    RedPackageBean.RedPackageData data = body.data;
                    if (data == null) return;
                    int vipStatus = data.vipStatus;
                    tvGreetings.setText(message.message);
                    tvFormRadPackage.setText(String.format("%s的红包", message.username));
                    ImageUtils.setNormalImage(DemolitionRedPackageResultActivity.this, message.avatar, R.drawable.my_user_default, R.drawable.my_user_default, ivHeader);
                    if (vipStatus == 3) {
                        tvMoney.setText(String.format("%.2f", data.vipCredit));
                    }
                }

            }
        });
    }
}
