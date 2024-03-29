package com.daoyu.chat.utils;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.daoyu.chat.base.BaseApplication;

public class GDLocationUtils {

    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private static GDLocationUtils singleton;

    private GDLocationUtils() {

    }

    public static GDLocationUtils getInstance(AMapLocationListener locationListener) {
        if (singleton == null) {
            synchronized (GDLocationUtils.class) {
                if (singleton == null) {
                    singleton = new GDLocationUtils();
                    singleton.initLoc(locationListener);
                }
            }
        }
        return singleton;
    }

    public void initLoc(AMapLocationListener locationListener) {
        mLocationClient = new AMapLocationClient(BaseApplication.getContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(locationListener);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);


    }

    ////启动定位
    public void startLocation() {
        mLocationClient.startLocation();
    }

    public void deactivate(){
        if (mLocationClient!=null){
            mLocationClient.stopLocation();
        }
    }

}
