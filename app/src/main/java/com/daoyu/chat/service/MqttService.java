package com.daoyu.chat.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.event.MessageTotalEvent;
import com.daoyu.chat.event.MsgListClearEvent;
import com.daoyu.chat.module.group.bean.GroupChatListBean;
import com.daoyu.chat.module.group.bean.GroupInfoBean;
import com.daoyu.chat.module.im.module.ChatTable;
import com.daoyu.chat.module.im.module.MessageDetailTable;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.MessageController;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.lzy.okgo.model.Response;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MqttService extends Service {
    public final String TAG = MqttService.class.getSimpleName();
    public static MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mMqttConnectOptions;
    //public String HOST = "tcp://39.108.78.60:10908";//服务器地址
    public String HOST = "tcp://192.168.1.143:10908";//服务器地址
    public String USERNAME = "admin";//用户名
    public String PASSWORD = "admin";//密码
    public List<String> subscribeList = new ArrayList<>();
    private String uid;

    private void requestGroupList() {
        subscribeList.clear();
        subscribeList.add("grouprefuse");
        subscribeList.add("groupinvite");
        subscribeList.add("creategroup");
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_SEARCH_USER_ALL_GROUP, params, this, new JsonCallback<GroupChatListBean>(GroupChatListBean.class) {
            @Override
            public void onSuccess(Response<GroupChatListBean> response) {
                if (response == null || response.body() == null) return;
                GroupChatListBean body = response.body();
                if (body.code == 1) {
                    ArrayList<GroupInfoBean.GroupInfoData> groupInfoDatas = body.data;
                    if (groupInfoDatas == null || groupInfoDatas.size() <= 0) {
                        initMQTT();
                        return;
                    }
                    for (int i = 0; i < groupInfoDatas.size(); i++) {
                        subscribeList.add(groupInfoDatas.get(i).group_id);
                    }
                    initMQTT();
                }

            }

            @Override
            public void onError(Response<GroupChatListBean> response) {
                super.onError(response);
                initMQTT();
            }
        }, UrlConfig.BASE_GROUP_URL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        requestGroupList();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 发布
     *
     * @param message 消息
     */
    public static void publish(String message, String topic) {
        Integer qos = 1;
        Boolean retained = false;
        try {
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            if (mqttAndroidClient == null || TextUtils.isEmpty(message)) return;
            mqttAndroidClient.publish(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化
     */
    private void initMQTT() {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        uid = String.valueOf(userInfoData.uid);
        String serverURI = HOST;
        mqttAndroidClient = new MqttAndroidClient(this, serverURI, uid);
        mqttAndroidClient.setCallback(mqttCallback);
        mMqttConnectOptions = new MqttConnectOptions();
        mMqttConnectOptions.setConnectionTimeout(10); //设置超时时间，单位：秒
        mMqttConnectOptions.setKeepAliveInterval(60); //设置心跳包发送间隔，单位：秒
        mMqttConnectOptions.setUserName(USERNAME); //设置用户名
        mMqttConnectOptions.setPassword(PASSWORD.toCharArray()); //设置密码
        mMqttConnectOptions.setCleanSession(false);
        doClientConnection();

    }

    /**
     * 连接MQTT服务器
     */
    private void doClientConnection() {
        if (!mqttAndroidClient.isConnected() && isConnectIsNomarl()) {
            try {
                mqttAndroidClient.connect(mMqttConnectOptions, null, iMqttActionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.i(TAG, "当前网络名称：" + name);
            return true;
        } else {
            Log.i(TAG, "没有可用网络");
            new Handler().postDelayed(() -> doClientConnection(), 4000);
            return false;
        }
    }

    //MQTT是否连接成功的监听
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.i(TAG, "连接成功 ");
            try {
                if (mqttAndroidClient == null) return;
                if (subscribeList == null || subscribeList.size() <= 0) return;
                for (int i = 0; i < subscribeList.size(); i++) {
                    mqttAndroidClient.subscribe(subscribeList.get(i), 1);//订阅
                }
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            Log.i(TAG, "连接失败 ");
            doClientConnection();//连接失败，重连（可关闭服务器进行模拟）
        }
    };

    //订阅主题的回调
    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String messageMQTT = new String(message.getPayload());
            Log.e(TAG, "主题：" + topic + "  收到消息： " + messageMQTT);
            switch (topic) {
                case "grouprefuse"://移除群聊
                    systemMQTTMessage(uid, messageMQTT, 2);
                    break;
                case "groupinvite"://加入群聊
                    systemMQTTMessage(uid, messageMQTT, 1);
                    break;
                case "creategroup"://创建群聊
                    systemMQTTMessage(uid, messageMQTT, 0);
                    break;
                default:
                    JSONObject objectDefault = new JSONObject(messageMQTT);
                    String userid = objectDefault.optString("userid");
                    if (uid.equals(userid)) {
                        return;
                    }
                    MessageController.getInstance().broadcastMessage(messageMQTT);
                    break;
            }

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            Log.i(TAG, "IMqttDeliveryToken:" + arg0.toString());
        }

        @Override
        public void connectionLost(Throwable arg0) {
            Log.i(TAG, "连接断开 ");
            doClientConnection();
        }
    };

    private void systemMQTTMessage(String uid, String messageMQTT, int type) throws Exception {
        if (mqttAndroidClient == null) return;
        JSONObject object = new JSONObject(messageMQTT);
        String groupId = object.optString("group");
        JSONArray array = object.optJSONArray("users");
        boolean isHas = false;
        for (int i = 0; i < array.length(); i++) {
            String userId = array.optString(i);
            if (uid.equals(userId)) {
                isHas = true;
                break;
            }
        }
        if (isHas) {
            switch (type) {
                case 0:
                case 1:
                    mqttAndroidClient.subscribe(groupId, 1);
                    break;
                case 2:
                    mqttAndroidClient.unsubscribe(groupId);
                    LitePal.deleteAllAsync(MessageDetailTable.class, "chat_id = ?", uid + "GL" + groupId).listen(rowsAffected -> Log.d("TAG", "影响的记录" + rowsAffected));
                    LitePal.deleteAllAsync(ChatTable.class, "chat_id = ?", uid + "GL" + groupId).listen(rowsAffected -> Log.d("TAG", "影响的记录" + rowsAffected));
                    EventBus.getDefault().post(new MsgListClearEvent(groupId));
                    EventBus.getDefault().post(new MessageTotalEvent());
                    break;
            }

        }
    }

    @Override
    public void onDestroy() {
        try {
            mqttAndroidClient.disconnect();
            mqttAndroidClient = null;
        } catch (MqttException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
