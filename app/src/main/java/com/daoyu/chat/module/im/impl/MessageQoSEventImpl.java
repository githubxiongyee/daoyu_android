package com.daoyu.chat.module.im.impl;

import android.util.Log;

import com.daoyu.chat.base.BaseActivity;
import com.daoyu.chat.module.home.activity.MainActivity;
import com.dy.dyim.android.event.MessageQoSEvent;
import com.dy.zserver.protocal.Protocal;

import java.util.ArrayList;


/**
 * Qos消息
 */
public class MessageQoSEventImpl implements MessageQoSEvent {
    private final static String TAG = "keyejxptwn";
    private BaseActivity mainGUI = null;

    @Override
    public void messagesLost(ArrayList<Protocal> lostMessages) {
        Log.d(TAG, "[MessageQoS]--收到系统的未实时送达事件通知，当前共有" + lostMessages.size() + "个包QoS保证机制结束，判定为【无法实时送达】！");
        for (int i = 0; i < lostMessages.size(); i++) {
            Protocal protocal = lostMessages.get(i);
            Log.d(TAG, "[MessageQoS]--消息内容为" + protocal.getFrom()+"---"+protocal.getTo()+"--"+protocal.getDataContent() );
        }
    }

    @Override
    public void messagesBeReceived(String theFingerPrint) {
        if (theFingerPrint != null) {
            Log.d(TAG, "【DEBUG_UI】收到对方已收到消息事件的通知，fp=" + theFingerPrint);

        }
    }

    public MessageQoSEventImpl setMainGUI(MainActivity mainGUI)
    {
        this.mainGUI = mainGUI;
        return this;
    }
}
