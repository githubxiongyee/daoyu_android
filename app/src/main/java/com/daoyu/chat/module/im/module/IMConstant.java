package com.daoyu.chat.module.im.module;

import com.daoyu.chat.module.chat.bean.LocalFriendData;

import java.util.HashMap;
import java.util.Map;

public class IMConstant {
    /**
     * 消息类型
     */
    public static class MessageType {
        public final static int TEXT = 1;          //文本类型
        public final static int IMAGE = 2;         //图片类型
        public final static int VOICE = 3;         //语音类型
        public final static int VIDEO = 4;         //视频类型
        public final static int LOCATION = 5;      //位置类型
        public final static int CARD = 6;          //名片类型
        public final static int REDPAPER = 7;      //红包
        public final static int GIF = 8;           //动图类型
        public final static int NOTE = 12;          //通知类型
        public final static int ASKFRIEND = 10;     //添加好友申请
        public final static int ANSWERFRIEND = 11;  //回复添加好友
        public final static int RECEIVE_PACKAGE = 9;//领取红包
    }

    /**
     * 消息发送状态
     */
    public static class MessageStatus {
        public final static int SUCCESSED = 1;       //发送成功
        public final static int FAILED = 2;         //发送失败
        public final static int DELIVERING = 3;     //发送中
    }

    public static Map<String, LocalFriendData> normalFriendMap = new HashMap<>();//正常的好友信息
}
