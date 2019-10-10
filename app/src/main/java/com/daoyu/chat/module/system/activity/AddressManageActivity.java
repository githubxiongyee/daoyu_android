package com.daoyu.chat.module.system.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseBean;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.adapter.AddressManageAdapter;
import com.daoyu.chat.module.mine.bean.ShippingAddressData;
import com.daoyu.chat.module.system.bean.AddressListBean;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

public class AddressManageActivity extends BaseTitleActivity implements SwipeRefreshLayout.OnRefreshListener, AddressManageAdapter.OnAddressViewClickListener {
    @BindView(R.id.rv_address_list)
    RecyclerView rvAddressList;
    @BindView(R.id.swipe_refresh_common)
    SwipeRefreshLayout swipeRefreshCommon;
    private int page = 1;
    private ArrayList<ShippingAddressData> shippingAddresss;
    private AddressManageAdapter adapter;
    private UserBean.UserData userInfoData;
    private boolean paymentInto;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_address_manage;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("地址管理");
        showRightAdd();
        Intent intent = getIntent();
        if (intent != null) {
            paymentInto = intent.getBooleanExtra(Constant.PAYMENT_INTO, false);
        }
        userInfoData = BaseApplication.getInstance().getUserInfoData();
        swipeRefreshCommon.setOnRefreshListener(this);
        swipeRefreshCommon.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        shippingAddresss = new ArrayList<>();
        bindingRecyclerView(rvAddressList);

        rvAddressList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AddressManageAdapter(this, shippingAddresss);
        adapter.setListener(this);
        rvAddressList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        swipeRefreshCommon.setRefreshing(true);
        requestAddressList();
    }

    private void requestAddressList() {
        Map<String, Object> params = new HashMap<>();
        params.put("uId", userInfoData.uid);
        params.put("page", page);
        params.put("limit", 50);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_ADDRESS_LIST, params, this, new JsonCallback<AddressListBean>(AddressListBean.class) {
            @Override
            public void onSuccess(Response<AddressListBean> response) {
                if (isActivityFinish) return;
                swipeRefreshCommon.setRefreshing(false);
                hideLoading();
                if (response == null || response.body() == null) return;
                AddressListBean body = response.body();
                if (body.success) {
                    if (page == 1) {
                        shippingAddresss.clear();
                    }
                    AddressListBean.AddressListData data = body.data;
                    if (data == null) {
                        shippingAddresss.clear();//
                        return;
                    }
                    ArrayList<ShippingAddressData> shippingAddressData = data.data;
                    if (shippingAddressData == null || shippingAddressData.size() <= 0) {
                        if (page == 1) {

                        } else {

                        }
                        shippingAddresss.clear();//
                    } else {
                        int topIndex = 0;
                        for (int i = 0;i < shippingAddressData.size();i ++){
                            if (shippingAddressData.get(i).isDef.equals("Y")){
                                topIndex = i;
                                ShippingAddressData topData = shippingAddressData.get(topIndex);
                                shippingAddressData.remove(topIndex);
                                shippingAddressData.add(0,topData);
                            }
                        }
                        shippingAddresss.addAll(shippingAddressData);
                        //adapter.notifyDataSetChanged();
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    toast.toastShow(body.msg);
                }
            }

            @Override
            public void onError(Response<AddressListBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                swipeRefreshCommon.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.iv_right:
                //新建地址页
                startActivity(new Intent(this, AddAddressActivity.class));
                break;
        }
    }

    @Override
    public void onRefresh() {
        requestAddressList();
    }


    @Override
    public void onMenuClickListener(int position, ShippingAddressData address, String type) {
        switch (type) {
            case "delete":
                if (paymentInto) {
                    toast.toastShow("当前模式下不允许删除");
                    return;
                }
                requestDeleteAddress(address, position);
                break;
            case "write":
                Intent intent = new Intent(this, AddAddressActivity.class);
                intent.putExtra(Constant.ADDRESS_BEAN, address);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onAddressItemViewClickListener(int position, ShippingAddressData address) {
        /*String isDef = address.isDef;
        if (!"Y".equals(isDef)) {
            requestSettingDefaultAddress(address);
        } else {
            if (paymentInto) {
                Intent data = new Intent();
                data.putExtra(Constant.ADDRESS_BEAN, address);
                setResult(RESULT_OK, data);
                finish();
            }
        }*/
        if (paymentInto) {
            Intent data = new Intent();
            data.putExtra(Constant.ADDRESS_BEAN, address);
            setResult(RESULT_OK, data);
            finish();
        }else {
            Intent intent = new Intent(this, ModifyAddressActivity.class);
            intent.putExtra(Constant.ADDRESS_BEAN, address);
            startActivity(intent);
        }

    }

    private void requestDeleteAddress(ShippingAddressData address, int position) {
        Map<String, Object> params = new HashMap<>();
        params.put("uId", userInfoData.uid);
        params.put("adid", address.id);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_DELETE_ADDRESS, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    shippingAddresss.remove(position);
                    adapter.notifyDataSetChanged();
                }
                toast.toastShow(body.msg);

            }
        });
    }

    private void requestSettingDefaultAddress(ShippingAddressData address) {
        Map<String, Object> params = new HashMap<>();
        params.put("uId", userInfoData.uid);
        params.put("adidNew", address.id);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_SET_DEFAULT_ADDRESS, params, this, new JsonCallback<BaseBean>(BaseBean.class) {
            @Override
            public void onSuccess(Response<BaseBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                BaseBean body = response.body();
                if (body.success) {
                    if (!paymentInto) {
                        requestAddressList();
                    } else {
                        Intent data = new Intent();
                        data.putExtra(Constant.ADDRESS_BEAN, address);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }
                toast.toastShow(body.msg);

            }
        });
    }

}
