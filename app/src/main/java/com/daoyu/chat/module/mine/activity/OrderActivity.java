package com.daoyu.chat.module.mine.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.module.envelope.bean.CreateChangeB;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.adapter.OrderAdapter;
import com.daoyu.chat.module.mine.bean.OrderListB;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 訂單
 */
public class OrderActivity extends BaseTitleActivity implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.rv_common)
    RecyclerView rvCommon;
    @BindView(R.id.swipe_refresh_common)
    SwipeRefreshLayout swipeRefreshCommon;
    OrderAdapter adapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_order;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle($$(R.string.text_order));
        showRightTxtTitle("售后服务");
        swipeRefreshCommon.setOnRefreshListener(this);
        swipeRefreshCommon.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        rvCommon.setLayoutManager(new LinearLayoutManager(this));
        rvCommon.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.top = getResources().getDimensionPixelSize(R.dimen.dp_10);
                }
            }
        });
        createChangeDatas = new ArrayList<>();
        adapter = new OrderAdapter(OrderActivity.this, R.layout.item_order, createChangeDatas);
        View view = View.inflate(OrderActivity.this, R.layout.layout_order_list_empty, null);
        adapter.setEmptyView(view);
        rvCommon.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        createChangeDatas.clear();
        requestOrderList();

    }

    private ArrayList<CreateChangeB.CreateChangeData> createChangeDatas;

    private void requestOrderList() {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        Map<String, Object> params = new HashMap<>();
        params.put("uid", userInfoData.uid + "");
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_ORDER_LIST, params, this, new JsonCallback<OrderListB>(OrderListB.class) {
            @Override
            public void onSuccess(Response<OrderListB> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                OrderListB body = response.body();
                if (body.code == 1) {
                    ArrayList<CreateChangeB.CreateChangeData> data = body.data;
                    createChangeDatas.addAll(data);
                } else {
                    toast.toastShow(body.msg);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Response<OrderListB> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        });

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_right_title:
                startActivity(new Intent(this, AfterSalesAct.class));
                break;
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshCommon.setRefreshing(false);
      /*  createChangeDatas.clear();
        requestOrderList();*/
    }

}
