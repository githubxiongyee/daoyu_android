package com.daoyu.chat.module.system.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.bean.GetUserValueB;
import com.daoyu.chat.module.mine.dialog.AddRangeDialog;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

public class PrivacySettingActivity extends BaseTitleActivity implements AddRangeDialog.IChooseAddWays {
    @BindView(R.id.tv_add_friend)
    TextView tvAddFriend;
    @BindView(R.id.tv_add_friend_range)
    TextView tvAddRange;

    @BindView(R.id.tv_address_manage)
    TextView tvAddressManage;

    AddRangeDialog addRangeDialog;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_privacy_setting;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("隐私设置");

        tvAddressManage.setOnClickListener(this);
        tvAddFriend.setOnClickListener(this);

        requestGetValue();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_address_manage:
                //去地址管理
                startActivity(new Intent(this, AddressManageActivity.class));
                break;
            case R.id.tv_add_friend:
                //弹窗
                showChooseAddRange();
                break;
        }
    }

    /**
     * 显示加好友对话框
     */
    private void showChooseAddRange() {
        if (addRangeDialog != null) {
            addRangeDialog.dismissAllowingStateLoss();
        }
        addRangeDialog = AddRangeDialog.getInstance();
        if (!addRangeDialog.isAdded()) {
            addRangeDialog.show(getSupportFragmentManager(), "chooseAddDialog");
        } else {
            addRangeDialog.dismissAllowingStateLoss();
        }

    }

    @Override
    public void onChooseAdd(String way) {
        if (TextUtils.isEmpty(way)) return;
        switch (way) {
            case "add_all":
                addRangeTxt("所有人可加我为好友");
                requestSetValue(1);
                break;
            case "add_check":
                addRangeTxt("加我时需要验证");
                requestSetValue(2);
                break;
            case "add_notAllowed":
                addRangeTxt("不允许加我为好友");
                requestSetValue(3);
                break;
        }
    }

    private void requestSetValue(int addValue) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        Map<String, Object> params = new HashMap<>();
        params.put("code", "invisible");
        params.put("module", "im");
        params.put("remark", "invisible_test");
        params.put("user_id", userInfoData.uid);
        params.put("value", addValue);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_SET_USER_VALUE, params, PrivacySettingActivity.this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.code == 1) {
                    //toast.toastShow(body.msg);
                } else {
                    toast.toastShow(body.msg);
                }
            }
        });
    }

    private void requestGetValue() {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        Map<String, Object> params = new HashMap<>();
        params.put("code", "invisible");
        params.put("user_id", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_GET_USER_VALUE, params, PrivacySettingActivity.this, new JsonCallback<GetUserValueB>(GetUserValueB.class) {
            @Override
            public void onSuccess(Response<GetUserValueB> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                GetUserValueB body = response.body();
                if (body.getCode() == 1) {
                    //toast.toastShow(body.getMsg());
                    if (body.getData().size() == 0 || body.getData() == null) return;
                    if (body.getData().get(0).getSettingValue().equals("1")) {
                        addRangeTxt("所有人可加我为好友");
                    } else if (body.getData().get(0).getSettingValue().equals("2")) {
                        addRangeTxt("加我时需要验证");
                    } else if (body.getData().get(0).getSettingValue().equals("3")) {
                        addRangeTxt("不允许加我为好友");
                    }
                } else {
                    toast.toastShow(body.getMsg());
                }
            }
        });
    }

    private void addRangeTxt(String s) {
        tvAddRange.setText(s);
    }

}
