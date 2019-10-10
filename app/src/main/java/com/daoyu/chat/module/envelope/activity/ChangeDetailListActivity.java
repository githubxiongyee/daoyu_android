package com.daoyu.chat.module.envelope.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.module.envelope.adapter.RedBagListAdapter;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.bean.GetPackageListB;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class ChangeDetailListActivity extends BaseTitleActivity implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.rv_common)
    RecyclerView rvCommon;

    @BindView(R.id.swipe_refresh_common)
    SwipeRefreshLayout swipeRefreshCommon;

    RedBagListAdapter adapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_red_bag_list;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("明细");

        swipeRefreshCommon.setOnRefreshListener(this);
        swipeRefreshCommon.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        rvCommon.setLayoutManager(new LinearLayoutManager(this));
        rvCommon.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        packageList = new ArrayList<>();
        adapter = new RedBagListAdapter(R.layout.item_reb_bag_list, packageList);
        View view = View.inflate(ChangeDetailListActivity.this, R.layout.layout_change_list_empty, null);
        adapter.setEmptyView(view);
        rvCommon.setAdapter(adapter);
        requestCreateList();

    }
    List<GetPackageListB.DataBeanX.DataBean> packageList = null;
    //获取红包列表
    private void requestCreateList() {
        Map<String, Object> params = new HashMap<>();
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        params.put("uId", userInfoData.uid);
        params.put("page", 0);
        params.put("limit", 20);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_GET_PACKAGE_LIST, params, this, new JsonCallback<GetPackageListB>(GetPackageListB.class) {
            @Override
            public void onSuccess(Response<GetPackageListB> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                GetPackageListB body = response.body();
                if (body.getCode() == 1) {
                    GetPackageListB.DataBeanX data = body.getData();
                    List<GetPackageListB.DataBeanX.DataBean> data1 = data.getData();
                    if (data1 == null || data1.size() <= 0) {
                    }else {
                        packageList.addAll(data1);

                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        swipeRefreshCommon.setRefreshing(false);
        //requestCreateList();
    }
}
