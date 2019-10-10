package com.daoyu.chat.module.im.impl;

import android.util.Log;

import com.daoyu.chat.base.BaseActivity;
import com.daoyu.chat.module.home.activity.MainActivity;
import com.dy.dyim.android.event.ChatBaseEvent;

import java.util.Observer;

public class ChatBaseEventImpl implements ChatBaseEvent {
    private final static String TAG = "keyejxptwn";
    private BaseActivity mainGUI = null;

    // 本Observer目前仅用于登陆时（因为登陆与收到服务端的登陆验证结果
    // 是异步的，所以有此观察者来完成收到验证后的处理）
    private Observer loginOkForLaunchObserver = null;

    //@Override
    public void onLoginMessage(int dwErrorCode) {
        if (dwErrorCode == 0) {
            Log.i(TAG, "【DEBUG_UI】IM服务器登录/连接成功！");

        } else {
            Log.e(TAG, "【DEBUG_UI】IM服务器登录/连接失败，错误代码：" + dwErrorCode);

        }

        // 此观察者只有开启程序首次使用登陆界面时有用
        if (loginOkForLaunchObserver != null) {
            loginOkForLaunchObserver.update(null, dwErrorCode);
            loginOkForLaunchObserver = null;
        }
    }
    public ChatBaseEventImpl setMainGUI(MainActivity mainGUI)
    {
        this.mainGUI = mainGUI;
        return this;
    }


    //@Override
    public void onLinkCloseMessage(int dwErrorCode) {
        Log.e(TAG, "【DEBUG_UI】与IM服务器的网络连接出错关闭了，error：" + dwErrorCode);


    }

    public void setLoginOkForLaunchObserver(Observer loginOkForLaunchObserver) {
        this.loginOkForLaunchObserver = loginOkForLaunchObserver;
    }
}
