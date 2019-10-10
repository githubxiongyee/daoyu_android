package com.daoyu.chat.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.event.ApplyFriendEvent;
import com.daoyu.chat.event.ChatListUpdateEvent;
import com.daoyu.chat.event.ReceiveMessageEvent;
import com.daoyu.chat.module.chat.bean.LocalFriendData;
import com.daoyu.chat.module.home.bean.ScanAddFriendB;
import com.daoyu.chat.module.im.module.ApplyFriendTable;
import com.daoyu.chat.module.im.module.ChatTable;
import com.daoyu.chat.module.im.module.IMConstant;
import com.daoyu.chat.module.im.module.MessageDetailTable;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.dy.dyim.android.core.LocalUDPDataSender;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ParserMessageUtils {
    private static Lock lock = new ReentrantLock();
    private static String remarks;

    public synchronized static void parser(String userid, String dataContent) {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        if (userInfoData == null || TextUtils.isEmpty(userInfoData.token)) return;
        if (userid.equals("-1")) {
            //显示通知栏
            showSystemNotify(dataContent);
        } else {
            doMessageMethod(userid, dataContent, userInfoData);
        }
    }

    private synchronized static void doMessageMethod(String userid, String dataContent, UserBean.UserData userInfoData) {
        JSONObject object = null;
        try {
            object = new JSONObject(dataContent);
            int typeMessage = object.optInt("type");
            if (typeMessage == IMConstant.MessageType.ASKFRIEND) {
                addFriendApply(dataContent, userInfoData);
            } else if (typeMessage == IMConstant.MessageType.ANSWERFRIEND) {
                IMConstant.normalFriendMap.put(userid, new LocalFriendData(null, 0));
            } else {
                Map<String, LocalFriendData> normalFriendMap = IMConstant.normalFriendMap;
                if (normalFriendMap.isEmpty()) {
                    return;
                }

                boolean containsKey = normalFriendMap.containsKey(userid);
                if (containsKey) {
                    LocalFriendData localFriendData = normalFriendMap.get(userid);
                    switch (localFriendData.status) {
                        case 0://正常
                            userMessage(dataContent, userInfoData, localFriendData.remarks);
                            break;
                        case 1://拉黑
                            if (typeMessage == IMConstant.MessageType.RECEIVE_PACKAGE) {
                                return;
                            }
                            sendSuccessReceiveRadPackage(userInfoData, false, userid);
                            break;
                    }
                } else {
                    String content = object.optString("content");
                    if (typeMessage == IMConstant.MessageType.TEXT && "我同意了你的朋友验证请求,现在我们可以开始聊天了!".equals(content)) {
                        IMConstant.normalFriendMap.put(userid, new LocalFriendData(null, 0));
                        userMessage(dataContent, userInfoData, null);
                        return;
                    }
                    if (typeMessage == IMConstant.MessageType.RECEIVE_PACKAGE) {
                        return;
                    }
                    requestIsFriend(userid, String.valueOf(userInfoData.uid), content, userInfoData);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送不是好友拒收
     */
    private static void sendSuccessReceiveRadPackage(UserBean.UserData userInfoData, boolean type, String fid) {
        String time = String.valueOf(System.currentTimeMillis());
        Map<String, Object> params = new HashMap<>();
        params.put("userid", userInfoData.uid);
        params.put("userImage", userInfoData.headImg);
        params.put("time", time);
        params.put("content", type ? "您还不是他的好友,请先发送朋友验证请求,对方验证通过后,才能聊天" : "信息已发出,但被对方拒收了");
        params.put("chattype", 1);
        params.put("username", userInfoData.nickName);
        params.put("type", IMConstant.MessageType.RECEIVE_PACKAGE);
        doSendMessage(new Gson().toJson(params), String.valueOf(fid));
    }

    @SuppressLint("StaticFieldLeak")
    private static void doSendMessage(String msg, String friendId) {
        new LocalUDPDataSender.SendCommonDataAsync(BaseApplication.getContext(), msg, friendId) {
            @Override
            protected void onPostExecute(Integer code) {
            }
        }.execute();
    }


    /**
     * 用户消息
     *
     * @param dataContent  消息内容
     * @param userInfoData 当前登录用户
     */
    private synchronized static void userMessage(String dataContent, UserBean.UserData userInfoData, String remarks) {
        try {
            JSONObject object = new JSONObject(dataContent);
            int typeMessage = object.optInt("type");
            switch (typeMessage) {
                case IMConstant.MessageType.TEXT://文本
                    receiveTextMessage(userInfoData, object, remarks);
                    break;
                case IMConstant.MessageType.IMAGE://图片
                    receiveImageMessage(userInfoData, object, remarks);
                    break;
                case IMConstant.MessageType.REDPAPER://红包
                    receiveRadPackageMessage(userInfoData, object, remarks);
                    break;
                case IMConstant.MessageType.RECEIVE_PACKAGE://红包回执
                    receivePackageReceipt(userInfoData, object, remarks);
                    break;
                case IMConstant.MessageType.CARD://名片
                    receiveCardMessage(userInfoData, object, remarks);
                    break;
                case IMConstant.MessageType.LOCATION://位置
                    receiveLocation(userInfoData, object, remarks);
                    break;
                case IMConstant.MessageType.VOICE://语音
                    receiveVoiceMessage(userInfoData, object, remarks);
                    break;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private static void requestIsFriend(String fId, String uId, String dataContent, UserBean.UserData userInfoData) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_friend_id", fId);
        params.put("user_id", uId);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_IS_FRIEND, params, "", new JsonCallback<ScanAddFriendB>(ScanAddFriendB.class) {
            @Override
            public void onSuccess(Response<ScanAddFriendB> response) {
                if (response == null || response.body() == null) return;
                ScanAddFriendB body = response.body();
                if (body.code == 1) {
                    ArrayList<ScanAddFriendB.ScanAddFrienData> data = body.data;
                    if (data == null || data.size() <= 0) {
                        sendSuccessReceiveRadPackage(userInfoData, true, fId);
                    } else {
                        Map<String, LocalFriendData> normalFriendMap = IMConstant.normalFriendMap;
                        LocalFriendData localFriendData = new LocalFriendData();
                        localFriendData.status = 0;
                        normalFriendMap.put(fId, localFriendData);
                        userMessage(dataContent, userInfoData, localFriendData.remarks);
                    }
                }
            }

            @Override
            public void onError(Response<ScanAddFriendB> response) {
                super.onError(response);
                UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
                sendSuccessReceiveRadPackage(userInfoData, true, fId);
            }
        });
    }


    /**
     * 添加好友申请
     *
     * @param dataContent
     * @param userInfoData
     */
    private static void addFriendApply(String dataContent, UserBean.UserData userInfoData) {
        ApplyFriendTable applyMessage = new Gson().fromJson(dataContent, ApplyFriendTable.class);
        String isNearBy = applyMessage.isNearBy;
        List<ApplyFriendTable> applyFriends = LitePal.where("userid = ? and self_uid = ?", applyMessage.userid, String.valueOf(userInfoData.uid)).find(ApplyFriendTable.class);
        if (applyFriends == null || applyFriends.size() <= 0) {
            applyMessage.status = "1";
            applyMessage.self_uid = String.valueOf(userInfoData.uid);
            applyMessage.isNearBy = isNearBy;
            applyMessage.saveAsync().listen(success -> EventBus.getDefault().post(new ApplyFriendEvent(true)));
        } else {
            ApplyFriendTable applyFriendTable = applyFriends.get(0);
            applyFriendTable.time = applyMessage.time;
            applyFriendTable.isNearBy = isNearBy;
            if (!"1".equals(applyFriendTable.status)) {
                applyFriendTable.status = "1";
            }
            applyFriendTable.saveAsync().listen(success -> EventBus.getDefault().post(new ApplyFriendEvent(true)));
        }
    }

    /**
     * 收到文字消息
     *
     * @param userInfoData
     * @param object
     */
    private synchronized static void receiveTextMessage(UserBean.UserData userInfoData, JSONObject object, String remarks) {

        MessageDetailTable message = new MessageDetailTable();
        message.message_type = IMConstant.MessageType.TEXT;

        String userId = object.optString("userid");
        message.user_id = userId;


        String time = object.optString("time");
        message.message_time = time;

        String content = object.optString("content");
        message.message = content;

        String username = object.optString("username");
        message.username = TextUtils.isEmpty(remarks) ? username : remarks;

        String userImage = object.optString("userImage");
        message.avatar = userImage;

        int chattype = object.optInt("chattype");
        message.chat_type = chattype;

        if (chattype == 1) {
            message.chat_id = userInfoData.uid + "DL" + userId;
        } else {
            message.chat_id = userInfoData.uid + "GL" + userId;
        }
        saveMsgList(message, userId, time, content, username, userImage, IMConstant.MessageType.TEXT);
        message.saveAsync().listen(success -> EventBus.getDefault().post(new ReceiveMessageEvent(userId)));

        MessageDetailTable table = new MessageDetailTable();
        table.avatar = message.avatar;
        table.username = message.username;
        table.updateAllAsync("chat_id = ? and user_id = ?", message.chat_id, message.user_id).listen(rowsAffected -> Log.d("TAG", "受影响的记录数:" + rowsAffected));

    }


    /**
     * 收到用户图片消息
     *
     * @param userInfoData
     * @param object
     */
    private synchronized static void receiveImageMessage(UserBean.UserData userInfoData, JSONObject object, String remarks) {
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
        message.username = (TextUtils.isEmpty(remarks) ? username : remarks);

        String userImage = object.optString("userImage");
        message.avatar = userImage;

        int chattype = object.optInt("chattype");
        message.chat_type = chattype;

        if (chattype == 1) {
            message.chat_id = userInfoData.uid + "DL" + userId;
        } else {
            message.chat_id = userInfoData.uid + "GL" + userId;
        }

        saveMsgList(message, userId, time, "[图片消息]", username, userImage, IMConstant.MessageType.IMAGE);

        message.saveAsync().listen(success -> EventBus.getDefault().post(new ReceiveMessageEvent(userId)));
        MessageDetailTable table = new MessageDetailTable();
        table.avatar = userImage;
        table.username = username;
        table.updateAllAsync("chat_id = ? and user_id = ?", message.chat_id, message.user_id).listen(rowsAffected -> Log.d("TAG", "受影响的记录数:" + rowsAffected));
    }

    /**
     * 收到用户红包消息
     */
    private synchronized static void receiveRadPackageMessage(UserBean.UserData userInfoData, JSONObject object, String remarks) {
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
        message.username = TextUtils.isEmpty(remarks) ? username : remarks;

        String userImage = object.optString("userImage");
        message.avatar = userImage;

        int chattype = object.optInt("chattype");
        message.chat_type = chattype;

        message.message_state = IMConstant.MessageStatus.DELIVERING;

        if (chattype == 1) {
            message.chat_id = userInfoData.uid + "DL" + userId;
        } else {
            message.chat_id = userInfoData.uid + "GL" + userId;
        }

        saveMsgList(message, userId, time, "[红包]", username, userImage, IMConstant.MessageType.REDPAPER);

        message.saveAsync().listen(success -> EventBus.getDefault().post(new ReceiveMessageEvent(userId)));

        MessageDetailTable table = new MessageDetailTable();
        table.avatar = userImage;
        table.username = username;
        table.updateAllAsync("chat_id = ? and user_id = ?", message.chat_id, message.user_id).listen(rowsAffected -> Log.d("TAG", "受影响的记录数:" + rowsAffected));
    }


    /**
     * 收到名片
     *
     * @param userInfoData
     * @param object
     */
    private synchronized static void receiveCardMessage(UserBean.UserData userInfoData, JSONObject object, String remarks) {
        MessageDetailTable message = new MessageDetailTable();
        message.message_type = IMConstant.MessageType.CARD;

        String userId = object.optString("userid");
        message.user_id = userId;


        String time = object.optString("time");
        message.message_time = time;

        String content = object.optString("content");
        message.message = content;

        String username = object.optString("username");
        message.username = TextUtils.isEmpty(remarks) ? username : remarks;

        String userImage = object.optString("userImage");
        message.avatar = userImage;

        int chattype = object.optInt("chattype");
        message.chat_type = chattype;

        if (chattype == 1) {
            message.chat_id = userInfoData.uid + "DL" + userId;
        } else {
            message.chat_id = userInfoData.uid + "GL" + userId;
        }
        String card = object.optString("card");
        message.card_name = card;
        String cardImage = object.optString("cardImage");
        message.card_image = cardImage;


        saveMsgList(message, userId, time, "[名片]", username, userImage, IMConstant.MessageType.CARD);

        message.saveAsync().listen(success -> EventBus.getDefault().post(new ReceiveMessageEvent(userId)));

        MessageDetailTable table = new MessageDetailTable();
        table.avatar = message.avatar;
        table.username = message.username;
        table.updateAllAsync("chat_id = ? and user_id = ?", message.chat_id, message.user_id).listen(rowsAffected -> Log.d("TAG", "受影响的记录数:" + rowsAffected));
    }

    private synchronized static void receiveLocation(UserBean.UserData userInfoData, JSONObject object, String remarks) {
        MessageDetailTable message = new MessageDetailTable();
        message.message_type = IMConstant.MessageType.LOCATION;

        String userId = object.optString("userid");
        message.user_id = userId;


        String time = object.optString("time");
        message.message_time = time;

        String content = object.optString("content");
        message.message = content;

        String username = object.optString("username");
        message.username = TextUtils.isEmpty(remarks) ? username : remarks;

        String userImage = object.optString("userImage");
        message.avatar = userImage;

        int chattype = object.optInt("chattype");
        message.chat_type = chattype;
        String lon = object.optString("lon");
        String lat = object.optString("lat");

        message.location = lon + "," + lat;

        if (chattype == 1) {
            message.chat_id = userInfoData.uid + "DL" + userId;
        } else {
            message.chat_id = userInfoData.uid + "GL" + userId;
        }


        saveMsgList(message, userId, time, "[位置]", username, userImage, IMConstant.MessageType.LOCATION);

        message.saveAsync().listen(success -> EventBus.getDefault().post(new ReceiveMessageEvent(userId)));

        MessageDetailTable table = new MessageDetailTable();
        table.avatar = message.avatar;
        table.username = message.username;
        table.updateAllAsync("chat_id = ? and user_id = ?", message.chat_id, message.user_id).listen(rowsAffected -> Log.d("TAG", "受影响的记录数:" + rowsAffected));
    }


    /**
     * 收到语音消息
     *
     * @param userInfoData
     * @param object
     */
    private synchronized static void receiveVoiceMessage(UserBean.UserData userInfoData, JSONObject object, String remarks) {

        MessageDetailTable message = new MessageDetailTable();
        message.message_type = IMConstant.MessageType.VOICE;

        String userId = object.optString("userid");
        message.user_id = userId;


        String time = object.optString("time");
        message.message_time = time;

        String content = object.optString("content");
        message.message = content;

        String username = object.optString("username");
        message.username = (TextUtils.isEmpty(remarks) ? username : remarks);

        String userImage = object.optString("userImage");
        message.avatar = userImage;

        int chattype = object.optInt("chattype");
        message.chat_type = chattype;

        if (chattype == 1) {
            message.chat_id = userInfoData.uid + "DL" + userId;
        } else {
            message.chat_id = userInfoData.uid + "GL" + userId;
        }
        String voiceDuration = object.optString("voiceDuration");
        message.voice_id = voiceDuration;

        saveMsgList(message, userId, time, "[语音消息]", username, userImage, IMConstant.MessageType.VOICE);
        message.saveAsync().listen(success -> EventBus.getDefault().post(new ReceiveMessageEvent(userId)));

        MessageDetailTable table = new MessageDetailTable();
        table.avatar = message.avatar;
        table.username = message.username;
        table.updateAllAsync("chat_id = ? and user_id = ?", message.chat_id, message.user_id).listen(rowsAffected -> Log.d("TAG", "受影响的记录数:" + rowsAffected));

    }

    /**
     * 领取红包回执
     */
    private synchronized static void receivePackageReceipt(UserBean.UserData
                                                                   userInfoData, JSONObject object, String remarks) {
        MessageDetailTable message = new MessageDetailTable();
        message.message_type = IMConstant.MessageType.RECEIVE_PACKAGE;

        String userId = object.optString("userid");
        message.user_id = userId;


        String time = object.optString("time");
        message.message_time = time;

        String content = object.optString("content");
        message.message = content;

        String username = object.optString("username");
        message.username = TextUtils.isEmpty(remarks) ? username : remarks;

        String userImage = object.optString("userImage");
        message.avatar = userImage;

        int chattype = object.optInt("chattype");
        message.chat_type = chattype;


        if (chattype == 1) {
            message.chat_id = userInfoData.uid + "DL" + userId;
        } else {
            message.chat_id = userInfoData.uid + "GL" + userId;
        }
        message.saveAsync().listen(success -> EventBus.getDefault().post(new ReceiveMessageEvent(userId)));

        MessageDetailTable table = new MessageDetailTable();
        table.avatar = userImage;
        table.username = username;
        table.updateAllAsync("chat_id = ? and user_id = ?", message.chat_id, message.user_id).listen(rowsAffected -> Log.d("TAG", "受影响的记录数:" + rowsAffected));
    }


    private static synchronized void saveMsgList(MessageDetailTable message, String userId, String time, String content, String username, String userImage, int type) {

        List<ChatTable> chatTables = LitePal.where("chat_id = ?", message.chat_id).find(ChatTable.class);
        lock.lock();
        try {
            if (chatTables == null || chatTables.size() <= 0) {
                ChatTable chatTable = new ChatTable();
                chatTable.chat_id = message.chat_id;
                chatTable.number = 1;
                chatTable.is_read = true;
                chatTable.last_message = content;
                chatTable.avatar = userImage;
                chatTable.number = 1;
                chatTable.user_id = userId;
                chatTable.username = username;
                chatTable.top = false;
                chatTable.message_type = type;
                chatTable.message_time = time;
                chatTable.current_id = String.valueOf(BaseApplication.getInstance().getUserInfoData().uid);
                chatTable.saveAsync().listen(success -> {
                    if (success) {
                        Log.d("TAG", "收到消息,保存聊天列表成功!");
                        EventBus.getDefault().post(new ChatListUpdateEvent());
                        lock.unlock();
                    }
                });
            } else {
                ChatTable chatTable = chatTables.get(0);
                chatTable.last_message = content;
                chatTable.is_read = true;
                int number = chatTable.number;
                chatTable.number = number + 1;
                chatTable.username = message.username;
                chatTable.avatar = userImage;
                chatTable.message_time = time;
                chatTable.saveAsync().listen(success -> {
                    if (success) {
                        Log.d("TAG", "更新成功!");
                        lock.unlock();
                        EventBus.getDefault().post(new ChatListUpdateEvent());
                    }
                });
            }
        } catch (Exception e) {
            lock.unlock();
        }

    }


    /**
     * 显示系统消息通知栏
     *
     * @param message
     */
    private synchronized static void showSystemNotify(String message) {
        NotificationUtils notificationUtils = new NotificationUtils(BaseApplication.getContext());
        notificationUtils.sendNotification(-1, "系统消息", message, R.drawable.login_logo);
    }

}
