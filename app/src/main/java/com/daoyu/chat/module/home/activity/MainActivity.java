package com.daoyu.chat.module.home.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseActivity;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.base.BaseFragment;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.event.MessageTotalEvent;
import com.daoyu.chat.event.ReceiveMessageEvent;
import com.daoyu.chat.event.SwitchContactEvent;
import com.daoyu.chat.module.chat.bean.LocalFriendData;
import com.daoyu.chat.module.home.adapter.ViewPagerAdapter;
import com.daoyu.chat.module.home.bean.ContactFriendBean;
import com.daoyu.chat.module.home.bean.OffLineMessageBean;
import com.daoyu.chat.module.home.fragment.ContactFragment;
import com.daoyu.chat.module.home.fragment.MineFragment;
import com.daoyu.chat.module.home.fragment.MsgChatFragment;
import com.daoyu.chat.module.home.fragment.RedBagFragment;
import com.daoyu.chat.module.home.utils.UpgradeUtil;
import com.daoyu.chat.module.im.module.ChatTable;
import com.daoyu.chat.module.im.module.IMConstant;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.bean.UpgradeApkB;
import com.daoyu.chat.module.system.bean.SignInfoB;
import com.daoyu.chat.module.system.dialog.SignInDialog;
import com.daoyu.chat.service.MqttService;
import com.daoyu.chat.utils.GDLocationUtils;
import com.daoyu.chat.utils.NoLeakHandler;
import com.daoyu.chat.utils.ParserMessageUtils;
import com.daoyu.chat.utils.RuntimeRationale;
import com.daoyu.chat.utils.SharedPreferenceUtil;
import com.daoyu.chat.utils.download.DownLoadUtil;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.daoyu.chat.view.NoneSwipeViewPager;
import com.dy.dyim.android.core.LocalUDPDataSender;
import com.leon.channel.helper.ChannelReaderUtil;
import com.lzy.okgo.model.Response;
import com.umeng.analytics.MobclickAgent;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener, AMapLocationListener {
    @BindView(R.id.rg_menu)
    RadioGroup rgMenu;
    @BindView(R.id.view_pager)
    NoneSwipeViewPager viewPager;

    @BindView(R.id.rb_chat)
    RadioButton rbChat;

    @BindView(R.id.tv_number)
    TextView tvNumber;
    private int numberTotal = 0;

    private long mLastBackPressTime;
    private UserBean.UserData userInfoData;
    private NoLeakHandler handler;
    private GDLocationUtils gdlocation;
    public static double latitude;
    public static double longitude;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        showNumberTotal();
    }

    @Override
    public void handleMessage(Message message) {
        if (message == null) return;
        int what = message.what;
        if (what == 1) {
            doLoginIM(String.valueOf(userInfoData.uid), userInfoData.token);
        }
    }

    @Override
    protected void initEvent() {
        requestPermission(Permission.Group.LOCATION);
        handler = new NoLeakHandler(this);
        rgMenu.setOnCheckedChangeListener(this);
        List<BaseFragment> fragmentList = new ArrayList<>();
        fragmentList.clear();
        fragmentList.add(new RedBagFragment());
        fragmentList.add(new MsgChatFragment());
        fragmentList.add(new MineFragment());
        fragmentList.add(new ContactFragment());
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(4);
        rgMenu.check(R.id.rb_red_bag);
        rbChat.setOnCheckedChangeListener(this);
        userInfoData = BaseApplication.getInstance().getUserInfoData();
        initFriendListMap();
        Intent intent = new Intent(MainActivity.this, MqttService.class);
        startService(intent);
        requestSignInfo();
        timeCompare();//改成19号开始升级
        SharedPreferenceUtil.getInstance().putBoolean(Constant.IS_FIRST_IN, false);
    }

    SignInDialog signInDialog;

    private void showSignInDialog() {
        if (signInDialog != null) {
            signInDialog.dismissAllowingStateLoss();
        }
        signInDialog = signInDialog.getInstance();
        if (!signInDialog.isAdded()) {
            signInDialog.show(getSupportFragmentManager(), "chooseSignDialog");
        } else {
            signInDialog.dismissAllowingStateLoss();
        }
    }

    /**
     * 得到上一次签到信息
     */
    private void requestSignInfo() {
        Map<String, Object> params = new HashMap<>();
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        params.put("user_id", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_GET_LAST_SIGN, params, this, new JsonCallback<SignInfoB>(SignInfoB.class) {
            @Override
            public void onSuccess(Response<SignInfoB> response) {
                //if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                SignInfoB body = response.body();
                if (body.code == 1) {
                    if (body.data == null) return;
                    SignInfoB.SignInfoData data = body.data;
                    if (data.lastCheckInDate == null) {
                        showSignInDialog();
                    } else if (data.lastCheckInDate != null) {
                        if (!isToday(data.lastCheckInDate)) {
                            showSignInDialog();
                        }
                    }
                }
            }
        });
    }

    private boolean isToday(String time) {
        SimpleDateFormat todayFormat = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());
        String today = todayFormat.format(new Date());
        if (time.equals(today)) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void doLoginIM(String uid, String password) {
        new LocalUDPDataSender.SendLoginDataAsync(this, uid, password) {
            @Override
            protected void fireAfterSendLogin(int code) {
                if (code == 0) {
                    BaseApplication.getInstance().getImClientManager().getBaseEventListener().setLoginOkForLaunchObserver((observable, data) -> {
                    });
                } else {
                    Log.d("TAG", "数据发送失败。错误码是：" + code);
                }
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        long currentMillis = System.currentTimeMillis();
        if (currentMillis - mLastBackPressTime > 2000) {
            toast.toastShow(getResources().getString(R.string.text_press_quit));
            mLastBackPressTime = currentMillis;
        } else {
            BaseApplication.getInstance().quitApp();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSwitchContactEvent(SwitchContactEvent event) {
        if (event == null) return;
        rgMenu.clearCheck();
        viewPager.setCurrentItem(3, false);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_chat:
                viewPager.setCurrentItem(1, false);
                break;
            case R.id.rb_red_bag:
                viewPager.setCurrentItem(0, false);
                break;
            case R.id.rb_mine:
                viewPager.setCurrentItem(2, false);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (gdlocation == null) return;
        gdlocation.deactivate();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.rb_chat:
                rgMenu.check(R.id.rb_chat);
                break;
        }
    }

    private void showNumberTotal() {
        LitePal.where("current_id = ?", String.valueOf(userInfoData.uid)).findAsync(ChatTable.class).listen(list -> {
            if (list != null && list.size() > 0) {
                numberTotal = 0;
                for (int i = 0; i < list.size(); i++) {
                    ChatTable chatTable = list.get(i);
                    boolean shield = chatTable.shield;
                    if (shield) continue;
                    numberTotal += chatTable.number;
                }
                if (numberTotal == 0) {
                    tvNumber.setVisibility(View.GONE);
                } else if (numberTotal > 99) {
                    tvNumber.setVisibility(View.VISIBLE);
                    tvNumber.setText("99+");
                } else {
                    tvNumber.setVisibility(View.VISIBLE);
                    tvNumber.setText(String.valueOf(numberTotal));
                }
            } else {
                tvNumber.setVisibility(View.GONE);
            }
        });

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageTotalEvent(MessageTotalEvent event) {
        if (event == null) return;
        showNumberTotal();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceviceMessageEvent(ReceiveMessageEvent event) {
        if (event != null) {
            showNumberTotal();
        }

    }

    private void initFriendListMap() {
        IMConstant.normalFriendMap.clear();
        requestContactFriend();
    }

    /**
     * 白名单列表
     */
    private void requestContactFriend() {
        Map<String, Object> params = new HashMap<>();
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        params.put("user_id", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_WHITE_LABEL_LIST, params, this, new JsonCallback<ContactFriendBean>(ContactFriendBean.class) {
            @Override
            public void onSuccess(Response<ContactFriendBean> response) {
                if (isActivityFinish) return;

                if (response == null || response.body() == null) return;
                ContactFriendBean body = response.body();
                ArrayList<ContactFriendBean.ContactFriendData> contactFriendList = body.data;

                if (contactFriendList != null && contactFriendList.size() > 0) {
                    for (int i = 0; i < contactFriendList.size(); i++) {
                        ContactFriendBean.ContactFriendData contactFriendData = contactFriendList.get(i);
                        IMConstant.normalFriendMap.put(String.valueOf(contactFriendData.userFriendId), new LocalFriendData(contactFriendData.fremarks, 0));
                    }
                    requestBlackList();
                } else {
                    requestBlackList();
                }


            }
        });
    }

    private void requestBlackList() {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_BLACK_LIST_CONTACT, params, this, new JsonCallback<ContactFriendBean>(ContactFriendBean.class) {
            @Override
            public void onSuccess(Response<ContactFriendBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                ContactFriendBean body = response.body();
                if (body.success) {
                    ArrayList<ContactFriendBean.ContactFriendData> data = body.data;

                    if (data != null && data.size() > 0) {
                        for (int i = 0; i < data.size(); i++) {
                            ContactFriendBean.ContactFriendData contactFriendData = data.get(i);
                            IMConstant.normalFriendMap.put(String.valueOf(contactFriendData.userFriendId), new LocalFriendData(contactFriendData.fremarks, 1));
                        }
                        requestOffLineMessage();
                    } else {
                        requestOffLineMessage();
                    }

                }
            }
        });
    }

    private void requestOffLineMessage() {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_OFFLINE_MESSAGE, params, this, new JsonCallback<OffLineMessageBean>(OffLineMessageBean.class) {
            @Override
            public void onSuccess(Response<OffLineMessageBean> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                OffLineMessageBean body = response.body();
                if (body.success) {
                    ArrayList<OffLineMessageBean.OffLineMessageData> data = body.data;
                    if (data == null || data.size() <= 0) {
                        doLoginIM(String.valueOf(userInfoData.uid), userInfoData.token);
                    } else {
                        for (int i = 0; i < data.size(); i++) {
                            OffLineMessageBean.OffLineMessageData offLineMessageData = data.get(i);
                            String dataContent = offLineMessageData.dataContent;
                            if (TextUtils.isEmpty(dataContent)) return;
                            ParserMessageUtils.parser(offLineMessageData.user_friend_id, dataContent);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessageDelayed(1, 35000);
                    }
                }
            }
        }, UrlConfig.BASE_GROUP_URL);
    }

    private void requestUpgrade() {
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_UPGRADE, null, MainActivity.this, new JsonCallback<UpgradeApkB>(UpgradeApkB.class) {
            @Override
            public void onSuccess(Response<UpgradeApkB> response) {
                if (isActivityFinish) return;
                if (response == null || response.body() == null) return;
                UpgradeApkB body = response.body();
                if (body.getCode() == 1) {
                    if (body.getData() == null || body.getData().size() == 0) return;
                    UpgradeApkB.DataBean dataBean = body.getData().get(0);
                    String remarks = dataBean.getRemarks();
                    String msg = TextUtils.isEmpty(remarks) ? "" : remarks;
                    String apkUrl = dataBean.getUrl().trim();
                    String ver = dataBean.getConstantName();
                    if (UpgradeUtil.getVersionName(MainActivity.this).equals(ver)) {
                        Log.d("TAG", "当前是最新版本");
                    } else {
                        if (TextUtils.isEmpty(apkUrl)) return;
                        String channel = ChannelReaderUtil.getChannel(getApplicationContext());
                        if (!TextUtils.isEmpty(channel)) {
                            String userPhone = userInfoData.userPhone;
                            if ("18028767791".equals(userPhone)) {
                                Log.d("TAG", "渠道包账号18028767791不升级");
                                return;
                            }
                        }
                        showUploadDialog(msg, apkUrl);
                        return;
                    }
                } else {
                    toast.toastShow(body.getMsg());
                }
            }
        });
    }

    private void timeCompare() {
        long current = System.currentTimeMillis();
        Date cDate = new Date(current);
        //注意：传过来的时间格式必须要和这里填入的时间格式相同
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String cStr = dateFormat.format(cDate);

            Date date1 = dateFormat.parse(cStr);
            Date date2 = dateFormat.parse("2019-09-19 00:00:00");
            if (date2.getTime() > date1.getTime()) {

            } else {
                requestUpgrade();
            }
        } catch (Exception e) {

        }
    }

    private void showUploadDialog(String msg, final String apkUrl) {
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);
        normalDialog.setTitle("版本升级");
        normalDialog.setMessage(msg);

        normalDialog.setPositiveButton("升级", (dialog, which) -> {
            dialog.dismiss();
            AndPermission.with(this)
                    .runtime()
                    .permission(Permission.Group.STORAGE)
                    .rationale(new RuntimeRationale())
                    .onGranted(data -> {
                        long aLong = SharedPreferenceUtil.getInstance().getLong(Constant.DOWNLOAD_ID);
                        if (aLong != 0) {
                            return;
                        }
                        DownLoadUtil.startDownload(MainActivity.this, apkUrl, "潜言", "daoyu.apk");
                        toast.toastShow("正在下载，请在通知栏查看进度");
                    })
                    .onDenied(data -> toast.toastShow("文件权限已被您拒绝"))
                    .start();

        });
        normalDialog.show();
    }

    public void onPermissionGrant(List<String> permissions) {
        if (AndPermission.hasPermissions(this, Permission.Group.LOCATION)) {
            gdlocation = GDLocationUtils.getInstance(this);
            gdlocation.startLocation();
        }
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation.getErrorCode() == 0) {
            latitude = aMapLocation.getLatitude();
            longitude = aMapLocation.getLongitude();
            Log.e("info", "/////////======退出登录之后==重登===经纬度===" + longitude + "---" + latitude);
            gdlocation.deactivate();
        }
    }


}