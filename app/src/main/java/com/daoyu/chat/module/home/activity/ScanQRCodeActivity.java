package com.daoyu.chat.module.home.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.home.bean.QrAddFriendB;
import com.daoyu.chat.module.home.bean.QrOrderBean;
import com.daoyu.chat.module.home.bean.ScanAddFriendB;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class ScanQRCodeActivity extends BaseTitleActivity implements QRCodeView.Delegate {
    private static final String TAG = ScanQRCodeActivity.class.getSimpleName();
    @BindView(R.id.zx)
    ZXingView zx;
    @BindView(R.id.iv_flash)
    ImageView ivFlash;
    private boolean isFlash = false;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_scan_qrcode;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("扫一扫");
        ivFlash.setOnClickListener(this);
        zx.startCamera();
        zx.startSpotAndShowRect();
        zx.startSpot();
        zx.setDelegate(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.iv_flash:
                isFlash = !isFlash;
                if (isFlash) {
                    zx.openFlashlight();
                } else {
                    zx.closeFlashlight();
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        zx.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        zx.onDestroy();
        super.onDestroy();
    }


    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        String tipText = zx.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                zx.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                zx.getScanBoxView().setTipText(tipText);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.i(TAG, "result:" + result);

        switch (judgeScan(result)) {
            case 0://添加好友
                requestScanAddFriend(result);
                break;
            case 1://付款
                requestCreateOrder(checkCode(result));
                break;
            case 2:
                //requestCreateOrder(result);
                Intent intent = new Intent(ScanQRCodeActivity.this, ScanSendMoneyActivity.class);
                String[] split = result.split(">>");
                String s = split[1];
                intent.putExtra("fId", s);
                startActivity(intent);
                break;
        }
        finish();
    }

    private String checkCode(String result) {
        if (result.contains("https://qr.95516.com")) {
            String[] split = result.split("&dy=");
            String s = split[1];
            return s;
        } else {
            return result;
        }
    }

    private void requestCreateOrder(String qrVal) {
        showLoading("生成订单中...", false);
        Map<String, Object> params = new HashMap<>();
        params.put("bqrval", qrVal);
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        params.put("uId", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_PAYMENT_QR_TEST, params, this, new JsonCallback<QrOrderBean>(QrOrderBean.class) {
            @Override
            public void onSuccess(Response<QrOrderBean> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                QrOrderBean body = response.body();
                if (body.success) {
                    QrOrderBean.QrOrderData data = body.data;
                    if (data == null) return;
                    double bCreditTotal = data.bCreditTotal * 100;
                    userInfoData.creditTotal = bCreditTotal;
                    BaseApplication.getInstance().saveUserInfo(userInfoData);
                    Intent intent = new Intent(ScanQRCodeActivity.this, PaymentActivity.class);
                    intent.putExtra(Constant.QR_VAL, data);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onError(Response<QrOrderBean> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        });
    }

    private int judgeScan(String str) {
        int scanType = 0;
        if (str.contains(">>")) {
            //商户
            scanType = 2;
        } else if (str.contains("<<")) {
            //付款
            scanType = 1;
        } else {
            //加好友
            scanType = 0;
        }
        return scanType;
    }

    private void requestScanAddFriend(String userQrVal) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        Map<String, Object> params = new HashMap<>();
        params.put("uId", userInfoData.uid);
        params.put("userQrVal", userQrVal);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_QR_AAD_FRIEND, params, this, new JsonCallback<QrAddFriendB>(QrAddFriendB.class) {
            @Override
            public void onSuccess(Response<QrAddFriendB> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                QrAddFriendB body = response.body();
                if (body.getCode() == 1) {
                    QrAddFriendB.DataBean data = body.getData();
                    if (data == null) return;
                    requestIsFriend(data.getId(), userInfoData.uid, data.getMobile());

                } else {
                    toast.toastShow(body.getMsg());
                }
            }

            @Override
            public void onError(Response<QrAddFriendB> response) {
                super.onError(response);
                if (isActivityFinish) return;
                hideLoading();
            }
        });

    }

    private void requestIsFriend(int fId, int uId, String mobile) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_friend_id", fId);
        params.put("user_id", uId);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_IS_FRIEND, params, this, new JsonCallback<ScanAddFriendB>(ScanAddFriendB.class) {
            @Override
            public void onSuccess(Response<ScanAddFriendB> response) {
                if (isActivityFinish) return;
                hideLoading();
                if (response == null || response.body() == null) return;
                ScanAddFriendB body = response.body();
                if (body.code == 1) {

                    if (body.data.size() == 0) {
                        //不是好友
                        Intent intent = new Intent(ScanQRCodeActivity.this, ContactDetailsActivity.class);
                        intent.putExtra(Constant.CONTACT_ID, String.valueOf(fId));

                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(ScanQRCodeActivity.this, ContactDetailsActivity.class);
                        intent.putExtra(Constant.CONTACT_ID, String.valueOf(fId));
                        startActivity(intent);
                        finish();
                    }
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
}
