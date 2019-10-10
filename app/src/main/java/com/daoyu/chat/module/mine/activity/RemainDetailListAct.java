package com.daoyu.chat.module.mine.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.module.mine.adapter.RemainListAdapter;
import com.daoyu.chat.module.mine.bean.RemainDetailListB;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

//余额明细
public class RemainDetailListAct extends BaseTitleActivity implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.rv_common)
    RecyclerView rvCommon;

    @BindView(R.id.swipe_refresh_common)
    SwipeRefreshLayout swipeRefreshCommon;

    RemainListAdapter adapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_red_bag_list;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("余额明细");

        swipeRefreshCommon.setOnRefreshListener(this);
        swipeRefreshCommon.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        rvCommon.setLayoutManager(new LinearLayoutManager(this));
        rvCommon.setBackgroundColor(getResources().getColor(R.color.colorWhite));

        remainDetailList = new ArrayList<>();
        adapter = new RemainListAdapter(R.layout.item_remain_list, remainDetailList);
        rvCommon.setAdapter(adapter);
        requestRemainDetail();
    }

    List<RemainDetailListB.DataBean> remainDetailList = null;

    private void requestRemainDetail() {
        Map<String, Object> params = new HashMap<>();
        params.put("page", 1);
        params.put("limit", 50);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_REMAIN_DETAIL, params, this, new JsonCallback<RemainDetailListB>(RemainDetailListB.class) {
            @Override
            public void onSuccess(Response<RemainDetailListB> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                RemainDetailListB body = response.body();
                if (body.getCode() == 1) {
                    if (body.getData() == null || body.getData().size() == 0) return;
                    for (int i = 0; i < response.body().getData().size(); i++) {
                        remainDetailList.add(response.body().getData().get(i));
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
