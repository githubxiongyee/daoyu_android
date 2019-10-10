package com.daoyu.chat.module.mine.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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

//收付款记录，收款记录
public class GetOrPayActivity extends BaseTitleActivity implements SwipeRefreshLayout.OnRefreshListener {
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
        int type = getIntent().getExtras().getInt("type");
        if (type == 0){
            setCurrentTitle("收款记录");
        }else if (type == 1){
            setCurrentTitle("收付款记录");
        }


        swipeRefreshCommon.setOnRefreshListener(this);
        swipeRefreshCommon.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        rvCommon.setLayoutManager(new LinearLayoutManager(this));
        rvCommon.setBackgroundColor(getResources().getColor(R.color.colorWhite));

        packageList = new ArrayList<>();
        adapter = new RedBagListAdapter(R.layout.item_get_pay_list, packageList);
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

                if (response.body().isSuccess()) {
                    Log.i("info","=========红包列表======="+response.body().getMsg());
                    if (response.body().getData().getData().size() == 0){
                        return;
                    }else {
                        for (int i=0;i<response.body().getData().getData().size();i++){
                            packageList.add(response.body().getData().getData().get(i));
                            Log.i("info","=========红包列表======="+packageList.get(i));
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        swipeRefreshCommon.setRefreshing(false);
    }
}
