package com.daoyu.chat.module.chat.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.chat.adapter.NearbyPeopleAdapter;
import com.daoyu.chat.module.chat.bean.NearbyPeopleB;
import com.daoyu.chat.module.chat.dialog.NearbyRangeDialog;
import com.daoyu.chat.module.home.activity.ContactDetailsActivity;
import com.daoyu.chat.module.home.bean.ScanAddFriendB;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.login.view.LocationServiceDialog;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

//附近的人
public class NearbyPeopleActivity extends BaseTitleActivity implements BaseQuickAdapter.OnItemClickListener, NearbyRangeDialog.IChooseNearbyWays, LocationServiceDialog.ICheckSequenceListener {
    @BindView(R.id.swipe_nearby)
    SwipeRefreshLayout swipeNearby;
    @BindView(R.id.rv_nearby)
    RecyclerView rvNearby;

    NearbyPeopleAdapter adapter;

    NearbyRangeDialog nearbyRangeDialog;
    private LocationServiceDialog locationServiceDialog;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_nearby_people;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    double x;
    double y;

    @Override
    protected void initEvent() {
        setCurrentTitle("附近的人");
        x = getIntent().getExtras().getDouble("longitude");
        y = getIntent().getExtras().getDouble("latitude");
        showRightAdd(R.drawable.maillist_btn_nearby_screen);

        if (x == 0 || y == 0) {
            toast.toastShow("定位失败");
            return;
        } else {
            requestNearbyPeople("");
        }

        swipeNearby.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        bindingRecyclerView(rvNearby);
        rvNearby.setLayoutManager(new LinearLayoutManager(this));
        dataBeans = new ArrayList<>();
        adapter = new NearbyPeopleAdapter(this, R.layout.item_nearby_people, dataBeans);

        adapter.setListener(this);
        rvNearby.setAdapter(adapter);
        swipeNearby.setRefreshing(false);

    }

    private void showLocationServiceDialog() {
        if (locationServiceDialog != null) {
            locationServiceDialog.dismissAllowingStateLoss();
        }
        locationServiceDialog = LocationServiceDialog.getInstance();
        if (locationServiceDialog.isAdded()) {
            locationServiceDialog.dismissAllowingStateLoss();
        } else {
            locationServiceDialog.show(getSupportFragmentManager(), "locationServiceDialog");
        }
    }

    List<NearbyPeopleB.DataBean> dataBeans;

    private void requestNearbyPeople(String sex/*,String x, String y*/) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        dataBeans = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        //params.put("page", 0);
        //params.put("limit", 50);
        params.put("sex", sex);//M+F
        //params.put("addressX", x);
        params.put("addressX", "" + x);
        //params.put("addressY", y);
        params.put("addressY", "" + y);
        params.put("dis", 2);//范围
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_NEARBY_PEOPLE_LIST, params, this, new JsonCallback<NearbyPeopleB>(NearbyPeopleB.class) {
            @Override
            public void onSuccess(Response<NearbyPeopleB> response) {
                hideLoading();
                if (response == null || response.body() == null) return;
                NearbyPeopleB body = response.body();
                if (body.isSuccess()) {
                    Log.i("info", "====requestNearbyPeople=========================");
                    if (body.getData() == null) return;
                    if (body.getData().size() != 0) {
                        for (int i = 0; i < body.getData().size(); i++) {
                            if (body.getData().get(i).getId() == userInfoData.uid) {
                                continue;
                            }
                            dataBeans.add(body.getData().get(i));
                        }
                        if (dataBeans == null) return;
                        adapter.setNewData(dataBeans);

                    }
                }
            }

            @Override
            public void onError(Response<NearbyPeopleB> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        });


    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (dataBeans == null || dataBeans.size() <= 0) return;
        NearbyPeopleB.DataBean dataBean = dataBeans.get(position);
        requestIsFriend(dataBean);
    }

    private void requestIsFriend(NearbyPeopleB.DataBean dataBean) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        Map<String, Object> params = new HashMap<>();
        params.put("user_friend_id", dataBean.getId());
        params.put("user_id", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_IS_FRIEND, params, this, new JsonCallback<ScanAddFriendB>(ScanAddFriendB.class) {
            @Override
            public void onSuccess(Response<ScanAddFriendB> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                ScanAddFriendB body = response.body();
                if (body.code == 1) {
                    Intent intent = new Intent(NearbyPeopleActivity.this, ContactDetailsActivity.class);
                    intent.putExtra(Constant.CONTACT_ID, String.valueOf(dataBean.getId()));
                    intent.putExtra(Constant.CONTACT_NEAR_BY, true);
                    startActivity(intent);
                }
            }

            @Override
            public void onError(Response<ScanAddFriendB> response) {
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
            case R.id.iv_right:
                showChooseAddRange();
                break;
        }
    }

    private void showChooseAddRange() {
        if (nearbyRangeDialog != null) {
            nearbyRangeDialog.dismissAllowingStateLoss();
        }
        nearbyRangeDialog = nearbyRangeDialog.getInstance();
        if (!nearbyRangeDialog.isAdded()) {
            nearbyRangeDialog.show(getSupportFragmentManager(), "chooseAddDialog");
        } else {
            nearbyRangeDialog.dismissAllowingStateLoss();
        }

    }

    @Override
    public void onChooseAdd(String way) {
        if (TextUtils.isEmpty(way)) return;
        switch (way) {
            case "add_man":
                requestNearbyPeople("M");
                break;
            case "add_woman":
                requestNearbyPeople("F");
                break;
            case "add_all":
                requestNearbyPeople("");
                break;
        }
    }


    @Override
    public void onConfirm() {

    }

    @Override
    public void onCancel() {

    }


}