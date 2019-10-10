package com.daoyu.chat.utils;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.event.ChatListUpdateEvent;
import com.daoyu.chat.event.ReceiveMessageEvent;
import com.daoyu.chat.module.im.module.ChatTable;
import com.daoyu.chat.module.im.module.IMConstant;
import com.daoyu.chat.module.im.module.MessageDetailTable;
import com.daoyu.chat.module.login.bean.UserBean;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.LinkedList;
import java.util.List;

/**
 * 消息管理类
 */

public class MessageController {
    private boolean isSpeaking;
    private MediaPlayer mMedialPlayer;
    private static MessageController sInstance;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private LinkedList<String> mMessageQueue = new LinkedList<>();

    private MessageController() {
    }

    public static MessageController getInstance() {
        if (sInstance == null) {
            synchronized (MessageController.class) {
                if (sInstance == null) {
                    sInstance = new MessageController();
                }
            }
        }
        return sInstance;
    }


    /**
     * 消息
     *
     * @param messageData
     */
    public void broadcastMessage(String messageData) {
        if (TextUtils.isEmpty(messageData)) return;
        queueMessageData(messageData);
        mHandler.post(() -> startPlayMessageQueue());
    }


    /**
     * 将消息加入到消息队列
     *
     * @param message 消息数据
     */
    private void queueMessageData(String message) {
        mMessageQueue.add(message);
    }

    private synchronized void startPlayMessageQueue() {
        if (mMessageQueue.isEmpty() || isSpeaking) return;
        isSpeaking = true;
        mMedialPlayer = MediaPlayer.create(BaseApplication.getContext(), R.raw.ding);
        if (mMedialPlayer == null) return;
        mMedialPlayer.setOnCompletionListener((mp -> {
            mp.release();
            String firstMsg = mMessageQueue.peekFirst(); // 读取第一条
            if (!TextUtils.isEmpty(firstMsg)) {
                parseJsonData(firstMsg);
            } else {
                isSpeaking = false;
                startPlayMessageQueue(); // 继续处理下一条
            }
        }));
        mMedialPlayer.start();
    }

    private void parseJsonData(String json) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        if (userInfoData == null) return;
        try {
            JSONObject object = new JSONObject(json);
            int typeMessage = object.optInt("type");
            switch (typeMessage) {
                case IMConstant.MessageType.TEXT://文本
                    receiveTextMessage(userInfoData, object);
                    break;
                case IMConstant.MessageType.IMAGE://图片
                    receiveImageMessage(userInfoData, object);
                    break;
                case IMConstant.MessageType.REDPAPER://红包
                    receiveRadPackageMessage(userInfoData, object);
                    break;
                case IMConstant.MessageType.RECEIVE_PACKAGE://红包回执
                    receivePackageReceipt(userInfoData, object);
                    break;
                case IMConstant.MessageType.CARD://名片
                    receiveCardMessage(userInfoData, object);
                    break;
                case IMConstant.MessageType.LOCATION://位置
                    receiveLocation(userInfoData, object);
                    break;
                case IMConstant.MessageType.VOICE://语音
                    receiveVoiceMessage(userInfoData, object);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 收到文字消息
     *
     * @param userInfoData
     * @param object
     */
    private void receiveTextMessage(UserBean.UserData userInfoData, JSONObject object) {

        MessageDetailTable message = new MessageDetailTable();
        message.message_type = IMConstant.MessageType.TEXT;

        String userId = object.optString("userid");
        message.user_id = userId;

        String time = object.optString("time");
        message.message_time = time;

        String content = object.optString("content");
        message.message = content;

        String username = object.optString("username");
        message.username = username;

        String userImage = object.optString("userImage");
        message.avatar = userImage;

        int chattype = object.optInt("chattype");
        message.chat_type = chattype;
        if (chattype == 1) {
            message.chat_id = userInfoData.uid + "DL" + userId;
        } else {
            String groupimg = object.optString("groupimg");
            message.group_img = groupimg;
            String groupname = object.optString("groupname");
            message.group_name = groupname;
            String groupid = object.optString("groupid");
            message.group_id = groupid;
            message.chat_id = userInfoData.uid + "GL" + groupid;
        }

        updateUserHeaderNick(userInfoData, message, chattype == 1 ? userId : message.group_id, chattype, message.avatar, message.username);

        mMessageQueue.pollFirst(); // 第一条处理完成，移出队列
        isSpeaking = false;
        startPlayMessageQueue();
    }


    /**
     * 收到用户图片消息
     *
     * @param userInfoData
     * @param object
     */
    private void receiveImageMessage(UserBean.UserData userInfoData, JSONObject object) {
        MessageDetailTable message = new MessageDetailTable();
        message.message_type = IMConstant.MessageType.IMAGE;

        String userId = object.optString("userid");
        message.user_id = userId;

        int imageWidth = object.optInt("imageWidth");
        message.width = imageWidth;

        int imageHeight = object.optInt("imageHeight");
        message.height = imageHeight;


        String time = object.optString("time");
        message.message_time = time;

        String content = object.optString("content");
        message.message = content;

        String username = object.optString("username");
        message.username = username;

        String userImage = object.optString("userImage");
        message.avatar = userImage;

        int chattype = object.optInt("chattype");
        message.chat_type = chattype;

        if (chattype == 1) {
            message.chat_id = userInfoData.uid + "DL" + userId;
        } else {
            String groupimg = object.optString("groupimg");
            message.group_img = groupimg;
            String groupname = object.optString("groupname");
            message.group_name = groupname;
            String groupid = object.optString("groupid");
            message.group_id = groupid;
            message.chat_id = userInfoData.uid + "GL" + groupid;
        }

        saveMsgList(message, userInfoData);

        updateUserHeaderNick(userInfoData, message, chattype == 1 ? userId : message.group_id, chattype, userImage, username);

        mMessageQueue.pollFirst(); // 第一条处理完成，移出队列
        isSpeaking = false;
        startPlayMessageQueue();
    }

    /**
     * 收到用户红包消息
     */
    private void receiveRadPackageMessage(UserBean.UserData userInfoData, JSONObject object) {
        MessageDetailTable message = new MessageDetailTable();
        message.message_type = IMConstant.MessageType.REDPAPER;

        String userId = object.optString("userid");
        message.user_id = userId;


        String time = object.optString("time");
        message.message_time = time;

        String content = object.optString("content");
        message.red_id = content;

        String redPackage = object.optString("redPackage");

        message.message = redPackage;


        String username = object.optString("username");
        message.username = username;

        String userImage = object.optString("userImage");
        message.avatar = userImage;

        int chattype = object.optInt("chattype");
        message.chat_type = chattype;

        message.message_state = IMConstant.MessageStatus.DELIVERING;

        if (chattype == 1) {
            message.chat_id = userInfoData.uid + "DL" + userId;
        } else {
            String groupimg = object.optString("groupimg");
            message.group_img = groupimg;
            String groupname = object.optString("groupname");
            message.group_name = groupname;
            String groupid = object.optString("groupid");
            message.group_id = groupid;
            message.chat_id = userInfoData.uid + "GL" + groupid;
        }
        updateUserHeaderNick(userInfoData, message, chattype == 1 ? userId : message.group_id, chattype, userImage, username);

        mMessageQueue.pollFirst(); // 第一条处理完成，移出队列
        isSpeaking = false;
        startPlayMessageQueue();
    }


    /**
     * 收到名片
     *
     * @param userInfoData
     * @param object
     */
    private void receiveCardMessage(UserBean.UserData userInfoData, JSONObject object) {
        MessageDetailTable message = new MessageDetailTable();
        message.message_type = IMConstant.MessageType.CARD;

        String userId = object.optString("userid");
        message.user_id = userId;


        String time = object.optString("time");
        message.message_time = time;

        String content = object.optString("content");
        message.message = content;

        String username = object.optString("username");
        message.username = username;

        String userImage = object.optString("userImage");
        message.avatar = userImage;

        int chattype = object.optInt("chattype");
        message.chat_type = chattype;

        if (chattype == 1) {
            message.chat_id = userInfoData.uid + "DL" + userId;
        } else {
            String groupimg = object.optString("groupimg");
            message.group_img = groupimg;
            String groupname = object.optString("groupname");
            message.group_name = groupname;
            String groupid = object.optString("groupid");
            message.group_id = groupid;
            message.chat_id = userInfoData.uid + "GL" + groupid;

        }
        String card = object.optString("card");
        message.card_name = card;
        String cardImage = object.optString("cardImage");
        message.card_image = cardImage;


        updateUserHeaderNick(userInfoData, message, chattype == 1 ? userId : message.group_id, chattype, message.avatar, message.username);

        mMessageQueue.pollFirst(); // 第一条处理完成，移出队列
        isSpeaking = false;
        startPlayMessageQueue();
    }

    private void receiveLocation(UserBean.UserData userInfoData, JSONObject object) {
        MessageDetailTable message = new MessageDetailTable();
        message.message_type = IMConstant.MessageType.LOCATION;

        String userId = object.optString("userid");
        message.user_id = userId;


        String time = object.optString("time");
        message.message_time = time;

        String content = object.optString("content");
        message.message = content;

        String username = object.optString("username");
        message.username = username;

        String userImage = object.optString("userImage");
        message.avatar = userImage;

        String lon = object.optString("lon");
        String lat = object.optString("lat");
        message.location = lon + "," + lat;

        int chattype = object.optInt("chattype");
        message.chat_type = chattype;
        if (chattype == 1) {
            message.chat_id = userInfoData.uid + "DL" + userId;
        } else {
            String groupimg = object.optString("groupimg");
            message.group_img = groupimg;
            String groupname = object.optString("groupname");
            message.group_name = groupname;
            String groupid = object.optString("groupid");
            message.group_id = groupid;
            message.chat_id = userInfoData.uid + "GL" + groupid;
        }

        updateUserHeaderNick(userInfoData, message, chattype == 1 ? userId : message.group_id, chattype, message.avatar, message.username);

        mMessageQueue.pollFirst(); // 第一条处理完成，移出队列
        isSpeaking = false;
        startPlayMessageQueue();
    }


    /**
     * 收到语音消息
     *
     * @param userInfoData
     * @param object
     */
    private void receiveVoiceMessage(UserBean.UserData userInfoData, JSONObject object) {

        MessageDetailTable message = new MessageDetailTable();
        message.message_type = IMConstant.MessageType.VOICE;

        String userId = object.optString("userid");
        message.user_id = userId;


        String time = object.optString("time");
        message.message_time = time;

        String content = object.optString("content");
        message.message = content;

        String username = object.optString("username");
        message.username = username;

        String userImage = object.optString("userImage");
        message.avatar = userImage;

        String voiceDuration = object.optString("voiceDuration");
        message.voice_id = voiceDuration;

        int chattype = object.optInt("chattype");
        message.chat_type = chattype;
        if (chattype == 1) {
            message.chat_id = userInfoData.uid + "DL" + userId;
        } else {
            String groupimg = object.optString("groupimg");
            message.group_img = groupimg;
            String groupname = object.optString("groupname");
            message.group_name = groupname;
            String groupid = object.optString("groupid");
            message.group_id = groupid;
            message.chat_id = userInfoData.uid + "GL" + groupid;
        }
        updateUserHeaderNick(userInfoData, message, chattype == 1 ? userId : message.group_id, chattype, message.avatar, message.username);

        mMessageQueue.pollFirst(); // 第一条处理完成，移出队列
        isSpeaking = false;
        startPlayMessageQueue();

    }


    /**
     * 领取红包回执
     */
    private void receivePackageReceipt(UserBean.UserData userInfoData, JSONObject object) {
        MessageDetailTable message = new MessageDetailTable();
        message.message_type = IMConstant.MessageType.RECEIVE_PACKAGE;

        String userId = object.optString("userid");
        message.user_id = userId;


        String time = object.optString("time");
        message.message_time = time;

        String content = object.optString("content");
        message.message = content;

        String username = object.optString("username");
        message.username = username;

        String userImage = object.optString("userImage");
        message.avatar = userImage;

        int chattype = object.optInt("chattype");
        message.chat_type = chattype;
        if (chattype == 1) {
            message.chat_id = userInfoData.uid + "DL" + userId;
        } else {
            String groupimg = object.optString("groupimg");
            message.group_img = groupimg;
            String groupname = object.optString("groupname");
            message.group_name = groupname;
            String groupid = object.optString("groupid");
            message.group_id = groupid;
            message.chat_id = userInfoData.uid + "GL" + groupid;
        }
        message.saveAsync().listen(success -> EventBus.getDefault().post(new ReceiveMessageEvent(chattype == 1 ? userId : message.group_id)));
        MessageDetailTable table = new MessageDetailTable();
        table.avatar = userImage;
        table.username = username;
        if (chattype == 2) {
            table.group_img = message.group_img;
            table.group_name = message.group_name;
        }
        table.updateAllAsync("chat_id = ? and user_id = ?", message.chat_id, message.user_id).listen(rowsAffected -> Log.d("TAG", "受影响的记录数:" + rowsAffected));
        mMessageQueue.pollFirst(); // 第一条处理完成，移出队列
        isSpeaking = false;
        startPlayMessageQueue();
    }


    /**
     * 更新用户头像信息
     *
     * @param userInfoData
     * @param message
     * @param userId
     * @param chattype
     * @param avatar
     * @param username2
     */
    private void updateUserHeaderNick(UserBean.UserData userInfoData, MessageDetailTable message, String userId, int chattype, String avatar, String username2) {
        message.saveAsync().listen(success -> EventBus.getDefault().post(new ReceiveMessageEvent(userId)));

        saveMsgList(message, userInfoData);

        MessageDetailTable table = new MessageDetailTable();
        table.avatar = avatar;
        table.username = username2;
        if (chattype == 2) {
            table.group_img = message.group_img;
            table.group_name = message.group_name;
        }
        table.updateAllAsync("chat_id = ? and user_id = ?", message.chat_id, message.user_id).listen(rowsAffected -> Log.d("TAG", "受影响的记录数:" + rowsAffected));
    }

    /**
     * 保存到消息列表数据库
     *
     * @param message
     * @param userInfoData
     */
    private void saveMsgList(MessageDetailTable message, UserBean.UserData userInfoData) {
        List<ChatTable> chatTables = LitePal.where("chat_id = ?", message.chat_id).find(ChatTable.class);
        if (chatTables == null || chatTables.size() <= 0) {
            ChatTable chatTable = new ChatTable();
            chatTable.chat_id = message.chat_id;
            chatTable.number = 1;
            chatTable.is_read = true;
            switch (message.message_type) {
                case IMConstant.MessageType.TEXT:
                    chatTable.last_message = message.message;
                    break;
                case IMConstant.MessageType.IMAGE:
                    chatTable.last_message = "[图片]";
                    break;
                case IMConstant.MessageType.CARD:
                    chatTable.last_message = "[名片]";
                    break;
                case IMConstant.MessageType.VOICE:
                    chatTable.last_message = "[语音]";
                    break;
                case IMConstant.MessageType.LOCATION:
                    chatTable.last_message = "[位置]";
                    break;
                case IMConstant.MessageType.REDPAPER:
                    chatTable.last_message = "[红包]";
                    break;

            }
            chatTable.number = 1;
            chatTable.top = false;
            chatTable.message_type = message.message_type;
            chatTable.message_time = message.message_time;
            chatTable.current_id = String.valueOf(userInfoData.uid);
            int chatType = message.chat_type;
            if (chatType == 2) {
                chatTable.chat_type = true;
                chatTable.avatar = message.group_img;
                chatTable.username = message.group_name;
                chatTable.user_id = message.group_id;
            } else {
                chatTable.chat_type = false;
                chatTable.avatar = message.avatar;
                chatTable.username = message.username;
                chatTable.user_id = message.user_id;
            }
            chatTable.saveAsync().listen(success -> {
                if (success) {
                    Log.d("TAG", "收到消息,保存聊天列表成功!");
                    EventBus.getDefault().post(new ChatListUpdateEvent());
                }
            });
        } else {
            ChatTable chatTable = chatTables.get(0);
            switch (message.message_type) {
                case IMConstant.MessageType.TEXT:
                    chatTable.last_message = message.message;
                    break;
                case IMConstant.MessageType.IMAGE:
                    chatTable.last_message = "[图片]";
                    break;
                case IMConstant.MessageType.CARD:
                    chatTable.last_message = "[名片]";
                    break;
                case IMConstant.MessageType.VOICE:
                    chatTable.last_message = "[语音]";
                    break;
                case IMConstant.MessageType.LOCATION:
                    chatTable.last_message = "[位置]";
                    break;
                case IMConstant.MessageType.REDPAPER:
                    chatTable.last_message = "[红包]";
                    break;

            }
            chatTable.is_read = true;
            int number = chatTable.number;
            chatTable.number = number + 1;

            int chatType = message.chat_type;
            if (chatType == 2) {
                chatTable.chat_type = true;
                chatTable.avatar = message.group_img;
                chatTable.username = message.group_name;
                chatTable.user_id = message.group_id;
                chatTable.message_time = message.message_time;
            } else {
                chatTable.chat_type = false;
                chatTable.avatar = message.avatar;
                chatTable.username = message.username;
                chatTable.user_id = message.user_id;
                chatTable.message_time = message.message_time;
            }
            chatTable.saveAsync().listen(success -> {
                if (success) {
                    Log.d("TAG", "更新成功!");
                    EventBus.getDefault().post(new ChatListUpdateEvent());
                }
            });
        }
    }
}
