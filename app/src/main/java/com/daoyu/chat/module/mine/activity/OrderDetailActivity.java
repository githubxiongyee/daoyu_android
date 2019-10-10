package com.daoyu.chat.module.mine.activity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.bean.ChangeDetailB;
import com.daoyu.chat.module.system.utils.TimeUtil;
import com.daoyu.chat.utils.ImageUtils;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

public class OrderDetailActivity extends BaseTitleActivity {
    @BindView(R.id.tv_status)
    TextView tvStatus;

    @BindView(R.id.image_icon_d)
    ImageView ivIconD;
    @BindView(R.id.tv_status_d)
    TextView tvStatusD;
    @BindView(R.id.tv_status_address)
    TextView tvStatusAddressD;
    @BindView(R.id.tv_your_address)
    TextView tvYourAddress;
    @BindView(R.id.tv_status_time)
    TextView tvStatusTimeD;
    @BindView(R.id.tv_shopper_u)
    TextView tvShopperName;
    @BindView(R.id.tv_shopper_tel)
    TextView tvTel;
    @BindView(R.id.tv_pay_style)
    TextView tvPayStyle;
    @BindView(R.id.tv_pay_time)
    TextView tvPayTime;

    @BindView(R.id.tv_order)
    TextView tvOrderNum;
    @BindView(R.id.tv_goods_name)
    TextView tvGoods;
    @BindView(R.id.tv_amount)
    TextView tvPriceReal;
    @BindView(R.id.iv_goods)
    ImageView ivGoods;

    @BindView(R.id.tv_deliver_num_txt)
    TextView tvDNumTxt;
    @BindView(R.id.tv_deliver_num)
    TextView tvDNum;
    @BindView(R.id.tv_copy)
    TextView tvCopy;

    private UserBean.UserData userInfoData;

    private String orderNum = "";
    private String orderPicUrl = "";
    private String orderProductName = "";

    public static final int REQUEST_CALL_PERMISSION = 10111; //拨号请求码


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_order_detail;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("订单详情", R.color.color_1A1A1A);
        setToolBarColor(R.color.colorWhite);
        setBackBtn(R.drawable.btn_back);
        showRightAdd(R.drawable.my_order_customerservice);


        userInfoData = BaseApplication.getInstance().getUserInfoData();
        Intent intent = getIntent();
        if (intent == null) return;


        int status = intent.getExtras().getInt(Constant.ORDER_STATUS);

        orderNum = intent.getExtras().getString(Constant.ORDER_NUMBER);
        orderPicUrl = intent.getExtras().getString(Constant.ORDER_PIC_URL);
        orderProductName = intent.getExtras().getString(Constant.ORDER_PRODUCT_NAME);

        if (status == 2) {
            //已取消
            tvStatus.setText("已取消订单");
            hideDeliver();
        } else if (status == 3) {
            //已支付
            tvStatus.setText("等待卖家发货");
            hideDeliver();
        } else if (status == 4) {
            //已发货
            tvStatus.setText("已发货");
            hideDeliverShowNum();
        }
        requestOrderDetail();
        tvCopy.setOnClickListener(this);
    }

    String numStr = "";

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_copy:
                //获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                String num = tvDNum.getText() + "";
                if (num == null || num.isEmpty()) return;
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("Label", num);
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                toast.toastShow("复制成功：" + num);
                numStr = num;
                break;
            case R.id.iv_right:
                call("tel:" + "18028767791");
        }
    }

    /**
     * 判断是否有某项权限
     *
     * @param string_permission 权限
     * @param request_code      请求码
     * @return
     */
    public boolean checkReadPermission(String string_permission, int request_code) {
        boolean flag = false;
        if (ContextCompat.checkSelfPermission(this, string_permission) == PackageManager.PERMISSION_GRANTED) {//已有权限
            flag = true;
        } else {//申请权限
            ActivityCompat.requestPermissions(this, new String[]{string_permission}, request_code);
        }
        return flag;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CALL_PERMISSION: //拨打电话
                if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {//失败
                    Toast.makeText(this, "请允许拨号权限后再试", Toast.LENGTH_SHORT).show();
                } else {//成功
                    call("tel:" + "18028767791");
                }
                break;
        }
    }

    /**
     * 拨打电话（直接拨打）
     *
     * @param telPhone 电话
     */
    public void call(String telPhone) {
        if (checkReadPermission(Manifest.permission.CALL_PHONE, REQUEST_CALL_PERMISSION)) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(telPhone));
            startActivity(intent);
        }
    }

    private void hideDeliver() {//等待卖家发货+取消订单 的时候 隐藏运输中信息
        ivIconD.setVisibility(View.GONE);
        tvStatusD.setVisibility(View.GONE);
        tvStatusAddressD.setVisibility(View.GONE);
        tvStatusTimeD.setVisibility(View.GONE);

        tvDNumTxt.setVisibility(View.GONE);
        tvDNum.setVisibility(View.GONE);
        tvCopy.setVisibility(View.GONE);
    }

    private void hideDeliverShowNum() {//等待卖家发货+取消订单 的时候 隐藏运输中信息 展示物流单号
        ivIconD.setVisibility(View.VISIBLE);
        tvStatusD.setVisibility(View.GONE);
        tvStatusAddressD.setVisibility(View.GONE);
        tvStatusTimeD.setVisibility(View.GONE);

        tvDNumTxt.setVisibility(View.VISIBLE);
        tvDNum.setVisibility(View.VISIBLE);
        tvCopy.setVisibility(View.VISIBLE);
    }

    private void requestOrderDetail() {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        Map<String, Object> params = new HashMap<>();
        //params.put("uId", userInfoData.uid);
        params.put("orderNumber", orderNum);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_CHANGE_LIST_DETAIL, params, this, new JsonCallback<ChangeDetailB>(ChangeDetailB.class) {
            @Override
            public void onSuccess(Response<ChangeDetailB> response) {
                if (response == null || response.body() == null) return;
                ChangeDetailB body = response.body();
                if (body.success) {
                    hideLoading();
                    if (body.getData() != null) {
                        tvOrderNum.setText(String.format("订单编号:%s", body.getData().orderNumber));
                        ImageUtils.setNormalImage(OrderDetailActivity.this, orderPicUrl, R.drawable.ic_placeholder, R.drawable.ic_placeholder, ivGoods);
                        tvGoods.setText(orderProductName);
                        double realPay = body.getData().amountRealpay;
                        tvPriceReal.setText(String.format("¥%.2f", realPay));
                        //=======================================================================
                        String aAddress = body.getData().signAddress;
                        String[] split = aAddress.split("=\\+=");
                        String area = split[0];
                        String address = split[1];
                        tvYourAddress.setText(area + address);
                        tvShopperName.setText(body.getData().signName);
                        tvTel.setText(body.getData().signPhone);
                        if (body.getData().payWay.equals("1")) {
                            tvPayStyle.setText("支付方式：" + "微信支付");
                        } else if (body.getData().payWay.equals("2")) {
                            tvPayStyle.setText("支付方式：" + "支付宝");
                        } else if (body.getData().payWay.equals("3")) {

                            tvPayStyle.setText("支付方式：" + "云闪付");
                        }
                        tvPayTime.setText("付款时间：" + TimeUtil.getStrTime3(body.getData().payTime));
                        if (body.getData().expNo == null || body.getData().expNo.isEmpty())
                            return;
                        tvDNum.setText(body.getData().expNo);
                    }
                } else {
                    hideLoading();
                    toast.toastShow(body.msg);
                }
            }
        });

    }
}
