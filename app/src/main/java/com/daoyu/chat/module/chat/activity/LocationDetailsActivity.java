package com.daoyu.chat.module.chat.activity;

import android.content.Intent;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseTitleActivity;
import com.daoyu.chat.common.Constant;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;

public class LocationDetailsActivity extends BaseTitleActivity {

    @BindView(R.id.mapView)
    MapView mapView;
    private double latitude;
    private double longitude;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_location_details;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("位置信息");
        Intent intent = getIntent();
        latitude = Double.parseDouble(intent.getStringExtra(Constant.LATITUDE));
        longitude = Double.parseDouble(intent.getStringExtra(Constant.LONGITUDE));
        AMap map = mapView.getMap();
        map.setTrafficEnabled(true);// 显示实时交通状况
        //地图模式可选类型：MAP_TYPE_NORMAL,MAP_TYPE_SATELLITE,MAP_TYPE_NIGHT
        map.setMapType(AMap.MAP_TYPE_NORMAL);// 卫星地图模式
        LatLng latLng = new LatLng(latitude, longitude);
        final Marker marker = map.addMarker(new MarkerOptions().position(latLng).snippet("位置"));
        marker.setDraggable(false);
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(latitude, longitude), 18, 30, 0));
        map.moveCamera(mCameraUpdate);

    }

    @Override
    public void savedInstanceState(Bundle savedInstanceState) {
        super.savedInstanceState(savedInstanceState);
        mapView.onCreate(savedInstanceState);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
