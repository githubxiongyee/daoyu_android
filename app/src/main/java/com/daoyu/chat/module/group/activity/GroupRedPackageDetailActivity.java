package com.daoyu.chat.module.group.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseActivity;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.group.adapter.RedBagGroupChatAdapter;
import com.daoyu.chat.module.group.bean.GroupRedpackageBean;
import com.daoyu.chat.module.group.bean.UsersHeaderBean;
import com.daoyu.chat.module.im.module.MessageDetailTable;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.daoyu.chat.view.CircleImageView;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 群红包领取详情
 */
public class GroupRedPackageDetailActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_header)
    CircleImageView ivHeader;
    @BindView(R.id.tv_form_rad_package)
    TextView tvFormRadPackage;
    @BindView(R.id.tv_greetings)
    TextView tvGreetings;
    @BindView(R.id.tv_receive_number)
    TextView tvReceiveNumber;
    @BindView(R.id.tv_reb_bag_money_count)
    TextView tvRebBagMoneyCount;
    @BindView(R.id.rv_group)
    RelativeLayout rvGroup;
    @BindView(R.id.rv_receive_details)
    RecyclerView rvReceiveDetails;


    public MessageDetailTable message;
    private ArrayList<GroupRedpackageBean.GroupRedpackageData> mGroupRedpackageData;
    private RedBagGroupChatAdapter mAdapter;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_red_package_detail_group;
    }

    @Override
    protected void initEvent() {
        Intent intent = getIntent();
        message = intent.getParcelableExtra(Constant.MESSAGE_RED_PACKAGE_INFO);
        if (message == null) {
            finish();
            return;
        }
        ivBack.setOnClickListener(v -> finish());
        tvFormRadPackage.setText(String.format("%s的红包", message.username));
        tvGreetings.setText(message.message);
        ImageUtils.setNormalImage(this, message.avatar, ivHeader);
        rvReceiveDetails.setLayoutManager(new LinearLayoutManager(this));
        mGroupRedpackageData = new ArrayList<>();
        mAdapter = new RedBagGroupChatAdapter(this, mGroupRedpackageData);
        rvReceiveDetails.setAdapter(mAdapter);
        requestGetRedPackage(message.red_id);
    }

    private void requestGetRedPackage(String id) {
        showLoading("加载中...", false);
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        Map<String, Object> params = new HashMap<>();
        params.put("hid", id);
        params.put("auth_token", userInfoData.token);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_RECEVICE_DETAILS, params, this, new JsonCallback<GroupRedpackageBean>(GroupRedpackageBean.class) {
            @Override
            public void onSuccess(Response<GroupRedpackageBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                GroupRedpackageBean body = response.body();
                if (body.code == 1) {
                    ArrayList<GroupRedpackageBean.GroupRedpackageData> data = body.data;
                    if (data == null || data.size() <= 0) {
                        hideLoading();
                        return;
                    }
                    GroupRedpackageBean.GroupRedpackageData groupRedpackageData = data.get(0);
                    int hbcount = groupRedpackageData.hbcount;
                    int namount = groupRedpackageData.ncount;
                    tvReceiveNumber.setText("已领取" + (hbcount - namount) + "/" + hbcount);
                    tvRebBagMoneyCount.setText(String.format("%d个红包，共%.2f元", hbcount, groupRedpackageData.vipCredit));

                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 1; i < data.size(); i++) {
                        stringBuffer.append(data.get(i).fid + ",");
                    }
                    String trim = stringBuffer.toString().trim();
                    if (!TextUtils.isEmpty(trim)) {
                        String ids = trim.substring(0, trim.length() - 1);
                        requestGetUsersHeader(ids, data);
                    } else {
                        hideLoading();
                    }
                } else {
                    toast.toastShow(body.msg);
                    hideLoading();
                }

            }
        });
    }

    private void requestGetUsersHeader(String ids, ArrayList<GroupRedpackageBean.GroupRedpackageData> groupRedpackageDatas) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        Map<String, Object> params = new HashMap<>();
        params.put("auth_token", userInfoData.token);
        params.put("user_ids", ids);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_GET_USERS_HEADER, params, this, new JsonCallback<UsersHeaderBean>(UsersHeaderBean.class) {
            @Override
            public void onSuccess(Response<UsersHeaderBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                UsersHeaderBean body = response.body();
                if (body.code == 1) {
                    ArrayList<UsersHeaderBean.UsersHeaderData> data = body.data;
                    if (data == null || data.size() <= 0) return;
                    for (int i = 0; i < data.size(); i++) {
                        GroupRedpackageBean.GroupRedpackageData groupRedpackageData = groupRedpackageDatas.get(i + 1);
                        UsersHeaderBean.UsersHeaderData usersHeaderData = data.get(i);
                        groupRedpackageData.name = usersHeaderData.nickName;
                        groupRedpackageData.headeImag = usersHeaderData.headImg;
                    }
                    groupRedpackageDatas.remove(0);
                    mGroupRedpackageData.addAll(groupRedpackageDatas);
                    mAdapter.notifyDataSetChanged();
                } else {
                    toast.toastShow(body.msg);
                    hideLoading();
                }

            }

            @Override
            public void onError(Response<UsersHeaderBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        });
    }

}
