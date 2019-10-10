package com.daoyu.chat.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.daoyu.chat.BuildConfig;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.im.IMClientManager;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.SharedPreferenceUtil;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.leon.channel.helper.ChannelReaderUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

public class BaseApplication extends MultiDexApplication {

    private static Context sContext;
    private static BaseApplication sInstance;

    private OkHttpClient mOKHttpClient;
    private List<BaseActivity> mActivityList;
    private UserBean.UserData mUserData;
    public IMClientManager imClientManager;

    public SQLiteDatabase database;

    public static BaseApplication getInstance() {
        return sInstance;
    }

    public static Context getContext() {
        return sContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sContext = getApplicationContext();
        initOkHttp();

        LitePal.initialize(this);
        if (BuildConfig.DEBUG)
            Stetho.initializeWithDefaults(this);//初始化Stetho
        database = LitePal.getDatabase();
        String channel = ChannelReaderUtil.getChannel(getApplicationContext());
        Log.d("channel", TextUtils.isEmpty(channel) ? "not" : channel);
        UMConfigure.init(this, "5d739b684ca357c8d400014f", channel, UMConfigure.DEVICE_TYPE_PHONE, "I88Mq0ee9s9");
        UMConfigure.setLogEnabled(false);
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        initUMShare();
    }

    private void initUMShare() {
        PlatformConfig.setWeixin("wx83ad0876587bd103", "e2002b0bb99f7484ca7c416b63507662");
        PlatformConfig.setSinaWeibo("2165967643", "974edeb03410f5df487909cd5703b6ea", "http://sns.whalecloud.com");
        PlatformConfig.setQQZone("1109855174", "4MxkVhwBvDE77ceR");
    }

    public OkHttpClient getOkHttpClient() {
        return mOKHttpClient;
    }

    private void initOkHttp() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.NONE);
        loggingInterceptor.setColorLevel(Level.INFO);
        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(new StethoInterceptor());
        }
        builder.addInterceptor(loggingInterceptor);
        builder.readTimeout(30000, TimeUnit.MILLISECONDS);
        builder.writeTimeout(30000, TimeUnit.MILLISECONDS);
        builder.connectTimeout(30000, TimeUnit.MILLISECONDS);
        mOKHttpClient = builder.build();
        OkGo.getInstance().setOkHttpClient(mOKHttpClient);
    }

    public void initIMManager() {
        imClientManager = IMClientManager.getInstance();
    }

    public IMClientManager getImClientManager() {
        return imClientManager;
    }

    public void addActivity(BaseActivity activity) {
        if (mActivityList == null) {
            mActivityList = new ArrayList<>();
        }
        mActivityList.add(activity);
    }

    public void removeActivity(BaseActivity activity) {
        if (mActivityList != null && mActivityList.contains(activity)) {
            mActivityList.remove(activity);
        }
    }

    public void clearActivityList() {
        if (mActivityList != null && !mActivityList.isEmpty()) {
            for (BaseActivity activity : mActivityList) {
                activity.finish();
            }
            mActivityList = null;
        }
    }

    public void quitApp() {
        clearActivityList();
    }


    public void saveUserInfo(UserBean.UserData userData) {
        if (userData == null) return;
        this.mUserData = userData;
        String json = new Gson().toJson(userData);
        SharedPreferenceUtil.getInstance().putString(Constant.USER_INFO, json);
    }

    public UserBean.UserData getUserInfoData() {
        if (mUserData != null) {
            return mUserData;
        }
        String json = SharedPreferenceUtil.getInstance().getString(Constant.USER_INFO);
        if (TextUtils.isEmpty(json)) {
            mUserData = new UserBean.UserData();
        } else {
            mUserData = new Gson().fromJson(json, UserBean.UserData.class);
        }
        return mUserData;
    }


    public void loginOut() {
        SharedPreferenceUtil.getInstance().putString(Constant.USER_INFO, "");
        mUserData = null;

    }
}
