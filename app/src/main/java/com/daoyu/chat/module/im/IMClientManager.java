package com.daoyu.chat.module.im;

import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.im.impl.ChatBaseEventImpl;
import com.daoyu.chat.module.im.impl.ChatTransDataEventImpl;
import com.daoyu.chat.module.im.impl.MessageQoSEventImpl;
import com.dy.dyim.android.ClientCoreSDK;
import com.dy.dyim.android.conf.ConfigEntity;


public class IMClientManager {
    private static String TAG = IMClientManager.class.getSimpleName();

    private static IMClientManager instance = null;

    private boolean init = false;

    private ChatBaseEventImpl baseEventListener = null;

    private ChatTransDataEventImpl transDataListener = null;
    private MessageQoSEventImpl messageQoSListener = null;


    public static IMClientManager getInstance() {
        if (instance == null)
            instance = new IMClientManager();
        return instance;
    }

    private IMClientManager() {
        initIMSDK();
    }

    public void initIMSDK() {
        if (!init) {
            // 设置AppKey
            ConfigEntity.appKey = Constant.ImConfiguration.IM_APP_KEY;
            // 设置服务器ip和服务器端口
            ConfigEntity.serverIP = Constant.ImConfiguration.SERVER_IP;
            ConfigEntity.serverUDPPort = Constant.ImConfiguration.SERVER_UDP_PORT;
            ConfigEntity.setSenseMode(ConfigEntity.SenseMode.MODE_10S);//配置灵敏度
            ClientCoreSDK.DEBUG = true;//输入日志
            ClientCoreSDK.autoReLogin = true;
            // 设置事件回调
            baseEventListener = new ChatBaseEventImpl();
            transDataListener = new ChatTransDataEventImpl();
            messageQoSListener = new MessageQoSEventImpl();
            ClientCoreSDK.getInstance().init(BaseApplication.getContext());
            ClientCoreSDK.getInstance().setChatBaseEvent(baseEventListener);
            ClientCoreSDK.getInstance().setChatTransDataEvent(transDataListener);
            ClientCoreSDK.getInstance().setMessageQoSEvent(messageQoSListener);
            init = true;
        }
    }

    public void release() {
        ClientCoreSDK.getInstance().release();
        resetInitFlag();
    }

    /**
     * 重置init标识。
     * <p>
     * <b>重要说明：</b>不退出APP的情况下，重新登陆时记得调用一下本方法，不然再
     * 次调用 {@link #initIMSDK()} ()} 时也不会重新初始化MobileIMSDK（
     * 详见 {@link #initIMSDK()}代码）而报 code=203错误！
     */
    public void resetInitFlag() {
        init = false;
    }

    public ChatTransDataEventImpl getTransDataListener() {
        return transDataListener;
    }

    public ChatBaseEventImpl getBaseEventListener() {
        return baseEventListener;
    }

    public MessageQoSEventImpl getMessageQoSListener() {
        return messageQoSListener;
    }
}
