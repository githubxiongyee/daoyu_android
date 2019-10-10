package com.daoyu.chat.module.chat.activity;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.chat.bean.RedPackageBean;
import com.daoyu.chat.module.im.module.IMConstant;
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
 * 红包详情
 */
public class RedPackageDetailActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;

    @BindView(R.id.iv_header)
    CircleImageView ivHeader;

    @BindView(R.id.tv_form_rad_package)
    TextView tvFormRadPackage;

    @BindView(R.id.tv_greetings)
    TextView tvGreetings;

    @BindView(R.id.iv_receive_header)
    CircleImageView ivReceiveHeader;

    @BindView(R.id.tv_receive_name)
    TextView tvReceiveName;

    @BindView(R.id.tv_rad_package_money)
    TextView tvRadPackageMoney;

    @BindView(R.id.cl_receive)
    ConstraintLayout clReceive;

    @BindView(R.id.tv_wait_receive)
    TextView tvWaitReceive;

    @BindView(R.id.cl_wait_receive)
    ConstraintLayout clWaitReceive;
    private String formContact;
    private String receiveName;
    private String receiveHeader;
    private String sendHeader;
    private String redId;
    public static MessageDetailTable messageDetailTable;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_red_package_detail;
    }

    @Override
    protected void initEvent() {
        Intent intent = getIntent();
        formContact = intent.getStringExtra(Constant.RED_SEND_NAME);
        sendHeader = intent.getStringExtra(Constant.RED_SEND_HEADER);
        receiveName = intent.getStringExtra(Constant.RED_RECEIVE_NAME);
        receiveHeader = intent.getStringExtra(Constant.RED_RECEIVE_HEADER);
        redId = intent.getStringExtra(Constant.RED_ID);
        if (TextUtils.isEmpty(redId)) finish();
        requestGetRedPackage(redId);
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
                    tvGreetings.setText(data.remarks);
                    tvFormRadPackage.setText(String.format("%s的红包", formContact));
                    ImageUtils.setNormalImage(RedPackageDetailActivity.this, sendHeader, R.drawable.my_user_default, R.drawable.my_user_default, ivHeader);
                    if (vipStatus == 3) {
                        clWaitReceive.setVisibility(View.GONE);
                        clReceive.setVisibility(View.VISIBLE);
                        ImageUtils.setNormalImage(RedPackageDetailActivity.this, receiveHeader, R.drawable.my_user_default, R.drawable.my_user_default, ivReceiveHeader);
                        tvReceiveName.setText(receiveName);
                        tvRadPackageMoney.setText(String.format("%.2f元", data.vipCredit));
                        if(messageDetailTable!=null){
                            messageDetailTable.message_state = IMConstant.MessageStatus.SUCCESSED;
                            messageDetailTable.saveAsync().listen(success -> Log.d("TAG", "保存成功!"));
                        }

                    } else if (vipStatus == 4) {
                        clWaitReceive.setVisibility(View.VISIBLE);
                        clReceive.setVisibility(View.GONE);
                        tvWaitReceive.setText(String.format("红包金额%.2f元，等待对方领取", data.vipCredit));

                    }
                }

            }
        });
    }
}
