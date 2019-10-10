package com.daoyu.chat.module.chat.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseActivity;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.group.bean.GroupInfoBean;
import com.daoyu.chat.module.im.module.ChatTable;
import com.daoyu.chat.module.im.module.IMConstant;
import com.daoyu.chat.module.im.module.MessageDetailTable;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.service.MqttService;
import com.dy.dyim.android.core.LocalUDPDataSender;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import org.litepal.LitePal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class SendLocationActivity extends BaseActivity implements LocationSource, AMapLocationListener {

    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    @BindView(R.id.mapView)
    MapView mapView;
    private AMap amap;
    private MyLocationStyle myLocationStyle;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private String friendId;
    private String friendName;
    private String friendHeader;
    private String address;
    private String aoiName;
    private double latitude;
    private double longitude;
    private boolean isGroup;
    private GroupInfoBean.GroupInfoData groupInfoData;


    @Override
    public void savedInstanceState(Bundle savedInstanceState) {
        super.savedInstanceState(savedInstanceState);
        mapView.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_send_location;
    }

    @Override
    protected void initEvent() {
        tvTitle.setText("发送位置");
        tvComplete.setText("发送");
        Intent intent = getIntent();
        friendId = intent.getStringExtra(Constant.CONTACT_FRIEND_ID);
        friendName = intent.getStringExtra(Constant.CONTACT_NAME);
        friendHeader = intent.getStringExtra(Constant.FRIEND_HEADER);
        isGroup = intent.getBooleanExtra(Constant.IS_GROUP, false);
        if (isGroup) {
            groupInfoData = intent.getParcelableExtra(Constant.CONTACT_GROUP_INFO);
        }
        if (TextUtils.isEmpty(friendId)) {
            finish();
            return;
        }


        amap = mapView.getMap();
        amap.setLocationSource(this);
        amap.setMyLocationEnabled(true);
        UiSettings uiSettings = amap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);

        uiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
        uiSettings.setMyLocationButtonEnabled(true);
        amap.setMyLocationEnabled(true);
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(200); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        amap.setMyLocationStyle(myLocationStyle);
        tvCancel.setOnClickListener(v -> finish());
        tvComplete.setOnClickListener(v -> {
            if (latitude == 0 || longitude == 0) return;
            sendLocationMessage(TextUtils.isEmpty(aoiName) ? address : aoiName, String.valueOf(latitude), String.valueOf(longitude));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();//启动定位
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                tvComplete.setEnabled(true);
                latitude = aMapLocation.getLatitude();
                longitude = aMapLocation.getLongitude();
                CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(latitude, longitude), 18, 30, 0));
                amap.moveCamera(mCameraUpdate);
                address = aMapLocation.getAddress();
                aoiName = aMapLocation.getAoiName();
                deactivate();
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }


    /**
     * 发送位置
     *
     * @param
     */
    private void sendLocationMessage(String address, String latitude, String longitude) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        String uid = String.valueOf(userInfoData.uid);
        String headImg = userInfoData.headImg;
        String time = String.valueOf(System.currentTimeMillis());

        Map<String, Object> params = new HashMap<>();
        params.put("userid", uid);
        params.put("userImage", headImg);
        params.put("time", time);
        params.put("content", address);
        params.put("username", userInfoData.nickName);
        params.put("lat", latitude);
        params.put("lon", longitude);
        params.put("type", IMConstant.MessageType.LOCATION);
        if (isGroup) {
            params.put("chattype", 2);
            params.put("groupimg", groupInfoData.groupurl);
            params.put("groupname", groupInfoData.groupname);
            params.put("groupid", friendId);
        } else {
            params.put("chattype", 1);
        }

        MessageDetailTable sendMessage = new MessageDetailTable();

        sendMessage.message_type = IMConstant.MessageType.LOCATION;
        sendMessage.message_state = IMConstant.MessageStatus.SUCCESSED;
        sendMessage.avatar = headImg;
        sendMessage.user_id = uid;
        sendMessage.chat_type = 1;
        sendMessage.location = longitude + "," + latitude;
        sendMessage.message_time = time;
        sendMessage.message = address;

        if (isGroup) {
            sendMessage.chat_id = userInfoData.uid + "GL" + friendId;
            sendMessage.chat_type = 2;
            sendMessage.group_id = friendId;
            sendMessage.group_name = groupInfoData.groupname;
            sendMessage.group_img = groupInfoData.groupurl;
        } else {
            sendMessage.chat_type = 1;
            sendMessage.chat_id = userInfoData.uid + "DL" + friendId;
        }

        sendMessage.saveAsync().listen(success -> {
            if (success) {
                Log.d("TAG", "保存成功");
            }
        });
        List<ChatTable> chatTables = LitePal.where("chat_id = ?", isGroup ? (userInfoData.uid + "GL" + friendId) : (userInfoData.uid + "DL" + friendId)).find(ChatTable.class);

        if (chatTables == null || chatTables.size() <= 0) {
            ChatTable chatTable = new ChatTable();
            chatTable.mobile = userInfoData.userPhone;
            chatTable.chat_id = sendMessage.chat_id;
            chatTable.number = 0;
            chatTable.is_read = true;
            chatTable.last_message = "[位置]";
            chatTable.avatar = friendHeader;
            chatTable.user_id = friendId;
            chatTable.username = friendName;
            chatTable.top = false;
            chatTable.current_id = String.valueOf(userInfoData.uid);
            chatTable.message_type = IMConstant.MessageType.LOCATION;
            chatTable.message_time = time;
            if (isGroup) {
                chatTable.chat_type = true;
            }
            chatTable.saveAsync().listen(success -> {
                if (success) {
                    Log.d("TAG", "保存成功!");
                }
            });
        } else {
            ChatTable chatTable = chatTables.get(0);
            chatTable.last_message = "[位置]";
            chatTable.is_read = true;
            chatTable.number = 0;
            chatTable.username = friendName;
            chatTable.avatar = friendHeader;
            chatTable.current_id = String.valueOf(userInfoData.uid);
            chatTable.message_time = time;
            chatTable.saveAsync().listen(success -> Log.d("TAG", "更新成功!"));

        }
        if (isGroup) {
            MqttService.publish(new Gson().toJson(params), friendId);
            finish();
        } else {
            doSendMessage(new Gson().toJson(params), friendId);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void doSendMessage(String msg, String friendId) {
        new LocalUDPDataSender.SendCommonDataAsync(this, msg, friendId) {
            @Override
            protected void onPostExecute(Integer code) {
                if (code == 0) {
                    finish();
                }
            }
        }.execute();
    }
}
