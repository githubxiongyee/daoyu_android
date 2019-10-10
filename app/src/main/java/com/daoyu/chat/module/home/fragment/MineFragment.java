package com.daoyu.chat.module.home.fragment;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseFragment;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.common.OnItemClickListener;
import com.daoyu.chat.module.home.activity.CommonWebViewActivity;
import com.daoyu.chat.module.home.activity.IOTAct;
import com.daoyu.chat.module.home.activity.SearchCommonActivity;
import com.daoyu.chat.module.home.adapter.MineAdapter;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.activity.ChangeActivity;
import com.daoyu.chat.module.mine.activity.FaceAct;
import com.daoyu.chat.module.mine.activity.OrderActivity;
import com.daoyu.chat.module.mine.activity.ReceivePaymentActivity;
import com.daoyu.chat.module.mine.activity.RemainAmountAct;
import com.daoyu.chat.module.mine.activity.SystemActivity;
import com.daoyu.chat.module.mine.activity.UserInfoActivity;
import com.daoyu.chat.utils.ImageUtils;

import butterknife.BindView;

/**
 * 我的
 */
public class MineFragment extends BaseFragment implements View.OnClickListener, OnItemClickListener {


    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.iv_user_head)
    ImageView ivUserHead;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_id_number)
    TextView tvIdNumber;
    @BindView(R.id.iv_setting)
    ImageView ivSetting;
    @BindView(R.id.rv_mine)
    RecyclerView rvMine;
    @BindView(R.id.container)
    ConstraintLayout container;

    private MineAdapter adapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initEvent() {
        initListener();
        rvMine.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        adapter = new MineAdapter(getContext());
        adapter.setListener(this);
        rvMine.setAdapter(adapter);
    }

    private void initListener() {
        ivAdd.setOnClickListener(this);
        ivSetting.setOnClickListener(this);
        container.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        tvIdNumber.setText("ID: " + userInfoData.userPhone);
        tvUserName.setText(TextUtils.isEmpty(userInfoData.nickName) ? userInfoData.userPhone : userInfoData.nickName);
        ImageUtils.setRoundImageView(getContext(), userInfoData.headImg, R.drawable.my_user_default, ivUserHead);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_add:
                showTopRightPopMenu(ivAdd);
                break;
            case R.id.iv_setting:
                startActivity(new Intent(getActivity(), SystemActivity.class));
                break;
            case R.id.container:
                startActivity(new Intent(getActivity(), UserInfoActivity.class));
                break;
        }
    }

    @Override
    public void onItemClick(Object data, int position) {
        if (data instanceof Integer) {
            int pos = (int) data;
            switch (pos) {
                case 0:
                    doWallet(position);
                    break;
                case 1:
                    doService(position);
                    break;
                case 2:
                    doThirdParty(position);
                    break;
            }
        }
    }

    private void doThirdParty(int position) {
        Intent intent = new Intent(getActivity(), CommonWebViewActivity.class);
        switch (position) {
            case 0:
                intent.putExtra(Constant.WEB_VIEW_URL, "http://www.suanya.cn/");
                intent.putExtra(Constant.WEB_VIEW_TITLE, "火车票");
                startActivity(intent);
                break;
            case 1:
                intent.putExtra(Constant.WEB_VIEW_TITLE, "酒店");
                intent.putExtra(Constant.WEB_VIEW_URL, "http://www.wyn88.com/");
                startActivity(intent);
                break;
            case 2:
                intent.putExtra(Constant.WEB_VIEW_TITLE, "购物");
                intent.putExtra(Constant.WEB_VIEW_URL, "https://www.yjzhsw.com/");
                startActivity(intent);
                break;
            case 3:
                intent.putExtra(Constant.WEB_VIEW_TITLE, "运动");
                intent.putExtra(Constant.WEB_VIEW_URL, "https://china.nba.com/");
                startActivity(intent);
                break;
            case 4:
                intent.putExtra(Constant.WEB_VIEW_TITLE, "出境");
                intent.putExtra(Constant.WEB_VIEW_URL, "https://www.nilai.com/");
                startActivity(intent);
                break;
        }
    }

    private void doService(int position) {
        switch (position) {
            /*case 0:
                startActivity(new Intent(getActivity(), OrderActivity.class));
                break;
            case 1:
                //toast.toastShow("收藏");
                startActivity(new Intent(getActivity(), SaveAct.class));
                break;
            case 2:
                //toast.toastShow("皮肤");
                startActivity(new Intent(getActivity(), FaceAct.class));
                break;
            case 3:
                //toast.toastShow("表情");
                startActivity(new Intent(getActivity(), ExpressionAct.class));
                break;
            case 4:
                //toast.toastShow("小程序");
                startActivity(new Intent(getActivity(), SmallProgramAct.class));
                break;*/
            case 0:
                startActivity(new Intent(getActivity(), OrderActivity.class));
                break;
            case 1:
                //toast.toastShow("皮肤");
                startActivity(new Intent(getActivity(), FaceAct.class));
                break;
            case 2:
                startActivity(new Intent(this.getContext(), IOTAct.class));
                //toast.toastShow("表情");
                //startActivity(new Intent(getActivity(), ExpressionAct.class));
                break;
        }
    }

    private void doWallet(int position) {
        switch (position) {
            case 0:
                startActivity(new Intent(getActivity(), ReceivePaymentActivity.class));
                break;
            case 1:
                startActivity(new Intent(getActivity(), ChangeActivity.class));
                break;
            case 2:
                //toast.toastShow("余额");
                startActivity(new Intent(getActivity(), RemainAmountAct.class));
                break;
        }
    }
}
