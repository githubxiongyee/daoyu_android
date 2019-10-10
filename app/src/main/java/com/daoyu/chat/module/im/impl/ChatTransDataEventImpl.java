package com.daoyu.chat.module.im.impl;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.module.login.bean.UserBean;
import com.daoyu.chat.module.mine.bean.GetUserValueB;
import com.daoyu.chat.utils.ParserMessageUtils;
import com.daoyu.chat.utils.http.CHttpUtils;
import com.daoyu.chat.utils.http.JsonCallback;
import com.daoyu.chat.utils.http.UrlConfig;
import com.dy.dyim.android.event.ChatTransDataEvent;
import com.lzy.okgo.model.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ChatTransDataEventImpl implements ChatTransDataEvent {
    private final static String TAG = "keyejxptwn";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onTransBuffer(String fingerPrintOfProtocal, String userid, String dataContent, int type) {
        Log.e(TAG, "[type=" + type + "]收到来自用户" + userid + "的消息:" + dataContent);
        ParserMessageUtils.parser(userid, dataContent);
        requestGetValue();
    }

    @Override
    public void onErrorResponse(int i, String s) {

    }

    private void requestGetValue() {
        UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
        Map<String, Object> params = new HashMap<>();
        params.put("code", "messageReminding");
        params.put("user_id", userInfoData.uid);
        CHttpUtils.getInstance().requestDataFromServer(UrlConfig.URL_GET_USER_VALUE, params, null, new JsonCallback<GetUserValueB>(GetUserValueB.class) {
            @Override
            public void onSuccess(Response<GetUserValueB> response) {
                if (response == null || response.body() == null) return;
                GetUserValueB body = response.body();
                if (body.getCode() == 1) {
                    if (body.getData().size() == 0 || body.getData() == null) return;
                    GetUserValueB.DataBean dataBean = body.getData().get(0);
                    if (dataBean.getSettingValue().equals("1")) {
                        sendNotification(BaseApplication.getContext());
                    }
                }
            }
        });
    }

    private long REMIND_PERIOD = 5 * 1000; //提醒（响铃震动）的周期

    private MediaPlayer mPlayer;

    private long mNotificationRemindTime; //通知栏上次提醒时间
    private long mForegroundRemindPreTime;//前台上次提醒时间

    private void sendNotification(Context context) {
        new Thread(() -> {
            showNotification(context);
        }).start();
    }

    private void showNotification(Context context) {
        ring(context);
        vibrate(context);
    }

    /**
     * 判断是否需要提醒，根据是否超过周期
     *
     * @return
     */
    private boolean shouldRemind(boolean isNotification) {
        if (isNotification) {
            if (System.currentTimeMillis() - mNotificationRemindTime >= REMIND_PERIOD) {
                mNotificationRemindTime = System.currentTimeMillis();
                return true;
            }
            return false;
        }

        //如果是判断前台提醒
        if (System.currentTimeMillis() - mForegroundRemindPreTime >= REMIND_PERIOD) {
            mForegroundRemindPreTime = System.currentTimeMillis();
            return true;
        }

        return false;
    }

    /**
     * 响铃
     *
     * @param context
     */
    private void ring(Context context) {
        try {
            if (mPlayer != null) {
                mPlayer.reset();
                mPlayer.release();
                mPlayer = null;
            }
            mPlayer = new MediaPlayer();
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mPlayer.setDataSource(context, uri);
            mPlayer.prepare();
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
                mPlayer.start();
            }
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPlayer.release();
                    mPlayer = null;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 震动
     *
     * @param context
     */
    private void vibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{100, 300, 200, 300}, -1);//表示停100ms, 震300ms, 停200ms，震300ms
    }


}
